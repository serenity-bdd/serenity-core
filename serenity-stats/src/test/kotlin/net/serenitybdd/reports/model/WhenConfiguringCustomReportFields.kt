package net.serenitybdd.reports.model

import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.util.SystemEnvironmentVariables
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WhenConfiguringCustomReportFields {

    @Test
    fun `custom fields are provided in the serenity configuration file under report-dot-customfields`() {
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.color","Red")
        environmentVariables.setProperty("report.customfields.food","Apples")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactlyInAnyOrder("Color","Food")
        assertThat(customReportFields.values).containsExactlyInAnyOrder("Red","Apples")
    }

    @Test
    fun `the order of custom fields can be mandated by using the report-dot-customfields-dot-order property`() {
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.color","Red")
        environmentVariables.setProperty("report.customfields.food","Apples")
        environmentVariables.setProperty("report.customfields.drink","Cidre")
        environmentVariables.setProperty("report.customfields.order","food,drink,color")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("Food","Drink","Color")
        assertThat(customReportFields.values).containsExactly("Apples","Cidre","Red")
    }

    @Test
    fun `incorrect fields in the order configuration should be ignored`() {
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.color","Red")
        environmentVariables.setProperty("report.customfields.food","Apples")
        environmentVariables.setProperty("report.customfields.drink","Cidre")
        environmentVariables.setProperty("report.customfields.order","food,drink,number,color")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("Food","Drink","Number","Color")
        assertThat(customReportFields.values).containsExactly("Apples","Cidre","","Red")
    }

    @Test
    fun `field values can be defined from system properties using the dollar notation`() {
        System.setProperty("COLOR","blue")
        val envProperties = Properties()
        val systemValues = mapOf("TARGET_PLATFORM" to "staging");
        val environmentVariables = SystemEnvironmentVariables(envProperties, systemValues);

        // GIVEN
        environmentVariables.setProperty("report.customfields.target_platform","\${TARGET_PLATFORM}")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("Target platform")
        assertThat(customReportFields.values[0]).isNotEmpty;
    }


    @Test
    fun `field names can be acronyms`() {
        val environmentVariables = SystemEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("report.customfields.XYZ","123")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactly("XYZ")
    }

    @Test
    fun `custom fields are provided in the serenity configuration file under report-dot-customfields using specific environment configuration`(){
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("environments.android.report.customfields.brand","pixel")
        environmentVariables.setProperty("environments.android.report.customfields.model","2 xl")
        environmentVariables.setProperty("environment","android")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactlyInAnyOrder("Brand","Model")
        assertThat(customReportFields.values).containsExactlyInAnyOrder("pixel","2 xl")
    }
    @Test
    fun `custom fields are provided in the serenity configuration file under report-dot-customfields using properties defined for all and specific environment`(){
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("environments.all.report.customfields.type","mobile")
        environmentVariables.setProperty("environments.android.report.customfields.brand","pixel")
        environmentVariables.setProperty("environments.android.report.customfields.model","2 xl")
        environmentVariables.setProperty("environment","android")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactlyInAnyOrder("Type","Brand","Model")
        assertThat(customReportFields.values).containsExactlyInAnyOrder("mobile","pixel","2 xl")
    }

    @Test
    fun `only custom fields defined in the specific environment are provided in the serenity configuration file under report-dot-customfields`(){
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("environments.android.report.customfields.brand","pixel")
        environmentVariables.setProperty("environments.android.report.customfields.model","2 xl")
        environmentVariables.setProperty("environments.ios.report.customfields.brand","Iphone")
        environmentVariables.setProperty("environment","android")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactlyInAnyOrder("Brand","Model")
        assertThat(customReportFields.values).containsExactlyInAnyOrder("pixel","2 xl")
    }
    @Test
    fun `when no environment is provided but default configuration is present custom fields are provided in the serenity configuration file under report-dot-customfields`(){
        val environmentVariables = MockEnvironmentVariables()

        // GIVEN
        environmentVariables.setProperty("environments.default.report.customfields.type","mobile")
        environmentVariables.setProperty("environments.android.report.customfields.brand","nokia")
        environmentVariables.setProperty("environments.ios.report.customfields.brand","Iphone")

        // WHEN
        val customReportFields = CustomReportFields(environmentVariables)

        // THEN
        assertThat(customReportFields.fieldNames).containsExactlyInAnyOrder("Type")
        assertThat(customReportFields.values).containsExactlyInAnyOrder("mobile")
    }

}