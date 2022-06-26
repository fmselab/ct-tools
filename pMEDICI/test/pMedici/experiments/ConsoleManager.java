package pMedici.experiments;

import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleManager {

	PrintStream originStream;

	/**
	 * @param originStream the stream which is intended to be activated/deactivated
	 */
	public ConsoleManager(PrintStream originStream) {
		this.originStream = originStream;
	}

	/**
	 * Deactivate console printing
	 */
	public void consolePrintingOff() {

		PrintStream emptyStream = new PrintStream(new OutputStream() {
			public void write(int b) {
				// NO-OP
			}
		});

		System.setOut(emptyStream);
	}

	/**
	 * Activate console printing
	 */
	public void consolePrintingOn() {
		System.setOut(this.originStream);
	}

}
