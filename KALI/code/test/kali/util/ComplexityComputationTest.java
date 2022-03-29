package kali.util;

import java.io.File;

import org.junit.Test;

import kali.main.SMTTestBatchExecutor;

public class ComplexityComputationTest {

	@Test
	public void test() {
		// Find all the files in the test folder
	    File file = new File(SMTTestBatchExecutor.BASE_FOLDER);
	    File[] fileList = file.listFiles();
	    for(File str : fileList) {	      	
	    	// Handle only CTW files
	    	if(str.getAbsolutePath().endsWith(".ctw")) {
	    		ConstraintComplexityEvaluator cce = new ConstraintComplexityEvaluator();
	    		System.out.println(str.getName() + " - " + cce.evaluateConstraintComplexity(str.getAbsolutePath()));
	    	}
	    }
	}

}
