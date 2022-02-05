package net.plantabyte.cartopen.test;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SVGManager {

	final DOMBuilder dom;
	final int width;
	final int height;
	public SVGManager(int w, int h) {
		width = w;
		height = h;
		dom = new DOMBuilder("svg");
		dom
				.setAttribute("width", "600")
				.setAttribute("height", "400")
				.setAttribute("viewbox", "0 0 600 400")
				.setAttribute("xmlns", "http://www.w3.org/2000/svg")
				.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink")
				.setAttribute("xmlns:svg", "http://www.w3.org/2000/svg")
				.setAttribute("xmlns:inkscape", "http://www.inkscape.org/namespaces/inkscape")
				.setAttribute("xmlns:sodipodi", "http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd")
				.appendElement(
						dom.newElement("style")
								.setText(".h1 { font: italic 22px serif; }")
				)
				.appendElement(
						dom.newElement("defs")
				)
		;
	}

	public String importAsDef(Path svgSrc) throws IOException {
		// TODO: add in defs as group and return ID
	}






}
