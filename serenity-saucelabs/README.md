[![SauceLabs](https://saucelabs.com/images/logo-saucelabs.png)](https://saucelabs.com)

# SauceLabs integration plugin

This plugin provides possibility to run UI tests using SauceLabs platform.

## Sample configuration

In serenity.conf the following properties can be used to run tests against SauceLabs:

```
webdriver {
    driver = remote
    remote.url = "https://#{saucelabs.username}:#{saucelabs.accessKey}@ondemand.#{saucelabs.datacenter}.saucelabs.com/wd/hub"
}

saucelabs {
    datacenter = "eu-central-1"
    username = "YOUR_USER_NAME"
    accessKey = "YOUR_ACCESS_KEY"
    browserName = edge
    screenResolution = "1920x1080"
    tunnelIdentifier = "YUOR_TUNNEL_ID"
}
```

All the properties under "saucelabs" will be added to "sauce:options" capability.

## Useful links

* [SauceLabs platform configurator](https://saucelabs.com/platform/platform-configurator)
* [SauceLabs test configuration options](https://docs.saucelabs.com/dev/test-configuration-options/index.html)
* [SauceLabs REST APIs](https://docs.saucelabs.com/dev/api/index.html)
