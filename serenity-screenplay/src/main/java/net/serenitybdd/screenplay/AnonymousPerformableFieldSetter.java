package net.serenitybdd.screenplay;

public class AnonymousPerformableFieldSetter<T extends AnonymousPerformable> {
    private final T anonymousPerformable;
    private final String fieldName;

    public AnonymousPerformableFieldSetter(T anonymousPerformable, String fieldName) {
        this.anonymousPerformable = anonymousPerformable;
        this.fieldName = fieldName;
    }

    public T of(Object fieldValue) {
        anonymousPerformable.setFieldValue(fieldName, fieldValue);
        return anonymousPerformable;
    }
}