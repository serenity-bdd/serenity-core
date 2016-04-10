package net.serenitybdd.screenplay.authoring;

import net.serenitybdd.screenplay.Actor;

/**
 * Created by john on 8/04/2016.
 */
public class Author extends Actor {
    public final String email;
    public final String password;

    public Author(String name, String email, String password) {
        super(name);
        this.email = email;
        this.password = password;
    }
}
