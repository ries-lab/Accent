## Installation on Windows

This step-by-step guides aims at building the Fiji and Micro-Manager plugins. 

1. Download and install JDK 1.8 [from the Oracle website](https://www.oracle.com/hk/java/technologies/javase/javase-jdk8-downloads.html). You will need an account, you can either create one or use [the magic Bug Me Not](http://bugmenot.com/).

2. Download and install [maven](https://maven.apache.org/download.cgi).

3. Add the JDK and maven to the environment variables, [see step-by-step here](https://www.tutorialspoint.com/maven/maven_environment_setup.htm).

4. Download and install [git for Windows](https://gitforwindows.org/).

5. **Optional**: download [the latest Micro-Manager 2 64bits](https://valelab4.ucsf.edu/~MM/nightlyBuilds/2.0.0-gamma/Windows/).

6. Verify that maven works by opening the newly installed git console and typing:

  ```bash
  mvn --version
  ```

  The prompt should point to the JDK and maven installation folders.

7. Using the git console, create a git folder in your user folder and clone the Accent repository:

   ```bash
   mkdir ~/git
   cd ~/git
   git clone https://github.com/ries-lab/Accent.git
   ```

   This should create an Accent folder in the git folder.

8. Finally build Accent. If you did not install Micro-Manager, then simply type to build only the Fiji plugin:

   ```bash
   ./build-Win.sh
   ```

   If you installed Micro-Manager and want to build the Micro-Manager plugin, copy the path to your Micro-Manager installation folder and type:

   ```bash
   ./build-Win.sh "C:/Path/To/Micro-Manager"
   ```

   The plugins are built in the accent-fiji/target and accent-mm2/target folders. Note that the Micro-Manager plugin is directly copied to the Micro-Manager installation folder, while the Fiji plugin needs to be copied manually.



## Setting-up Eclipse

To modify the source code, we are better off using an IDE such as Eclipse.

1. Download and install [Eclipse](https://www.eclipse.org/downloads/).
2. Launch Eclipse and choose the default workspace.
3. In the Package Explorer, click on "Import projects...". Select "Maven" and "Existing Maven Projects". Then next.
4. Navigate to the Accent folder (in the previously created git folder).
5. Eclipse should find a main pom.xml file and three sub-pom.xml files in accent-common, accent-fiji and accent-mm2. Make sure they are all selected. In the bottom of the window, in "Add project(s) to working set", select "Java Main Sources". Finish.
6. Eclipse should create four projects. It will take some time to download all the sources and javadoc.