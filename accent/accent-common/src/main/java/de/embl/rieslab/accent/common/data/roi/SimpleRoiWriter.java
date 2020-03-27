package de.embl.rieslab.accent.common.data.roi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A class used to write a SimpleRoi to the disk.
 * 
 * @author Joran Deschamps
 *
 */
public class SimpleRoiWriter {
	
	/**
	 * Writes a SimpleRoi to a file.
	 * 
	 * @param fileToWriteTo File to which the roi should be written
	 * @param roi Roi to write
	 * @return True fi the write was successful, false otherwise
	 */
	public static boolean write(File fileToWriteTo, SimpleRoi roi) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try {
			objectMapper.writeValue(new FileOutputStream(fileToWriteTo), roi); 
			return true;
			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
