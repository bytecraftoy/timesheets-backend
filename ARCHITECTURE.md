Timesheets architectural overview
=================================

Frontend
--------

Backend
-------

### Scala

### sbt

### Scalafmt

### Play

### Root scope

com.codeflow.timesheets

### Packages

1.  Web

    1.  Routes

    2.  Controllers

    3.  Data Transfer Objects (DTOs)

        Case Classes without methods

2.  Domain

    1.  Models

        Use case classes. Describe attributes and associations, but do
        not include methods.

        1.  Project

        2.  User

    2.  Services

        -   Changes Data Transfer Objects to models and calls Data
            Access Objects.
        -   The data access operations should be in the persistence
            layer (e.g. dao.add()).
        -   Changes data from Data Access Objects to Data Transfer
            Objects

3.  Persistence

    1.  Data Access Objects (DAOs)

Domain language
===============

| Concept              | Definition                       | Synonyms   | Määritelmä                           | Synonyymejä    |
|----------------------|----------------------------------|------------|--------------------------------------|----------------|
| Application          | The software program that        | App        |                                      |                |
| Sovellus             | tracks and maintains             |            |                                      |                |
|                      | timesheets. Used by an           |            |                                      |                |
|                      | organization.                    |            |                                      |                |
| Application Owner    | The individual or group with     |            | Yksilö tai ryhmä, joiden             |                |
| Sovelluksen omistaja | the responsibility to ensure     |            | vastuulla on, että sovelluksen       |                |
|                      | that the program(s) making       |            | muodostavat ohjelmat saavuttavat     |                |
|                      | up the application               |            | tavoitteensa.                        |                |
|                      | accomplishes its objectives      |            |                                      |                |
| Architecture         | Structural composition of ﻿      |            |                                      |                |
| Arkkitehtuuri        | the application and its          |            |                                      |                |
|                      | supporting technologies          |            |                                      |                |
| Billing              | The process of preparing         |            | Kuittien tai laskujen valmistelu     |                |
| Laskutus             | or sending invoices﻿             |            | tai lähettämisprosessi﻿              |                |
| Budget               | Project's planned costs          |            | Projektin suunnitellut tunti- ja ﻿   | Talousarvio    |
| Budjetti             | in time and money﻿               |            | rahalliset kustannukset              |                |
| Client               | A person or organization         | Customer   | Henkilö tai organisaatio, joka       | Toimeksiantaja |
| Asiakas              | using the services of a          | Buyer      | käyttää yrityksen palveluja.         | Ostaja         |
|                      | company. May buy several         |            | Voi ostaa useita projekteja.         |                |
|                      | projects.                        |            |                                      |                |
| Cost                 | The time and money that has      |            | Projektin suorittamiseen jo          | Toteutunut     |
| Kustannus            | been used to execute a project.﻿ |            | käytetyt tunnit ja raha.             | kustannus﻿     |
| Employee             | A person employed for wages      |            | Henkilö, joka työskentelee           | Alainen        |
| Työntekijä           | or salary.﻿                      |            | palkallisesti.﻿                      |                |
| Manager              | A person responsible for         | Supervisor | Henkilö, joka on vastuussa           | Johtaja        |
| Manageri             | controlling or administering an  |            | organisaation tai työntekijöiden     | Esimies        |
|                      | organization or group of staff﻿  |            | hallinnoinnista﻿                     |                |
| Project              | An individual or collaborative   |            | Yksilön tai yhteinen hanke, joka     | Hanke          |
| Projekti             | enterprise that is planned to    |            | on suunniteltu päämäärän             | Urakka         |
|                      | achieve an aim. Takes time and   |            | saavuttamiseksi. Vie aikaa ja        |                |
|                      | one or more tasks to complete.   |            | vaatii yhden tai useamman tehtävän.﻿ |                |
| Report               | A human-readable and printable   |            | Ihmisselkoinen ja tulostettava       |                |
| Raportti             | summary of a user-specified      |            | yhteenveto halutusta asiasta         |                |
|                      | context.                         |            |                                      |                |
| Task                 | A piece of work to be done or    |            | Suoritettava työ, joka vie aikaa.﻿   |                |
| Tehtävä              | undertaken. Takes time.          |            |                                      |                |
| Task description     | Description of what work was     | Comment    | Kuvaus tehdystä työstä tai kuinka    |                |
| Tehtävän kuvaus      | done or how time was spent       |            | aikaa käytettiin tehtävän            |                |
|                      | during a task﻿                   |            | suorittamiseen.﻿                     |                |
| Time                 | The difference in numbers in a   |            | Erotus kellon numeroissa työsession  |                |
| Aika                 | clock between the beginning and  |            | alun ja ja lopen välillä. Voidaan    |                |
|                      | the end of a work session. Can   |            | esittää tunnin kymmenkantaisina      |                |
|                      | be represented as hours with     |            | desimaaleina tai tunteina ja         |                |
|                      | decimal fractions or with hours  |            | minuutteina.﻿                        |                |
|                      | and minutes.﻿                    |            |                                      |                |
| Timesheets           | Collective record of tasks and   |            | Tehtävien ja niiden viemän ajan      | Kellokortti    |
| Työaikakortti        | time spent working.              |            | luettelo﻿.                           |                |
|                      | The main purpose of the﻿         |            |                                      |                |
|                      | application.                     |            |                                      |                |
| User                 | A person who uses the            | End user   | Henkilö joka käyttää sovellusta      | Loppukäyttäjä  |
| Käyttäjä             | application﻿.                    |            |                                      |                |

