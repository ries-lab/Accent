package de.embl.rieslab.accent.common.utils;

public class utils {


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
	
	public static double extractExposureMs(String fileName) {
		if (fileName == null || fileName.length() < 3)
			return 0;
		
		// inverts string, gets the first occurrence of "sm" to get the last "ms" in fileName
		String inv = (new StringBuilder(fileName)).reverse().toString(); 
		int ind = inv.indexOf("sm"); 

		if(ind != -1) {
			int curr_ind  = fileName.length()-ind-3;

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
			return Double.parseDouble(sb.reverse().toString());
		}
		return 0;
	}  
    
}
