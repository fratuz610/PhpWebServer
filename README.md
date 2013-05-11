# PhpWebServer

A simple php webserver with a GUI for Windows and Mac

## Features

* Jetty / Fastcgi based
    * Use the php version you like 
        * just replace the <build>/php folder with the version you like
* Easy GUI to
    * Set the web root home dynamically
    * Change the http and fastcgi ports dynamically
    * Get the live php and http error log
* Packaged
    * as an EXE for Windows
    * as an executable JAR on Mac

### TODO

* PCRE mod_rewrite module

## Build Instructions

* 1 - Download and build with ANT
    * Make sure both a ../PhpWebServer.build and ../PhpWebServer.build.macosx folders exist
    * Make sure they contain
        * a `www` folder
        * a `php` folder with an appropriate build of php to use
* 2 - Run PhpWebServer.exe on Windows or the PhpWebServer package on Mac
* 3 - Enjoy

## License

Apache 2.0