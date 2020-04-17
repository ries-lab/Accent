package de.embl.rieslab.accent.fiji.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.embl.rieslab.accent.common.utils.AccentUtils;

public class AccentFijiUtils {
	
	/**
	 * Returns the number of tif files (.tif, .tiff, .TIF, .TIFF) found in path.  Files with "Avg" and "Var" will be ignored as we consider they were generated by Accent.
	 * @param path Directory path
	 * @return Tiffs count, 0 if the path is not a directory or no file is found.
	 */
	public static int getNumberTifs(String path) {
		int count = 0;
		
		if(!(new File(path)).isDirectory())
			return count;
		
		try {
			count = (int) Files.list(Paths.get(path))
				.map(Path::toString)
				.filter(e -> e.endsWith(".tif") || e.endsWith(".tiff") || e.endsWith(".TIF") || e.endsWith(".TIFF"))
				.filter(e -> !(e.contains("Avg") || e.contains("Var")))
				.count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * Returns the number of tif files (.tif, .tiff, .TIF, .TIFF) found in path that contain a number followed by "ms" 
	 * (regardless of capitalization, last occurrence) in their name.  Files with "Avg" and "Var" will be ignored as we consider they were generated by Accent.
	 * @param path Directory path
	 * @return Tiffs count, 0 if the path is not a directory or no file is found.
	 */
	public static int getNumberTifsContainMs(String path) {
		int count = 0;
		
		if(!(new File(path)).isDirectory())
			return count;
		
		try {
			count = (int) Files.list(Paths.get(path))
				.map(Path::toString)
				.filter(e -> e.endsWith(".tif") || e.endsWith(".tiff") || e.endsWith(".TIF") || e.endsWith(".TIFF"))
				.filter(e -> AccentUtils.extractExposureMs(e) > 0)
				.filter(e -> !(e.contains("Avg") || e.contains("Var")))
				.count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * Returns the number of directories found in path.
	 * @param path Directory path
	 * @return Directories count, 0 if the path is not a directory or no directory is found.
	 */
	public static int getNumberDirectories(String path) {
		int count = 0;
		
		if(!(new File(path)).isDirectory())
			return count;
		
		try {
			count = (int) Files.list(Paths.get(path))
				.map(Path::toFile)
				.filter(File::isDirectory)
				.count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * Returns the number of directories found in path that contain a number followed by "ms" 
	 * (regardless of capitalization, last occurrence) in their name. 
	 * @param path Directory path
	 * @return Directories count, 0 if the path is not a directory or no directory is found.
	 */
	public static int getNumberDirectoriesContainMs(String path) {
		int count = 0;
		
		if(!(new File(path)).isDirectory())
			return count;
		
		try {
			count = (int) Files.list(Paths.get(path))
				.map(Path::toFile)
				.filter(File::isDirectory)
				.filter(e -> AccentUtils.hasExposureMs(e.toString()))
				.count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Extract the exposure value (number found before the last occurrence of "ms") of each tiffs found 
	 * in path. If filterOutDirectories is false, then only folders with the exposure value in the name 
	 * will be retained. Files with "Avg" and "Var" will be ignored as we consider they were generated by Accent.
	 * 
	 * @param path Path in which the files are.
	 * @param filterOutDirectories True to filter only tiff, false to filter only directories.
	 * @return Map of exposures and file paths
	 */
	public static Map<Double, String> getExposures(String path, boolean filterOutDirectories) {
		Map<Double, String> c = new HashMap<Double, String>();
	
		if(!(new File(path)).isDirectory())
			return c;
				
		try {
			c = Files.list(Paths.get(path))
					.map(Path::toString)
					.filter(e -> (new File(e)).isDirectory() != filterOutDirectories)
					.filter(e -> !filterOutDirectories || !(e.contains("Avg") || e.contains("Var")))
					.filter(e -> AccentUtils.hasExposureMs(e.toString()))
					.collect(Collectors.toMap(AccentUtils::extractExposureMs, e -> e));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return c;
	}
}
