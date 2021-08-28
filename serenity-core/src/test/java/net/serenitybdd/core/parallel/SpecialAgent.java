package net.serenitybdd.core.parallel;

public class SpecialAgent implements Agent {
    private final String id;
    private final String name;

    public SpecialAgent(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public static Agent named(String name, String id) {
        return new SpecialAgent(name, id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
