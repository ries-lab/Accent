package de.embl.rieslab.accent.common.utils;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class AccentUtils {


	public static boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
		
	public static double extractExposureMs(Path path) {
		return extractExposureMs(path.toString());
	}
	
	/**
	 * Extracts the last occurrence of numbers followed by "ms" (regardless of capitalization) in fileName. Accepts exposures with digits (only with a dot seprator).
	 * 
	 * @param fileName String to extract the exposure from.
	 * @return Exposure, or 0 if none was found.
	 */
	public static double extractExposureMs(String fileName) {
		if (fileName == null || fileName.length() < 3)
			return 0.;
		
		// inverts string, gets the first occurrence of "sm" to get the last "ms" in fileName
		String inv = (new StringBuilder(fileName)).reverse().toString().toLowerCase(); 
		
		boolean done = false;
		int offset = 0;
		while(!done) {
			int ind = inv.indexOf("sm"); 
	
			if(ind != -1) {
				int curr_ind  = fileName.length()-ind-offset-3;
	
				StringBuilder sb = new StringBuilder();
				while(curr_ind>=0) {
					if(Character.isDigit(fileName.charAt(curr_ind))) {
						sb.append(fileName.charAt(curr_ind));
					} else if(fileName.charAt(curr_ind) == '.' && curr_ind > 0 && Character.isDigit(fileName.charAt(curr_ind-1))) {
						sb.append(fileName.charAt(curr_ind));
					} else {
						break;
					}
					curr_ind --;	
				}
				if(curr_ind < fileName.length()-ind-offset-3) {
					return Double.parseDouble(sb.reverse().toString());
				} else {
					offset += ind+2;
					inv = inv.substring(ind+2);
				}
			} else {
				done = true;
			}
		}
		return 0.;
	}      
	
	/*
	 * From https://www.baeldung.com/java-check-string-number
	 */
	public static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
	    if (strNum == null) {
	        return false; 
	    }
	    return pattern.matcher(strNum).matches();
	}
}
