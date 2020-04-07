package de.embl.rieslab.accent.fiji;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.embl.rieslab.accent.common.utils.AccentUtils;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.axis.Axes;
import net.imagej.ops.Ops;
import net.imagej.ops.convert.RealTypeConverter;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.Cursor;
import net.imglib2.FinalDimensions;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class PipelineTest {

	
	@Test
	public void testPipeline() throws IOException {
		final ImageJ ij = new ImageJ();
		String path = "D:\\Accent\\fiji";
		
		// gets folders
		Map<Double, String> c = Files
				.list(Paths.get(path))
				.map(Path::toString)
				.filter(e -> ij.scifio().datasetIO().canOpen(e))
				.collect(Collectors.toMap(AccentUtils::extractExposureMs, e -> e));
				
//		c.remove(0); // files without ###ms in the name
		
		// loads datasets and compute average and variance
		ArrayImgFactory<FloatType> factory = new ArrayImgFactory<FloatType>( new FloatType() );
		//ArrayList<Img<FloatType>> avgs = new ArrayList<Img<FloatType>>();

		for (Entry<Double, String> e : c.entrySet()) {
			Img<RealType<?>> input = ij.scifio().datasetIO().open(e.getValue());
			
			if(e.getKey().equals(10.) && input.numDimensions() == 3) {
				long w  =input.dimension(0);
				long h  = input.dimension(1);
				long depth  = input.dimension(2);
				RandomAccess<RealType<?>> r = input.randomAccess();
					
				Img<FloatType> avg = factory.create(new long[] {w,h,1});
				Cursor<FloatType> cursor = avg.localizingCursor();
				
			/*	while(cursor.hasNext()) {
					cursor.next();
					r.setPosition(cursor);
					System.out.println(r.getIntPosition(0)+", "+r.getIntPosition(1)+", "+r.getIntPosition(2));
				}
				*/
				
				for(int z=0;z<20;z++) {
					r.setPosition(z, 0);
					System.out.println(r.getIntPosition(0)+", "+r.getIntPosition(1)+", "+r.getIntPosition(2));
										
					//System.out.println(r.get().getRealFloat());
				}
				
				/*for(int x=0;x<w;x++) {
					for(int y=0;y<h;y++) {
						
					}
				}*/
				
			}	
			
			// convert to float
			/*final Converter<RealType<?>, FloatType> converter = (realtype, floattype) -> { floattype.set(realtype.getRealFloat()); };
			RandomAccessibleInterval<FloatType> target = Converters.convert((RandomAccessibleInterval<RealType<?>>) input, converter, new FloatType());
			System.out.println(target.dimension(0));
			System.out.println(target.dimension(1));
			System.out.println(target.dimension(2));
			
			RandomAccess<FloatType> r = target.randomAccess();
			for(int t=0;t<4;t++) {
				r.setPosition(t,2);
				System.out.println("---");
				System.out.println(r.get());
				System.out.println(r.getIntPosition(0));
				System.out.println(r.getIntPosition(1));
				System.out.println(r.getIntPosition(2));
			}
*/
		//	final RandomAccessibleInterval< FloatType > intervalView = Views.interval( target, Intervals.createMinSize( 0, 0, 0, 1, 1, 4) );

		/*	RandomAccess<FloatType> r = target.randomAccess(Intervals.createMinMax(0,0,0,1,1,40));
			for(int t=0;t<4;t++) {
				r.setPosition(t,2);
				System.out.println("---");
				System.out.println(r.get());
				System.out.println(r.getIntPosition(0));
				System.out.println(r.getIntPosition(1));
				System.out.println(r.getIntPosition(2));
			}
			*/
			//IterableInterval<FloatType> it = Views.iterable(intervalView);
			//FloatType f = new FloatType(0);
			//it.forEach(t -> f.add(t));
			
			
			// should probably use ij.ops()
			/*for(int x = 0; x<target.dimension(0); x++) {
				for(int y = 0; y<target.dimension(1); y++) {
					RandomAccess<FloatType> it = target.randomAccess(Intervals.createMinSize(x,y,0,1,1,target.dimension(2)));

					final RandomAccessibleInterval< FloatType > intervalView = Views.interval( img, Intervals.createMinSize( x, y, 100, 100 ) );
					
					
					//ij.op().stats().mean(it);
					
				}
			}*/
			
			
			//ij.op().stats().mean(in)
			
		}
	}
}
