package net.serenitybdd.screenplay.questions.targets;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.targets.Target;

import java.util.Collection;
import java.util.List;

public class TheTarget {
    public static Question<String> valueOf(Target target) {
        return new TargetValue(target);
    }

    public static Question<Collection<String>> valuesOf(Target target) {
        return new TargetValues(target);
    }

    public static TargetText textOf(Target target) {
        return new TargetText(target);
    }

    public static TargetTextValues textValuesOf(Target target) {
        return new TargetTextValues(target);
    }

    public static Question<String> selectedValueOf(Target target) {
        return new TargetSelectedValue(target);
    }

    public static Question<String> selectedVisibleTextValueOf(Target target) {
        return new TargetSelectedVisibleText(target);
    }

    public static Question<List<String>> selectOptionsOf(Target target) {
        return new TargetSelectOptions(target);
    }

    public static TargetAttributeBuilder attributeNamed(String name) {
        return new TargetAttributeBuilder(name);
    }

    public static TargetCSSBuilder cssValueNamed(String name) {
        return new TargetCSSBuilder(name);
    }

    public static class TargetAttributeBuilder {
        private final String name;

        public TargetAttributeBuilder(String name) {
            this.name = name;
        }

        public Question<String> forTarget(Target target) {
            return new TargetAttribute(target, name);
        }

        public Question<Collection<String>> forTargetsMatching(Target target) {
            return new TargetAttributes(target, name);
        }
    }

    public static class TargetCSSBuilder {
        private final String name;

        public TargetCSSBuilder(String name) {
            this.name = name;
        }

        public Question<String> forTarget(Target target) {
            return new TargetCSSValue(target, name);
        }
        public Question<Collection<String>> forTargetsMatching(Target target) {
            return new TargetCSSValues(target, name);
        }
    }

}