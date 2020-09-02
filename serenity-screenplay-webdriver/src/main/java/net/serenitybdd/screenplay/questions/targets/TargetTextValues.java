package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.annotations.Subject;
import net.serenitybdd.screenplay.questions.Attribute;
import net.serenitybdd.screenplay.targets.Target;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Subject("#target")
public class TargetTextValues implements Question<List<String>> {

    private final Target target;
    private final Function<String, String> renderElement;

    private static Function<String, String>  NO_CHANGE = value -> value;
    private static Function<String, String>  TRIM_WHITESPACE = String::trim;


    TargetTextValues(Target target) {
        this(target, NO_CHANGE);
    }

    private TargetTextValues(Target target, Function<String, String> renderElement) {
        this.target = target;
        this.renderElement = renderElement;
    }

    public TargetTextValues withNoSurroundingWhiteSpace() {
        return new TargetTextValues(target, TRIM_WHITESPACE);
    }

    @Override
    public List<String> answeredBy(Actor actor) {
        List<String> textValues = Attribute.of(target)
                                            .named("innerText")
                                            .viewedBy(actor)
                                            .asList();


        return textValues.stream()
                         .map(renderElement::apply)
                         .collect(Collectors.toList());

    }
}
