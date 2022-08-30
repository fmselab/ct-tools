package mantra.kali;

import org.apache.commons.lang.StringUtils;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;

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

}
