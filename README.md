# ![accent-logo-blue-32](accent-fiji/src/main/resources/images/accent-logo-blue-32.png) - ACCéNT

(s)CMOS camera calibration tool without shining light on your camera. ACCéNT is compatible with:

- [Micro-Manager 2.0.0-gamma](https://micro-manager.org/wiki/Download_Micro-Manager_Latest_Release): acquisition and analysis.
- [Fiji](https://imagej.net/Fiji/Downloads): TIFF-only analysis.
- A [Micro-Manager 1.4 script](accent-mm1) is available to perform the acquisition.

## How to install ACCéNT?

### Micro-Manager 2.0.0-gamma

1. [Download](https://github.com/ries-lab/Accent/releases) the pre-compiled plugin and place it in your Micro-Manager 2 installation folder under "mmplugins".

Or

2. Requires few steps, [see guide for Windows](guide/win-installation.md). Compile the plugin yourself using the build script (Windows):

   ```bash
   $ ./build-Win.sh "C:/Path/To/Micro-Manager2-gamma"
   ```

   The script should automatically place the generated .jar (in accent-mm2/target/) in the "mmplugins" folder of Micro-Manager.

### Fiji

1. [Download](https://github.com/ries-lab/Accent/releases) the pre-compiled plugin and place it in your Fiji installation folder under "plugins".

Or

2. Requires few steps, [see guide for Windows](guide/win-installation.md). Compile the plugin yourself using the build script (Windows) : 

   ```bash
   $ ./build-Win.sh
   ```

   Finally, place the compiled .jar (generated in accent-fiji/target/) in the "plugins" folder of Fiji.



## Resources

This repository contains guides for the Micro-Manager and Fiji plugins:

- [Micro-manager 2 guide](guide/guide-mm2.md)
- [Fiji guide](guide/guide-fiji.md)
- [Installation guide (Windows)](guide/win-installation.md). 
- [Complete step by step guide with SMAP](guide/Complete_step_by_step.md)

Additionally, results from Accent can be used to fit SMLM data using SMAP:
- [SMAP repository](https://github.com/jries/SMAP)

## Cite us

Robin Diekmann, Joran Deschamps, Yiming Li, Aline Tschanz, Maurice Kahnwald, Ulf Matti, Jonas Ries, "Photon-free (s)CMOS camera characterization for artifact reduction in high- and super-resolution microscopy", bioRxiv 2021.04.16.440125. [doi: 2021.04.16.440125](https://doi.org/10.1101/2021.04.16.440125)


