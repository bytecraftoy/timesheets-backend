# Scala Play App

## Available scripts

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
