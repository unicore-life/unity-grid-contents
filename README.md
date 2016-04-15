# unity-grid-contents

The goal of this project is to provide fast and easy initialization of Unity IDM content for the use with UNICORE
middleware. It helps to setup all attributes, groups and server entities together with statements, because some UNICORE
security logs are misleading especially for newbies.

[![Build Status](https://travis-ci.org/unicore-life/unity-grid-contents.svg?branch=master)](https://travis-ci.org/unicore-life/unity-grid-contents)
[![Download](https://api.bintray.com/packages/unicore-life/maven/unity-grid-contents/images/download.svg)](https://bintray.com/unicore-life/maven/unity-grid-contents/_latestVersion)

## Installation

Plugin can be downloaded from [Bintray](https://bintray.com/unicore-life/maven) maven repository.
In case of latest release version download archive using command:

```bash
curl -O https://dl.bintray.com/unicore-life/maven/pl/edu/icm/unity/unity-grid-contents/0.2.1/unity-grid-contents-0.2.1.jar
```

and place it in `lib/` directory of Unity IDM installation
(in case of RPM distribution it is `/usr/share/unity-idm/lib/`).

Plugin version depends on Unity IDM version which show compatibility table below.

| Unity IDM version | Plugin version |
| --- | --- |
| 1.8.0 | 0.1.x |
| 1.9.x | 0.2.x |

## Configuration

The library provides generic initializer named **configurationFileInitializer** which prepares content based on
UNICORE specific configuration file. In order to execute this initializer file `unityServer.conf` should contain:

```
unityServer.core.initializers.0=configurationFileInitializer
```

The initializer first tries to read file `conf/content-init.json`. If it exists it will be used, otherwise plugin
tries to read content from path `/etc/unity-idm/content-init.json`. Again, if it exists and has valid content
initializer will use it. Otherwise, database will be initialized based on sample configuration file:
[content-all.json](src/main/resources/content-all.json).

Next, you should clean database (make a backup!) and start Unity IDM.
Remember, initializers are executed only once, when Unity IDM database has not been set.

All identities are added with requirements **Empty requirement**, so be sure to has such in Unity IDM.
One way of accomplish that is to add lines:

```
unityServer.core.credentialRequirements.1.credentialReqName=Empty requirement
unityServer.core.credentialRequirements.1.credentialReqDescription=Empty credential requirement
```

in configuration file *unityServer.conf*.

## Specific initializers

The library contains also several specific initializers used in PLGrid.
To enable them please edit `unityServer.conf` configuration file and put lines:

```
unityServer.core.initializers.0=polishGridInitializer
unityServer.core.initializers.1=hydraInitializer
unityServer.core.initializers.2=testbedGridInitializer
```

All of presented initializers use configuration files boundled with archive as resources.
In above example, they are respectively:

* [content-plgrid.json](src/main/resources/content-plgrid.json),
* [content-hydra.json](src/main/resources/content-hydra.json),
* [content-testbed.json](src/main/resources/content-testbed.json).

Remember, that also here **Empty requirement** needs to be defined in Unity IDM configuration file
(see previous section).

## Logging

To enable more exhaustive logs simply configure appropriate logger in file `log4j.properties` located in Unity IDM
configuration directory. Setting full logging needs adding a line:

```
logger.log4j.grid.contents = TRACE
```

# Links

* [Unity IDM](http://unity-idm.eu)
* [UNICORE](http://unicore.eu)
