package net.thucydides.core.model;

public class ManualTestEvidence {

    private final String link;
    private final String label;

    public ManualTestEvidence(String link) {
        this.link = link;
        this.label = "Test Evidence";
    }

    public ManualTestEvidence(String link, String label) {
        this.link = link;
        this.label = label;
    }

    /**
     * Rendered evidence can be a simple link or take the form [Label]http://link.to.evidence
     * @param evidenceAsText
     * @return
     */
    public static ManualTestEvidence from(String evidenceAsText) {
        String label = "Test Evidence";
        String link = evidenceAsText;
        if (evidenceAsText.startsWith("[") && evidenceAsText.contains("]") && !evidenceAsText.endsWith("]")) {
            label = replaceUnderscorsWithSpaces(evidenceAsText.substring(1, evidenceAsText.indexOf("]")));
            link = stripOptionalParantheses(evidenceAsText.substring(evidenceAsText.indexOf("]") + 1));
        } else if (evidenceAsText.endsWith(")") && evidenceAsText.contains("(")) {
            link = evidenceAsText.substring(evidenceAsText.indexOf("(") + 1, evidenceAsText.length() - 1);
            label = replaceUnderscorsWithSpaces(evidenceAsText.substring(0, evidenceAsText.indexOf("(")));
        }
        return new ManualTestEvidence(link, label);
    }

    private static String replaceUnderscorsWithSpaces(String label) {
        return label.replace("_", " ");
    }

    private static String stripOptionalParantheses(String linkText) {
        if (linkText.startsWith("(") && linkText.endsWith(")")) {
            return linkText.substring(1, linkText.length() - 1);
        }
        return linkText;
    }

    public String getLink() {
        return link;
    }

    public String getLabel() {
        return label;
    }
}
