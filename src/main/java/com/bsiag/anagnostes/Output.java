package com.bsiag.anagnostes;

public class Output {

	private double m_confidence;
	private char m_value;

	public Output(double confidence, char value) {
		this.m_confidence = confidence;
		this.m_value = value;
	}

	public double getConfidence() {
		return m_confidence;
	}

	public void setConfidence(double confidence) {
		this.m_confidence = confidence;
	}

	public char getCharacter() {
		return m_value;
	}

	public void setCharacter(char character) {
		this.m_value = character;
	}
	
	@Override
	public String toString() {
		return "Recognized character: '" + getCharacter() + "', confidence: " + getConfidence();
	}
	
	public void print() {
		System.out.println(this);
	}
}
