package shop.annotated;

import net.serenitybdd.annotations.Epic;
import net.serenitybdd.annotations.Feature;
import net.serenitybdd.annotations.Story;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SerenityJUnit5Extension.class)
@Epic("Buying books")
@Feature("Paying for an order")
@Story("Pay by card")
class WhenPayingForAnOrderTest {
    @Test
    void aReaderCanPayByCard() { }
}
