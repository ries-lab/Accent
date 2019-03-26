package main.java.embl.rieslab.photonfreecamcalib.utils;

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
	
    public static boolean isNumeric(String s) {
    	if(s != null){
	    	if(s.matches("[-+]?\\d*\\.?\\d+")){
	    		return true;
	    	} else if(s.matches("[-+]?\\d*\\,?\\d+")){
	    		return true;
	    	}
    	}
        return false;  
    }  
    
}
