package kali.main;

import java.io.IOException;

import org.junit.Test;
import org.sosy_lab.common.configuration.InvalidConfigurationException;

public class SMTTestTest {

	/**
	 * Using main as test with JUnit has some problems in threads management
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		KALI.main(new String[]{"2","examples/bool2.ctw"});
		KALI.main(new String[]{"-n","64", "2","examples/bool2.ctw"});
	}
	
	@Test
	public void testMain() throws IOException, InterruptedException, InvalidConfigurationException {
		KALI.main(new String[]{"2","examples/bool2.ctw"});
	}

}
