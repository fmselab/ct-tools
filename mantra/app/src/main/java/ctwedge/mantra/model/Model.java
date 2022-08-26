package ctwedge.mantra.model;

import org.pf4j.ExtensionPoint;

import ctwedge.mantra.safeelements.SafeQueue;

public interface Model extends ExtensionPoint {

    void loadModelFromPath(String filename);

	SafeQueue<?, ?> getSafeQueue();

}
