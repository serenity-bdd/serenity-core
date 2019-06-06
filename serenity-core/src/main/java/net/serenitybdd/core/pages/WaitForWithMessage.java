package net.serenitybdd.core.pages;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.function.Function;

class WaitForWithMessage<T> implements ExpectedCondition<T> {

        private final String message;
        private final ExpectedCondition<T> expectedCondition;

        public WaitForWithMessage(String message, ExpectedCondition<T> expectedCondition) {
            this.message = message;
            this.expectedCondition = expectedCondition;
        }

        @Override
        public Function andThen(Function after) {
            return expectedCondition.andThen(after);
        }

        @Override
        public Function compose(Function before) {
            return expectedCondition.compose(before);
        }

        @Override
        public String toString() {
            return message;
        }

        @NullableDecl
        @Override
        public T apply(@NullableDecl WebDriver input) {
            return expectedCondition.apply(input);
        }
    }
