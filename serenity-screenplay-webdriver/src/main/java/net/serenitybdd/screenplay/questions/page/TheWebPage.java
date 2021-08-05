package net.serenitybdd.screenplay.questions.page;

public class TheWebPage {
    public static PageTitleQuestion title() {
        return new PageTitleQuestion();
    }
    public static CurrentURLQuestion currentUrl() { return new CurrentURLQuestion(); }
    public static AlertTextQuestion alertText() {
        return new AlertTextQuestion();
    }
}
