# Closure compiler server #

## What's this? ##

Closure compiler server *CCS* is local (aimed to run on localhost) HTTP service based on [Unfiltered][1]
HTTP servicing toolkit and [Google Closure compiler][2].

It serves for compile multiple JS files on a hard disk and serve compiled single JS file on a well known URL
as a stream with `application/x-javascript` mime-type.

*CCS* is written in [Scala][3].

[1]: http://unfiltered.databinder.net/ "Scala HTTP servicing toolkit"
[2]: http://code.google.com/p/closure-compiler/ "Google Closure JavaScript compiler"
[3]: http://www.scala-lang.org/ "Scala language"

## Features roadmap ##

### v1.0 (not released yet) ###

* running via conscript
* support of configuration files to include/exclude JS files in the response
* support for zip archives in configuration files as well as folders for recursive search
* compiling and serving JS
* configuration setting: `minify true|false`