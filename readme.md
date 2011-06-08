Guardian Management
===================

This project contains various helpers to ease administrative management
of production java apps.

Our policy is that each app exposes its user facing pages on a sub url,
and administrative pages on `/management`. So, for example, `content-api.war`
when deployed to a container has the actual api under `/content-api/api` and
the management pages on `/content-api/management`.

The `/management` url should return a html page that links to all management
pages.

This simple framework aims to make it simple to generate the standard management
pages and easy to create new app-specific ones.


Note for the Old Skool
======================

The old management libraries, that were web-framework specific, can be found in
the [3.x](https://github.com/guardian/guardian-management/tree/3.x) branch of this project.
There is no intention to maintain these further.


Getting Started
===============

The management pages are web framework agnostic: they use their own mini
framework, blatently inspired/ripped off from [lift](http://www.liftweb.net).

1. Add the dependency to your build
-----------------------------------

In sbt 0.7.x:

    val guardianGithubSnapshots = "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-snapshots"
    val guManagement = "com.gu" %% "management" % "4.0-SNAPSHOT"

In your build.sbt for sbt 0.10:

    resolvers += "Guardian Github Snapshots" at "http://guardian.github.com/maven/repo-snapshots"
    libraryDependencies += "com.gu" %% "management" % "4.0-SNAPSHOT"

Currently only scala 2.8.1 is supported, we'll add cross builds as demand arises. It's all a bit pre-release
at the moment, hence the snapshot version: release versions will be built in the next few weeks.

2. Add the management filter to your web.xml
--------------------------------------------

To avoid any conflict with your choice of web framework, the managment
pages are implemented as a filter. So, for example:

    <!DOCTYPE web-app PUBLIC
            "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
            "http://java.sun.com/dtd/web-app_2_3.dtd" >

    <web-app>

        <filter>
            <filter-name>managementFilter</filter-name>
            <filter-class>com.gu.management.example.MyAppManagementFilter</filter-class>
        </filter>

        <filter-mapping>
            <filter-name>managementFilter</filter-name>
            <url-pattern>/management/*</url-pattern>
        </filter-mapping>

    </web-app>

The filter-class is a class that you are going to implement.

3. Implement the filter class
-----------------------------

Your filter class should derive from `com.gu.management.ManagementFilter` and implement
the pages member:

    class MyAppManagementFilter extends ManagementFilter {
      lazy val pages =
        new DummyPage() ::
        new ManifestPage() ::
        new Switchboard(Switches.all) ::
        new StatusPage(TimingMetrics.all) ::
        Nil
    }

Even for mostly java projects, you'll need to write your management pages in scala. However,
things like timing metrics and switches have a java-friendly interface and are usable from java.

4. Look at the example!
-----------------------

The [example project](https://github.com/guardian/guardian-management/tree/master/example) has
a filter set up and uses some switches and timing metrics from both scala and java.

    $ git clone git@github.com:guardian/guardian-management.git
    $ cd guardian-management
    $ ./sbt010
    > project example
    > jetty-run

It also has very simple custom management page, but the best thing to do if you want to write your
own management pages is to look at how the pre-defined ones are implemented: a simple readonly page to look at is
the
[status page](https://github.com/guardian/guardian-management/blob/master/management/src/main/scala/com/gu/management/StatusPage.scala),
and a more complex page that supports POSTs is
[the switchboard](https://github.com/guardian/guardian-management/blob/master/management/src/main/scala/com/gu/management/switchables.scala).



