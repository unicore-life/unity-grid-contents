# unity-grid-contents

The goal of this project is to provide library for fast and easy initialization of Unity IDM content
for the use with UNICORE middleware. It helps to setup all attributes, groups and server entities
together with statements, because some UNICORE security logs are misleading especially for newbies.

[![Build Status](https://travis-ci.org/unicore-life/unity-grid-contents.svg?branch=master)](https://travis-ci.org/unicore-life/unity-grid-contents)

## Installation

Plugin can be downloaded from [Bintray](https://bintray.com/unicore-life/maven) maven repository.
First download it archive:

```
curl -O https://dl.bintray.com/unicore-life/maven/pl/edu/icm/unity/unity-grid-contents/0.1.4/unity-grid-contents-0.1.4.jar
```

and then place it in `lib/` directory of Unity IDM installation
(in case of RPM distribution it is `/usr/share/unity-idm/lib/`).

## Configuration

Edit `unityServer.conf` configuration file and add any of those initializers:

```
unityServer.core.initializers.0=polishGridInitializer
unityServer.core.initializers.1=hydraInitializer
unityServer.core.initializers.2=testbedGridInitializer
```

Next, you should just clean database (make a backup!) and start Unity IDM.
Remember, initializers are executed only once, when Unity database has not been set.

All identities are added with requirements **Empty requirement**, so be sure to has such in Unity IDM.
One way of accomplish that is to add lines:

```
unityServer.core.credentialRequirements.1.credentialReqName=Empty requirement
unityServer.core.credentialRequirements.1.credentialReqDescription=Empty credential requirement
```

to configuration file *unityServer.conf*.

## Generic initializer

The library provides also more generic initializer named **configurationFileInitializer**
based on UNICORE specific configuration file. In order to execute this initilizer
file `unityServer.conf` should contain:

```
unityServer.core.initializers.0=configurationFileInitializer
```

This initializer first tries to read file `conf/content-init.json`. If it exists it will use it, otherwise tries
to read content from path `/etc/unity-idm/content-init.json`. Again, if it exists and has valid content
initializer will use it. Otherwise, will initialize database with sample configuration file
[content-all.json](src/main/resources/content-all.json).

## Logging

To enable more exhaustive logs simply configure appropriate logger in file `log4j.properties` located in Unity IDM
configuration directory. Setting full logging needs adding a line:

```
logger.log4j.grid.contents = TRACE
```

# Links

* [Unity IDM](http://unity-idm.eu)
