package net.serenitybdd.model.buildinfo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenStoringBuildInformationProgrammatically {
    @Test
    void we_can_add_arbitrary_build_info_fields_programmatically() {

        // Given
        BuildInfo.section("Toggles").setProperty("toggle1", "on");

        // When
        BuildInfo.clear();
        BuildInfo.load();

        // Then
        assertThat(BuildInfo.section("Toggles").getValues()).containsEntry("toggle1", "on");
    }

    @Test
    void we_can_several_values_in_each_section() {

        // Given
        BuildInfo.section("Toggles").setProperty("toggle1", "on");
        BuildInfo.section("Toggles").setProperty("toggle2", "on");
        BuildInfo.section("Toggles").setProperty("toggle3", "off");

        // When
        BuildInfo.clear();
        BuildInfo.load();

        // Then
        assertThat(BuildInfo.section("Toggles").getValues()).containsEntry("toggle1", "on");
        assertThat(BuildInfo.section("Toggles").getValues()).containsEntry("toggle2", "on");
        assertThat(BuildInfo.section("Toggles").getValues()).containsEntry("toggle3", "off");
    }

    @Test
    void we_can_several_different_sections() {

        // Given
        BuildInfo.section("Toggles").setProperty("toggle1", "on");
        BuildInfo.section("Toggles").setProperty("toggle2", "on");

        BuildInfo.section("Versions").setProperty("module1", "1.2.3");
        BuildInfo.section("Versions").setProperty("module2", "2.3.4");

        // When
        BuildInfo.clear();
        BuildInfo.load();

        // Then
        assertThat(BuildInfo.section("Toggles").getValues()).containsEntry("toggle1", "on");
        assertThat(BuildInfo.section("Toggles").getValues()).containsEntry("toggle2", "on");
        assertThat(BuildInfo.section("Versions").getValues()).containsEntry("module1", "1.2.3");
        assertThat(BuildInfo.section("Versions").getValues()).containsEntry("module2", "2.3.4");
    }
}
