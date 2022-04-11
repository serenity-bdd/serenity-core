package net.serenitybdd.screenplay;

class Pet {
    private final String name;
    private final int age;
    private final String species;

    private static final String DEFAULT_SPECIES = "cat";

    Pet() {
        this.species = DEFAULT_SPECIES;
    }

    Pet(String name, int age, String species) {
        this.name = name
        this.age = age
        this.species = species
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Pet person = (Pet) o

        if (age != person.age) return false
        if (species != person.species) return false
        if (name != person.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = result + (species != null ? species.hashCode() : 0)
        result = 31 * result + age
        return result
    }
}