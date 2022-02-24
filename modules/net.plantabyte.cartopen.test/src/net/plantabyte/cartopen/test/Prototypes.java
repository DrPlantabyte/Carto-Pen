package net.plantabyte.cartopen.test;


import net.plantabyte.drptrace.IntMap;
import net.plantabyte.drptrace.PolylineTracer;
import net.plantabyte.drptrace.geometry.Vec2;
import net.plantabyte.drptrace.utils.BufferedImageIntMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static net.plantabyte.cartopen.test.Main.print;

public class Prototypes {
	public static void test1() throws Exception{
		var img = ImageIO.read(Prototypes.class.getResource("test-map-img-1.png"));
		//var shapes = net.plantabyte.drptrace.utils.ImageTracer.traceBufferedImage(img, nColors);
		var bitBuffer = BufferedImageIntMap.fromBufferedImage(img);
		var tracer = new PolylineTracer();
		var shapes = tracer.traceAllShapes(bitBuffer);
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
		var bitBuffer = BufferedImageIntMap.fromBufferedImage(img);
		var tracer = new PolylineTracer();
		var shapes = tracer.traceAllShapes(bitBuffer);
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

	public static void test4() throws Exception {
		final int w = 300; final int h = 200; final int cellSize = 20;
		var svg = new SVGManager(w, h);
		var mountainIDs = new ArrayList<String>(4);
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-1.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-2.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-3.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-4.svg").toURI())));
		var prng = new Random();
		var center = new Vec2(w/2, h/2);
		var r = 50;
		for(double theta = 0; theta < 2*Math.PI; theta += 0.25*Math.PI){
			double size = 20; // make 20 pixels as base size
			var pos = new Vec2(r*Math.cos(theta)+center.x, r*Math.sin(theta)+center.y);
			var scale = new Vec2(0.5+prng.nextDouble(), 0.5+prng.nextDouble());
			var rotation = 90*(prng.nextDouble()-0.5);
			var iconID = svg.placeIcon(mountainIDs.get(prng.nextInt(mountainIDs.size())), size, pos, scale, rotation);
			print(iconID);
		}

		svg.writeToFile(Paths.get("test4.svg"));
	}



	public static void test5() throws Exception {
		// load source color map
		var img = ImageIO.read(Prototypes.class.getResource("test-map-img-1.png"));
		IntMap intMap = BufferedImageIntMap.fromBufferedImage(img);

		final int w = intMap.getWidth();
		final int h = intMap.getHeight();
		final int cellSize = 20;

		// trace source map?
//		var tracer = new PolylineTracer();
//		var shapes = tracer.traceAllShapes(bitBuffer);

		// create SVG manager and load icons
		var svg = new SVGManager(w, h);
		var mountainIDs = new ArrayList<String>(4);
		var bubbleIDs = new ArrayList<String>();
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-1.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-2.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-3.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-4.svg").toURI())));
		bubbleIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("bubble-1.svg").toURI())));

		Map<Integer, String[]> pallete = new HashMap<>();
		pallete.put(0xff898989, mountainIDs.toArray(new String[0]));
		Map<Integer, String> borderStyles  = new HashMap<>();
		borderStyles.put(0xff2438ff, "fill:none;fill-opacity:1;stroke:#000000;stroke-width:2px;stroke-linecap:round");

		// TODO: create decorator data class with pallette, style, decorFrequency, positionJitter, sizeJitter, distortion
		// place icons on map
		var prng = new Random();
		decorateMap(intMap, pallete, borderStyles, svg, cellSize, cellSize, 1.0F, 0.0F, 0.0F, 0F, prng, false);
		// TODO: test hex grid or similar pattern

		svg.writeToFile(Paths.get("test5.svg"));
	}

	private static void decorateMap(IntMap map, Map<Integer, String[]> decoratorPallet, Map<Integer, String> styles, SVGManager svg, float size, float spacing, float decorFrequency, float positionJitter, float sizeJitter, float distortion, Random prng, boolean ignoreMissing){
		/*
vertically stacked hex pattern
 __
/  \__/
\__/  \
/  \__/
		 */
		// TODO: colors and borders

		//final double piOverThree = Math.PI / 3; // 60 degrees
		final double root3over2 = Math.sqrt(3.0)/2.0;
		final double colWidth = spacing * root3over2;
		final double alternatingOffset = spacing * 0.5;
		final double rowHeight = spacing;
		final int numCols = (int)(svg.getWidth() / colWidth) + 1;
		final int numRows = (int)(svg.getWidth() / rowHeight) + 1;
		for(int row = 0; row < numRows; ++row){
			for(int col = 0; col < numCols; col += 2){
				final double x = col * colWidth; final int ix = (int)(x+0.5);
				final double y = row * rowHeight; final int iy = (int)(y+0.5);
				if(!map.isInRange(ix, iy)) continue;
				final int color = map.get(ix, iy);
				final String[] paletteOfIDs = decoratorPallet.get(color);
				if(paletteOfIDs == null || paletteOfIDs.length == 0) {
					// no decorations found for this color!
					if(paletteOfIDs == null && !ignoreMissing) {
						throw new IllegalArgumentException (String.format("No decorators found for color 0x%s", Integer.toHexString(color)));
					}
					continue;
				}
				_place(x, y, paletteOfIDs, size, spacing, decorFrequency, positionJitter, sizeJitter, distortion, prng, svg);
			}
			for(int col = 1; col < numCols; col += 2){
				final double x = col * colWidth; final int ix = (int)(x+0.5);
				final double y = row * rowHeight + alternatingOffset; final int iy = (int)(y+0.5);
				if(!map.isInRange(ix, iy)) continue;
				final int color = map.get(ix, iy);
				final String[] paletteOfIDs = decoratorPallet.get(color);
				if(paletteOfIDs == null || paletteOfIDs.length == 0) {
					// no decorations found for this color!
					if(paletteOfIDs == null && !ignoreMissing) {
						throw new IllegalArgumentException (String.format("No decorators found for color 0x%s", Integer.toHexString(color)));
					}
					continue;
				}
				_place(x, y, paletteOfIDs, size, spacing, decorFrequency, positionJitter, sizeJitter, distortion, prng, svg);
			}
		}
	}

	private static void _place(double x, double y, String[] paletteOfIDs, float size, float spacing, float decorFrequency, float positionJitter, float sizeJitter, float distortion, Random prng, SVGManager svg){
		if(prng.nextFloat() > decorFrequency) return;
		x = x + spacing * (plusOrMinusOne(prng) * positionJitter);
		y = y + spacing * (plusOrMinusOne(prng) * positionJitter);
		final float iconSize = size + size * sizeJitter * plusOrMinusOne(prng);
		final var iconProportions = new Vec2(1+distortion*plusOrMinusOne(prng),1+distortion*plusOrMinusOne(prng));
		final String iconID = paletteOfIDs[prng.nextInt(paletteOfIDs.length)];
		final double iconRotation = 0;
		svg.placeIcon(iconID, iconSize,new Vec2(x, y), iconProportions, iconRotation);
	}
	private static float plusOrMinusOne(Random prng){
		return (2F*prng.nextFloat() - 1F);
	}

	private static void showImg(BufferedImage img){
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)));
	}
}
