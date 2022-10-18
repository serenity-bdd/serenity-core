# BitBar integration plugin

This plugin provides possibility to run UI tests using BitBar platform.

## Sample configuration

In serenity.conf the following properties can be used to run tests against BitBar:

```
webdriver {
  driver = remote
  capabilities {
    platformName = "Windows"
    browserName = "chrome"
    browserVersion = "latest"
    "bitbar:options" {
      osVersion = "10"
      screenResolution = "1920x1200"
    }
  }
}

bitbar {
  active = true
  apiKey = "YOUR_API_KEY"
  hub = "eu-desktop-hub"  
  cloudUrl = "https://cloud.bitbar.com"
}
```

All the properties under "bitbar" will be added to "bitbar:options" capability.

## Useful links

* [BitBar Serenity Junit Sample](https://github.com/bitbar/test-samples/tree/master/samples/testing-frameworks/serenity-junit)
* [BitBar Capabilities Creator](https://cloud.bitbar.com/#public/capabilities-creator)
* [BitBar Appium Support](https://support.smartbear.com/bitbar/docs/testing-with-bitbar/automated-testing/appium/index.html)
* [BitBar Selenium Support](https://support.smartbear.com/bitbar/docs/desktop-testing/automated-testing.html)
