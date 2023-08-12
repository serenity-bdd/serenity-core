package net.serenitybdd.screenplay.webtests.questions;

import net.serenitybdd.model.collect.NewList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.List;

/**
 * Created by john on 13/06/2016.
 */
public class TheValidationMessages implements Question< List< String > > {
    public static Question< List< String > > displayed()
    {
        return new TheValidationMessages();
    }

    @Override public List< String > answeredBy( Actor actor )
    {
        return NewList.of("BSB must be 6 digits"); // should really be something like: Text.of( ErrorMessages.MESSAGES ).viewedBy( actor ).asList();
    }
}
