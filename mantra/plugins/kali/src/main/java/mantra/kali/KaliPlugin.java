package mantra.kali;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;

import mantra.model.Model;
import mantra.safeelements.SafeQueue;

import org.pf4j.Extension;
import org.pf4j.Plugin;

public class KaliPlugin extends Plugin {

	public KaliPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {
		System.out.println("KaliPlugin.start()");
		// for testing the development mode
		if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
			System.out.println(StringUtils.upperCase("KaliPlugin"));
		}
	}

	@Override
	public void stop() {
		System.out.println("KaliPlugin.stop()");
	}

	@Extension
	public static class KaliModel implements Model {

		@Override
		public void loadModelFromPath(String filename) {
			System.out.println("Kali model!");
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
