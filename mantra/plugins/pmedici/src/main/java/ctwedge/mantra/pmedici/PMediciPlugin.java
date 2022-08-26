package ctwedge.mantra.pmedici;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import ctwedge.mantra.model.Model;

/**
 * A very simple plugin.
 */
public class PMediciPlugin extends Plugin {

	public PMediciPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {
		System.out.println("PMediciPlugin.start()");
	}

	@Override
	public void stop() {
		System.out.println("PMediciPlugin.stop()");
	}

	@Extension(ordinal = 1)
	public static class PMediciModel implements Model {

		@Override
		public void loadModelFromPath(String filename) {
			System.out.println("PMedici model!");
		}

	}

}
