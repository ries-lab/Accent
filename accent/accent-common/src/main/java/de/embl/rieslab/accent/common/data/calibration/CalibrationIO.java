package de.embl.rieslab.accent.common.data.calibration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A class to read/write calibrations to/from the disk. It uses JacksonJSON to serialize objects.
 * 
 * @author Joran Deschamps
 *
 */
public class CalibrationIO {
	/**
	 * Accent calibration file extension.
	 */
	public final static String CALIB_EXT = "calb";
	
	/**
	 * Attempts to read a calibration from a file. 
	 * 
	 * @param fileToReadFrom Calibration file
	 * @return Read calibration, or null if reading was unsuccessful.
	 */
	public static Calibration read(File fileToReadFrom) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try {

			Calibration config = objectMapper.readValue(new FileInputStream(fileToReadFrom), Calibration.class);
			return config;

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes a calibration to a file.
	 * 
	 * @param fileToWriteTo FIle to which the calibration is written.
	 * @param calibration Calibration to write.
	 * @return True if the write was successful, false otherwise.
	 */
	public static boolean write(File fileToWriteTo, Calibration calibration) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		try {
			// should write a GlobalConfigurationWrapper, not a GlobalConfiguration
			objectMapper.writeValue(new FileOutputStream(fileToWriteTo), calibration); 
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
