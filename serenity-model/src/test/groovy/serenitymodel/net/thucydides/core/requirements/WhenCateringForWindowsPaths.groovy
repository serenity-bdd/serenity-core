package serenitymodel.net.thucydides.core.requirements

import serenitymodel.net.thucydides.core.requirements.WindowsFriendly
import spock.lang.Specification
import spock.lang.Unroll

class WhenCateringForWindowsPaths extends Specification {

    @Unroll
    def shouldWorkForWindowsAndUnixPaths() {
        expect:
        WindowsFriendly.formOf(path) == expectedPath
        where:
        path        | expectedPath
        "a/b/c"     | "a/b/c"
        "/a/b/c"    | "/a/b/c"
        "/D:/a/b/c" | "D:\\a\\b\\c"
        "D:/a/b/c"  | "D:\\a\\b\\c"
    }
}
