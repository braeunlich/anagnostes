package com.bsiag.anagnostes;

public class NumbersEvalResult {

	private double m_confidence;
	private char m_character;

	public NumbersEvalResult(double confidence, char character) {
		this.m_confidence = confidence;
		this.m_character = character;
	}

	public double getConfidence() {
		return m_confidence;
	}

	public void setConfidence(double confidence) {
		this.m_confidence = confidence;
	}

	public char getCharacter() {
		return m_character;
	}

	public void setCharacter(char character) {
		this.m_character = character;
	}
}
