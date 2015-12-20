# jtxt2img

[![Build Status](https://travis-ci.org/clajder/jtxt2img.svg)](https://travis-ci.org/clajder/jtxt2img)    [ ![Download](https://api.bintray.com/packages/clajder/maven/jtxt2img/images/download.svg) ](https://bintray.com/clajder/maven/jtxt2img/_latestVersion)

## Java Text to Image Library

This README documents the latest release, but master contains the current development version. 

## Overview

This is small pure java library to write text on image. 
The library aims for real-life usage in production.

It takes care of a text dimensions calculation and central its positioning on an image. 

## Getting started

Add the repositories:

```gradle
repositories {
    mavenCentral()
    maven { url  "http://dl.bintray.com/clajder/maven" }
}
```

Dependency:

```gradle
dependencies {
  compile 'com.embedler.moon:jtxt2img:INSERT_LATEST_VERSION_HERE'
}
```

How to use the latest build with Maven:

```xml
<repository>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <id>bintray-clajder-maven</id>
    <name>bintray</name>
    <url>http://dl.bintray.com/clajder/maven</url>
</repository>
```

Dependency:

```xml
<dependency>
    <groupId>com.embedler.moon</groupId>
    <artifactId>jtxt2img</artifactId>
    <version>INSERT_LATEST_VERSION_HERE</version>
</dependency>
```

## Hello world

The main class `JTxt2Img` provides convenient interface to interact with library internals.

```java

/** output file for generated image **/            
File file = new File(outputDirectory, "image-0001.jpg");

/** calling builder and generate image with various parameters **/
JTxt2Img.withText(String.valueOf(i))
        .backgroundColor("487")
        .foregroundColor("278")
        .format(ImgTextProperties.IMG_FORMAT.JPG)
        .width(50)
        .font(Font.getFont(Font.MONOSPACED))
        .height(90)
        .generate()
        .write(file);
```

## Build it

Just clone the repo and type

```bash
./gradlew build
```

Running the tests:

```bash
./gradlew test
```

Installing in the local Maven repository:

```bash
./gradlew install
```

## Details

The implementation is in Java 7.

The only runtime dependencies are Apache Commons Lang and Slf4j.

License

jtxt2img is licensed under the MIT License. 
See [LICENSE](https://github.com/clajder/jtxt2img/blob/master/LICENSE.md) for details.

Copyright (c) 2016, Anton Y

## Feedback

I would appreciate any feedback via Pull Request/Issue.
