# Resource Planner Laboratory Instructions
## Goals
* Use Resource Planner authoring tools within Business Central.
* Deploy a Resource Planner project in the KIE Server.
* Use the KIE Serer REST API to solve a problem configuration.

In this activity we will leverage the Resource Planner workbench to solve a curriculum course scheduling problem. The problem definition model will look like the following diagram:
![curriculum course class diagram](curriculumCourseClassDiagram.png)

This is a real world example that deals with scheduling of lectures in an university. It was presented by the International Timetabling Competation in 2007. You can find more information in the [competition web site](http://www.cs.qub.ac.uk/itc2007/curriculmcourse/course_curriculm_index.htm).

The main idea of this problem is to assign university lectures to rooms with different capacities and to assign them in a specific time period: By period we refer to a fixed size time range in a day of the week like: `Friday from 9 to 10 AM`.

To describe the lecture content we have other entities:
The `Course` represents the different courses that the university offers like Mathematics, Physics or Biology. Each `Course` has an specific number of students who entered the `Course`. Every `Course` can be represented in multiple instances that are known as `Lectures`. It is possible to one `Teacher` to teach a multiple number of courses. There is also the concept of `Curriculum`, which represents a group of courses with the same focus: there can be for example a _scientific curriculum_ that could contain courses like physics, chemistry and biology courses among others to support that curriculum; in the other hand _humanities curriculum_ would offer courses like arts and humanities.
This is a constraints satisfaction problem with multiple restrictions that need to be taken into account. We call them **HARD CONSTRAINTS** and **SOFT CONSTRAINTS**.

The **HARD CONSTRAINTS** that _can't be broken by any means_ are for example:
* A teacher can't teach 2 lectures at the same time.

The **SOFT CONSTRAINTS** are not necessarily mean to be met, but _they influence the quality of the solution_. For example:
* The lectures of the same course should be assigned to a single room.

The summarized problem definition gathered in the [Resource Planner documentation](https://access.redhat.com/documentation/en-us/red_hat_jboss_brms/6.4/html/business_resource_planner_guide/usecasesandexamples#curriculumcourse) is:

Problem Description:
Schedule each lecture into a timeslot and into a room.

Hard constraints:
* Teacher conflict: A teacher must not have 2 lectures in the same period.
* Curriculum conflict: A curriculum must not have 2 lectures in the same period.
* Room occupancy: 2 lectures must not be in the same room in the same period.
* Unavailable period (specified per dataset): A specific lecture must not be assigned to a specific period.

Soft constraints:
* Room capacity: A room’s capacity should not be less than the number of students in its lecture.
* Minimum working days: Lectures of the same course should be spread out into a minimum number of days.
* Curriculum compactness: Lectures belonging to the same curriculum should be adjacent to each other (so in consecutive periods).
* Room stability: Lectures of the same course should be assigned the same room.

## Activity Prerequisites
* Installation and configuration of the rhte-process-driven-apps.vdi ([read instructions](vdi_installation.md)).
* Business Central and KIE Server EAP instances up and running in the rhte-process-driven-apps virtual machine.
* `Acme` organizational unit created in the Business Central.

# Setup Activity Environment
## Enable Resource Planner Authoring and Runtime
Resource Planner is included in the JBoss BPM Suite distribution for Business Central and KIE Server.
In this section we will review the required instructions to enable authoring and run time for the Resource Planner.

1. Open a terminal window and change to the business central EAP instance configuration folder and edith the `application-roles.properties` file:
  ```bash
  cd ~/lab/bpms/bc/standalone/configuration
  pluma  application-roles.properties
  ```
2. add the `plannermgmt` role to the `jboss` user and save the file:
  ```
  jboss=admin,analyst,user,kie-server,rest-all,plannermgmt
  ```
3. Login as `jboss/bpms` in business central by navigating to `http://localhost:8080/business-central`.
4. Clone the `planner-labs` git repository in business central:
  1. Select the **Authoring**>**Administration** menu option.
    ![Business Central: Authoring > Administration](bc-authoring-admin.png)
  2. From the **Repositories** menu select the **Clone repository** option.
    ![Business Central: Authoring > Administration > Repository > Clone](auth-repo-clone.png)
  3. Fill the _Clone Repository_ form with the follosing information:
    * **Repository Name:** planner-labs
    * **Organizational Unit:** acme
    * **Git URL:** https://github.com/gpe-mw-training/planner-labs.git
    * Leave empty the `user name` and `password` (planner-labs is a public repository).
5. Check that the authoring for Resource Planner have been enabled:
  1. Select the **Authoring**>**Project Authoring** menu option.
    ![Business Central: Authoring > Administration](bc-authoring-admin.png)
  2. Click on the **New Item** menu and check that the `Solver configuration` option is present:
    ![Business Central: Authoring > New Item > Solver Configuration](new-item-solver-config.png)
  > If the option is not enabled log out and log in to Business Central, also double check the roles configuration for the `jboss` user.

6. Open a terminal window and review the configuration switch for the `optaplanner extension`:
  ```bash
  cat ~/lab/bpms/kieserver/bin/standalone.conf | grep optaplanner
  ```
2. You should see a `false` value for the disable optaplanner switch:
  ```
  JAVA_OPTS="$JAVA_OPTS -Dorg.optaplanner.server.ext.disabled=false"
  ```
> If you see a `true` value: Edith the standalone.conf file, change the value to `false` and restart the KIE-Server EAP instance.

3. Open the API endpoints list in your browser by navigating to: http://localhost:8230/kie-server/docs
  * Search for the `solver` endpoints:
    ![KIE Server: API > Solver Endpoints](kie-server-solver-endpoints.png)
  > If you are unable to find the solver endpoints, double check the kieserver `standalone.conf` extension disable value and restart the kieserver EAP instance; There is a common mistake to edit the `standalone.conf` from the business central EAP instance instead of the KIE Server one, check that this is not your case.
