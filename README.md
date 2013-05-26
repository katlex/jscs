# Javascript compilation service #

## What's this? ##

Javascript compilation service is a set of [Scala][3] routines around well known libs, helping to build and develop Javascript code (as well as Coffeescript code too in future).


There are ideally three ways of using it:

1. Local HTTP service compiler.  
```
$ jscs-serv --port 7770
```
1. Using from SBT  
```
addSbtPlugin("com.katlex" % "jscs-sbt" % "1.0")
```
1. CLI JS project compiler  
```
$ jscs-cli compilation.conf >target.js
```

See [Runmodes](#runmodes) for details.

## Installation ##

To install this instal [conscript][4] first. Follow instructions there.
After it is installed use this command to intall *JSCS*:

    $ cs --no-exec katlex/jscs

This will install `~/bin/jscs bat` or sh script (depending on your OS).
And you should be able to run `jscs` command starting 

## Runmodes ##

### 1. Local HTTP service ###

**IMPORTANT: This is the only implemented runmode at moment!**

Is a local (aimed to run on the localhost) HTTP service based on [Unfiltered][1]
HTTP servicing toolkit and [Google Closure compiler][2].

It serves for compile multiple JS files on a hard disk and serve compiled single JS file on a well known URL
as a stream with `application/x-javascript` mime-type.

To use it just run [install](#installation) the app and run `jscs`. Configuration files should be places to `~/.closure-compiler-server` directory.  
Compilation result is mapped to `http://localhost:7770/${config_file_name}` URL. See also [configuring info](#configuration).

### 2. SBT plugin mode ###

To be driven by [SBT][5]. Helping to build script artifacts for web projects.

### 3. CLI JS project compiler

Can be used from any build script to build JS/Coffescript sources into a single minified JS.

## Configuration ##

Compilation configuration files file have the following format:

    BASE=/Users/alun/Work
    
    srcRoot $BASE/rm-html5/rm-js/src/main/javascript
    
    exclude generated

First line declares a variable to be used in further declarations.  
Second line adds sources root. That root will be scanned recursively for `*.js` files and they all will be added the compilation config.  
Third line excludes all files from the compilation config where word "generated" is present in the absolute path.

[1]: http://unfiltered.databinder.net/ "Scala HTTP servicing toolkit"
[2]: http://code.google.com/p/closure-compiler/ "Google Closure JavaScript compiler"
[3]: http://www.scala-lang.org/ "Scala language"
[4]: https://github.com/n8han/conscript
[5]: https://github.com/harrah/xsbt
