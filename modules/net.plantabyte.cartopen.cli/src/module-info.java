module net.plantabyte.cartopen.cli {
	exports net.plantabyte.cartopen.cli;
	requires net.plantabyte.cartopen;
	requires net.plantabyte.drptrace;
	requires net.plantabyte.drptrace.utils;
	requires info.picocli;
	requires java.base;
	requires java.xml;
	requires java.desktop;

	opens net.plantabyte.cartopen.cli to info.picocli;
}
