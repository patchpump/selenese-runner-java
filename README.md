Selenese Runner Java
====================

This is selenese script interpreter implemented by Java.

It supports test-case and test-suite which are Selenium IDE's native format.

[![Build Status](https://travis-ci.org/vmi/selenese-runner-java.svg?branch=master)](https://travis-ci.org/vmi/selenese-runner-java)

Download
--------

You can download the executable jar from:

https://github.com/vmi/selenese-runner-java/releases

Features
--------

* Run test-case and test-suite generated by Selenium IDE from command line.
* Support commands of Selenium IDE: Flow Control.
  https://github.com/davehunt/selenium-ide-flowcontrol
* Log URL/title/Cookies at all commands.
* Take screenshots at all commands. (optional)
* Override base URL.
* Override directory of screenshot path.
* Generate JUnit XML result.
* Generate HTML result.

Usage
-----

    java -jar selenese-runner.jar <option> ... <test-case|test-suite> ...
    
     -d,--driver <driver>                    firefox (default) | chrome | ie | safari | htmlunit | phantomjs | remote | appium | FQCN-of-WebDriverFactory
     -p,--profile <name>                     profile name (Firefox only)
     -P,--profile-dir <dir>                  profile directory (Firefox only)
        --proxy <proxy>                      proxy host and port (HOST:PORT) (excepting IE)
        --proxy-user <user>                  proxy username (HtmlUnit only *)
        --proxy-password <password>          proxy password (HtmlUnit only *)
        --no-proxy <no-proxy>                no-proxy hosts
        --remote-url <url>                   Remote test runner URL (Remote only)
        --remote-platform <platform>         Desired remote platform (Remote only)
        --remote-browser <browser>           Desired remote browser (Remote only)
        --remote-version <browser-version>   Desired remote browser version (Remote only)
     -H,--highlight                          highlight locator always.
     -s,--screenshot-dir <dir>               override captureEntirePageScreenshot directory.
     -S,--screenshot-all <dir>               take screenshot at all commands to specified directory.
        --screenshot-on-fail <dir>           take screenshot on fail commands to specified directory.
        --ignore-screenshot-command          ignore captureEntirePageScreenshot command.
     -b,--baseurl <baseURL>                  override base URL set in selenese.
        --firefox <path>                     path to 'firefox' binary. (implies '--driver firefox')
        --chromedriver <path>                path to 'chromedriver' binary. (implies '--driver chrome')
        --iedriver <path>                    path to 'IEDriverServer' binary. (implies '--driver ie')
        --phantomjs <path>                   path to 'phantomjs' binary. (implies '--driver phantomjs')
        --xml-result <dir>                   output XML JUnit results to specified directory.
        --html-result <dir>                  output HTML results to specified directory.
     -t,--timeout <timeout>                  set timeout (ms) for waiting. (default: 30000 ms)
        --set-speed <speed>                  same as executing setSpeed(ms) command first.
        --height <height>                    set initial height. (excluding mobile)
        --width <width>                      set initial width. (excluding mobile)
     -D,--define <key=value or key+=value>   define parameters for capabilities. (multiple)
        --rollup <file>                      define rollup rule by JavaScript. (multiple)
        --cookie-filter <+RE|-RE>            filter cookies to log by RE matching the name. ("+" is passing, "-" is ignoring)
     -h,--help                               show this message.

Requirements
------------

* Java 6 or later.
* Apache Maven 2.x or later to build.

Release Note
------------

The release note is moved to [RELEASENOTE.md](RELEASENOTE.md) file.

Building the Application
------------------------

* Install Apache Maven.
* clone this repository
* run build script
	`mvn -P package`

That will create the *selenese-runner.jar* file within the 'target' directory.

Options
-------

### PhantomJS driver

If you want to add command line options to PhantomJS binary, add following options:

    java -jar selenese-runner.jar --driver phantomjs \
      --define phantomjs.cli.args+=ARG1 \
      --define phantomjs.cli.args+=ARG2 \
      ...

Example:

    java -jar selenese-runner.jar --driver phantomjs \
      --define phantomjs.cli.args+=--ssl-certificates-path=/path/to/certs-dir/
      ...

### Rollup

"--rollup" option and "rollup" command are used for a definition and execution of a user-defined command.

Refer to the following for how to write "rollup" script:

* Using the rollup feature of Selenium  
http://sanjitmohanty.wordpress.com/2012/07/06/using-the-rollup-feature-of-selenium/
* Selenium Tutorial : Testing Strategies
https://thenewcircle.com/static/bookshelf/selenium_tutorial/testing_strategies.html

However, this feature has the following limitations:

* supported properties of rollup rule:
** name
** args
** expandedCommands or getExpandedCommans
* cannot access any browser object.

### Cookie filter

You can filter cookies to log by the regular expression matching the name.

Example:

* logging the cookie whose name ends with "ID":

    java -jar selenese-runner.jar --cookie-filter +'ID$' ...

* don't logging the cookie whose name contains "__utm":

    java -jar selenese-runner.jar --cookie-filter -__utm ...

License
-------

The Apache License, Version 2.0.

see "LICENSE" file.
