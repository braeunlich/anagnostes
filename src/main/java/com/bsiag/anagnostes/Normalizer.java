package com.bsiag.anagnostes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.imageio.ImageIO;

public class Normalizer {

	private static final int SCALE_TARGET_SIZE_X = 20;
	private static final int SCALE_TARGET_SIZE_Y = SCALE_TARGET_SIZE_X;
	private static final int TARGET_SIZE_X = 28;
	private static final int TARGET_SIZE_Y = TARGET_SIZE_X;

	/* ************************************************************************************* */

	public static BufferedImage transform(String fileName) throws IOException {
		return transform(readImage(fileName));
	}

	public static BufferedImage transform(BufferedImage image) throws IOException {
		// 1. binarize
		BufferedImage binaryImage = toBinaryImage(image);
		// 2. scale to fit into 20x20 box while preserving aspect ratio. this gives us grayscale image because of
		// anti-aliasing
		BufferedImage scaledImage = resize(binaryImage, SCALE_TARGET_SIZE_X, SCALE_TARGET_SIZE_Y);
		int[][] scaledImageMatrix = grayscaleImageToPixelMatrix(scaledImage);
		// 3. calculate center of gravity of image
		// 4. center image using center of gravity in a 28x28 box
		int[][] targetScaledImageMatrix = scaleAndCenterToTarget(scaledImageMatrix);
		return toImage(targetScaledImageMatrix);
	}

	public static double[] transformToMnsitIteratorFormat(BufferedImage image) throws IOException {
		// 1. binarize
		BufferedImage binaryImage = toBinaryImage(image);
		// 2. scale to fit into 20x20 box while preserving aspect ratio. this gives us grayscale image because of
		// anti-aliasing
		BufferedImage scaledImage = resize(binaryImage, SCALE_TARGET_SIZE_X, SCALE_TARGET_SIZE_Y);
		int[][] scaledImageMatrix = grayscaleImageToPixelMatrix(scaledImage);
		// 3. calculate center of gravity of image
		// 4. center image using center of gravity in a 28x28 box
		int[][] targetScaledImageMatrix = scaleAndCenterToTarget(scaledImageMatrix);
		return toMnistFormat(targetScaledImageMatrix);
	}

	/* ************************************************************************************* */

