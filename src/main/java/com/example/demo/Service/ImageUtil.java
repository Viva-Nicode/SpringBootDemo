package com.example.demo.Service;

import java.awt.image.BufferedImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.validation.constraints.Pattern.Flag;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtil {
	private static float _compression_Rate = 0.3f;

	public static void compress(String srcFilename, String desFilename) {
		try {
			File input = new File(srcFilename);

			int ori = ImageUtil.getOrientation(input);
			BufferedImage newimg = ImageUtil.checkImage(input, ori);

			File output = new File(desFilename);
			OutputStream out = new FileOutputStream(output);
			ImageOutputStream ios = ImageIO.createImageOutputStream(out);

			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			writer.setOutput(ios);

			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(_compression_Rate);
			}

			writer.write(null, new IIOImage(newimg, null, null), param);

			out.close();
			ios.close();
			writer.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (MetadataException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void compress(String Filename) {
		try {

			File input = new File(Filename);
			BufferedImage image = ImageUtil.checkImage(input, ImageUtil.getOrientation(input));

			File output = new File(Filename);
			OutputStream out = new FileOutputStream(output);

			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(out);
			writer.setOutput(ios);

			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(_compression_Rate);
			}

			writer.write(null, new IIOImage(image, null, null), param);

			out.close();
			ios.close();
			writer.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pngToJpg(String srcFilenamem, String desFilename) {
		/*
		 * srcFilename : /User/nicode./.../pins/imagename.png
		 * desFilename : /User/nicode./.../Thumbnail/imagename.png
		 */
		try {

			Path source = Paths.get(srcFilenamem);
			Path target = Paths.get(ImageUtil.changeExtension(desFilename, "jpg"));
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

	public static int getOrientation(File file) throws Exception {
		int orientation = 1;
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		if (directory != null) {
			if (directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			}
		}
		return orientation;
	}

	private static BufferedImage rotateImage(BufferedImage bimage, int radians) {
		BufferedImage newImage;
		if (radians == 90 || radians == 270) {
			newImage = new BufferedImage(bimage.getHeight(), bimage.getWidth(),
					bimage.getType());
		} else if (radians == 180) {
			newImage = new BufferedImage(bimage.getWidth(), bimage.getHeight(),
					bimage.getType());
		} else {
			return bimage;
		}
		Graphics2D graphics = (Graphics2D) newImage.getGraphics();
		graphics.rotate(Math.toRadians(radians), newImage.getWidth() / 2,
				newImage.getHeight() / 2);
		graphics.translate((newImage.getWidth() - bimage.getWidth()) / 2,
				(newImage.getHeight() - bimage.getHeight()) / 2);
		graphics.drawImage(bimage, 0, 0, bimage.getWidth(), bimage.getHeight(),
				null);
		return newImage;
	}

	public static BufferedImage checkImage(File file, int orientation) throws Exception {
		BufferedImage bi = ImageIO.read(file);
		if (orientation == 1) { // 정위치
			return bi;
		} else if (orientation == 6) {
			return rotateImage(bi, 90);
		} else if (orientation == 3) {
			return rotateImage(bi, 180);
		} else if (orientation == 8) {
			return rotateImage(bi, 270);
		} else {
			return bi;
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

	public static int getResolutionRatio(int width, int height) {
		/*
		 * width : 1920
		 * height : 1080
		 */
		float ratio = (float)height / width;
		System.out.println(width);
		System.out.println(height);
		System.out.println(ratio);

		if (ratio <= 1.1f)
			return 1;
		else if (ratio <= 1.5f)
			return 2;
		else
			return 3;
	}
}
/* height > width && height - width <= */