package net.serenitybdd.screenplay.questions;

public class ValueOf {
    public static <OUTPUT> OUTPUT the(UIState<OUTPUT> answer) {
        return answer.value();
    }
}