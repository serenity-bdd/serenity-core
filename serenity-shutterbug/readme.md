## Serenity Shutterbug 1.x integration

To use Shutterbug 0.9.5 with Serenity, add the Serenity Shutterbug dependency to your project:

```xml
        <dependency>
            <groupId>net.serenity-bdd</groupId>
            <artifactId>serenity-shutterbug</artifactId>
        </dependency>
```

Then set the `serenity.screenshooter` property in your Serenity config file:
```hocon
serenity.screenshooter=shutterbug
```

You can configure Shutterbug with the following properties:
```hocon
shutterbug {
    scrollstrategy = VIEWPORT_ONLY (or) WHOLE_PAGE (or) WHOLE_PAGE_SCROLL_AND_STITCH
    betweenScrollTimeout = 100
    useDevicePixelRatio = true 
}
```