Tool installation
=================

Backend
-------

### IntelliJ, sbt, openjdk

<https://www.jetbrains.com/idea/download/other.html>

Play-projektin pohja: sbt new playframework/play-scala-seed.g8

1.  MacOS

    ``` shell
    brew install openjdk

    brew install sbt

    brew install intellij-idea-ce   # (tai ilman ce:tä, jos haluaa täyden)
    ```

### PostgreSQL

1.  MacOS

    ``` shell
    brew install postgresql
    pg_ctl -D /usr/local/var/postgres start
    psql
    ```

    ``` sql
    CREATE ROLE "timesheet" SUPERUSER LOGIN ENCRYPTED PASSWORD 'tähän salasana';
    ```

2.  application.conf:

    Laita Ideassa sbt:n parametreilla ympäristömuuttujiksi ao. kuvan
    arvot, niin ei tarvitse muokata application.confia.

    HUOM! Koskee vain Idean sbt shelliä. Muihin terminaaleihin pitää
    määrittää esim. sbt:n käynnistysparametrina.

        db.timesheet.driver=org.postgresql.Driver

        db.timesheet.url="jdbc:postgresql://localhost:5432/postgres"

        db.timesheet.username=timesheet

        db.timesheet.password=tähän salasana

Frontend
--------

### MacOS [macos-2]

``` shell
brew cask install visual-studio-code
brew install yarn
```

Database
========

PostgreSQL
----------

H2
--

Play Evolutions
---------------

-   There is a conflict when adding new information from frontend to the
    database and then rolling evolutions back

Schema
------

Swagger
=======

<https://codeflow-timesheets-staging.herokuapp.com/docs/swagger-ui/index.html?url=/swagger.json>

### Known security vulnerability: `+ nocsrf`

In order to get `POST` API calls working in Swagger UI, we had to
disable Play's CSRF by adding `+ nocsrf` annotation above the affected
routes in the [file:./conf/routes](./conf/routes) file. This creates a
security vulnerability.

Authorizations
==============

| Routes                     | Affected methods                 | Use case             | Manager | Employee | Specific user |
|----------------------------|----------------------------------|----------------------|---------|----------|---------------|
| GET *projects*:id/hours    | TimeInputController.byProject    | User can view and    |         |          | x             |
| POST /hours                | TimeInputController.add          | edit own             |         |          |               |
| PUT /hours                 | TimeInputController.update       | timeinputs           |         |          |               |
| GET /projects              | ProjectController.listProjects   | User can view        |         |          | x             |
|                            |                                  | own projects         |         |          |               |
| GET /projects              | ProjectController.listProjects   | Manager can view all | x       |          |               |
|                            |                                  | projects             |         |          |               |
| POST /projects             | ProjectController.addProject     | Manager can add a    | x       |          |               |
| GET /clients               | ClientController.listClients     | project              |         |          |               |
| GET /managers              | ManagerController.listManagers   |                      |         |          |               |
| GET /clients               | ClientController.listClients     | Manager can generate | x       |          |               |
| GET *clients*:id/projects  | ProjectController.               | a billing report     |         |          |               |
|                            | listProjectsByClientId           |                      |         |          |               |
| GET /projects/employees    | ProjectController.               |                      |         |          |               |
|                            | getUsersByProject                |                      |         |          |               |
| GET *report/client*:id     | ReportController.                |                      |         |          |               |
|                            | getClientReport                  |                      |         |          |               |
| GET /employees             | EmployeeController.listEmployees | Manager can          | x       |          |               |
| GET employees/:id/clients  | EmployeeController.              | generate a salary    |         |          |               |
|                            | listClientsOfEmployee            | report for anyone    |         |          |               |
| GET *report/employee*:id   | ReportController.                |                      |         |          |               |
|                            | getSalaryReport                  |                      |         |          |               |
| GET *employees*:id/clients | EmployeeController.              | Employee can         |         |          | x             |
|                            | listClientsOfEmployee            | generate a salary    |         |          |               |
| GET *report/employee*:id   | ReportController.                | report for oneself   |         |          |               |
|                            | getSalaryReport                  |                      |         |          |               |
