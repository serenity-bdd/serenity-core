package net.serenitybdd.core.reports;

public interface WithTitle {
    AndContent withTitle(String title);
    WithTitle asEvidence();
}