package net.thucydides.core.annotations;

/**
 * Thrown when there is an implementation error of WebElementFacade extension
 * 
 * @author Alex Okrushko
 *
 */
public class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 870167953834215097L;
	
	public NotImplementedException(final String message) {
        super(message);
    }

    public NotImplementedException(final String message, final IllegalAccessException cause) {
        super(message, cause);
    }

}
