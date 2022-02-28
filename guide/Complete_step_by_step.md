# Complete step by step guide

This guide is adapted from the supplementary of the original paper:
Robin Diekmann, Joran Deschamps, Yiming Li, Aline Tschanz, Maurice Kahnwald, Ulf Matti, Jonas Ries, "Photon-free (s)CMOS camera characterization for artifact reduction in high- and super-resolution microscopy", bioRxiv 2021.04.16.440125. [doi: 2021.04.16.440125](https://doi.org/10.1101/2021.04.16.440125)

In particular, it includes the steps to use Accent results when fitting SMLM data with [SMAP](https://github.com/jries/SMAP).

1. [Introduction](https://github.com/ries-lab/Accent/blob/main/guide/Complete_step_by_step.md#introduction)
2. [Micro-Manager 2 calibration](https://github.com/ries-lab/Accent/blob/main/guide/Complete_step_by_step.md#micro-manager-2-calibration)
3. [Micro-Manager 1.4 + Fiji calibration](https://github.com/ries-lab/Accent/blob/main/guide/Complete_step_by_step.md#micro-manager-14-and-fiji-calibration)
4. [Single molecule localization using SMAP](https://github.com/ries-lab/Accent/blob/main/guide/Complete_step_by_step.md#single-molecule-localization-using-smap)

## Introduction

The complete ACCeNT workflow for camera characterization and SMLM
fitting can be performed in two different ways:

-   *EITHER* using the Micro-Manager 2 ACCeNT plugin for acquisition and
    simultaneous ACCeNT camera map computation ("Micro-Manager 2
    calibration") followed by SMAP SMLM fitting ("Single-molecule
    localization using SMAP");

-   *OR* using the Micro-Manager 1.4 ACCeNT script for acquisition
    followed by using the Fiji plugin for ACCeNT camera map computation
    ("Micro-Manager 1.4 and Fiji calibration") followed by using SMAP
    for SMLM fitting ("Single-molecule localization using SMAP").

Since all implementations are either available as plugins or scripts for
FIJI or Micro-Manager, no specific installation of ACCeNT is necessary.

As an alternative to the complete workflow, users can choose to perform
only certain parts of the pipeline. For instance, the Micro-Manager 2
plugin allows to perform acquisition and computation independently, or
the Fiji plugin can be used for computation of appropriately acquired
raw camera data from other software.

To test our pipeline on provided example data, start at point 2. of the
"Micro Manager 1.4 and Fiji calibration" workflow below. This includes
using Fiji for ACCeNT camera calibration and using SMAP for fitting of
3D SMLM data. The example data corresponds to the 3D STORM data of
nuclear pore complexes shown in Fig. 3. The example data has been
recorded using an uncooled, industry-grade CMOS camera (IDS
UI-3060CP-M-GL Rev.2, Sensor Sony IMX174) and can be found here:
<https://rieslab.de/#accent>.

We successfully tested ACCeNT with the following cameras:

-   UI-3060CP-M-GL Rev.2, IDS

-   Edge 4.2bi, PCO

-   DCC1545M, Thorlabs

-   Chameleon3 CM3-U3-50S5M, FLIR

-   Chameleon3 CM3-U3-31S4M-CS, FLIR

-   Prime BSI, Photometrics

We did not manage to run ACCeNT with the Hamamatsu Orca Flash4.0v2
because of an apparent dark current correction implemented by the
manufacturer.

The software of ACCeNT is licensed under GNU GPL v3.0.

## Micro-Manager 2 calibration

1.  Installation

    a.  Download the latest version of Micro-Manager 2 from:

        -   https://micro-manager.org/wiki/Download_Micro-Manager_Latest_Release

    b.  Download the latest release of the ACCeNT Micro-Manager 2
        plugin:

        -   https://github.com/ries-lab/Accent/releases

    c.  Place the downloaded .jar in the "mmplugins" folder of your
        Micro-Manager 2 installation folder.

2.  Start Micro-Manager 2 using a hardware configuration that includes
    your camera. Refer to the Micro-Manager wiki or the Image.sc forum
    for any trouble regarding hardware.

    a.  ACCeNT can be started from the plugins menu, under "Acquisition
        Tools". The plugin consists of three steps: acquisition,
        processing and maps generation. All steps can be carried out
        together or separately.

3.  Set ACCeNT parameters:

    a.  Using the "\..." button, select a folder in which the images
        will be saved.

    b.  Select the name you want to give the images, the number of
        frames per exposure and the exposures. We advise a minimum of
        15000 frames and 3 exposure times.

    c.  By clicking on "Options", you can select further acquisition
        options:

        -   Pre-run (min): pre-acquisition run time to thermally
            equilibrate the camera. This is particularly necessary in
            the case of uncooled cameras. The total run time is an
            approximation as overhead exists and is camera dependent.
        
        -   Save frames as: allows saving the frames as tiff stacks or
            individual images.
        
        -   Process data: process the calibration live (in parallel) or
            wait for the user to start it. If the acquisition is faster
            than the processing, the plugin will run into a buffer
            overflow and crash. In such a case, run your calibration
            with the "separately" option.
        
        -   Roi: sets the roi on which to perform the calibration. Roi
            set by Micro-Manager are ignored. If all fields are 0, then
            the calibration is performed on the full chip.

> Save the options to validate them.

4.  Click on "Run" to start the acquisition. If the "in parallel"
    processing options was chosen, the processing also starts. The
    acquisition step saves the images, as well as a JSON representation
    of the roi (roi.roi file).

5.  Once the acquisition is done, the folder and roi fields of the
    processing panel are updated.

    -   If the processing was chosen to be in parallel to the
        acquisition, then it finishes soon after the last image is
        acquired.

    -   If the processing is done separately, click on "Process".

> The processing step can be performed later by just loading the folder
> containing the images using the "\..." button. The plugin
> automatically detects the roi.roi file and updates the fields. If no
> roi file is present, input the roi x, y, width and height manually.

6.  The processing steps creates a calibration file (results.calb), an
    average and variance map for each exposure, as well as images of the
    various estimates present in the calibration.

7.  The map generation step is automatically performed after each
    processing step. To run a new generation step, you can load a
    calibration file using the "\..." button. Input the desired
    exposures in the exposure field and click on "Generate". The map
    generation step generates the average and variance maps for the
    required exposures.

The calibration file (results.calb) can then be used with SMAP.

## Micro-Manager 1.4 and Fiji calibration

In this pipeline, we make use of Micro-Manager 1.4 to generate the data.
In principle the Fiji plugin can be used regardless of the way the raw
data was generated. The only conditions are:

-   The images are tiff (stacks or individual images)

-   Each stack's name contains "XXXms", where XXX is the corresponding
    exposure time, and all stacks are in the same folder.
    **Alternatively**, each exposure (stack or individual images) can be
    in a folder with name containing "XXXms". All exposure folders must
    be grouped in one folder.

1.  Acquire camera frames with Micro-Manager 1.4.

    a.  Download the latest version of Micro-Manager 1.4 from:

> <https://micro-manager.org/wiki/Download_Micro-Manager_Latest_Release>

b.  Start Micro-Manager 1.4 with a hardware configuration containing
    your camera.

c.  In "Tools", open the "Scripting panel".

d.  Then load "accent-acquisition_script.bsh" from the Github
    repository:

> https://github.com/ries-lab/Accent/blob/master/accent-mm1/accent-acquisition_script.bsh

e.  In the script, modify the parameters "path", "exposures" and
    "numFrames". Additionally, change the roi on line 12. The parameters
    of the setRoi method are in order x0, y0, width and height; with x0
    and y0 defining the top-left corner of the roi. Note that certain
    cameras do not support this function. In such case, comment out
    (using "//") line 12 and set the roi manually in Micro-Manager
    (refer to Micro-Manager user's guide)

f.  Run the script to acquire the images. This can take some time, watch
    the console for progress and wait for the acquisition to complete.

```{=html}
<!-- -->
```
2.  **Start here to test ACCeNT on provided example data.** The example
    data can be downloaded at <https://rieslab.de/#accent>. The
    zip-archive for the example data contains raw data from the camera
    calibration, 3D stacks of beads for the point spread function (PSF)
    characterization and raw STORM data corresponding to Figure 3k-m.

3.  Install Fiji

    a.  Download Fiji:

> [https://imagej.net/Fiji/Downloads\
> ](https://imagej.net/Fiji/Downloads)Please note that the ACCeNT plugin
> uses features that have been added recently to Fiji. If you encounter
> errors, make sure to use the latest version of Fiji.

b.  Download the latest release of the ACCeNT Fiji plugin:

> <https://github.com/ries-lab/Accent/releases>

c.  Place the downloaded accent-fiji-1.0.jar in the "plugins" folder of
    your Fiji installation folder.

```{=html}
<!-- -->
```
4.  Start Fiji and the ACCeNT plugin from the plugins menu.

5.  Set ACCeNT parameters:

    a.  Detect the images by selecting the folder in which they are
        contained using the "\..." button. They should appear in the
        table with the correct exposure time in the second column.

        -   The example raw ACCeNT data is located in the folder
            "ACCENT_raw"

    b.  Set the roi parameters x (X0), and y (Y0), e.g. used in the
        Micro-Manager 1.4 acquisition under point 5.

        -   For the example data, keep the default values X0 = 0 and Y0
            = 0.

6.  Click on "Process" and wait.

> Processing of the example data takes about 20 minutes (Windows 64-bit
> operating system, i5-4690K CPU @ 4x3.5GHz, 32 GB memory).

7.  Output files:

    a.  The processing steps creates a calibration file (results.calb)
        to be used with SMAP, as well as different camera maps (in units
        of ADU counts unless stated otherwise):

    b.  The mean pixel values for each exposure time are saved as
        Avg_XXXms.tiff, where XXX denotes the exposure time in ms.

    c.  The variance pixel values for each exposure time are saved as
        Var_XXXms.tif, where XXX denotes the exposure time in ms.

    d.  The computed baseline is saved as Baseline.tiff.

    e.  The computed dark current per 1 second is saved as
        DC-per_sec.tiff.

    f.  The square of the computed read noise (in units of ADU counts
        squared) is saved as RN_sq.tiff.

    g.  The square of the thermal noise per 1 second (in units of ADU
        counts squared) is saved as TN_sq_per_sec.tiff.

    h.  The computed gain (in units of ADU counts per electron) is saved
        as Gain.tiff.

    i.  The computed offset maps for arbitrary exposure times (default
        values are 15 ms, 20 ms, 30 ms, 50 ms and 100 ms) are saved as
        generated_Avg_XXXms.tiff, where XXX denotes the exposure time in
        ms.

    j.  The computed variance maps for arbitrary exposure times (default
        values are 15 ms, 20 ms, 30 ms, 50 ms and 100 ms) are saved as
        generated_Var_XXXms.tiff, where XXX denotes the exposure time in
        ms.

    k.  The R_sq_YYY.tiff files show the R² values for inspection of the
        quality of the fits used to compute baseline (R_sq_avg.tiff),
        read noise squared (R_sq_var.tiff) and gain (R_sq_gain.tiff).

        -   You can inspect the generated camera maps of the example
            data using Fiji. For instance, open the "DC_per_sec.tiff" in
            Fiji. While the mean dark current is 54 counts/s (using
            Analyze -\> Measure; set mean via Results -\> Set
            Measurement), you will find that pixel (256,289) features
            significantly higher dark current of 1429 counts/s (placing
            the cursor over the pixel reading the value from the main
            Fiji window). Visual inspection of the image shows other
            pixels of pronounced dark current, e.g. (220,315), (289,284)
            and (266,247). To find the dark current in units of
            electrons/s, open the "Gain.tiff" file and measure the
            median gain value (using Analyze -\> Measure; set median via
            Results -\> Set Measurement). For the example data, the gain
            is 2.16 counts/electron. Hence, the mean dark current is (54
            counts/s) / (2.16 counts/electron) = 25 electrons/s.
    
        -   To inspect the read noise, open the "RN_sq.tiff" and
            calculate its square root (using Process -\> Math -\> Square
            Root). The mean read noise is 13.2 counts, corresponding to
            (13.2 counts) / (2.16 counts/electron) = 6.1 electrons.

8.  Though not needed for fitting of single molecule localization data
    in SMAP, ACCeNT can create exposure time specific camera maps. These
    can for instance be used to explore camera characteristics, find
    particularly bad pixels, or as input for software other than SMAP.\
    The map generation step is automatically performed after each
    processing step. To run a new generation step, you can load a
    calibration file using the "\..." button. Input the desired
    exposures in the exposure field and click on "Generate". The map
    generation step generates the average and variance maps for the
    required exposures.

    -   For the example data, type "999" into the field "Exposures
        (ms):" and click "Generate". The generated camera maps for
        offset and variance corresponding to 999 ms exposure time will
        be saved to the folder "ACCeNT_raw". To check the consistency
        with the measurement, open both the "Avg_999ms.tiff" (the mean
        pixel values directly from the measured data) and the
        "generated_Avg_999ms.tiff" (the computed offset values for this
        exposure time) files in Fiji. Compute the difference (using
        Process -\> Image calculator\...; Image 1: Avg_999ms.tiff,
        Operation: Subtract, Image 2: generated_Avg_999ms.tiff, check
        Create new window, check 32-bit (float) result). The mean
        difference between the measured and computed maps is --0.042
        counts corresponding to (-0.042 counts) / (2.16 counts/electron)
        = -0.019 electrons.

## Single-molecule localization using SMAP

1.  Install SMAP from: https://github.com/jries/SMAP. The version from
    time of publication is available at:
    https://github.com/jries/SMAP/releases/tag/v210404.

> following the instructions. Alternatively, a stand-alone version for
> PC and Mac can be downloaded at: <https://rieslab.de/#software>.
>
> Please follow the installation instructions provided with the
> executables.
>
> Familiarize yourself with SMAP by consulting the documentation, using
> example data downloaded at <https://rieslab.de/#accent>. Make sure to
> install Micro-Manager and select its path in the Preferences menu.

2.  As described in the user guide (SMAP_UserGuide.pdf (embl.de), page
    6), add your camera to the camera manager.

For the example data, the camera is already registered in the camera
manager.

3.  You need to specify in SMAP the location of the calibration file: In
    the 'correctionfile' field, select the camera calibration file, for
    the example the results.calb file. Save the changes in the camera
    manager.

4.  For fitting of 3D SMLM data, perform the 3D calibration as described
    in the SMAP user guide
    (<https://www.embl.de/download/ries/Documentation/SMAP_UserGuide.pdf>,
    page 9 "experimental PSF model"). For the provided example data, you
    can EITHER skip this step and use the file:

> "60xOil_sampleHolderInv\_\_CC0.140_1\_MMStack.ome_3dcal.mat"
>
> from the "PSF_raw" folder in the second next step, OR perform the 3D
> PSF calibration yourself, based on 3D stacks of 100 nm Tetraspeck
> beads:

-   Open "Plugins -\> Analyze -\> sr3D -\> calibrate3DsplinePSF"

-   On the new window, click "Run"

-   On the new window, click "Select camera files"

-   On the new window, click "add dir"

-   Navigate to the "PSF_raw" folder and mark all folders

-   Click "Open"

-   In the previous window, click "Done"

-   In the previous window, click "Calculate bead calibration" and wait.
    Processing of the example data takes about 2 minutes (Windows 64-bit
    operating system, i5-4690K CPU @ 4x3.5GHz, 32 GB memory, GeForce GTX
    970 GPU).

5.  In the SMAP main window select the 'Localize' tab and the 'Input
    Image' subtab to load the raw SMLM data via "load images".

6.  For the example data, open the first Tiff-file in the "SMLM_raw
    folder". Click "set Cam Parameters" to validate that the camera has
    been recognized. Do not change any values and click "OK". The
    example data corresponds to the 3D STORM data of nuclear pore
    complexes shown in Fig. 3k-m. The example data has been recorded
    using an uncooled, industry-grade CMOS camera (IDS UI-3060CP-M-GL
    Rev.2, Sensor Sony IMX174). It contains 19000 frames. The recording
    was manually stopped after bleaching of most fluorophores. Check
    'correct offset'.

7.  In the SMAP main window, change to the 'Fitter' subtab, select
    'Spline' as the fitting model via the drop-down menu, click "Load 3D
    cal" and load the 3D PSF calibration file.

> For the example data, load the file:
>
> "60xOil_sampleHolderInv\_\_CC0.140_1\_MMStack.ome_3dcal.mat".

8.  Check "sCMOS correction".

9.  Check "RI mismatch"

10. Click "Preview" to test fitting.

11. If successful (i.e. you get the message "preview done"), press
    "Localize" to start the localization process.

> For the provided example data, this takes about 6 minutes (Windows
> 64-bit operating system, i5-4690K CPU @ 4x3.5GHz, 32 GB memory,
> GeForce GTX 970 GPU). The file containing the localizations is saved
> as "SMLM_raw_sml.mat" in the main folder.

12. You can repeat the fitting process by unchecking the "sCMOS" check
    box in the "Fitter" tab, unchecking the "Correct offset" checkbox in
    the "Input Image" tab and compare the results.

> For the provided example data, go to the "Input Image" tab and unclick
> "Correct offset", go to the "Fitter" tab and unclick "sCMOS", click
> "Localize" and wait. This will take another about 6 minutes.

-   In the main window, click "Load" and navigate to the main folder of
    the example data. Mark both "SMLM_raw_sml.mat" (the fitting results
    using the CMOS fitter with the ACCeNT calibration) and
    "SMLM_raw_2\_sml.mat" (the fitting result not using the CMOS
    fitter). Click "Open". If you have not performed the fitting step
    yourself, you can alternatively load the provided localization files
    "provided_SMLM_CMOS_fitter_sml.mat" and
    "provided_SMLM_no_correction_sml.mat".

-   Change to the "Render" tab.

-   In the drop-down menu, select " SMLM_raw_sml"

-   Change "LUT:" to "green" via the drop-down menu.

-   Next to "quantile", change the value to "--3.0"

-   Set the localization filters on the right to the following intervals
    (lower bound is left value, upper bound is right value):

    -   locprec: \[0, 30\]

    -   z: \[-200, 300\]

    -   frame: \[600, Inf\]

-   Next to "Layer1", click "+".

-   In the drop-down menu for the new "Layer2", select "
    SMLM_raw_2\_sml"

-   Click the button "frame" to re-activate the frame filter.

-   Click "Inv" next to "LUT:".

-   Click "Render".

-   In the "format" region of the main window, click "Reset".

-   To perform drift correction, change to the "Process" tab, check
    "Correct z-drift" and click "Run." Drift correction will take about
    2 minutes.

-   Go back to the "Render" tab and click "Render".

-   In the "format" region of the main window, change the value of
    Pixrec (nm) to "3" and press enter.

> You can now inspect the reconstructed STORM image. Move around by
> right-clicking the region you want to bring to the center of the ROI.
> Data that has been fitted with the CMOS fitter is shown in green, data
> that has been fitted without the CMOS fitter is shown in magenta.
> Accordingly, regions where localizations from both fitting processes
> coincide well are displayed greyish.
>
> To visualize the reconstructed STORM image in 3D, in the "layers" part
> of the main window, uncheck the box of "2". Go to "Layer1", change
> "Colormode:" to "z", change "LUT:" to "jet", change the values next to
> "c range" to --100. In the "format" region of the main window, change
> the value of "Pixrec (nm)" to 10, press enter and click "Render". The
> z-coordinate of the dataset that has been fitted using the
> CMOS-specific fitter is now color-coded. Move around by right-clicking
> the region you want to bring to the center of the ROI.
