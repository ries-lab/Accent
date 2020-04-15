package de.embl.rieslab.accent.fiji.datagen;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import org.junit.Test;

import de.embl.rieslab.accent.common.processor.CalibrationProcessor;
import de.embl.rieslab.accent.fiji.AccentFiji;
import io.scif.services.DatasetIOService;
import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;

public class CompareResults {
	
	static int width = 11;
	static int height = 21;
	static int numFrames = 20000;
	double[] exps = {0.1, 10, 300, 1000, 2000};
	double[] generated = {15,20,30,50,100};
	
	double tolerance_avg = 0.01;
	double tolerance_var = 0.05;
	double tolerance_gen_avg = 0.05;
	double tolerance_gen_var = 0.1;
	double tolerance_rsq = 0.03;
	double tolerance_phys = 0.05;
		
	@Test
	@SuppressWarnings("unchecked")
	public void compareResults() {
		final ImageJ ij = new ImageJ();
		DatasetIOService data_service = ij.scifio().datasetIO();
		
		String folder = "D:\\Accent\\fiji\\short\\stacks";

		//////////////////////////////
		// tests average and variance
		for(double e: exps) {
			try {
				Img<FloatType> avg, var;
				if(Double.compare(e, (int) e) == 0){
					avg = (Img<FloatType>) (data_service.open(folder+"\\Avg_"+((int) e)+"ms.tiff")).getImgPlus().getImg();
					var = (Img<FloatType>) (data_service.open(folder+"\\Var_"+((int) e)+"ms.tiff")).getImgPlus().getImg();
				} else {
					avg = (Img<FloatType>) (data_service.open(folder+"\\Avg_"+e+"ms.tiff")).getImgPlus().getImg();
					var = (Img<FloatType>) (data_service.open(folder+"\\Var_"+e+"ms.tiff")).getImgPlus().getImg();
				}
				
				float lowpix_avg = (float) GenerateData.getLowPixAverage(e);
				float hotpix_avg = (float) GenerateData.getHotPixAverage(e);
				float lowpix_var = (float) GenerateData.getLowPixVariance(e);
				float hotpix_var = (float) GenerateData.getHotPixVariance(e);
				
				Cursor<FloatType> curs = avg.localizingCursor();
				RandomAccess<FloatType> r_var2 = var.randomAccess();
				while(curs.hasNext()) {
					FloatType t = curs.next();
					r_var2.setPosition(curs);
					FloatType u = r_var2.get();
					
					if(curs.getIntPosition(0) % 10 == 0 && curs.getIntPosition(1) % 20 == 0) {
						assertEquals(hotpix_avg, t.get(), tolerance_avg*hotpix_avg);
						assertEquals(hotpix_var, u.get(), tolerance_var*hotpix_var); 
					} else {
						assertEquals(lowpix_avg, t.get(), tolerance_avg*lowpix_avg);
						assertEquals(lowpix_var, u.get(), tolerance_var*lowpix_var);
					}
				}				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		///////////////////////////////////////
		// tests generated average and variance
		for(double e: generated) {
			try {
				Img<FloatType> avg, var;
				if(Double.compare(e, (int) e) == 0){
					avg = (Img<FloatType>) (data_service.open(folder+"\\generated_Avg_"+((int) e)+"ms.tiff")).getImgPlus().getImg();
					var = (Img<FloatType>) (data_service.open(folder+"\\generated_Var_"+((int) e)+"ms.tiff")).getImgPlus().getImg();
				} else {
					avg = (Img<FloatType>) (data_service.open(folder+"\\generated_Avg_"+e+"ms.tiff")).getImgPlus().getImg();
					var = (Img<FloatType>) (data_service.open(folder+"\\generated_Var_"+e+"ms.tiff")).getImgPlus().getImg();
				}
				
				float lowpix_avg = (float) GenerateData.getLowPixAverage(e);
				float hotpix_avg = (float) GenerateData.getHotPixAverage(e);
				float lowpix_var = (float) GenerateData.getLowPixVariance(e);
				float hotpix_var = (float) GenerateData.getHotPixVariance(e);
				
				Cursor<FloatType> curs = avg.localizingCursor();
				RandomAccess<FloatType> r_var2 = var.randomAccess();
				while(curs.hasNext()) {
					FloatType t = curs.next();
					r_var2.setPosition(curs);
					FloatType u = r_var2.get();
					
					if(curs.getIntPosition(0) % 10 == 0 && curs.getIntPosition(1) % 20 == 0) {
						assertEquals("Hot pix gen Avg: "+e+" ms", hotpix_avg, t.get(), tolerance_gen_avg*hotpix_avg);
						assertEquals("Hot pix gen Var: "+e+" ms", hotpix_var, u.get(), tolerance_gen_var*hotpix_var); 
					} else {
						assertEquals("Low pix gen Avg: "+e+" ms", lowpix_avg, t.get(), tolerance_gen_avg*lowpix_avg);
						assertEquals("Low pix gen Var: "+e+" ms", lowpix_var, u.get(), tolerance_gen_var*lowpix_var);
					}
				}				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		///////////////////////////////////////
		// tests estimated physical parameters
		try {
			Img<FloatType> gain  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.GAIN)).getImgPlus().getImg();
			Img<FloatType> baseline  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.BASELINE)).getImgPlus().getImg();
			Img<FloatType> dcpersec  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.DCPERSEC)).getImgPlus().getImg();
			Img<FloatType> rnsq  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.RNSQ)).getImgPlus().getImg();
			Img<FloatType> rsqavg  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.RSQAVG)).getImgPlus().getImg();
			Img<FloatType> rsqgain  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.RSQGAIN)).getImgPlus().getImg();
			Img<FloatType> rsqvar  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.RSQVAR)).getImgPlus().getImg();
			Img<FloatType> tnsqpersec  = (Img<FloatType>) (data_service.open(folder+"\\"+CalibrationProcessor.TNSQPERSEC)).getImgPlus().getImg();
			
			Cursor<FloatType> curs = gain.localizingCursor();
			RandomAccess<FloatType> r_baseline = baseline.randomAccess();
			RandomAccess<FloatType> r_dcpersec = dcpersec.randomAccess();
			RandomAccess<FloatType> r_rnsq = rnsq.randomAccess();
			RandomAccess<FloatType> r_rsqavg = rsqavg.randomAccess();
			RandomAccess<FloatType> r_rsqgain = rsqgain.randomAccess();
			RandomAccess<FloatType> r_rsqvar = rsqvar.randomAccess();
			RandomAccess<FloatType> r_tnsqpersec = tnsqpersec.randomAccess();
			while(curs.hasNext()) {
				curs.next();
				r_baseline.setPosition(curs);
				r_dcpersec.setPosition(curs);
				r_rnsq.setPosition(curs);
				r_rsqavg.setPosition(curs);
				r_rsqgain.setPosition(curs);
				r_rsqvar.setPosition(curs);
				r_tnsqpersec.setPosition(curs);
				
				if (curs.getIntPosition(0) % 10 == 0 && curs.getIntPosition(1) % 20 == 0) {
				//	assertEquals(CalibrationProcessor.GAIN,
					//		GenerateData.HOTPIX_TNSQPERSEC / GenerateData.LOWPIX_DCPERSEC, curs.get().get(),
						//	tolerance_phys * GenerateData.HOTPIX_TNSQPERSEC / GenerateData.LOWPIX_DCPERSEC);

					assertEquals(CalibrationProcessor.BASELINE, GenerateData.HOTPIX_BASELINE, r_baseline.get().get(),
							tolerance_phys * GenerateData.HOTPIX_BASELINE);
					
					assertEquals(CalibrationProcessor.DCPERSEC, GenerateData.HOTPIX_DCPERSEC, r_dcpersec.get().get(),
							tolerance_phys * GenerateData.HOTPIX_DCPERSEC);
					
					assertEquals(CalibrationProcessor.RNSQ, GenerateData.HOTPIX_RNSQ, r_rnsq.get().get(),
							tolerance_phys * GenerateData.HOTPIX_RNSQ);
					
					assertEquals(CalibrationProcessor.TNSQPERSEC, GenerateData.HOTPIX_TNSQPERSEC,
							r_tnsqpersec.get().get(), tolerance_phys * GenerateData.HOTPIX_TNSQPERSEC);
				} else {
				//	assertEquals(CalibrationProcessor.GAIN,
					//		GenerateData.LOWPIX_TNSQPERSEC / GenerateData.LOWPIX_DCPERSEC, curs.get().get(),
						//	tolerance_phys * GenerateData.LOWPIX_TNSQPERSEC / GenerateData.LOWPIX_DCPERSEC);

					assertEquals(CalibrationProcessor.BASELINE, GenerateData.LOWPIX_BASELINE, r_baseline.get().get(),
							tolerance_phys * GenerateData.LOWPIX_BASELINE);
					
					assertEquals(CalibrationProcessor.DCPERSEC, GenerateData.LOWPIX_DCPERSEC, r_dcpersec.get().get(),
							tolerance_phys * GenerateData.LOWPIX_DCPERSEC);
					
					assertEquals(CalibrationProcessor.RNSQ, GenerateData.LOWPIX_RNSQ, r_rnsq.get().get(),
							tolerance_phys * GenerateData.LOWPIX_RNSQ);
				}
				
				assertEquals(CalibrationProcessor.RSQAVG, 1, r_rsqavg.get().get(), tolerance_rsq);
				assertEquals(CalibrationProcessor.RSQGAIN, 1, r_rsqgain.get().get(), tolerance_rsq);
				assertEquals(CalibrationProcessor.RSQVAR, 1, r_rsqvar.get().get(), tolerance_rsq);
			}				
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	// super slow
	public String writeToDisk() {
		String dir = "D:\\Accent\\fiji";

		String dir_short = dir+"\\short";
		(new File(dir_short)).mkdir();
		String dir_short_stacks = dir_short+"\\stacks";
		(new File(dir_short_stacks)).mkdir();
		
		/*
		String dir_short_singles = dir_short+"\\singles";
		(new File(dir_short_singles)).mkdir();

		String dir_byte = dir+"\\byte";
		(new File(dir_byte)).mkdir();
		String dir_byte_stacks = dir_byte+"\\stacks";
		(new File(dir_byte_stacks)).mkdir();
		String dir_byte_singles = dir_byte+"\\singles";
		(new File(dir_byte_singles)).mkdir();
		
		String dir_int = dir+"\\int";
		(new File(dir_int)).mkdir();
		String dir_int_stacks = dir_int+"\\stacks";
		(new File(dir_int_stacks)).mkdir();
		String dir_int_singles = dir_int+"\\singles";
		(new File(dir_int_singles)).mkdir();
		*/
		
		// writes data
		GenerateData.generateAndWriteToDisk(dir_short_stacks, width, height, numFrames, exps, true, new UnsignedShortType());
		//GenerateData.generateAndWriteToDisk(dir_short_singles, width, height, numFrames, exps, false, new UnsignedShortType());
		//GenerateData.generateAndWriteToDisk(dir_byte_stacks, width, height, numFrames, exps, true, new UnsignedByteType());
		//GenerateData.generateAndWriteToDisk(dir_byte_singles, width, height, numFrames, exps, false, new UnsignedByteType());
		//GenerateData.generateAndWriteToDisk(dir_int_stacks, width, height, numFrames, exps, true, new UnsignedIntType());
		//GenerateData.generateAndWriteToDisk(dir_int_singles, width, height, numFrames, exps, false, new UnsignedIntType());
		
		return dir_short_stacks;
	}
	
	public void testPipeline() {
		String dir = writeToDisk();
        
		final ImageJ ijlaunch = new net.imagej.ImageJ();
        ijlaunch.ui().showUI();
        
		ijlaunch.command().run(AccentFiji.class, true);
	}
}
