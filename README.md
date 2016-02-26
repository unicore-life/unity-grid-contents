# unity-grid-contents

[![Build Status](https://travis-ci.org/unicore-life/unity-grid-contents.svg?branch=master)](https://travis-ci.org/unicore-life/unity-grid-contents)

## Installation

After successful build, copy file from `build/libs/unity-grid-contents-VERSION.jar`

Plugin can be downloaded from [Bintray](https://bintray.com/unicore-life/maven) maven repository.
First download it archive:

```
curl -O https://dl.bintray.com/unicore-life/maven/pl/edu/icm/unity/unity-grid-contents/0.1.2/unity-grid-contents-0.1.2.jar
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
Remember, initalizers are executed only once, when Unity database has not been set.

All identities are added with requirements **Empty requirement**, so be sure to has such in Unity IDM.
One way of accomplished that is to add lines:

```
unityServer.core.credentialRequirements.1.credentialReqName=Empty requirement
unityServer.core.credentialRequirements.1.credentialReqDescription=Empty credential requirement
```

to configuration file *unityServer.conf*.

# Links

* [Unity IDM](http://unity-idm.eu)
