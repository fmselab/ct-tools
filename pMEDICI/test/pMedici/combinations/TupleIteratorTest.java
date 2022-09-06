package pMedici.combinations;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.medici.MediciCITGenerator;
import ctwedge.generator.util.Utility;
import pMedici.util.Operations;
import pMedici.util.Pair;
import pMedici.util.TestModel;

public class TupleIteratorTest {

	@Test
	public void test() throws IOException {
		CitModel model = Utility.loadModelFromPath("examples/BOOLC_4_Simple.ctw");
		MediciCITGenerator gen = new MediciCITGenerator();
		MediciCITGenerator.OUTPUT_ON_STD_OUT_DURING_TRANSLATION = false;
		String mediciModel = gen.translateModel(model, false);
		TestModel tm = Operations.readModelFromReader(new BufferedReader(new StringReader(mediciModel)));
		Iterator<List<Pair<Integer, Integer>>> l = TupleGenerator.getAllKWiseCombination(tm);
		List<List<Pair<Integer, Integer>>> actualList = new ArrayList<>();
		l.forEachRemaining(actualList::add);
		//System.out.println(actualList);
		//System.out.println(actualList.size());
		assertEquals(12, actualList.size());
	}

}
