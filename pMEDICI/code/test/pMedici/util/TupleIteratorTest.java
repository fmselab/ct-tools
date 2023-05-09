package pMedici.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import ctwedge.ctWedge.CitModel;
import ctwedge.util.ext.Utility;
import pMedici.util.Pair;
import pMedici.util.TupleGenerator;

public class TupleIteratorTest {

	@Test
	public void test() throws IOException {
		CitModel model = Utility.loadModelFromPath("examples/BOOLC_4_Simple.ctw");
		Iterator<List<Pair<Integer, Integer>>> l = TupleGenerator.getAllKWiseCombination(model, 2);
		List<List<Pair<Integer, Integer>>> actualList = new ArrayList<>();
		l.forEachRemaining(actualList::add);
		//System.out.println(actualList);
		//System.out.println(actualList.size());
		assertEquals(12, actualList.size());
	}

}
