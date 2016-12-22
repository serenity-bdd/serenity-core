package net.thucydides.core.requirements.model;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LoadedNarrative {

    public static LoadedNarrative load() {
        return new LoadedNarrative();
    }

    public Optional<Narrative> fromFile(File narrativeFile, String defaultType) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(narrativeFile), StandardCharsets.UTF_8))) {
            List<String> lines = readPreambleFrom(reader);

            String title = null;
            String type = defaultType;
            String cardNumber = findCardNumberIn(lines);
            List<String> versionNumbers = findVersionNumberIn(lines);
            Optional<String> titleLine = readOptionalTitleFrom(lines);
            if (titleLine.isPresent()) {
                title = titleFrom(titleLine.get());
                type = typeFrom(titleLine.get()).or(defaultType);
            }
            String text = readNarrativeFrom(lines);
            reader.close();
            return Optional.of(new Narrative(Optional.fromNullable(title),
                    Optional.of(narrativeFile.getPath()),
                    Optional.fromNullable(cardNumber),
                    versionNumbers,
                    type,
                    text));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Optional.absent();
    }


    private List<String> readPreambleFrom(BufferedReader reader) throws IOException {
        List<String> usefulLines = Lists.newArrayList();

        boolean preambleFinished = false;
        while (!preambleFinished) {
            String nextLine = reader.readLine();
            if (nextLine == null) {
                preambleFinished = true;
            } else {
                if (preambleFinishedAt(nextLine)) {
                    preambleFinished = true;
                } else if (thereIsUsefulInformationIn(nextLine)) {
                    usefulLines.add(nextLine);
                }
            }
        }
        return usefulLines;
    }

    private boolean preambleFinishedAt(String nextLine) {
        return (normalizedLine(nextLine).startsWith("scenario")
                || normalizedLine(nextLine).startsWith("background:"));
    }

    private String normalizedLine(String nextLine) {
        return nextLine.trim().toLowerCase();
    }

    private boolean thereIsUsefulInformationIn(String nextLine) {
        String normalizedText = normalizedLine(nextLine);
        return !normalizedText.startsWith("#")
                && !normalizedText.startsWith("meta:")
                && !normalizedText.startsWith("background:")
                && !(normalizedText.startsWith("@")
                && (!normalizedText.startsWith("@issue")
                && (!normalizedText.startsWith("@versions"))));
    }


    private String findCardNumberIn(List<String> lines) {
        String cardNumber = null;
        for (String line : lines) {
            String normalizedLine = line.toUpperCase();
            if (normalizedLine.startsWith("@ISSUES")) {
                String issueList = normalizedLine.replace("@ISSUES", "").trim();
                List<String> issues = Lists.newArrayList(Splitter.on(",").trimResults().split(issueList));
                if (!issues.isEmpty()) {
                    cardNumber = issues.get(0);
                }
            } else if (normalizedLine.startsWith("@ISSUE")) {
                String issueNumber = normalizedLine.replace("@ISSUE", "").trim();
                if (!StringUtils.isEmpty(issueNumber)) {
                    cardNumber = issueNumber;
                }
            }
        }
        return cardNumber;
    }

    private List<String> findVersionNumberIn(List<String> lines) {
        for (String line : lines) {
            String normalizedLine = line.toUpperCase();
            if (normalizedLine.startsWith("@VERSIONS")) {
                String versionList = line.substring("@VERSIONS".length()).trim();
                return Lists.newArrayList(Splitter.on(",").trimResults().split(versionList));
            }
        }
        return ImmutableList.of();
    }

    private String readNarrativeFrom(List<String> lines) {

        StringBuilder description = new StringBuilder();
        for (String line : lines) {
            if (!isMarkup(line) && !isAnnotation(line) && !(isComment(line))) {
                description.append(line.trim());
                description.append(System.lineSeparator());
            }
        }
        return description.toString().trim();

    }


    private boolean isAnnotation(String line) {
        return normalizedLine(line).startsWith("@");
    }

    private Optional<String> readOptionalTitleFrom(List<String> lines) {
        if (!lines.isEmpty()) {
            String firstLine = lines.get(0);
            if (!isMarkup(firstLine)) {
                Optional<String> title = Optional.of(stripFeatureFrom(firstLine));
                lines.remove(0);
                return title;
            }
        }
        return Optional.absent();
    }

    private boolean isMarkup(String line) {
        String normalizedLine = normalizedLine(line);
        return normalizedLine.startsWith("narrative:")
                || normalizedLine.startsWith("givenstory:")
                || normalizedLine.startsWith("background:")
                || normalizedLine.startsWith("meta")
                || normalizedLine.startsWith("@")
                || normalizedLine.startsWith("givenstories:");
    }



    private String stripFeatureFrom(String firstLine) {
        return (firstLine.toLowerCase().startsWith("feature:")) ? firstLine.substring(8).trim() : firstLine;
    }

    private boolean isComment(String line) {
        return line.startsWith("#");
    }


    private String titleFrom(String titleLine) {
        return (titleLine.contains(":")) ? titleLine.substring(titleLine.indexOf(":") + 1) : titleLine;
    }

    Optional<String> TYPE_UNDEFINED = Optional.absent();

    private Optional<String> typeFrom(String titleLine) {
        if (titleLine.contains(":")) {
            String featureNameSection = titleLine.substring(0, titleLine.indexOf(":"));
            String featureName = featureNameSection.split("\\s")[0];
            return Optional.of(featureName.toLowerCase().trim());
        } else {
            return TYPE_UNDEFINED;
        }
    }

}