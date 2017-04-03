package com.bsiag.anagnostes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsiag.anagnostes.configuration.ConfigurationFactory;

public class NeuralNetwork {
	private static final Logger log = LoggerFactory.getLogger(NeuralNetwork.class);

	private MultiLayerNetwork m_model;
	private DataSetIterator m_trainSet;
	private DataSetIterator m_testSet;
	private int m_epochs = 1;

	public static class Builder {
		
		private NeuralNetwork m_network;
		
		public Builder() {
			m_network = new NeuralNetwork();
		}
		
		public Builder configuration(MultiLayerConfiguration configuration) {
			m_network.m_model = new MultiLayerNetwork(configuration);
			m_network.m_model.init();
			return this;
		}
		
		public Builder trainSetIterator(DataSetIterator trainSet) {
			m_network.m_trainSet = trainSet;
			return this;
		}
		
		public Builder testSetIterator(DataSetIterator testSet) {
			m_network.m_testSet = testSet;
			return this;
		}
		
		public Builder leNetConfiguration() {
			return configuration(ConfigurationFactory.configuration());
		}
		
		public Builder numbersTrainDataSetIterator(String numbersBaseFolder) {
			return trainSetIterator(ConfigurationFactory.numbersTrainDataSetIterator(numbersBaseFolder));
		}
		
		public Builder numbersTrainSetIterator() {
			return trainSetIterator(ConfigurationFactory.numbersTrainDataSetIterator());
		}
		
		public Builder numbersTestDataSetIterator(String numbersBaseFolder) {
			return testSetIterator(ConfigurationFactory.numbersTestDataSetIterator(numbersBaseFolder));
		}
		
		public Builder numbersTestSetIterator() {
			return testSetIterator(ConfigurationFactory.numbersTestDataSetIterator());
		}
		
		public Builder mnistTrainSetIterator() {
			return trainSetIterator(ConfigurationFactory.mnistTrainSetIterator());
		}
		
		public Builder mnistTestSetIterator() {
			return testSetIterator(ConfigurationFactory.mnistTestSetIterator());
		}
		
		public Builder logScore() {
			if(!m_network.isModelInitialized()) {
				throw new RuntimeException("Model is not yet initialized. You can initialized the model first with e.g. configuration() or loadFromFile()");
			}
			
			m_network.setListeners(new ScoreIterationListener(1));
			return this;
		}
		
		public Builder logProcess() {
			if(!m_network.isModelInitialized()) {
				throw new RuntimeException("Model is not yet initialized. You can initialized the model first with e.g. configuration() or loadFromFile()");
			}
			
			m_network.setListeners(new TrainProgressIterationListener(1));
			return this;
		}
		
		public Builder epochs(int epochs) {
			m_network.m_epochs = epochs;
			return this;
		}
		
		public NeuralNetwork fromFile(File modelFile) {
			m_network.loadModel(modelFile);
			return m_network;
		}
		
		public NeuralNetwork build() {
			return m_network;
		}
	}

	public void store(File file)  {
		try {
			ModelSerializer.writeModel(m_model, file, true);
		} catch (IOException e) {
			throw new RuntimeException("Coudln't store model: " + e.getMessage(), e);
		}
	}

	public boolean isModelInitialized() {
		return m_model != null;
	}
	
	public void setListeners(IterationListener iterationListener) {
		m_model.setListeners(iterationListener);		
	}

	public void loadModel(File file) {
		try {
			m_model = ModelSerializer.restoreMultiLayerNetwork(file);
		} catch (IOException e) {
			log.error("couldn't load model", e);
		}
	}
	
	public void train() {
		if(m_trainSet == null) {
			throw new RuntimeException("No train data set iterator specified");
		}
		
		if(m_testSet == null) {
			log.warn("No test data set iterator specified. Model will not be evaluated.");
		}
		
		for (int i = 0; i < m_epochs; i++) {
			m_model.fit(m_trainSet);
			log.info("*** Completed epoch {} ***", i);

			if (m_testSet != null) {
				evaluate();
			}
		}
	}

	public void evaluate() {
		log.info("Evaluate model....");
		Evaluation eval = new Evaluation(ConfigurationFactory.NUM_OUTPUTS);
		while (m_testSet.hasNext()) {
			DataSet ds = m_testSet.next();
			INDArray output = m_model.output(ds.getFeatureMatrix(), false);
			eval.eval(ds.getLabels(), output);
		}
		log.info(eval.stats());
		m_testSet.reset();
	}

	public Output output(String fileName) {
		return output(new File(fileName));
	}
	
	public Output output(File imageFile) {
		try {
			BufferedImage image = ImageIO.read(imageFile);
			return output(image);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read image: " + imageFile, e);
		}
	}
	
	public Output output(BufferedImage image) {
		float[] normalizeImage = normalizeImage(image);

		INDArray input = Nd4j.create(normalizeImage);
		INDArray output = m_model.output(input);
		int result = getLabel(output);

		return new Output(output.getDouble(result), String.valueOf(result).charAt(0));
	}

	private int getLabel(INDArray output) {
		int maxValueIndex = 0;
		for (int i = 1; i < ConfigurationFactory.NUM_OUTPUTS; i++) {
			if (output.getDouble(i) > output.getDouble(maxValueIndex)) {
				maxValueIndex = i;
			}
		}
		return maxValueIndex;
	}

	private float[] normalizeImage(BufferedImage image) {
		float[] normalizedRawData;
		try {
			normalizedRawData = Normalizer.transformToMnsitIteratorFormat(image);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't normalize the image", e);
		}
		return normalizedRawData;
	}
}
