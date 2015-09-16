# Attached Artifact Enforcer
This project provides a rule for the [maven enforcer plugin](http://maven.apache.org/components/enforcer/) that allows
for matching on attached artifacts and failing the build if banned artifacts are found.

## Usage
One example is if you do not wish to publish the test jar, then this plugin can check for the test jar being attached
based on filename pattern matching.

In your pom.xml add the following:

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-enforcer-plugin</artifactId>
                <dependencies>
                    <dependency>
                        <groupId>org.elasticsearch.maven</groupId>
                        <artifactId>attached-artifact-enforcer</artifactId>
                        <version>1.0.0</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <id>check-attached-artifacts</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>enforce</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <bannedAttachedArtifacts implementation="org.elasticsearch.enforcer.rules.BannedAttachedArtifacts">
                                    <excludes>
                                        <exclude>.*-tests\.jar</exclude>
                                    </excludes>
                                </bannedAttachedArtifacts>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

The above snippet adds the custom rules as a dependency of the enforcer plugin and then adds an execution that bans files
that end in `-tests.jar` from being attached to the project.
