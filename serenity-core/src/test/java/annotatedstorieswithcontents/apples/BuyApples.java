package annotatedstorieswithcontents.apples;

import net.serenitybdd.annotations.Narrative;
import org.junit.Test;

@Narrative(
        title = "Title for test 1",
        text = {"A Narrative for test 1",
                "Multiple lines"}
)
public class BuyApples {
    @Test
    public void someTest() {}
}
