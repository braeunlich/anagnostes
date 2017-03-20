package com.bsiag.anagnostes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadLocalRandom;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeNetMnist {
	private static final Logger log = LoggerFactory.getLogger(LeNetMnist.class);

	private static final int NUM_OUTPUTS = 10;
	private static final int NUM_CHANNELS = 1;
	private static final int NUM_ITERATIONS = 1;
	private static final int SEED = 123;
	private static final int BATCH_SIZE = 64;
	private static final int NUM_EPOCHS = 1;

	private MultiLayerNetwork m_model;

	public void trainAndSaveModel(File file) throws Exception {
		
		log.info("Load data....");
		DataSetIterator mnistTrain = new MnistDataSetIterator(BATCH_SIZE, true, 12345);
		DataSetIterator mnistTest = new MnistDataSetIterator(BATCH_SIZE, false, 12345);

		log.info("Build model....");
		buildModel();

		log.info("Train model....");
		train(mnistTrain, mnistTest, m_model);

		log.info("Storing model...");
		store(file);
	}

	public void store(File file) throws IOException, URISyntaxException {
		ModelSerializer.writeModel(m_model, file, true);
	}

	public void buildModel() {
		m_model = new MultiLayerNetwork(getConfiguration());
		m_model.init();
	}

	public void loadModel(File file) {
		try {
			m_model = ModelSerializer.restoreMultiLayerNetwork(file);
		} catch (IOException e) {
			log.error("couldn't load model", e);
		}
	}

	public void train(DataSetIterator mnistTrain, DataSetIterator mnistTest, MultiLayerNetwork model) {
		model.setListeners(new ScoreIterationListener(1));
		for (int i = 0; i < NUM_EPOCHS; i++) {
			model.fit(mnistTrain);
			log.info("*** Completed epoch {} ***", i);

			log.info("Evaluate model....");
			Evaluation eval = new Evaluation(NUM_OUTPUTS);
			while (mnistTest.hasNext()) {
				DataSet ds = mnistTest.next();
				INDArray output = model.output(ds.getFeatureMatrix(), false);
				eval.eval(ds.getLabels(), output);
			}
			log.info(eval.stats());
			mnistTest.reset();
		}
	}

	protected MultiLayerConfiguration getConfiguration() {
		/*
		 * Regarding the .setInputType(InputType.convolutionalFlat(28,28,1))
		 * line: This does a few things. (a) It adds preprocessors, which handle
		 * things like the transition between the convolutional/subsampling
		 * layers and the dense layer (b) Does some additional configuration
		 * validation (c) Where necessary, sets the nIn (number of input
		 * neurons, or input depth in the case of CNNs) values for each layer
		 * based on the size of the previous layer (but it won't override values
		 * manually set by the user) InputTypes can be used with other layer
		 * types too (RNNs, MLPs etc) not just CNNs. For normal images (when
		 * using ImageRecordReader) use
		 * InputType.convolutional(height,width,depth). MNIST record reader is a
		 * special case, that outputs 28x28 pixel grayscale (nChannels=1)
		 * images, in a "flattened" row vector format (i.e., 1x784 vectors),
		 * hence the "convolutionalFlat" input type used here.
		 */
		return new NeuralNetConfiguration.Builder().seed(SEED).iterations(NUM_ITERATIONS).regularization(true)
				.l2(0.0005).
				/*
				 * Uncomment the following for learning decay and bias
				 */
				learningRate(.01).// biasLearningRate(0.02).
				// learningRateDecayPolicy(LearningRatePolicy.Inverse).lrPolicyDecayRate(0.001).lrPolicyPower(0.75).
				weightInit(WeightInit.XAVIER).optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(Updater.NESTEROVS).momentum(0.9).list()
				.layer(0,
						new ConvolutionLayer.Builder(5, 5).nIn(NUM_CHANNELS).stride(1, 1).nOut(20)
								.activation(Activation.IDENTITY).build())
				.layer(1,
						new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2).stride(2, 2)
								.build())
				.layer(2,
						new ConvolutionLayer.Builder(5, 5).stride(1, 1).nOut(50).activation(Activation.IDENTITY)
								.build())
				.layer(3,
						new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2).stride(2, 2)
								.build())
				.layer(4, new DenseLayer.Builder().activation(Activation.RELU).nOut(500).build())
				.layer(5,
						new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nOut(NUM_OUTPUTS)
								.activation(Activation.SOFTMAX).build())
				.setInputType(InputType.convolutionalFlat(28, 28, 1)).backprop(true).pretrain(false).build();
	}
	
	public NumbersEvalResult eval(BufferedImage image) {
		return new NumbersEvalResult(
				(ThreadLocalRandom.current().nextInt(50, 100) / 100d),
				String.valueOf(ThreadLocalRandom.current().nextInt(0, 10)).charAt(0));
	}
}
