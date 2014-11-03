## OpenTox authorization and authentication REST client

A library and a command line client to facilitate access to the OpenTox authorization and authentication services.

http://opentox.org/dev/apis/api-1.2/AA

###Usage:

####1) As a [Maven dependency](http://ambit.uni-plovdiv.bg:8083/nexus/index.html#nexus-search;gav~ambit~opentox-opensso~~~)

Release 

```
<dependency>
  <groupId>ambit</groupId>
  <artifactId>opentox-opensso</artifactId>
  <version>1.0.6</version>
</dependency>
```

Development

```
<dependency>
  <groupId>ambit</groupId>
  <artifactId>opentox-opensso</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
```

Repositories:

Releases:
````
    <repository>
        <id>ambit-plovdiv-releases</id>
        <url>http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/releases</url>
        <snapshots>
  	<enabled>false</enabled>
         </snapshots>
    </repository>
````

Snapshots:
````
    <repository>
        <id>ambit-plovdiv-snapshots</id>
        <url>http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/snapshots</url>
        <snapshots>
               <enabled>true</enabled>
        </snapshots>
    </repository>
````

###2) As a command line utility

````
java -jar opentox-opensso-{version}-jar-with-dependencies.jar

	An OpenTox Authentication and Authorization client.
	usage: 
	 -c,--command <the command>   The command to be performed. One of
	                              authorize|list|delete|create|archive Default value:  authorize
	 -h,--help                    OpenTox Authentication and Authorization
	                              client
	 -i,--policyid <policyid>     An OpenSSO/OpenAM policy identifier
	 -n,--authn <URI>             URI of OpenSSO/OpenAM service 
	 -p,--password <password>     OpenTox user password
	 -r,--uri <URI>               URI of an OpenTox resource
	 -s,--subjectid <token>       OpenSSO/OpenAM token. If the token is
	                              present, user and password are ignored.
	 -u,--user <username>         OpenTox user name
	 -z,--authz <URI>             URI of OpenTox policy service 
````

###Build:

>mvn clean package

####Set Maven profile

This is a maven project. The following maven profile must be configured for build to succeed.

#####opentox-opensso
````
<!-- Start config -->
  <profiles>
   <profile>
      <id>opentox-opensso</id>
      <activation>
        <activeByDefault>false</activeByDefault>
      </activation>
      <properties>
         <!-- Configuration of OpenTox authentication service -->
	    <aa.opensso>http://opensso.in-silico.ch/opensso/identity</aa.opensso>
		<!-- Configuration of OpenTox authorization service -->
        <aa.policy>http://opensso.in-silico.ch/Pol/opensso-pol</aa.policy>
		<!-- User credentials for junit tests -->
        <aa.user>YOUR-USER-FOR-TESTING</aa.user>
        <aa.pass>YOUR-PASS-FOR-TESTING</aa.pass>
      </properties>
    </profile>
  </profiles>
<!-- End config -->
````

#####aa-openam

````
  <profile>
      <id>aa-openam</id>
      <activation>
        <activeByDefault>false</activeByDefault>
      </activation>
      <properties>
        <aa.opensso>https://openam.in-silico.ch/auth</aa.opensso>
        <aa.policy>https://openam.in-silico.ch/pol</aa.policy>
        <aa.user>YOUR-USER-FOR-TESTING</aa.user>
        <aa.pass>YOUR-PASS-FOR-TESTING</aa.pass>
      </properties>
    </profile>
````
