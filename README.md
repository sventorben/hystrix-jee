# Hystrix JEE

An extension to use [Hystrix](https://github.com/Netflix/Hystrix) in a JEE environment.

[![Build Status](https://travis-ci.org/sventorben/hystrix-jee.svg?branch=master)](https://travis-ci.org/sventorben/hystrix-jee)

[![Code Coverage](https://img.shields.io/codecov/c/github/sventorben/hystrix-jee/master.svg)](https://codecov.io/github/sventorben/hystrix-jee?branch=master)

## How to use

Simply add the Maven dependency (see below) to use the default [ManagedThreadFactory](https://docs.oracle.com/javaee/7/api/javax/enterprise/concurrent/ManagedThreadFactory.html) available via ```java:comp/DefaultManagedThreadFactory```

If you want to configure your own ManagedThreadFactory you need to make it available via JNDI and pass the JNDI name to a context parameter in your web.xml like this:

```
<context-param>
    <param-name>de.sven_torben.hystrix_jee.ManagedThreadFactory.jndi</param-name>
    <param-value>java:jboss/ee/concurrency/factory/default</param-value>
</context-param>
```

## Maven Coordinates

The latest RELEASE is available via Maven Central.

```
    <dependency>
        <groupId>de.sven-torben.hystrix-jee</groupId>
        <artifactId>hystrix-jee</artifactId>
        <version>0.0.2</version>
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
