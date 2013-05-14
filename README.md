# PhpWebServer

A simple php webserver with a GUI for Windows and Mac

## Features

* Designed to facilitate development/debugging only
* Jetty / Fastcgi based
    * Use the php version you like 
        * just replace the <build>/php folder with the version you like
* Easy GUI to
    * Set the web root home dynamically
    * Change the http and fastcgi ports dynamically
    * Get the live php and http error log
* Packaged
    * as an EXE for Windows using [Launch4J](http://launch4j.sourceforge.net/)
    * as an executable JAR on Mac

### TODO

* PCRE mod_rewrite module
* Better Jetty integration

## Screenshots

![PhpWebServer Main Windows Screenshot](http://fratuz610.s3.amazonaws.com/sw/PhpWebServer/phpwebserver-screenshot-main-window.png "PhpWebServer Main Windows Screenshot")
![PhpWebServer Settings Window Screenshot](http://fratuz610.s3.amazonaws.com/sw/PhpWebServer/phpwebserver-screenshot-settings.png "PhpWebServer Settings Window Screenshot")

## Binaries download

http://fratuz610.s3.amazonaws.com/sw/PhpWebServer/PhpWebServer-macosx.build.zip
http://fratuz610.s3.amazonaws.com/sw/PhpWebServer/PhpWebServer-windows.build.zip

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