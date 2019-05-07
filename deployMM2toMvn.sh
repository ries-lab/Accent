#!/bin/bash
# Simple bash script to add the main Micro-manager jars to Maven.
# This script was directly adapter from https://github.com/nicost/MMClearVolumePlugin.

MM2_PLUGINS_HOME="D:\Micromanager\Micro-Manager-2.0gamma\plugins\Micro-Manager"

#mvn deploy:deploy-file -Dfile="$MM2_PLUGINS_HOME/MMJ_.jar" -DgroupId=org.micromanager -DartifactId=MMJ_ -Dversion=2.0-SNAPSHOT -Dpackaging=jar -Durl=file://repo -DrepositoryId=in-project
mvn deploy:deploy-file -Dfile="$MM2_PLUGINS_HOME/MMAcqEngine.jar" -DgroupId=org.micromanager -DartifactId=MMAcqEngine -Dversion=2.0-SNAPSHOT -Dpackaging=jar -Durl=file://repo -DrepositoryId=in-project
#mvn deploy:deploy-file -Dfile="$MM2_PLUGINS_HOME/MMCoreJ.jar" -DgroupId=org.micromanager -DartifactId=MMCoreJ -Dversion=2.0-SNAPSHOT -Dpackaging=jar -Durl=file://repo -DrepositoryId=in-project