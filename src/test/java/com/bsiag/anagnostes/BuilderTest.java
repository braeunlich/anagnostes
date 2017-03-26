package com.bsiag.anagnostes;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

public class BuilderTest {

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
	
	public static final String USER_HOME = System.getProperty("user.home");
	public static final String SEPARATOR = File.separator;
	
	@Test
	public void loadFromFile() {
		NeuralNetwork model = new NeuralNetwork.Builder()
				.fromFile(new File(MODEL_FILE_NAME));

		assertNotNull(model);
		
		printTestSetAOutput(model);
		printTestSetBOutput(model);
	}
	
	@Test
	public void buildAndTrain() {
		NeuralNetwork model = new NeuralNetwork.Builder()
				.leNetConfiguration()
				.numbersTrainDataSetIterator(IMG_PATH)
				.numbersTestDataSetIterator(IMG_PATH)
				.epochs(6)
				.build();
		
		model.train();
		
		model.store(new File(MODEL_FILE_NAME + "2"));		
	}
	
	public static void main(String[] args) {
		NeuralNetwork model = new NeuralNetwork.Builder()
				.numbersTestDataSetIterator(IMG_PATH)
				.fromFile(new File(MODEL_FILE_NAME));
		
		model.evaluate();
	}
	
	private void printTestSetAOutput(NeuralNetwork model) {
		System.out.println("\nArbitrary test set examples (Person A): ");
		
		model.output(IMG_A_0).print();
		model.output(IMG_A_1).print();
		model.output(IMG_A_2).print();
		model.output(IMG_A_3).print();
		model.output(IMG_A_4).print();
		model.output(IMG_A_5).print();
		model.output(IMG_A_6).print();
		model.output(IMG_A_7).print();
		model.output(IMG_A_8).print();
		model.output(IMG_A_9).print();
	}
	
	private void printTestSetBOutput(NeuralNetwork model) {
		System.out.println("\nArbitrary test set examples (Person B): ");
		
		model.output(IMG_B_0).print();
		model.output(IMG_B_1).print();
		model.output(IMG_B_2).print();
		model.output(IMG_B_3).print();
		model.output(IMG_B_4).print();
		model.output(IMG_B_5).print();
		model.output(IMG_B_6).print();
		model.output(IMG_B_7).print();
		model.output(IMG_B_8).print();
		model.output(IMG_B_9).print();
	}
}
