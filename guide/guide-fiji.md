# Fiji guide

### Prerequisite

- Fiji
- ACCéNT plugin installed
- A set of suitable acquisitions (see processing)

ACCéNT can be started from the "Plugins" menu of Fiji. It has twosteps, which can be performed together in a pipeline or independently.

## Processing

The processing step expects either multiple stacks or folders with "XXXms" in the name, where XXX is the exposure time at which the images were acquired. Two configurations are possible:

- Several stacks in the same folder, with XXXms in the name. Each stack will be processed independently (but sequentially).
- Several folders, with XXXms in the name, each containing images (single images or stack). Each folder will be processed independently (but sequentially).
- Multiple stacks and multiple folders with XXXms in the name. The plugin will calculate the number of stacks vs the number of folders, and will try to process the larger one.

We tested the the pipeline with the following types:

- unsigned byte images (1 Byte = 8 bits)
- unsigned short images (2 Bytes = 16 bits)
- unsigned int images (4 Bytes = 32 bits)

We recommend taking **at least 20'000 frames** with **at least 3 different exposures spanning a wide range** (e.g. 10, 300, 1000). 

Processing does not require any parameters but the folder where the images have been saved. The processor produces multiple files:

- Average and variance images for all processed exposures.
- The maps for the gain, baseline, dark current per second, the square read-noise, the square thermal noise per second and the coefficient of determination for the gain, average and variance fits.
- A calibration file (.calb) which contains the values of all maps.

**Important notes**:

- **Number of acquisitions**: the processor requires **at least 2 folders** in the path. 

- **Processing progress**: when loading individual images, the processing panel will show the number of processed frames over the total number of frames. When loading a single stack, the processing panel will show the number of pixels processed over the total number of pixels. When loading multiple stacks, the processing panel will display the number of pixels processed in the current stack over the image total number of pixels. 

  

## Generation

Generation of average and variance maps are performed automatically at the end of the processing. Additional generation can be done anytime by selecting the path to a calibration file, entering the required exposures in ms and finally clicking on "generate".

Generation produces an expected average and variance images for each indicated exposure time.