package net.plantabyte.cartopen.test;


import com.grack.nanojson.JsonParserException;
import net.plantabyte.drptrace.*;
import net.plantabyte.drptrace.geometry.*;
import net.plantabyte.drptrace.intmaps.ZOrderIntMap;
import net.plantabyte.drptrace.utils.BufferedImageIntMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static net.plantabyte.cartopen.test.Main.print;
import static java.lang.Math.max;
import static java.lang.Math.min;

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
		var fileList = ("borial-forest-1.svg;desert-1.svg;desert-2.svg;desert-3.svg;desert-4.svg;desert-5.svg;"
				+"jungle-1.svg;jungle-2.svg;jungle-3.svg;jungle-4.svg;mountain-1.svg;mountain-2.svg;mountain-3.svg;"
				+"mountain-4.svg;plains-1.svg;plains-2.svg;sand-sea-1.svg;sand-sea-2.svg;sand-sea-3.svg;swamp-1.svg;"
				+"swamp-2.svg;swamp-3.svg;temperate-forest-1.svg;temperate-forest-2.svg;wasteland-1.svg;wasteland-2.svg;"
				+"wasteland-3.svg;wasteland-4.svg;wasteland-5.svg;water-1.svg;water-2.svg").split(";");
		print(fileList);
		var rsrcMap = new HashMap<String, List<URI>>();
		for(vas s : fileList){
			rsrcMap.put()
		}

		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-1.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-2.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-3.svg").toURI())));
		mountainIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("mountain-4.svg").toURI())));
		bubbleIDs.add(svg.importAsDef(Paths.get(Prototypes.class.getResource("bubble-1.svg").toURI())));

		Map<Integer, Decorator> pallete = new HashMap<>();
		pallete.put(0xff898989, new Decorator(mountainIDs,"", 1F, 1F, 1F, 0F, 0F, 0F));
		pallete.put(0xff2438ff, new Decorator(bubbleIDs,"fill:#0000ff;fill-opacity:1;stroke:#000000;stroke-width:2px;stroke-linecap:round", 1F, 1.5F, 0.5F, 0F, 0F, 0F));

		// place icons on map
		var prng = new Random();
		decorateMap(intMap, pallete, svg, cellSize, prng);

		svg.writeToFile(Paths.get("test5.svg"));
	}

	private static void decorateMap(IntMap map, Map<Integer, Decorator> decoratorPallet, SVGManager svg, float size, Random prng){
		/*
vertically stacked hex pattern
 __    __
/  \__/  \
\__/  \__/
/  \__/  \
\__/  \__/
/  \__/  \
		 */


		//final double piOverThree = Math.PI / 3; // 60 degrees
		final double root3over2 = Math.sqrt(3.0)/2.0;
		// Using TreeSet to sort icons front (bottom) to back (top)
		final var sortedPlacement = new TreeSet<IconPlacement>();
		//var tracer = new PolylineTracer();
		var tracer = new IntervalTracer(1+(int)size);
		for(var kv : decoratorPallet.entrySet()) {
			final var targetColor = kv.getKey();
			final var decorator = kv.getValue();
			print(kv.getValue().toJSON());
			try {
				print(Decorator.fromJSON(kv.getValue().toJSON()).toJSON().equals(kv.getValue().toJSON()));
			} catch (JsonParserException e) {
				e.printStackTrace(System.err);
			}
			if (kv.getValue().getStyle().isPresent()) {
				String style = kv.getValue().getStyle().orElseThrow();
				//if(style.isBlank()) continue; // no style
				List<BezierShape> paths = tracer.traceColor(map, kv.getKey());
				String pathStr = _join(" ", paths.stream().map((var p) -> p.toSVGPathString()).collect(Collectors.toList()));
				svg.appendPathToBGLayer(pathStr, style);
			}

			// now honey-comb the icons
			final float spacing = size * decorator.getCellSize();
			final double colWidth = spacing * root3over2;
			final double alternatingOffset = spacing * 0.5;
			final double rowHeight = spacing;
			final int numCols = (int) (svg.getWidth() / colWidth) + 1;
			final int numRows = (int) (svg.getWidth() / rowHeight) + 1;

			for (int row = 0; row < numRows; ++row) {
				for (int col = 0; col < numCols; ++col) {
					final double x = col * colWidth;
					final int ix = (int) (x + 0.5);
					final double y = row * rowHeight + ((col % 2) * alternatingOffset);
					final int iy = (int) (y + 0.5);
					if (!map.isInRange(ix, iy)) continue;
					final int color = map.get(ix, iy);
					if(color == targetColor) {
						//_place(x, y, size, spacing, decorator, prng, svg);
						if (prng.nextFloat() > decorator.getDecorFrequency()) continue;
						final var nx = jitter(x, spacing * decorator.getPositionJitter(), prng);
						final var ny = jitter(y, spacing * decorator.getPositionJitter(), prng);
						final float _size = spacing * decorator.getIconSize();
						final float iconSize = jitter(_size, _size * decorator.getSizeJitter(), prng);
						final var distortion = decorator.getDistortionJitter();
						final var iconProportions = new Vec2(jitter(1, distortion, prng), jitter(1, distortion, prng));
						final var paletteOfIDs = decorator.getDecorationIDs();
						final String iconID = paletteOfIDs[prng.nextInt(paletteOfIDs.length)];
						final double iconRotation = 0;
						//svg.placeIcon(iconID, iconSize,new Vec2(x, y), iconProportions, iconRotation);
						sortedPlacement.add(new IconPlacement(iconID, iconSize, new Vec2(nx, ny), iconProportions, iconRotation));
					}
				}
			}
		}
		// place all icons on map in sorted order (bottom in front of top)
		for(var p : sortedPlacement){
			svg.placeIcon(p.id, p.size, p.pos, p.proportions, p.rotation);
		}
	}

	private static String _join(String s, List<Object> o){
		if(o.size() == 0) return "";
		if(o.size() == 1) return String.valueOf(o.get(0));
		var sb = new StringBuilder();
		boolean first = true;
		for(var oo : o){
			if(!first) sb.append(s);
			sb.append(String.valueOf(oo));
			first = false;
		}
		return sb.toString();
	}
	private static float plusOrMinusOne(Random prng){
		return (2F*prng.nextFloat() - 1F);
	}
	private static float jitter(float mean, float plusMinus, Random prng){
		if(plusMinus == 0) return mean;
		return max(0F, mean + plusMinus * (2F*prng.nextFloat() - 1F));
	}
	private static double jitter(double mean, double plusMinus, Random prng){
		if(plusMinus == 0) return mean;
		return max(0D, mean + plusMinus * (2F*prng.nextFloat() - 1F));
	}

	private static IntMap mapToColorPalette(int[] rgbColors, IntMap src){
		var out = new ZOrderIntMap(src.getWidth(), src.getHeight());
		for(int y = 0; y < src.getHeight(); ++y){
			for(int x = 0; x < src.getWidth(); ++x){
				final int src_argb = src.get(x, y);
				var shortestDist = Double.MAX_VALUE;
				int index = 0;
				for(int i = 0; i < rgbColors.length; ++i){
					double d = colorDistance(src_argb, rgbColors[i]);
					if(d < shortestDist){
						shortestDist = d;
						index = 0;
					}
				}
				out.set(x, y, rgbColors[index]);
			}
		}
		return out;
	}

	private static double colorDistance(int argb1, int argb2){
		final float[] hsb1 = new float[3];
		final float[] hsb2 = new float[3];
		Color.RGBtoHSB((argb1>>16)&0xFF, (argb1>>8)&0xFF, argb1&0xFF, hsb1);
		Color.RGBtoHSB((argb2>>16)&0xFF, (argb2>>8)&0xFF, argb2&0xFF, hsb2);
		var colorAngle = Math.abs(hsb2[0] - hsb1[0]);
		if(colorAngle > 0.5F) colorAngle = 1F - colorAngle;
		var dsb = Math.abs(hsb2[1] * hsb2[2] - hsb1[1] * hsb1[2]);
		return Math.sqrt(colorAngle*colorAngle + dsb*dsb);
	}

	private static void showImg(BufferedImage img){
		JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)));
	}

	private static class IconPlacement implements Comparable<IconPlacement>{
		public final String id;
		public final float size;
		public final Vec2 pos;
		public final Vec2 proportions;
		public final double rotation;

		private IconPlacement(String id, float size, Vec2 pos, Vec2 proportions, double rotation) {
			this.id = id;
			this.size = size;
			this.pos = pos;
			this.proportions = proportions;
			this.rotation = rotation;
		}


		@Override
		public int compareTo(IconPlacement that) {
			int c = Double.compare(this.pos.y, that.pos.y);
			if(c == 0) c = Double.compare(this.pos.x, that.pos.y);
			if(c == 0) c = Integer.compare(this.hashCode(), that.hashCode());
			return c;
		}
	}
}
