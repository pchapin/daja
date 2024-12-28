# Daja

This project is about creating a compiler for the [D programming language](http://dlang.org/)
using Scala as the implementation language. The purpose of the project is educational. It is
used to support Vermont State University's CIS-4050 and CIS-6050 (Compiler Design) courses. It
may also be useful to others interested in learning about compiler design.

## Overview

The repository contains the main Daja project and two subprojects named "Dragon" and "Tiger."
Dragon is a library of supporting components for compiler construction written in Scala or a
related language (e.g., Java). Tiger is a simple compiler for the toy language described in the
book "Modern Compiler Implementation in ML" by Andrew W. Appel. The book uses ML as the
implementation language, but the Tiger project implements the book's exercises and examples in
Scala.

Both Dragon and Tiger could reasonably be factored into separate repositories. However, they are
included here to make it easier to work with the Daja project as a whole. Also, both Dragon and
Tiger directly support the overall project's primary educational goal. For now, all three
projects are bundled together, but this may change in the future.

## Building

The Daja project uses [sbt](https://www.scala-sbt.org/) as its build tool. To build the project,
you need to have a suitable Java (we use version 21.0.x) and SBT installed on your system. You
can then build the project by running the following command in the root directory of the
project:

```shell
sbt compile
```

This will download the necessary dependencies, including the Scala compiler, and compile the
project along with its dependency projects (Dragon in this case). Use the `sbt test` command to
execute the unit tests, and the `sbt assembly` command to create a standalone JAR file (left in
the `target/scala-3.3.4` folder).

The Dragon and Tiger projects can be built separately, and they have separate tests that are not
executed by running `sbt test` in the Daja project. To test Dragon and Tiger, use the following
commands:

```shell
sbt dragon/test
sbt tiger/test
``` 

Note that you can use the `sbt console` command to start the Scala REPL with the project's
dependencies loaded. This can be useful for experimenting with the project interactively.
_NOTE_: At this time there is a JLine incompatibility that prevents SBT from launching the
console in certain cases. There is an [open issue about
this](https://github.com/sbt/sbt/issues/7177) on the SBT GitHub.

## Documentation

The documentation for Daja (and Dragon and Tiger) are written using DocBook 5.1. The file
`Daja.xpr` in the root of the repository is an oXygen project file that can be used to open the
documentation in the [oXygen XML editor](https://www.oxygenxml.com/).

_TODO_: Provide details on how to edit and build the documentation using an open source tool,
such as Visual Studio Code.

## Development Environments

We primarily use two development environments for working on Daja: Visual Studio Code and
IntelliJ IDEA. Visual Studio Code is a general tool that can work across the entire project
seamlessly. We use IntelliJ to focus specifically on the Scala code base. Both tools support all
three of the platforms we target (Windows, macOS, and Linux).

We recommend that you execute the full build from the console via SBT before configuring your
development environment. It is easier to troubleshoot the build without the complexity of a
large development environment interfering. Also, SBT will download all prerequisites, including
the Scala compiler, reducing the chance of spurious errors when you first configure your other
tools.

During normal development, continue to build the project via SBT even from within your
development environment. This ensures the build is done properly. The SBT build is the source of
truth for the project's configuration and dependencies, so it should be used consistently. One
advantage of this is that any tool that knows how to work with SBT will work for Daja
development.

### Visual Studio Code

Download and install [Visual Studio Code](https://code.visualstudio.com/). Then, install the
following extension into Visual Studio Code:

+ Scala (Metals) by Scalameta

Open Visual Studio Code on the top level folder of this repository. The Metals extension should
notice that there is an SBT build defined, and prompt you to import it. Some time is required to
execute this import and perform related indexing. Also, when you first open a Scala file, there
is some additional extra time required while Metals compiles the project.

### IntelliJ IDEA

Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/). The Community Edition
should be sufficient. Then, install the following plugins into IntelliJ:

+ Scala
+ ANTLR

Open IntelliJ IDEA on the top level folder of this repository. As with Visual Studio Code,
IntelliJ should notice the SBT build and prompt you to import it. This will also take some time
while IntelliJ configures and indexes the project.

Peter Chapin  
spicacality@kelseymountain.org  
