package net.plantabyte.cartopen.test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
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
	private static void showImg(BufferedImage img){
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)));
	}
}
