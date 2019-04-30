package main.java.test;

import org.micromanager.Studio;
import org.micromanager.data.Image;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import main.java.embl.rieslab.accent.data.FloatImage;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

public class ImageConversions {

	public static void testImage(Studio studio) {
		long start, end;
		double avg_floatproc=0, avg_floatim=0, avg_addproc=0, avg_addim=0;
		
		FloatImage floatim = null;
		FloatProcessor avg_im = null;
		for(int i=0;i<50;i++) {
			
			// get image
			Image mm_im = studio.live().snap(false).get(0);
					
			// floatproc
			start = System.currentTimeMillis();

			ImageProcessor improc =  studio.data().ij().createProcessor(mm_im);
			avg_im = new FloatProcessor(improc.getWidth(), improc.getHeight());
			avg_im.setFloatArray(improc.getFloatArray());
			
			end = System.currentTimeMillis();

			avg_floatproc += end-start;
			
			/////// imglib2
			
			start = System.currentTimeMillis();
			floatim = new FloatImage(mm_im,0);
			avg_im.setFloatArray(improc.getFloatArray());
			
			end = System.currentTimeMillis();
			avg_floatim += end-start;

		}
		System.out.println("At creation, floatproc: "+avg_floatproc/50);
		System.out.println("At creation, floatim: "+avg_floatim/50);
		
		// get image
		Image mm_im = studio.live().snap(false).get(0);			
		ImageProcessor improc =  studio.data().ij().createProcessor(mm_im);

		for(int i=0;i<50;i++) {
			
			// floatproc
			start = System.currentTimeMillis();

			addPixels(avg_im.getWidth(), avg_im.getHeight(), avg_im, improc);
			
			end = System.currentTimeMillis();

			avg_addproc += end-start;
			
			/////// imglib2
			
			start = System.currentTimeMillis();
			floatim.addPixels(mm_im.getWidth(), mm_im.getHeight(), (byte[]) mm_im.getRawPixels());
			avg_im.setFloatArray(improc.getFloatArray());
			
			end = System.currentTimeMillis();
			avg_addim += end-start;
		}
		System.out.println("Addition, floatproc: "+avg_addproc/50);
		System.out.println("Addition, floatim: "+avg_addim/50);
		
	}

	public static void addPixels(int w, int h, FloatProcessor avg_im, ImageProcessor improc) {
		for (int x = 0; x < w; x++) {

			for (int y = 0; y < h; y++) {

				avg_im.setf(x, y, avg_im.getf(x, y) + improc.getf(x, y));
			}
		}
	}
	
}
