package mantra.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.pf4j.ExtensionPoint;

import ctwedge.ctWedge.Parameter;
import ctwedge.util.Pair;
import mantra.util.Order;

public interface Model extends ExtensionPoint {

	void loadModelFromPath(String filename) throws InterruptedException, IOException;

	Map<Object, List<Object>> getElements(Order order);

	int getNParams();

	boolean getUseConstraints();

	EList<Parameter> getParameters();

	void translateOutputToString(HashSet<String> tests);

	String printTuple(Vector<Pair<Object, Object>> tuple);
}