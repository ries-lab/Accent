# ![accent-logo-blue-32](accent-fiji\src\main\resources\images\accent-logo-blue-32.png) - ACCéNT

(s)CMOS camera calibration tool without shining light on your camera. ACCéNT is compatible with:

- [Micro-Manager 2.0.0-gamma](https://micro-manager.org/wiki/Download_Micro-Manager_Latest_Release): acquisition and analysis.
- [Fiji](https://imagej.net/Fiji/Downloads): TIFF-only analysis.

A Micro-Manager 1.4 script is available to perform the acquisition.

## How to install ACCéNT?

### Micro-Manager 2.0.0-gamma

1. [Download](https://github.com/ries-lab/Accent/releases) the pre-compiled plugin and place it in your Micro-Manager installation folder under "mmplugins".

2. Requires [maven](https://maven.apache.org/). Compile the plugin yourself using the build script (Windows or macOS):

   ```bash
   $ ./build-Win.sh "C:/Path/To/Micro-Manager2-gamma"
   ```

   The script should automatically place the generated .jar (in accent-mm2/target/) in the "mmplugins" folder of Micro-Manager.

### Fiji

1. (soon in the Fiji update site)

2. [Download](https://github.com/ries-lab/Accent/releases) the pre-compiled plugin and place it in your Fiji installation folder under "plugins".

3. Requires  [maven](https://maven.apache.org/). Compile the plugin yourself using the build script (Windows or macOS) : 

   ```bash
   $ ./build-Win.sh
   ```

   Finally, place the compiled .jar (generated in accent-fiji/target/) in the "plugins" folder of Fiji.



## Resources

This repository contains guides for the Micro-Manager and Fiji plugins:

- [Micro-manager 2 guide](guide/guide-mm2.md)
- [Fiji guide](guide/guide-fiji)
- Loading the camera map in [SMAP](https://github.com/jries/SMAP) (soon)