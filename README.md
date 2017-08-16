# unity-grid-content

The goal of this project is to provide fast and easy initialization of Unity IDM content for the use with UNICORE
middleware. It helps to setup all attributes, groups and server entities together with statements, because some UNICORE
security logs are misleading especially for newbies.

[![Build Status](https://travis-ci.org/unicore-life/unity-grid-content.svg?branch=master)](https://travis-ci.org/unicore-life/unity-grid-content)
[![Download](https://api.bintray.com/packages/unicore-life/maven/unity-grid-content/images/download.svg)](https://bintray.com/unicore-life/maven/unity-grid-content/_latestVersion)

## Installation

Plugin can be downloaded from [Bintray](https://bintray.com/unicore-life/maven) maven repository.
In case of latest release version download archive using command:

```bash
curl -O https://dl.bintray.com/unicore-life/maven/pl/edu/icm/unity/unity-grid-content/1.0.0/unity-grid-content-1.0.0.jar
```

and place it in `lib/` directory of Unity IDM installation
(in case of RPM distribution it is `/usr/share/unity-idm/lib/`).

Plugin version depends on Unity IDM version which show compatibility table below.

| Unity IDM version | Plugin version | Documentation Link |
| --- | --- | --- |
| 1.8.0 | 0.1.x | [unicore-grid-contents-0.1.8](https://github.com/unicore-life/unity-grid-content/blob/unicore-grid-contents-0.1.8/README.md) |
| 1.9.x | 0.2.x | [unity-grid-contents-0.2.4](https://github.com/unicore-life/unity-grid-content/blob/unity-grid-contents-0.2.4/README.md) |
| 2.1.x | 1.0.x | [unity-grid-content-1.0.0](https://github.com/unicore-life/unity-grid-content/blob/unity-grid-content-1.0.0/README.md) |

## Configuration

All identities are added with requirements **Empty requirement**, so first, be sure to has such in Unity IDM.
One way of accomplish that is to add lines:

```properties
unityServer.core.credentialRequirements.1.credentialReqName=Empty requirement
unityServer.core.credentialRequirements.1.credentialReqDescription=Empty credential requirement
```

in configuration file *unityServer.conf*.

Next, copy script [gridContentInitializer.groovy](scripts/gridContentInitializer.groovy) into Unity IDM configuration 
directory, change grid content JSON configuration file, and add properties in file *unityServer.conf*:

```properties
unityServer.core.script.5.file=/etc/unity-idm/scripts/gridContentInitializer.groovy
unityServer.core.script.5.trigger=pre-init
```

to be executed when Unity IDM starts. Examples of grid content JSON files:

* [content-production.json](scripts/content-production.json)
* [content-testbed.json](scripts/content-testbed.json)

Note, that *defaultContentInitializer.groovy* initialization script shipped with Unity IDM distribution have to enabled.
It is enough to uncomment lines below in *unityServer.confg* file.

```properties
unityServer.core.script.0.file=/etc/unity-idm/scripts/defaultContentInitializer.groovy
unityServer.core.script.0.trigger=pre-init
```

## Logging

To enable more exhaustive logs simply configure appropriate logger in file `log4j2.xml` located in Unity IDM 
configuration directory. Setting full logging needs adding a line:

```xml
<Logger name="grid.content" level="TRACE"/>
```

in *Loggers* section.

## Development

### Building

Just clone the project and run Gradle command presented below.

```bash
./gradlew build
```

### Releasing

To see current version of the sources use Gradle task
[currentVersion](http://axion-release-plugin.readthedocs.io/en/latest/configuration/tasks.html#currentversion)
(it is stored as a git tag).

```bash
./gradlew currentVersion
```

To release a new version use
[release](http://axion-release-plugin.readthedocs.io/en/latest/configuration/tasks.html#release) task.
Later, for uploading artifact to [Bintray](https://dl.bintray.com/unicore-life/maven) maven repository
use [bintrayUpload](https://github.com/novoda/bintray-release) task.
Sample command are presented below.

```bash
./gradlew release
./gradlew bintrayUpload -PdryRun=false
```

Remember to configure [Bintray](https://bintray.com) user and key by using parameters
`-PbintrayUser=BINTRAY_USERNAME -PbintrayKey=BINTRAY_KEY` or just put them in `gradle.properties` file.

# Links

* [Unity IDM](http://unity-idm.eu)
* [UNICORE](http://unicore.eu)
