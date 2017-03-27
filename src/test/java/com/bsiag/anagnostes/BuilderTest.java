package com.bsiag.anagnostes;

import static org.junit.Assert.assertNotNull;
import static com.bsiag.anagnostes.TestConstants.*;

import java.io.File;

import org.junit.Test;

public class BuilderTest {
	
	@Test
	public void loadFromFile() {
		NeuralNetwork model = new NeuralNetwork.Builder()
				.fromFile(MODEL_NUMBERS_FILE);

		assertNotNull(model);
		
		printTestSetAOutput(model);
		printTestSetBOutput(model);
	}
	
	@Test
	public void buildAndTrain() {
		NeuralNetwork model = new NeuralNetwork.Builder()
				.leNetConfiguration()
				.numbersTrainDataSetIterator()
				.numbersTestDataSetIterator()
				.epochs(6)
				.build();
		
		model.train();
		model.store(new File(USER_HOME + SEPARATOR + MODEL_NUMBERS_ZIP_NAME));		
	}
	
	public static void main(String[] args) {
		NeuralNetwork model = new NeuralNetwork.Builder()
				.numbersTestDataSetIterator()
				.fromFile(MODEL_NUMBERS_FILE);
		
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
