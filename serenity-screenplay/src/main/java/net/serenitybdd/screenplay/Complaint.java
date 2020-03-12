package net.serenitybdd.screenplay;

public class Complaint {

    public static final String NO_VALID_CONSTRUCTOR = "%s should have a constructor with the signature MyException(String message) and MyException(String message, Throwable cause)";

    public static Error from(Class<? extends Error> complaintType,
                                        String complaintDetails,
                                        Throwable actualError) {

        if ((complaintDetails == null) && (actualError.getMessage() == null)) {
            return from(complaintType, actualError);
        }

        complaintDetails = errorMessageFrom(complaintDetails, actualError);

        try {
            return complaintType.getConstructor(String.class, Throwable.class).newInstance(complaintDetails, actualError);
        } catch (Exception e) {
            return new AssertionError(String.format(NO_VALID_CONSTRUCTOR, complaintType.getSimpleName()));
        }
    }

    private static String errorMessageFrom(String complaintDetails, Throwable actualError) {
        if (complaintDetails == null) {
            return actualError.getMessage();
        }
        if (actualError.getMessage() == null) {
            return complaintDetails;
        }
        return complaintDetails + " - " + actualError.getMessage();
    }

    public static Error from(Class<? extends Error> complaintType, Throwable actualError) {
        try {
            return complaintType.getConstructor(Throwable.class).newInstance(actualError);
        } catch (Exception e) {
            try {
                return complaintType.getConstructor(String.class, Throwable.class).newInstance(actualError.getMessage(), actualError);
            } catch (Exception e1) {
                return new AssertionError(String.format(NO_VALID_CONSTRUCTOR, complaintType.getSimpleName()));
            }
        }
    }
}
