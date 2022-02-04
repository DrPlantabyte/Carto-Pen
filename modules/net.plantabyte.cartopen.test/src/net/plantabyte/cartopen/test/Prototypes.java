package net.plantabyte.cartopen.test;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.beans.XMLDecoder;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static net.plantabyte.cartopen.test.Main.print;

public class Prototypes {
	public static void test1() throws Exception{
		var img = ImageIO.read(Prototypes.class.getResource("test-map-img-1.png"));
		final int nColors = 10;
		var shapes = net.plantabyte.drptrace.utils.ImageTracer.traceBufferedImage(img, nColors);
		BufferedImage canvas = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(var shape : shapes) for(var path : shape){
			net.plantabyte.drptrace.utils.BezierPlotter.drawBezier(path, canvas.createGraphics(), Optional.of(new Color(shape.getColor())), Optional.of(new BasicStroke(3)));
		}
		showImg(canvas);
	}
	public static void test2() throws Exception{
		var img = ImageIO.read(Prototypes.class.getResource("test-map-img-1.png"));
		final int[] biome_colors = {0xff898989, 0xff24ff2e, 0xff2438ff, 0xfffaff24, 0xff00ffff};
		final String[] biome_ids = {"mountain","forest","ocean","desert","ice"};
		if(biome_ids.length != biome_colors.length) throw new AssertionError("Bad implementation");
		final Map<Integer, String> biomeMap = new HashMap<>();
		for(int i = 0; i < biome_colors.length; i++){
			biomeMap.put(biome_colors[i], biome_ids[i]);
		}
		final int nColors = 10;
		var shapes = net.plantabyte.drptrace.utils.ImageTracer.traceBufferedImage(img, nColors);
		BufferedImage canvas = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		var colors = new HashSet<Integer>();
		for(var shape : shapes) {
			colors.add(shape.getColor());
			for(var path : shape){
				net.plantabyte.drptrace.utils.BezierPlotter.drawBezier(path, canvas.createGraphics(), Optional.of(new Color(shape.getColor())), Optional.of(new BasicStroke(3)));
			}
		}
		colors.stream().forEach((var i) ->print("0x"+Integer.toHexString(i)));
		//showImg(canvas);
		try(var out = Files.newOutputStream(Paths.get("test2.svg"))) {
			net.plantabyte.drptrace.utils.SVGWriter.writeToSVG(shapes, img.getWidth(), img.getHeight(), out);
		}
		var svg = new StringBuilder();
		svg.append("""
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg width="WWW" height="HHH" id="svgroot" version="1.1" viewBox="0 0 WWW HHH" xmlns="http://www.w3.org/2000/svg">
	<defs
	id="biomeFills">
		<pattern
			id="mountain"
			patternUnits="userSpaceOnUse"
			width="25"
			height="25"
			patternTransform="translate(287,168)">
			<g
				transform="translate(-287,-168)">
				<path
					style="fill:#bfbfbf;fill-opacity:1;stroke:none"
					d="m 287,168 v 25 h 25 v -25 z" />
				<path
					style="fill:none;fill-opacity:1;stroke:#000000;stroke-opacity:1"
					d="m 292,188 3,-8 3,8" />
				<path
					style="fill:none;fill-opacity:1;stroke:#000000;stroke-opacity:1"
					d="m 301,180 3,-6 3,6" />
				<path
					style="fill:none;fill-opacity:1;stroke:#000000;stroke-opacity:1"
					d="m 304,188 2,-3 2,3" />
				<path
					style="fill:none;fill-opacity:1;stroke:#000000;stroke-opacity:1"
					d="m 290,174 2,-3 2,3" />
			</g>
		</pattern>
		<pattern
			id="forest"
			patternUnits="userSpaceOnUse"
			width="25"
			height="25"
			patternTransform="translate(287,168)">
			<g
				transform="translate(-287,-168)">
				<path
					style="fill:#008000;fill-opacity:1;stroke:none"
					d="m 287,168 v 25 h 25 v -25 z" />
			</g>
		</pattern>
		<pattern
			id="desert"
			patternUnits="userSpaceOnUse"
			width="25"
			height="25"
			patternTransform="translate(287,168)">
			<g
				transform="translate(-287,-168)">
				<path
					style="fill:#ffff88;fill-opacity:1;stroke:none"
					d="m 287,168 v 25 h 25 v -25 z" />
			</g>
		</pattern>
		<pattern
			id="ocean"
			patternUnits="userSpaceOnUse"
			width="25"
			height="25"
			patternTransform="translate(287,168)">
			<g
				transform="translate(-287,-168)">
				<path
					style="fill:#4444FF;fill-opacity:1;stroke:none"
					d="m 287,168 v 25 h 25 v -25 z" />
			</g>
		</pattern>
		<pattern
			id="ice"
			patternUnits="userSpaceOnUse"
			width="25"
			height="25"
			patternTransform="translate(287,168)">
			<g
				transform="translate(-287,-168)">
				<path
					style="fill:#9999FF;fill-opacity:1;stroke:none"
					d="m 287,168 v 25 h 25 v -25 z" />
			</g>
		</pattern>
	</defs>
	<g id="mainGroup">
"""
				.replace("WWW", String.valueOf(img.getWidth()))
				.replace("HHH", String.valueOf(img.getHeight()))
		);
		for(var shape : shapes) {
			var id = biomeMap.get(shape.getColor());
			svg.append("<path style=\"stroke:none;fill:url(#{ID})\" ".replace("{ID}", id));
			svg.append("d=\"")
					.append(shape.toSVGPathString())
					.append("\" />\n");
		}
		svg.append("""
  </g>
</svg>""");
		Files.writeString(Paths.get("test2-B.svg"), svg);
	}

