package com.bsiag.anagnostes;

import java.io.File;

import javax.imageio.ImageIO;

public class App {


	public static final String DEBUG_PATH = "/home/chb/tmp/";
	
	private static final String MODEL_FILE_NAME = "/home/chb/tmp/model.zip";
	
	private static final String IMG_PATH = "/home/chb/git/numbers/";
	
	
	private static final String IMG_0 = IMG_PATH + "0003_CH3M/0/number-25.png";
	private static final String IMG_1 = IMG_PATH + "0003_CH3M/1/number-55.png";
	private static final String IMG_2 = IMG_PATH + "0003_CH3M/2/number-5.png";
	private static final String IMG_3 = IMG_PATH + "0003_CH3M/3/number-475.png";
	private static final String IMG_4 = IMG_PATH + "0003_CH3M/4/number-133.png";
	private static final String IMG_5 = IMG_PATH + "0003_CH3M/5/number-8.png";
	private static final String IMG_6 = IMG_PATH + "0003_CH3M/6/number-28.png";
	private static final String IMG_7 = IMG_PATH + "0003_CH3M/7/number-20.png";
	private static final String IMG_8 = IMG_PATH + "0003_CH3M/8/number-22.png";
	private static final String IMG_9 = IMG_PATH + "0003_CH3M/9/number-2.png";
	
	
	public static void main(String[] args) throws Exception {
		LeNetMnist model = new LeNetMnist();
		
//		model.trainAndSaveModel(new File(MODEL_FILE_NAME + "2"));
		
		model.loadModel(new File(MODEL_FILE_NAME));
		
		System.out.println(model.eval(ImageIO.read(new File(IMG_1))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_2))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_3))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_4))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_5))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_6))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_7))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_8))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_9))));
		System.out.println(model.eval(ImageIO.read(new File(IMG_0))));
	}
}
