package com.bsiag.anagnostes;

import java.io.File;

public class App {

	private static final String MODEL_FILE_NAME = "C:\\\\tmp\\model.zip";
	
	public static void main(String[] args) throws Exception {
		
		
		
		new LeNetMnist().trainAndSaveModel(new File(MODEL_FILE_NAME));
	}
}
