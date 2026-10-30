package shop.ordering;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Task;

public class AddToBasket {
    public static Performable theBook(String title) {
        return Task.where("{0} adds '" + title + "' to the basket",
                actor -> actor.<Basket>recall("basket").add(title));
    }
}
