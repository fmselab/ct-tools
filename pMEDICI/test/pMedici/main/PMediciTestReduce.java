package pMedici.main;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.TestSuite;
import pMedici.safeelements.TestContext;

public class PMediciTestReduce {

	@Test
	public void test() throws IOException, InterruptedException {
		TestContext.IN_TEST = true;
		PMedici pMedici = new PMedici();
		pMedici.verb = true;
		ArrayList<String> testsuite = pMedici.generateTests("examples\\CTComp\\BOOLC_0.ctw",2);
		convertToTestSuite(testsuite, pMedici.model);
		
	}

	
	TestSuite convertToTestSuite(ArrayList<String> testsuite, CitModel model) {
		StringBuffer tsAsCSV = new StringBuffer();
		//add the parameters
		List<String> parameters = model.getParameters().stream().map(p -> p.getName())
				   .collect(Collectors.toList());
		tsAsCSV.append(String.join(",", parameters));
		for(String t: testsuite) {
			System.out.println(t);
		}
		System.out.println(tsAsCSV);
		TestSuite ts = new TestSuite(tsAsCSV.toString(), model);
		return ts;
	}
}
