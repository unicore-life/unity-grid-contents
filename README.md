# unity-grid-contents

[![Build Status](https://travis-ci.org/unicore-life/unity-grid-contents.svg?branch=master)](https://travis-ci.org/unicore-life/unity-grid-contents)

## Building Plugin

To build plugin, just run:

```bash
./gradlew build
```

## Installation

After successful build, copy file from `build/libs/unity-grid-contents-VERSION.jar`
to `lib/` directory of UNITY IDM installation
(in case of RPM distribution it is `/usr/share/unity-idm/lib/`).

Already build plugin can be downloaded from [Bintray](https://bintray.com/unicore-life/maven).

```
curl -O https://dl.bintray.com/unicore-life/maven/pl/icm/edu/unity/unity-grid-contents/0.1.1/:unity-grid-contents-0.1.1.jar
```

## Configuration

Edit `unityServer.conf` configuration file and add any of those initializers:

```
unityServer.core.initializers.0=polishGridInitializer
unityServer.core.initializers.1=hydraInitializer
unityServer.core.initializers.2=testbedGridInitializer
```

Next, you should just clean database (make a backup!) and start Unity IDM.
Remember, that its executed only once, when database is not initialized.


# Links

* [Unity IDM](http://unity-idm.eu)
