package de.embl.rieslab.accent.fiji.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import de.embl.rieslab.accent.common.utils.AccentUtils;

public class AccentFijiUtils {
	
	/**
	 * Returns the number of tif files (.tif, .tiff, .TIF, .TIFF) found in path.
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
				.count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * Returns the number of tif files (.tif, .tiff, .TIF, .TIFF) found in path that contain a number followed by "ms" (regardless of capitalization, last occurrence) in their name.
	 * @param path Directory path
	 * @return Tiffs count, 0 if the path is not a directory or no file is found.
	 */
	public static int getNumberTifsContainMs(String path) {
		int count = 0;
		try {
			count = (int) Files.list(Paths.get(path))
				.map(Path::toString)
				.filter(e -> e.endsWith(".tif") || e.endsWith(".tiff") || e.endsWith(".TIF") || e.endsWith(".TIFF"))
				.filter(e -> AccentUtils.extractExposureMs(e) > 0)
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
	 * Returns the number of directories found in path that contain a number followed by "ms" (regardless of capitalization, last occurrence) in their name.
	 * @param path Directory path
	 * @return Directories count, 0 if the path is not a directory or no directory is found.
	 */
	public static int getNumberDirectoriesContainMs(String path) {
		int count = 0;
		try {
			count = (int) Files.list(Paths.get(path))
				.map(Path::toFile)
				.filter(File::isDirectory)
				.filter(e -> AccentUtils.extractExposureMs(e.toString()) > 0)
				.count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Extract the exposure value (number found before the last occurrence of "ms")
	 * 
	 * @param path
	 * @return
	 */
	public static Map<Double, String> getExposures(String path) {
		Map<Double, String> c = null;
		try {
			c = Files.list(Paths.get(path))
					.map(Path::toString)
					.collect(Collectors.toMap(AccentUtils::extractExposureMs, e -> e));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return c;
	}
}
