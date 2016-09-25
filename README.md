# Hystrix JEE

An extension to use Hystrix in a JEE environment.

## Maven Coordinates

Releases are available on Maven Central.

```
    <dependency>
        <groupId>de.sven-torben.hystrix-jee</groupId>
        <artifactId>core</artifactId>
        <version>0.0.1</version>
    </dependency>
```

For latest SNAPHOT artifacts you need to integrate the Sonatype Maven repository like this: 

```
    <repository>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository
```