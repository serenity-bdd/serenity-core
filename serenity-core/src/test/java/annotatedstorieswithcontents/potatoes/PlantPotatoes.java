package annotatedstorieswithcontents.potatoes;

import net.serenitybdd.annotations.Narrative;
import org.junit.Test;

@Narrative(
        text = {"Plant some Potatoes"}
)
public class PlantPotatoes {
    @Test
    public void someTest(){}
}
