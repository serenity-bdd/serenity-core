package net.thucydides.core.screenshots.integration

import net.serenitybdd.annotations.BlurLevel
import net.serenitybdd.annotations.BlurScreenshots
import net.thucydides.core.screenshots.ScreenshotBlurCheck
import spock.lang.Specification

class WhenDecidingWhenToBlurScreenshots extends Specification {

    @BlurScreenshots(BlurLevel.HEAVY)
    def "should blur screenshot if annotation is present for a test method"() {
        when:
            Optional<BlurLevel> blurLevel = new ScreenshotBlurCheck().blurLevel();
        then:
            assert blurLevel.isPresent();
            assert blurLevel.get() == BlurLevel.HEAVY;
    }

    def "should NOT blur screenshot if annotation is NOT present for a test method"() {
        when:
            Optional<BlurLevel> blurLevel = new ScreenshotBlurCheck().blurLevel();
        then:
            assert ! blurLevel.isPresent();
    }

    @BlurScreenshots(BlurLevel.HEAVY)
    def "should override Blur Level with the value of inner method annotation"() {
        when:
        Optional<BlurLevel> blurLevel = getBlurLevelFromStepMethodSetWithLightBlur();
        then:
            assert blurLevel.isPresent();
            assert blurLevel.get() == BlurLevel.LIGHT;
    }


    @BlurScreenshots(BlurLevel.LIGHT)
    Optional<BlurLevel> getBlurLevelFromStepMethodSetWithLightBlur() {
        return new ScreenshotBlurCheck().blurLevel();
    }


}
