package de.embl.rieslab.accent.common.data.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class RoiIOTest {

	@Test
	public void testIO() {
		int x = 12;
		int y = 85;
		int h = 45;
		int w = 26;
		
		SimpleRoi roi = new SimpleRoi(x,y,w,h);
		
		File f_roi = new File("myroi");
		
		// write calibration
		SimpleRoiWriter.write(f_roi, roi);
		assertTrue(f_roi.exists());
		
		// read calibration
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try {
			SimpleRoi roi2 = objectMapper.readValue(new FileInputStream(f_roi), SimpleRoi.class);
			assertEquals(roi.height, roi2.height);
			assertEquals(roi.width, roi2.width);
			assertEquals(roi.x0, roi2.x0);
			assertEquals(roi.y0, roi2.y0);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// delete temp
		assertTrue(f_roi.delete());
	}
}
