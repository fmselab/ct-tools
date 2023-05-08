package kali.main;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.util.Utility;
import ctwedge.util.TestSuite;
import ctwedge.util.validator.SMTTestSuiteValidator;
import kali.threads.TestBuilder;

public class KaliTestWithValidation {

	@Test
	public void test() throws InterruptedException, IOException {
		TestBuilder.IN_TEST = true;
		String fileName = "examples/bool1.ctw";
		// read the spec		
		CitModel m = Utility.loadModelFromPath(fileName);
		// generate the test with kali
		TestSuite ts = new KALI().testGeneration(2, fileName, m);
		// check the validity
		//		
		SMTTestSuiteValidator validator;
	}

}
