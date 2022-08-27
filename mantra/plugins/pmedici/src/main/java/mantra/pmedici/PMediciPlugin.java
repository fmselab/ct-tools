package mantra.pmedici;

import java.util.List;
import java.util.Map;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import mantra.model.Model;
import mantra.safeelements.SafeQueue;

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

		@Override
		public SafeQueue<?, ?> getSafeQueue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<?, List<?>> getElements() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
