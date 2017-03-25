package com.bsiag.anagnostes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.deeplearning4j.datasets.fetchers.BaseDataFetcher;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class NumbersDataFetcher extends BaseDataFetcher {
	private static final long serialVersionUID = 1L;

	private static final String FILE_REGEX = "^number-\\d+.png$";
	private static final String FOLDER_REGEX = "^\\d{4}_\\S{4}$";

	private final String m_baseFolder;
	private List<String> m_testFolderNames = Arrays.asList("0020_CH4M", "0021_CH4M");
	private final boolean m_train;
	private List<String> m_labels = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

	private List<Entry<String, String>> m_allFileNames;

	static class Entry<K, V> implements Map.Entry<K, V> {
		final K key;
		V value;

		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public final K getKey() {
			return key;
		}

		public final V getValue() {
			return value;
		}

		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
	}

	public NumbersDataFetcher(final String baseFolder) {
		this(baseFolder, true);
	}

	public NumbersDataFetcher(final String baseFolder, boolean train) {
		m_baseFolder = baseFolder.endsWith(File.separator) ? baseFolder : baseFolder + File.separator;
		m_train = train;

		numOutcomes = m_labels.size();
		inputColumns = getNumExamples();
		totalExamples = getNumExamples();

		m_allFileNames = getAllFileNames();
	}

	private List<Entry<String, String>> getAllFileNames() {
		List<Entry<String, String>> allFileNames = new ArrayList<>();
		for (String folder : getFolderNames()) {
			for (String label : m_labels) {
				String[] imageFileNames = new File(m_baseFolder + folder + File.separator + label)
						.list(new RegexFileFilter(FILE_REGEX));
				for (String imageFileName : imageFileNames) {
					allFileNames.add(new Entry<String, String>(label,
							m_baseFolder + folder + File.separator + label + File.separator + imageFileName));
				}
			}
		}
		return allFileNames;
	}

	public NumbersDataFetcher(final String baseFolder, boolean train, final List<String> testFolderNames) {
		this(baseFolder, train);
		m_testFolderNames = testFolderNames;
	}

	public int getNumExamples() {
		int numExamples = 0;
		for (String folderName : getFolderNames()) {
			numExamples += getNumExamples(folderName);
		}
		return numExamples;
	}

	private int getNumExamples(String folder) {
		int numExamples = 0;
		for (String label : m_labels) {
			numExamples += new File(m_baseFolder + folder + File.separator + label)
					.list(new RegexFileFilter(FILE_REGEX)).length;
		}
		return numExamples;
	}

	@Override
	public void fetch(int numExamples) {
		float[][] featureData = new float[numExamples][0];
		float[][] labelData = new float[numExamples][0];

		int examplesRead = 0;

		for (; examplesRead < numExamples; examplesRead++) {
			if (cursor + examplesRead >= m_allFileNames.size()) {
				break;
			}
			Entry<String, String> entry = m_allFileNames.get(cursor + examplesRead);

			featureData[examplesRead] = imageFileNameToMnsitFormat(entry.getValue());
			labelData[examplesRead] = toLabelArray(entry.getKey());
		}
		cursor += examplesRead;

		INDArray features = Nd4j.create(featureData);
		INDArray labels = Nd4j.create(labelData);
		curr = new DataSet(features, labels);
	}

	private float[] toLabelArray(String label) {
		float[] labels = new float[m_labels.size()];
		labels[Integer.parseInt(label)] = 1F;
		return labels;
	}

	private float[] imageFileNameToMnsitFormat(String imageFileName) {
		BufferedImage image;
		try {
			image = ImageIO.read(new File(imageFileName));
			return Normalizer.transformToMnsitIteratorFormat(image);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't read image: " + imageFileName);
		}
	}

	private List<String> getFolderNames() {
		return m_train ? getTrainFolderNames() : getTestFolderNames();
	}

	private List<String> getTestFolderNames() {
		return m_testFolderNames;
	}

	private List<String> getTrainFolderNames() {
		String[] array = new File(m_baseFolder).list(new RegexFileFilter(FOLDER_REGEX));
		List<String> list = new ArrayList<>(Arrays.asList(array));
		list.removeAll(m_testFolderNames);
		return list;
	}

}
