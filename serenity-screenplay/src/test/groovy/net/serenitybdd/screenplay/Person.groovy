package net.serenitybdd.screenplay;

class Person {
    private String name;
    private int age;
    private boolean isAuthorised;

    Person() {}

    Person(String name, int age, boolean isAuthorised) {
        this.name = name
        this.age = age
        this.isAuthorised = isAuthorised
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Person person = (Person) o

        if (age != person.age) return false
        if (isAuthorised != person.isAuthorised) return false
        if (name != person.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + age
        result = 31 * result + (isAuthorised ? 1 : 0)
        return result
    }
}