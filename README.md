# Hystrix JEE

An extension to use [Hystrix](https://github.com/Netflix/Hystrix) in a JEE environment.

## Maven Coordinates


```
    <dependency>
        <groupId>de.sven-torben.hystrix-jee</groupId>
        <artifactId>hystrix-jee</artifactId>
        <version>0.0.1-SNAPSHOT</version>
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
    
    <repository>
        <id>sonatype-nexus-releases</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/releases</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
```
