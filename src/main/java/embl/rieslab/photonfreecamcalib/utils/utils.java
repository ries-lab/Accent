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
	
	public static int extractExposurefromFolderName(String dataFolder) {
		
		if(dataFolder.substring(dataFolder.length()-2).equals("ms")) {
			int length = 0;
			int index  = dataFolder.length()-3;

			while(Character.isDigit(dataFolder.charAt(index))) {
				length ++;
				index --;
			}

			return Integer.parseInt(dataFolder.substring(index+1, index+1+length));
		}
		
		return 0;
	}  	
	
	public static int extractExposurefromTiff(String tiffImage) {
		
		if(tiffImage.substring(tiffImage.length()-7).equals("ms.tiff")) {
			int length = 0;
			int index  = tiffImage.length()-8;

			while(Character.isDigit(tiffImage.charAt(index))) {
				length ++;
				index --;
			}

			return Integer.parseInt(tiffImage.substring(index+1, index+1+length));
		}
		
		return 0;
	}  
    
}
