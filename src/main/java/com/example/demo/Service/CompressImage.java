package com.example.demo.Service;

import java.awt.image.BufferedImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CompressImage {

	public static void compress(String srcFilename, String desFilename) {
		try {

			File input = new File(srcFilename);
			BufferedImage image = ImageIO.read(input);

			File output = new File(desFilename);
			OutputStream out = new FileOutputStream(output);

			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(out);
			writer.setOutput(ios);

			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(0.01f);
			}

			writer.write(null, new IIOImage(image, null, null), param);

			out.close();
			ios.close();
			writer.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void compress(String Filename) {
		try {

			File input = new File(Filename);
			BufferedImage image = ImageIO.read(input);

			File output = new File(Filename);
			OutputStream out = new FileOutputStream(output);

			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(out);
			writer.setOutput(ios);

			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(0.01f);
			}

			writer.write(null, new IIOImage(image, null, null), param);

			out.close();
			ios.close();
			writer.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getResolutionRatio(int width, int height) {
		if (width >= height)
			return 1;
		else if (height - width <= width / 2)
			return 2;
		else
			return 3;
	}
}
