package net.thucydides.model.requirements.classpath;

import net.serenitybdd.annotations.Narrative;
import net.serenitybdd.model.strings.Joiner;
import net.thucydides.model.requirements.annotations.NarrativeFinder;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by john on 20/07/2016.
 */
public abstract class PackageInfoNarrative {

    public abstract Optional<String> definedInPath(String path);


    protected Optional<Narrative> getClassLevelNarrativeFor(String path) {
        try {
            String packageInfoPath = (path.endsWith("package-info")) ? path : path + ".package-info";
            return NarrativeFinder.forClass(getClass().getClassLoader().loadClass(packageInfoPath));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static PackageInfoNarrative text() {
        return new TextPackageInfoNarrative();
    }

    public static TypePackageInfoNarrative type() {
        return new TypePackageInfoNarrative();
    }

    public static class TextPackageInfoNarrative extends PackageInfoNarrative {
        public Optional<String> definedInPath(String path) {
            Optional<Narrative> narrative = getClassLevelNarrativeFor(path);
            if (!narrative.isPresent()) {
                return Optional.empty();
            }
            String narrativeText = Joiner.on("\n").join(narrative.get().text());
            return Optional.of((narrative.get().title() + System.lineSeparator() + narrativeText).trim());
        }
    }

    public static class TypePackageInfoNarrative extends PackageInfoNarrative {
        public Optional<String> definedInPath(String path) {
            Optional<Narrative> narrative = getClassLevelNarrativeFor(path);
            if (narrative.isPresent() && !isEmpty(narrative.get().type())) {
                return Optional.of(narrative.get().type());
            }
            return Optional.empty();
        }
    }

}
