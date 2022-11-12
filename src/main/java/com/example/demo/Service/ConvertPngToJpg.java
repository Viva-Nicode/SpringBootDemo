package com.example.demo.Service;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConvertPngToJpg {

	public static void pngToJpg(String srcFilenamem, String desFilename) {
		/*
		 * srcFilename : /User/nicode./.../pins/imagename.png
		 * desFilename : /User/nicode./.../Thumbnail/imagename.png
		 */
		try {

			Path source = Paths.get(srcFilenamem);

			Path target = Paths.get(ConvertPngToJpg.changeExtension(desFilename, "jpg"));

			BufferedImage originalImage = ImageIO.read(source.toFile());

			BufferedImage newBufferedImage = new BufferedImage(
					originalImage.getWidth(),
					originalImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			newBufferedImage.createGraphics()
					.drawImage(originalImage,
							0,
							0,
							Color.WHITE,
							null);

			ImageIO.write(newBufferedImage, "jpg", target.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String changeExtension(String filename, String desExt) {
		for (int idx = filename.length() - 1; idx >= 0; idx--) {
			if (filename.charAt(idx) == 46)
				return filename.substring(0, idx + 1) + desExt;
		}
		return "not fount extention";
	}

	public static String getExtentioString(String filename) {
		for (int idx = filename.length() - 1; idx >= 0; idx--) {
			if (filename.charAt(idx) == 46)
				return filename.substring(idx + 1);
		}
		return "";
	}
}
