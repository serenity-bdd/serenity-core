package net.thucydides.core.webdriver.chrome

import spock.lang.Specification
import spock.lang.Unroll

class WhenPassingChromeSwitchesToWebdriver extends Specification {

    @Unroll("#optionText")
    def "should split chrome switches into an array"() {
        given:
            def splitter = new OptionsSplitter();
        when:
            def options = splitter.split(optionText);
        then:
            options == expectedOptions
        where:
        optionText | expectedOptions
        "--allow-file-access" | ["--allow-file-access"]
        "--allow-file-access, --assert-test" | ["--allow-file-access", "--assert-test"]
        "--allow-file-access --assert-test" | ["--allow-file-access", "--assert-test"]
        "--allow-file-access; --assert-test" | ["--allow-file-access", "--assert-test"]
        "test-type;--test-type;--enable-automation;--assert-test" | ["--test-type", "--enable-automation","--assert-test"]
        "--user-agent=Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B)AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 MobileSafari/535.19" | ["--user-agent=Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B)AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 MobileSafari/535.19"]
        "--allow-file-access, --user-agent=Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B)AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 MobileSafari/535.19" | ["--allow-file-access","--user-agent=Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B)AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 MobileSafari/535.19"]
    }
}
