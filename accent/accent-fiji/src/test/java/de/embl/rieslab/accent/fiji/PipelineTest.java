package de.embl.rieslab.accent.fiji;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.embl.rieslab.accent.fiji.datagen.GenerateData;
import io.scif.config.SCIFIOConfig;
import io.scif.img.ImgSaver;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class PipelineTest {

	ss
	//@Test
	public void testPipeline() throws IOException {
		final ImageJ ij = new ImageJ();
		String path = "D:\\Accent\\test2";
		
		SCIFIOConfig config = new SCIFIOConfig();

		ImgSaver saver = new ImgSaver();
		Img<UnsignedShortType> img_s = GenerateData.generateUnsignedShortType(100, 100, 10, 10.);
		
		config.writerSetSequential(true);
		
		saver.saveImg(path+"\\"+"mytest.tif", img_s, config);
		
		
		
		Img<RealType<?>> img = ij.scifio().datasetIO().open(path);
		//System.out.println(img.numDimensions());
		
		// gets folders
		//Map<Double, String> c = Files
	/*			Files.list(Paths.get(path))
				.map(Path::toString)
				.filter(e -> ij.scifio().datasetIO().canOpen(e))
				.forEach(System.out::println);
		*/		//.collect(Collectors.toMap(AccentUtils::extractExposureMs, e -> e));
				
//		c.remove(0); // files without ###ms in the name
		
		// loads datasets and compute average and variance
/*		for (Entry<Double, String> e : c.entrySet()) {
			Img<RealType<?>> input = ij.scifio().datasetIO().open(e.getValue());
			
			if(e.getKey().equals(10.) && input.numDimensions() == 3) {
				
				
			}				
		}*/
	}
}
