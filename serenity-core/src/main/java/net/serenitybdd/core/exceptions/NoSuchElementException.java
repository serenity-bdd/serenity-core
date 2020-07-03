package net.serenitybdd.core.exceptions;

/**
 * I need {@link org.openqa.selenium.NoSuchElementException} that does
 * <strong>not</strong> include
 * {@link org.openqa.selenium.InvalidSelectorException}. See
 * <a href="https://github.com/serenity-bdd/serenity-core/issues/2138">issue
 * #2138</a> for discussion.
 * 
 * @author SiKing
 */
public class NoSuchElementException extends org.openqa.selenium.NoSuchElementException {

    private static final long serialVersionUID = 1L;

    public NoSuchElementException(String reason) {
	super(reason);
    }

    public NoSuchElementException(String reason, Throwable cause) {
	super(reason, cause);
    }
}
