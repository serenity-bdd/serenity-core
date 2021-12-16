## Serenity Shutterbug 1.x integration

To use Shutterbug 1.x with Serenity, add the Serenity Shutterbug dependency to your project:

```xml
        <dependency>
            <groupId>net.serenity-bdd</groupId>
            <artifactId>serenity-shutterbug1x</artifactId>
        </dependency>
```

Then set the `serenity.screenshooter` property in your Serenity config file:
```
serenity.screenshooter=shutterbug1x
```

You can configure Shutterbug with the following properties:
```hocon
shutterbug {
    capturestrategy = VIEWPORT (or) FULL (or) FULL_SCROLL (or) VERTICAL_SCROLL (or) HORIZONTAL_SCROLL
    betweenScrollTimeout = 100
    useDevicePixelRatio = true 
}
```

[Available capture strategies](https://github.com/assertthat/selenium-shutterbug#available-capture-types)