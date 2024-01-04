package net.thucydides.model.requirements;

import net.serenitybdd.model.collect.NewList;
import net.thucydides.model.requirements.model.Requirement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequirementAncestryTest {

    /**
     * Parent ID must be set to an empty string for the {@link MergedRequirementList}
     * to correctly merge the requirements discovered by different tag providers.
     */
    @Test
    void sets_the_parent_id_to_empty_string_when_parent_does_not_exist() {

        Requirement requirement = Requirement.named("card-payments")
                .withOptionalDisplayName("Card payments")
                .withTypeOf("feature");

        List<Requirement> requiremnts = NewList.of(requirement);

        RequirementAncestry.addParentsTo(requiremnts);

        assertThat(requiremnts.get(0).getParent()).isEqualTo("");
    }

    @Test
    void sets_the_parent_id_to_requested_string() {

        Requirement requirement = Requirement.named("card-payments")
                .withOptionalDisplayName("Card payments")
                .withTypeOf("feature");

        List<Requirement> requiremnts = NewList.of(requirement);

        RequirementAncestry.addParentsTo(requiremnts, "parent");

        assertThat(requiremnts.get(0).getParent()).isEqualTo("parent");
    }
}
