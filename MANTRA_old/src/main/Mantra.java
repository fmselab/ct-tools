package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ctwedge.ctWedge.CitModel;
import ctwedge.generator.util.Utility;
import mantra.safeelements.SafeQueue;
import mantra.model.Model;


public class Mantra {
	
	@Argument
	private List<String> arguments = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		new Mantra().doMain(args);
	}
	
	@SuppressWarnings("deprecation")
	public void doMain(String[] args) throws IOException, InterruptedException {
		
		CmdLineParser parser = new CmdLineParser(this);
		Integer strength = null;
		try {
			// Parse the arguments.
			parser.parseArgument(args);
			// After parsing arguments, you should check if enough arguments are given.
			if (arguments.isEmpty())
				throw new CmdLineException(parser, "No argument is given");
			// Check the arguments
			try {
				strength = Integer.parseInt(arguments.get(0));
				if (strength < 2)
					throw new CmdLineException(parser, "strength cannot be less than 2");
				// TODO: We should check that the strength is lower than the number of parameters of the model
			} catch (NumberFormatException nf) {
				throw new CmdLineException(parser, "strength must be a number >= 2 " + nf.getLocalizedMessage());
			}
			// Check that the file containing the model actually exists
			if (!(Files.exists(Paths.get(arguments.get(1))))) {
				throw new CmdLineException(parser, "file " + arguments.get(1) + " not found");
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("kali [options...] strength file.ctw");
			// Print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			return;
		}
		String fileName = arguments.get(1);
		
		// Read the combinatorial model and get the MDD representing the model without constraints
		Model m;

		int nCovered = 0;
		int totTuples = 0;

		// Get current time
		long start = System.currentTimeMillis();

	}

}
