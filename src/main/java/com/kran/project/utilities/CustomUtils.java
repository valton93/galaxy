package com.kran.project.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CustomUtils {
	public static String padCharacters(Object text, int length, char padWord, String position) {
		if (position.equalsIgnoreCase("R")) {
			return String.format("%1$-" + length + "s", text.toString().trim()).replace(' ', padWord);
		} else {
			return String.format("%1$" + length + "s", text.toString().trim()).replace(' ', padWord);
		}
	}
	
	public static boolean removeFile(String filePath) {
		Path path = Paths.get(filePath);
		try {
			if (Files.exists(path)) {
				Files.delete(path);
			}
		} catch (IOException e) {
			return false;
		}

		return true;
	}
}
