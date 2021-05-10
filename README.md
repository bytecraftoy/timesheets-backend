<!-- markdown-toc start - Don't edit this section. Run M-x markdown-toc-refresh-toc -->
**Table of Contents**

- [Timesheets](#timesheets)
    - [Available scripts](#available-scripts)
        - [`sbt run`](#sbt-run)
        - [`sbt compile`](#sbt-compile)
        - [`sbt test`](#sbt-test)
        - [`sbt clean`](#sbt-clean)
        - [`sbt scalafmt`](#sbt-scalafmt)
        - [`sbt scalastyle`](#sbt-scalastyle)

<!-- markdown-toc end -->


# Timesheets
**Bytecraft** collaborated with a group of talented students (**team Codeflow**)
from Aalto University in [Software Project Course](https://mycourses.aalto.fi/course/view.php?id=28162).

The output is an open-source application, *timesheets* app.

The backend is a Scala Play app. Further information about its architecture is found in [ARCHITECTURE.md](ARCHITECTURE.md). 

The known defects and the backlog of user stories and individual tasks are in [BACKLOG.md](BACKLOG.md).
Instructions for contributing to the development of the application are in [CONTRIBUTING.md](CONTRIBUTING.md).

## Available scripts
After installing sbt (see instructions: [ARCHITECTURE.md](ARCHITECTURE.md)), you can run, compile, lint, and test the application with the following command line commands.

### `sbt run`
Runs the application in development mode. By default, the server runs on port 9000.

### `sbt compile`
Compiles the main sources without running the HTTP server.
The compile command displays any application errors in the command window.

### `sbt test`
Compiles and runs all tests.

### `sbt clean`
Deletes all generated files (in the target directory).

### `sbt scalafmt`

Formats the code using Scalafmt. If you are using IntelliJ IDEA built-in Scalafmt plugin, 
you can format current file with `Opt + Cmd + L` (Mac) or `Ctrl + Alt + L` (other). 
You can also enable format on save.

Documentation for Scalafmt: https://scalameta.org/scalafmt/

### `sbt scalastyle`

Check the project with Scalastyle.

Documentation for Scalastyle: http://www.scalastyle.org
