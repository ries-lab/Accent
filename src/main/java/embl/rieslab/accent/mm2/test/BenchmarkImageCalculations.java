package main.java.embl.rieslab.accent.mm2.test;

import org.micromanager.Studio;
import org.micromanager.data.Image;

import main.java.embl.rieslab.accent.common.data.image.BareImage;
import main.java.embl.rieslab.accent.common.data.image.FloatImage;

public class BenchmarkImageCalculations {

	public static void testPerformancesImageToFloatImage(Studio studio) {
		double length = 200.;
		long start, end;
		double bare_creation=0, avg_floatim=0,avg_addim=0,avg_div=0,avg_sq=0,avg_var=0;

		Image mm_im = studio.live().snap(false).get(0);
		
		// Creation
		BareImage bareim = null;
		for(int i=0;i<length;i++) {
			
			// get image
					
			start = System.currentTimeMillis();

			bareim = new BareImage(BareImage.DataType.BYTE, mm_im.getRawPixels(), mm_im.getWidth(), mm_im.getHeight(), 0);
		
			end = System.currentTimeMillis();
			bare_creation += end-start;

		}
		System.out.println("At creation, bareim: "+bare_creation/length+" ms");
		
		// Creation
		FloatImage im = null;
		for(int i=0;i<length;i++) {
			
			// get image
					
			start = System.currentTimeMillis();

			im = new FloatImage(bareim);
		
			end = System.currentTimeMillis();
			avg_floatim += end-start;

		}
		System.out.println("At creation, floatim: "+avg_floatim/length+" ms");
		
		// Addition
		for(int i=0;i<length;i++) {	
			im = new FloatImage(bareim);
			
			start = System.currentTimeMillis();

			im.addPixels(bareim);
			
			end = System.currentTimeMillis();

			avg_addim += end-start;
		}
		System.out.println("Addition, floatim: "+avg_addim/length+" ms");
		

		// Division
		for(int i=0;i<length;i++) {		
			im = new FloatImage(bareim);
			
			start = System.currentTimeMillis();

			im.dividePixels((float) 1.);
			
			end = System.currentTimeMillis();

			avg_div += end-start;
		}
		System.out.println("Division, floatim: "+avg_div/length+" ms");
		
		// Square addition
		for(int i=0;i<length;i++) {
			im = new FloatImage(bareim);
			
			start = System.currentTimeMillis();

			im.addSquarePixels(bareim);
			
			end = System.currentTimeMillis();

			avg_sq += end-start;
		}
		System.out.println("Square addition, floatim: "+avg_sq/length+" ms");
		
		// To variance
		FloatImage mean = new FloatImage(bareim);; 
		for(int i=0;i<length;i++) {
			im = new FloatImage(bareim);
			
			start = System.currentTimeMillis();

			im.toVariance(mean.getImage(), (float) 2.);
			
			end = System.currentTimeMillis();

			avg_var += end-start;
		}
		System.out.println("To variance, floatim: "+avg_var/length+" ms");
	}	
	
	public static void testPerformancesBytesArrayToFloatImage() {
		double length = 200.;
		int size = 512;
		long start, end;
		double byte_floatim=0,byte_addim=0,byte_div=0,byte_sq=0,byte_var=0;
		double short_floatim=0,short_addim=0,short_div=0,short_sq=0;

		byte[] byte_im = new byte[size*size];
		short[] short_im = new short[size*size];
		for(int i=0;i<size;i++) {
			byte_im[i] = 1;
			short_im[i] = 1;
		}
		
		// Creation
		FloatImage im = null;
		for(int i=0;i<length;i++) {

			start = System.currentTimeMillis();
			im = new FloatImage(size, size, byte_im, 0);
			end = System.currentTimeMillis();
			byte_floatim += end-start;
			
			start = System.currentTimeMillis();
			im = new FloatImage(size, size, short_im, 0);
			end = System.currentTimeMillis();
			short_floatim += end-start;

		}
		System.out.println("At creation, byte to floatimage: "+byte_floatim/length+" ms");
		System.out.println("At creation, short to floatimage: "+short_floatim/length+" ms");
		
		// Addition
		for(int i=0;i<length;i++) {
			im = new FloatImage(size, size, byte_im, 0);
			
			start = System.currentTimeMillis();
			im.addPixels(byte_im);		
			end = System.currentTimeMillis();
			byte_addim += end-start;
			

			im = new FloatImage(size, size, short_im, 0);	
			start = System.currentTimeMillis();
			im.addPixels(short_im);		
			end = System.currentTimeMillis();
			short_addim += end-start;
			
			
		}
		System.out.println("Addition, byte to floatim: "+byte_addim/length+" ms");
		System.out.println("Addition, short to floatim: "+short_addim/length+" ms");
		

		// Division
		for(int i=0;i<length;i++) {
			im = new FloatImage(size, size, byte_im, 0);
			
			start = System.currentTimeMillis();
			im.dividePixels((float) 1.);
			end = System.currentTimeMillis();
			byte_div += end-start;

			im = new FloatImage(size, size, short_im, 0);
			
			start = System.currentTimeMillis();
			im.dividePixels((float) 1.);
			end = System.currentTimeMillis();
			short_div += end-start;
			
		}
		System.out.println("Division, byte to floatim: "+byte_div/length+" ms");
		System.out.println("Division, short to floatim: "+short_div/length+" ms");
		
		// Square addition
		for(int i=0;i<length;i++) {
			im = new FloatImage(size, size, byte_im, 0);
			
			start = System.currentTimeMillis();
			im.addSquarePixels(byte_im);
			end = System.currentTimeMillis();
			byte_sq += end-start;
			
			im = new FloatImage(size, size, short_im, 0);
			
			start = System.currentTimeMillis();
			im.addSquarePixels(short_im);
			end = System.currentTimeMillis();
			short_sq += end-start;
		}
		System.out.println("Square addition, byte to floatim: "+byte_sq/length+" ms");
		System.out.println("Square addition, short to floatim: "+short_sq/length+" ms");
		
		// To variance
		FloatImage mean = new FloatImage(size, size, byte_im, 0); 
		for(int i=0;i<length;i++) {
			im = new FloatImage(size, size, byte_im, 0);
			start = System.currentTimeMillis();
			im.toVariance(mean.getImage(), (float) 2.);
			end = System.currentTimeMillis();
			byte_var += end-start;
			
		}
		System.out.println("To variance, floatim: "+byte_var/length+" ms");
	}	
}
