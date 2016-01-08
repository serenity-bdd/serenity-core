package net.serenitybdd.screenplay;

import static com.google.common.base.Optional.fromNullable;

public class Complaint {

    public static final String NO_VALID_CONSTRUCTOR = "%s should have a constructor with the signature MyException(String message) and MyException(String message, Throwable cause)";

    public static Error from(Class<? extends Error> complaintType,
                                        String complaintDetails,
                                        Error actualError) {

        if ((complaintDetails == null) && (actualError.getMessage() == null)) {
            return from(complaintType, actualError);
        }

        complaintDetails = fromNullable(complaintDetails).or(actualError.getMessage());

        try {
            return complaintType.getConstructor(String.class, Throwable.class).newInstance(complaintDetails, actualError);
        } catch (Exception e) {
            return new AssertionError(String.format(NO_VALID_CONSTRUCTOR, complaintType.getSimpleName()));
        }
    }

    public static Error from(Class<? extends Error> complaintType, Throwable actualError) {
        try {
            return complaintType.getConstructor(String.class, Throwable.class).newInstance(actualError);
        } catch (Exception e) {
            return new AssertionError(String.format(NO_VALID_CONSTRUCTOR, complaintType.getSimpleName()));
        }
    }
}
