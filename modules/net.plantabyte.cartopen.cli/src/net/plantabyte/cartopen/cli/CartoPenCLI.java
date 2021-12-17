package net.plantabyte.cartopen.cli;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "carto-pen", mixinStandardHelpOptions = true, version = "carto-pen 0.0.0",
		description = "Reads .png/.gif color-coded biome maps and outputs a Tolkien-esque SVG map.")
public class CartoPenCLI implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		// This si the real main method
		throw new UnsupportedOperationException("Not implemented yet :(");
	}

	public static void main(String[] args){
		int exitCode = new CommandLine(new CartoPenCLI()).execute(args);
		System.exit(exitCode);
	}

}