	private static BufferedImage toImage(int[][] imageMatrix) {
		int width = imageMatrix.length;
		int height = imageMatrix[0].length;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, (255 << 24) | (imageMatrix[x][y] << 16) | (imageMatrix[x][y] << 8) | imageMatrix[x][y]);
			}
		}
		return image;
	}

	/* ************************************************************************************* */

	private static int[][] scaleAndCenterToTarget(int[][] sourceImageMatrix) {
		double[] centerOfGravity = calcCenterOfGravity(sourceImageMatrix);
		int centerX = (int) Math.round(centerOfGravity[0]);
		int centerY = (int) Math.round(centerOfGravity[1]);
		// center could be too far to one side. best effort:
		int maxCenterDeltaX = (TARGET_SIZE_X - SCALE_TARGET_SIZE_X) / 2;
		int maxCenterDeltaY = (TARGET_SIZE_Y - SCALE_TARGET_SIZE_Y) / 2;

		if (centerX < SCALE_TARGET_SIZE_X / 2 - maxCenterDeltaX) {
			centerX = SCALE_TARGET_SIZE_X / 2 - maxCenterDeltaX;
		} else if (centerX > SCALE_TARGET_SIZE_X / 2 + maxCenterDeltaX) {
			centerX = SCALE_TARGET_SIZE_X / 2 + maxCenterDeltaX;
		}
		if (centerY < SCALE_TARGET_SIZE_Y / 2 - maxCenterDeltaY) {
			centerY = SCALE_TARGET_SIZE_Y / 2 - maxCenterDeltaY;
		} else if (centerY > SCALE_TARGET_SIZE_Y / 2 + maxCenterDeltaY) {
			centerY = SCALE_TARGET_SIZE_Y / 2 + maxCenterDeltaY;
		}
		int translateX = TARGET_SIZE_X / 2 - centerX;
		int translateY = TARGET_SIZE_Y / 2 - centerY;
		int[][] targetImageMatrix = new int[TARGET_SIZE_X][TARGET_SIZE_Y];
		for (int x = 0; x < TARGET_SIZE_X; x++) {
			for (int y = 0; y < TARGET_SIZE_Y; y++) {
				int sourceImageMatrixX = x - translateX;
				int sourceImageMatrixY = y - translateY;
				if (sourceImageMatrixX >= 0 && sourceImageMatrixX < SCALE_TARGET_SIZE_X && sourceImageMatrixY >= 0 && sourceImageMatrixY < SCALE_TARGET_SIZE_Y) {
					targetImageMatrix[x][y] = sourceImageMatrix[sourceImageMatrixX][sourceImageMatrixY];
				} else {
					targetImageMatrix[x][y] = 255;
				}
			}
		}
		return targetImageMatrix;
	}

	/* ************************************************************************************* */

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return dimg;
	}

	/* ************************************************************************************* */

	private static BufferedImage toBinaryImage(BufferedImage image) {
		return OtsuBinarize.transform(image);
	}

	/* ************************************************************************************* */

	private static int[][] grayscaleImageToPixelMatrix(BufferedImage bwImage) {
		int width = bwImage.getWidth();
		int height = bwImage.getHeight();
		int[][] matrix = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int p = bwImage.getRGB(x, y);
				matrix[x][y] = p & 0xff;
			}
		}
		return matrix;
	}

	/* ************************************************************************************* */

	private static BufferedImage readImage(String imageName) throws IOException {
		return ImageIO.read(new File(imageName));
	}

	/* ************************************************************************************* */

	/**
	 * One dimensional: let coordinates x be with values v. center of gravity: cog = sum(x_i * v_i) / sum(v_i). If all
	 * values v_i are 0 then undefined. We can do this cog calculation for each dimension separately and combine the
	 * answers.
	 *
	 * @param picture
	 *          in gray scale values. points are weighed in accordance with their gray scale. So double the gray scale
	 *          means double weight. But beware: value 255=white, 0=black, so 255-value is in fact the weight of a pixel
	 * @return center of gravity (x,y), (undefined if picture is all white)
	 */
	public static double[] calcCenterOfGravity(int[][] picture) {
		MathContext mc = MathContext.DECIMAL32;
		BigDecimal zero = new BigDecimal(0, mc);
		BigDecimal summedGrayScaleValues = zero;
		BigDecimal summedRowGrayScaleValues = zero;
		BigDecimal summedColumnGrayScaleValues = zero;
		for (int rowIndex = 0; rowIndex < picture.length; rowIndex++) {
			int[] row = picture[rowIndex];
			for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
				int grayScaleWeight = 255 - row[columnIndex];
				if (grayScaleWeight > 0) {
					summedRowGrayScaleValues = summedRowGrayScaleValues.add(BigDecimal.valueOf(grayScaleWeight * rowIndex), mc);
					summedColumnGrayScaleValues = summedColumnGrayScaleValues.add(BigDecimal.valueOf(grayScaleWeight * columnIndex), mc);
					summedGrayScaleValues = summedGrayScaleValues.add(BigDecimal.valueOf(grayScaleWeight), mc);
				}
			}
		}
		BigDecimal rowAverage = summedGrayScaleValues.longValue() > 0 ? summedRowGrayScaleValues.divide(summedGrayScaleValues, mc) : zero;
		BigDecimal columnAverage = summedGrayScaleValues.longValue() > 0 ? summedColumnGrayScaleValues.divide(summedGrayScaleValues, mc) : zero;
		double[] center = new double[] { rowAverage.doubleValue(), columnAverage.doubleValue() };
		return center;
	}

	/* ************************************************************************************* */

	private static void outputJpgFile(String filename, BufferedImage newImage) throws IOException {
		File outputfile = new File(filename);
		if (!outputfile.exists()) {
			outputfile.createNewFile();
		}
		ImageIO.write(newImage, "jpg", outputfile);
	}
	
	/* ************************************************************************************* */
	
	public static void outputJpgFile(String filename, double[] mnsitFormat) throws IOException {
		outputJpgFile(filename, toBufferedImage(mnsitFormat));
	}
	
	public static void outputJpgFile(String filename, float[] mnsitFormatFloat) throws IOException {
		double[] mnsitFormat = new double[mnsitFormatFloat.length];
		for (int i=0; i<mnsitFormatFloat.length; i++) {
			mnsitFormat[i] = mnsitFormatFloat[i];
		}
		outputJpgFile(filename, toBufferedImage(mnsitFormat));
	}

	/* ************************************************************************************* */
	
	private static BufferedImage toBufferedImage(double[] mnsitFormat) {
		int[][] imageMatrix = new int[TARGET_SIZE_X][TARGET_SIZE_Y];
		for(int i=0; i<TARGET_SIZE_X * TARGET_SIZE_Y; i++) {
			imageMatrix[i%TARGET_SIZE_X][i/TARGET_SIZE_X] = toRGB(mnsitFormat[i]);
		}
		return toImage(imageMatrix);
	}

	private static double[] toMnistFormat(int[][] intarray) {
		double[] doublearray = new double[intarray.length * intarray[0].length];
		
		for (int i = 0; i < intarray.length; i++) {
			for (int j = 0; j < intarray[0].length; j++){
				int index = intarray.length * i + j;
				doublearray[index] = toMnsitFormat(intarray[j][i]);
			}
		}
		return doublearray;
	}
	
	private static double toMnsitFormat(int rgb) {
		return (255.0 - rgb) / 255.0;
	}
	
	private static int toRGB(double d) {
		return (int) (255 - (d * 255));
	}

	/* ************************************************************************************* */

	public static BufferedImage convert(BufferedImage src, int newImgType) {
		BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), newImgType);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(src, 0, 0, null);
		g2d.dispose();
		return img;
	}

	/* ************************************************************************************* */

	public static void main(String[] args) throws IOException {
		BufferedImage originalImage = ImageIO.read(new File("src/main/resources/seven60.jpg"));
		BufferedImage newImage = transform(originalImage);
		outputJpgFile("c:/temp3/seven60_conv.jpg", newImage);
	}
}
