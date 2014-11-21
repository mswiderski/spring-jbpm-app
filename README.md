jbpm-spring-app
===============

jBPM spring application that shows how to build execution server using jBPM 6 and spring

Installation notes:

JBoss EAP 6.x
-----------------------------

Application after successful build works out of the box on JBoss EAP with its in memory data base. 

Wildfly
-----------------------------

For Wildfly there needs to be one configuraion change apply to ensure that CDI container will only resolve beans from actual bean archives (those that include META-INF/beans.xml)

issue following JBoss CLI command for wildfly service prior deployment
``` plain
/subsystem=weld:write-attribute(name=require-bean-descriptor,value=true)
```
Tomcat
-----------------------------

1. Install bitronix transaction manager into your tomcat 7
- copy following libs into TOMCAT_HOME/lib
    * btm-2.1.4.jar
    * btm-tomcat55-lifecycle-2.1.4.jar
    * h2-1.3.161.jar
    * jta-1.1.jar
    * slf4j-api-1.7.2.jar
    * slf4j-jdk14-1.7.2.jar

    NOTE: versions of the libraries can be different as these are the actual on the time of writing.

2. Create configuration files inside TOMCAT_HOME/conf
    * btm-config.properties
    ************************ sample btm-config.properties *************************
         bitronix.tm.serverId=tomcat-btm-node0
         bitronix.tm.journal.disk.logPart1Filename=${btm.root}/work/btm1.tlog
         bitronix.tm.journal.disk.logPart2Filename=${btm.root}/work/btm2.tlog
         bitronix.tm.resource.configuration=${btm.root}/conf/resources.properties

    *******************************************************************************


    * resources.properties
    ************************ sample resources.properties **************************
         resource.ds1.className=bitronix.tm.resource.jdbc.lrc.LrcXADataSource
         resource.ds1.uniqueName=jdbc/jbpm
         resource.ds1.minPoolSize=10
         resource.ds1.maxPoolSize=20
         resource.ds1.driverProperties.driverClassName=org.h2.Driver
         resource.ds1.driverProperties.url=jdbc:h2:file:~/jbpm
         resource.ds1.driverProperties.user=sa
         resource.ds1.driverProperties.password=
         resource.ds1.allowLocalTransactions=true
    *******************************************************************************

    NOTE: jdbc/jbpm is the JNDI name used by tomcat distribution of the application

3. Define btm.root system property and location where bitronix config file is placed

    create setenv.sh (or setenv.bat) file inside TOMCAT_HOME/bin and add following:

    CATALINA_OPTS="-Xmx512M -XX:MaxPermSize=512m -Dbtm.root=$CATALINA_HOME -Dbitronix.tm.configuration=$CATALINA_HOME/conf/btm-config.properties -Djbpm.tsr.jndi.lookup=java:comp/env/TransactionSynchronizationRegistry"

    NOTE: this is an example for unix like systems for Windows $CATALINA_HOME needs to be replaced with windows env variable or absolute path

4. use proper persistence.xml file - application comes with two persistence.xml files in web module, just use rename the default one to jboss and the other rename from tomcat to jbpm-persistence.xml and build the application and that should be all.
