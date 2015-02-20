#ESign
####Definition
ESign is an open source electronic signature application which consists of ESignCore, ESignUI and ESignWebApp applications.

1. ESignCore: Includes core functionalities.
2. ESignUI: Includes almost complete sample UI for testing core functionalities. Includes desktop and web UI:
  1. Desktop: JFrame is used as a main container. Application runs inside frame and pages are separated by panels.
  2. Web: JApplet is used as a main panel. Applet can be embedded into JFrame for testing or it can be embedded into jsp file inside ESignWebApp to run.
3. ESignWebApp: Sample web application to use as a container for applet.

####Jar Signing
To run application using JFrame no need to sign application. In pom.xml by default build configuration for sign operation is added. If a valid trusted certificate is not provided, it will prevent application to be built. One can comment out those fields to successfully built and run application.

If applet will be used it has to be signed by trusted certificate. Its forced by browsers. Some other ways could be found to bypass this for testing(google it). Signing is done in 3 steps.
  1. Define manifest file.
  2. One jar all jar files.
  3. Sign with trusted key.

To achieve these below code is added to pom.xml in ESignUI application:
```
<build>
    <plugins>
        <!--Set manifest file-->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>2.5</version>
            <configuration>
                <archive>
                    <addMavenDescriptor>false</addMavenDescriptor>
                    <manifest>
                        <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
                        <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                    </manifest>
                    <!--Add manifest properties-->
                    <manifestEntries>
                        <Permissions>all-permissions</Permissions>
                        <Trusted-Library>true</Trusted-Library>
                    </manifestEntries>
                </archive>
            </configuration>
        </plugin>
        <!--Set manifest file ends-->
        <!--one jar plugin-->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>2.3</version>
            <configuration>
                <filters>
                    <filter>
                        <artifact>*:*</artifact>
                        <!--Remove uncessary files which prevents application to be signed-->
                        <excludes>
                            <exclude>META-INF/*.SF</exclude>
                            <exclude>META-INF/*.DSA</exclude>
                            <exclude>META-INF/*.RSA</exclude>
                        </excludes>
                    </filter>
                </filters>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <!--one jar plugin ends-->
        <!--Sign application with trusted key-->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jarsigner-plugin</artifactId>
            <version>1.3.2</version>
            <executions>
                <execution>
                    <id>sign</id>
                    <goals>
                        <goal>sign</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <keystore>${keystore}</keystore>
                <storetype>${storetype}</storetype>
                <alias>${alias}</alias>
                <storepass>${storepass}</storepass>
            </configuration>
        </plugin>
        <!--Sign application with trusted key ends-->
    </plugins>
</build>
```
#####Settings File
To sign application keystore information is need to be provided in pom.xml. Since its not suitable to distribute this data along with code, settings file can be used. Settings file is used to create maven specific environment variables. Later on data can be reached using ${...}.

######Example:

pom.xml
```
...
<configuration>
<keystore>${keystore}</keystore>
             <storetype>${storetype}</storetype>
             <alias>${alias}</alias>
            <storepass>${storepass}</storepass>
</configuration>
...
```

settings.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<!--
    User-specific configuration for maven. Includes things that should not 
    be distributed with the pom.xml file, such as developer identity, along with 
    local settings, like proxy information. The default location for the
    settings file is ~/.m2/settings.xml 
-->
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>signer</id>
            <properties>
                <keystore>/user/home/../../code-signer.pfx</keystore>
                <storetype>pkcs12</storetype>
                <alias>myalias</alias>
                <storepass>storepassword </storepass>
            </properties>
        </profile>
    </profiles>
<!—Defines which profiles shall be active and reachable--→
    <activeProfiles>
        <activeProfile>signer</activeProfile>
    </activeProfiles>
</settings>
```
####E-SignUI Basic Steps
1.	Check if library pkcs11wrapper is installed.
2.	List modules.
  1.	Select module.
3.	Read certificate.
  1.	Show certificate details.
  2.	Show user agreement.
  3.	Accept user agreement.
4.	Let user enter pin code.
  1.	Check pin code.
  2.	Sign user agreement.
5.	Show last page.
  1.	Let user download signed user agreement or login.


####Properties File
If you want your application to use your own properties file which extends esignui application, just put properties files with same names to your application and set their values to your liking.

####Adding Jars To Local Repository
iaik_jce_full and iaik_cms libraries by default does not exists in maven repository. Therefore we need to add them to our repository manually. By default this is done for you no need to do this again. Local repository 

Below commands are used to put jars into local maven repository:

```
mvn install:install-file –Dfile=/path/to/iaik_jce_full_jar_file/iaik_jce_full.jar -DgroupId=at.tugraz.iaik -DartifactId=iaik_jce_full -Dversion=5.24 -Dpackaging=jar -DlocalRepositoryPath=/path/to/repo
```
```
mvn install:install-file -Dfile=/path/to/iaik_cms_jar_file/iaik_cms.jar -DgroupId=at.tugraz.iaik -DartifactId=iaik_cms -Dversion=5.0 -Dpackaging=jar –DlocalRepositoryPath=/path/to/repo
```
link to api:
(http://maven.apache.org/plugins/maven-install-plugin/install-file-mojo.html)


