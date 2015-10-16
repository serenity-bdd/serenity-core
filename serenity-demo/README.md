# Serenity Demo


__WARNING:__ Works with Serenity v0.8.31, Selenium v2.23.1


## Execute the Demo


### Run Serenity with Maven, using FireFox

	mvn  verify

Does not run on Mac OS-X v10.10.5 with FireFox v41.0.1
Probably the `serenity-firefox-driver v2.23.1` is to old.


### Run Serenity with Maven, using Chrome

__WARNING:__ Before running the Maven command below, make sure the property `webdriver.chrome.driver` has the 
correct value. Open the `pom.xml` and search for the profile _chrome_. Correct the path for _webdriver.chrome.driver_.

	mvn  -Pchrome  verify

Runs on Mac OS-X v10.10.5 with Chrome v45.0.2454.93 (64-bit)


## Using Maven settings, for system specific properties

The Chrome Web-Driver path can be set in the Maven `pom.xml`. But it can also be added in the `settings.xml`. This
makes it easier, as no system / user specific settings are needed in the project configuration (`pom.xml`).

Add the following lines to the `settings.xml`, in the section for `profiles`.

    <profile>
  	  <id>chrome</id>
  	  <properties>
        <webdriver.chrome.driver>/Users/tjeerd/applications-dev/chromedriver</webdriver.chrome.driver>
  	  </properties>
  	</profile>
