package mantra.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.pf4j.ExtensionPoint;

import mantra.safeelements.SafeQueue;
import mantra.util.Pair;

public interface Model extends ExtensionPoint {

    void loadModelFromPath(String filename);

	SafeQueue<?, ?> getSafeQueue();

	Map<?, List<?>> getElements();

}
