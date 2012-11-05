# Javascript compilation service #

## What's this? ##

Javascript compilation service *JSCS* is local (aimed to run on localhost) HTTP service based on [Unfiltered][1]
HTTP servicing toolkit and [Google Closure compiler][2].

It serves for compile multiple JS files on a hard disk and serve compiled single JS file on a well known URL
as a stream with `application/x-javascript` mime-type.

*JSCS* is written in [Scala][3].

[1]: http://unfiltered.databinder.net/ "Scala HTTP servicing toolkit"
[2]: http://code.google.com/p/closure-compiler/ "Google Closure JavaScript compiler"
[3]: http://www.scala-lang.org/ "Scala language"

## Features roadmap ##

### v1.0 (not released yet) ###

* running via [conscript][4] **[ready]**
  currently you could run the application with `cs katlex/jscs`
* compiling and serving JS **[ready]**
* support of configuration files to include/exclude JS files in the response **[ready]**
  configuration files are specified under `~/.closure-compiler-service`
* support for zip archives in configuration files as well as folders for recursive search **[not-ready-yet]**

[4]: https://github.com/n8han/conscript