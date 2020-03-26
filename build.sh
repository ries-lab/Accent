#!/bin/bash
# Simple bash script to add the main Micro-manager jars to Maven.
# tests if mvn is installed
command -v mvn >/dev/null 2>&1 || { echo >&2 "Could not call mvn, are you sure Maven is installed?";}

if [ $# -eq 1 ]
	then
		MM2_PLUGINS_HOME=$1
		
		mvn install:install-file -Dfile="$MM2_PLUGINS_HOME\plugins\Micro-Manager\MMJ_.jar" -DgroupId=org.micromanager  -DartifactId=MMJ_ -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		mvn install:install-file -Dfile="$MM2_PLUGINS_HOME\plugins\Micro-Manager\MMAcqEngine.jar" -DgroupId=org.micromanager  -DartifactId=MMAcqEngine -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		mvn install:install-file -Dfile="$MM2_PLUGINS_HOME\plugins\Micro-Manager\MMCoreJ.jar" -DgroupId=org.micromanager  -DartifactId=MMCoreJ -Dversion=2.0.0-SNAPSHOT -Dpackaging=jar
		
		mvn -f accent/ clean install -Dmaven.test.skip=true
		
	else
		mvn -f accent/accent-common/ clean install -Dmaven.test.skip=true
		mvn -f accent/accent-fiji/ clean install -Dmaven.test.skip=true
fi