#!/bin/bash
# Simple bash script to add the main Micro-manager jars to Maven.
# tests if mvn is installed
command -v mvn >/dev/null 2>&1 || { echo >&2 "Could not call mvn, are you sure Maven is installed?";}

if [ $# -eq 1 ]
	then
		MM2_HOME=$1
		MM2_PLUGINS_HOME="$MM2_HOME\mmplugins"
		
		mvn install:install-file -Dfile="$MM2_HOME\plugins\Micro-Manager\MMJ_.jar" -DgroupId=org.micromanager  -DartifactId=MMJ_ -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		mvn install:install-file -Dfile="$MM2_HOME\plugins\Micro-Manager\MMAcqEngine.jar" -DgroupId=org.micromanager  -DartifactId=MMAcqEngine -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		mvn install:install-file -Dfile="$MM2_HOME\plugins\Micro-Manager\MMCoreJ.jar" -DgroupId=org.micromanager  -DartifactId=MMCoreJ -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		
		cd accent-common
		mvn clean install -DskipTests
		
		cd ../accent-mm2
		mvn clean package shade:shade install -DskipTests 

		# finally copy the jar to MM
		cp "target\accent-mm2-1.0.jar" "$MM2_PLUGINS_HOME\accent-mm2-1.0.jar"

		cd ..
		
	else
		cd accent-common
		mvn clean install -DskipTests

		cd ../accent-fiji
		mvn clean package shade:shade install -DskipTests

		cd ..
fi
