package com.bsiag.anagnostes;

import java.io.File;

import javax.imageio.ImageIO;

public class App {


	public static final String USER_HOME = System.getProperty("user.home");
	public static final String SEPARATOR = File.separator;
	
	public static final String DEBUG_PATH = "/home/chb/tmp/";
	
	private static final String MODEL_FILE_NAME = "/home/chb/git/anagnostes/src/main/resources/model_numbers.zip";
	
	private static final String IMG_PATH = "/home/chb/git/numbers/";
	
	
	private static final String IMG_A_0 = IMG_PATH + "0020_CH4M/0/number-25.png";
	private static final String IMG_A_1 = IMG_PATH + "0020_CH4M/1/number-55.png";
	private static final String IMG_A_2 = IMG_PATH + "0020_CH4M/2/number-5.png";
	private static final String IMG_A_3 = IMG_PATH + "0020_CH4M/3/number-475.png";
	private static final String IMG_A_4 = IMG_PATH + "0020_CH4M/4/number-133.png";
	private static final String IMG_A_5 = IMG_PATH + "0020_CH4M/5/number-8.png";
	private static final String IMG_A_6 = IMG_PATH + "0020_CH4M/6/number-28.png";
	private static final String IMG_A_7 = IMG_PATH + "0020_CH4M/7/number-20.png";
	private static final String IMG_A_8 = IMG_PATH + "0020_CH4M/8/number-22.png";
	private static final String IMG_A_9 = IMG_PATH + "0020_CH4M/9/number-6.png";
	
	private static final String IMG_B_0 = IMG_PATH + "0021_CH4M/0/number-25.png";
	private static final String IMG_B_1 = IMG_PATH + "0021_CH4M/1/number-55.png";
	private static final String IMG_B_2 = IMG_PATH + "0021_CH4M/2/number-5.png";
	private static final String IMG_B_3 = IMG_PATH + "0021_CH4M/3/number-475.png";
	private static final String IMG_B_4 = IMG_PATH + "0021_CH4M/4/number-133.png";
	private static final String IMG_B_5 = IMG_PATH + "0021_CH4M/5/number-8.png";
	private static final String IMG_B_6 = IMG_PATH + "0021_CH4M/6/number-28.png";
	private static final String IMG_B_7 = IMG_PATH + "0021_CH4M/7/number-20.png";
	private static final String IMG_B_8 = IMG_PATH + "0021_CH4M/8/number-22.png";
	private static final String IMG_B_9 = IMG_PATH + "0021_CH4M/9/number-2.png";
	
	
	public static void main(String[] args) throws Exception {
		LeNetMnist model = new LeNetMnist();
		
//		model.trainAndSaveNumbersModel(IMG_PATH, new File(MODEL_FILE_NAME));
		
//		model.trainAndSaveModel(new File(MODEL_FILE_NAME + "2"));
		
		model.loadModel(new File(MODEL_FILE_NAME));
		
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_0))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_1))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_2))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_3))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_4))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_5))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_6))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_7))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_8))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_A_9))));
		
		
		System.out.println("---");
		
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_0))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_1))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_2))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_3))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_4))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_5))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_6))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_7))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_8))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_B_9))));
	}
}
