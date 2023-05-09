package pMedici.main;

import static org.junit.Assert.*;

import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.junit.Test;

public class TestUIError {

	@Test
	public void test() {
		System.out.println(EcorePlugin.INSTANCE.getString("_UI_DiagnosticRoot_diagnostic"));		
	}

}
