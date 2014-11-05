package net.thucydides.core.screenshots.integration

import com.google.common.base.Optional
import net.thucydides.core.annotations.BlurScreenshots
import net.thucydides.core.screenshots.BlurLevel
import net.thucydides.core.screenshots.ScreenshotBlurCheck
import spock.lang.Specification

class WhenDecidingWhenToBlurScreenshots extends Specification {

    @BlurScreenshots("HEAVY")
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

    @BlurScreenshots("HEAVY")
    def "should override Blur Level with the value of inner method annotation"() {
        when:
        Optional<BlurLevel> blurLevel = getBlurLevelFromStepMethodSetWithLightBlur();
        then:
            assert blurLevel.isPresent();
            assert blurLevel.get() == BlurLevel.LIGHT;
    }


    @BlurScreenshots("LIGHT")
    Optional<BlurLevel> getBlurLevelFromStepMethodSetWithLightBlur() {
        return new ScreenshotBlurCheck().blurLevel();
    }


}
