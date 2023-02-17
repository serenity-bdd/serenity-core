The Serenity Single Page Report produces a summary of the Serenity test outcomes in a stand-alone format that is compatible with most email clients, and so can be embedded in CI notification messages.

To produce single page reports, you need to add the `serenity-single-page-report` dependency to the `serenity-maven-plugin` in your `pom.xml` file, and then include "single-page-html" in the reports configuration. A sample configuration is shown below:

```xml
            <plugin>
                <groupId>net.serenity-bdd.maven.plugins</groupId>
                <artifactId>serenity-maven-plugin</artifactId>
                <version>${serenity.version}</version>
                <dependencies>
                    <dependency>
                        <groupId>net.serenity-bdd</groupId>
                        <artifactId>serenity-single-page-report</artifactId>
                        <version>${serenity.version}</version>
                    </dependency>
                </dependencies>
                <configuration>
                    <reports>single-page-html</reports>
                </configuration>
                <executions>
                    <execution>
                        <id>serenity-reports</id>
                        <phase>post-integration-test</phase>
                        <goals>
                            <goal>aggregate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

```