	public static void test3() throws Exception {
		var root = new DOMBuilder("svg");
		root
				.setAttribute("width", "600")
				.setAttribute("height", "400")
				.setAttribute("viewbox", "0 0 600 400")
				.setAttribute("xmlns", "http://www.w3.org/2000/svg")
				.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink")
				.setAttribute("xmlns:svg", "http://www.w3.org/2000/svg")
				.appendElement(
						root.newElement("style")
								.setText(".h1 { font: italic 22px serif; }")
				)
				.appendElement(
						root.newElement("defs")
								.appendElement(
										root.newElement("g")
												.setAttribute("id", "mountain1")
												.appendElement(
														root.newElement("path")
																.setAttribute("style", "fill:#AAAAAA;fill-opacity:1;stroke:none")
																.setAttribute("d", "M 0,0 v 40 h 60 v -40 z")
												)
												.appendElement(
														root.newElement("path")
																.setAttribute("style", "fill:none;fill-opacity:1;stroke:#000000;stroke-opacity:1")
																.setAttribute("d", "m 10,30 12,-24 12,24")
												)
								)
								.appendElement(
										root.newElement("text")
												.setAttribute("id", "mountainName1")
												.setAttribute("class", "h1")
												.setText("The Bumpy Mountains")
								)
				)
				.appendElement(
						root.newElement("g")
								.appendElement(
										root.newElement("use")
												.setAttribute("xlink:href","#mountain1")
												.setAttribute("x","200")
												.setAttribute("y","100")
												.setAttribute("width","100%")
												.setAttribute("height","100%")
												.setAttribute("transform","translate(0,0)")
								)
				)
				.appendElement(
						root.newElement("g")
								.appendElement(
										root.newElement("use")
												.setAttribute("id","text1outline")
												.setAttribute("xlink:href","#mountainName1")
												.setAttribute("x","180")
												.setAttribute("y","120")
												.setAttribute("width","100%")
												.setAttribute("height","100%")
												.setAttribute("style","fill:none;stroke:#FFFFFF;stroke-opacity:1;stroke-width:4;stroke-linejoin:round;stroke-linecap:round")
								)
								.appendElement(
										root.newElement("use")
												.setAttribute("id","text1")
												.setAttribute("xlink:href","#mountainName1")
												.setAttribute("x","180")
												.setAttribute("y","120")
												.setAttribute("width","100%")
												.setAttribute("height","100%")
												.setAttribute("style","fill:#000000;fill-opacity:1;stroke:none")
								)
				)
		;
		print(root.toXMLString());
		root.writeToFile(Paths.get("test3A.svg"));
		var m1 = DOMBuilder.fromFile(Paths.get(Prototypes.class.getResource("mountain-1.svg").toURI()));
		root.getFirstElementByName("defs").orElseThrow()
				.appendElement(
						root.newElement("g")
								.setAttribute("id", "m1svg")
								.appendAll(m1.root().getChildElements())
				)
		;
		var m1ViewBox = Arrays.asList(m1.root().element().getAttribute("viewBox").split("[\\s,]+"))
				.stream().map(Float::parseFloat).collect(Collectors.toList()).toArray(new Float[0]); // min-x, min-y, w, h
		root.appendElement(
				root.newElement("use")
						.setAttribute("xlink:href","#m1svg")
						.setAttribute("x","100")
						.setAttribute("y","100")
						.setAttribute("width","100%")
						.setAttribute("height","100%")
						.setAttribute("transform",String.format("translate(%s,%s)", -0.5*m1ViewBox[2], -0.5*m1ViewBox[3]))
		);
		root.writeToFile(Paths.get("test3B.svg"));
	}

	private static void showImg(BufferedImage img){
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)));
	}
}
