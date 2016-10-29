# Windows
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.os="Windows" -Dbrowserstack.os_version="10" -Dbrowserstack.browser="firefox" -Dbrowserstack.version="45"
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.os="Windows" -Dbrowserstack.os_version="10" -Dbrowserstack.browser="chrome" -Dbrowserstack.version="54"
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.os="Windows" -Dbrowserstack.os_version="10" -Dbrowserstack.browser="IE" -Dbrowserstack.version="11"
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.os="Windows" -Dbrowserstack.os_version="7" -Dbrowserstack.browser="IE" -Dbrowserstack.version="10"
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.os="Windows" -Dbrowserstack.os_version="7" -Dbrowserstack.browser="firefox" -Dbrowserstack.version="40"
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.os="Windows" -Dbrowserstack.os_version="10" -Dbrowserstack.browser="Edge" -Dbrowserstack.version="13"

# Mobile
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.browserName="android" -Dbrowserstack.device="Samsung Galaxy S5" -Dbrowserstack.platform="ANDROID" -Dwebdriver.driver=remote
mvn clean verify -Dtags=smoketest -Dparallel.tests=2 -Dbrowserstack.url=$BROWSERSTACK -Dbrowserstack.browserName="iPhone" -Dbrowserstack.device="iPhone 6S Plus" -Dbrowserstack.platform="MAC" -Dwebdriver.driver=remote
