package annotatedstorieswithcontents.potatoes.big_potatoes;

import net.serenitybdd.annotations.Narrative;
import org.junit.Test;

@Narrative(
        text = {"This is a narrative for",
                "test big potato"}
)
public class PlantBigPotatoes {
    @Test
    public void someTest(){}
}
