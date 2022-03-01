package net.plantabyte.cartopen.test;

import net.plantabyte.drptrace.geometry.Vec2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SVGManager {

	private static final String MAIN_CONTENT_ID = "mainLayer";
	private static final String BG_CONTENT_ID = "bgLayer";
	private static final String FG_CONTENT_ID = "fgLayer";

	private final DOMBuilder dom;
	private final int width;
	private final int height;
	private final IDMaker idMaker = new IDMaker();
	
	public SVGManager(int w, int h) {
		width = w;
		height = h;
		dom = new DOMBuilder("svg");
		dom
				.setAttribute("width", String.valueOf(w))
				.setAttribute("height", String.valueOf(h))
				.setAttribute("viewbox", String.format("0 0 %s %s", w, h))
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
				.appendElement(
						dom.newElement("g")
								.setAttribute("id", BG_CONTENT_ID)
				)
				.appendElement(
						dom.newElement("g")
								.setAttribute("id", MAIN_CONTENT_ID)
				)
				.appendElement(
						dom.newElement("g")
								.setAttribute("id", FG_CONTENT_ID)
				)
		;
	}

	public String importAsDef(Path svgSrc) throws IOException, DOMBuilder.XMLException {
		String filename = svgSrc.getFileName().toString();
		String id = idMaker.makeID(filename.substring(0,filename.lastIndexOf(".")));
		var loaded = DOMBuilder.fromFile(svgSrc);
		prefixIDs(loaded, id+"_");
		loaded.setAttribute("id", id);
		//
		dom.getFirstElementByName("defs").orElseThrow().appendElement(loaded);
		return id;
	}

	private static void prefixIDs(DOMBuilder src, String prefix){
		final var allElements = src.recursiveGetAllChildElements();
		final var replaceMap = new HashMap<String,String>();
		for(final var e : allElements){
			e.getAttribute("id").ifPresent((final String oldAttr) ->{
				final String newAttr = prefix+oldAttr;
				e.setAttribute("id", newAttr);
				replaceMap.put(oldAttr, newAttr);
			});
		}
		for(final var o : allElements){
			// replace references
			o.getAttribute("xlink:href").ifPresent((final var oldAttr)->{
				var attr = oldAttr;
				for(final var kv : replaceMap.entrySet()) {
					final var oldID = kv.getKey();
					final var newId = kv.getValue();
					attr = attr.replace(oldID, newId);
				}
				o.setAttribute("xlink:href", attr);
			});
		}
	}

	public String placeIcon(final String id, final double iconDiameter, final Vec2 pos, final Vec2 scale, final double rotationDegrees) throws NoSuchElementException {
		final var src = dom.getElementByID(id).orElseThrow();
		final double halfW = 0.5*convertToPixelUnits(src.getAttribute("width").orElseThrow());
		final double halfH = 0.5*convertToPixelUnits(src.getAttribute("height").orElseThrow());
		final double resizer = iconDiameter / Math.sqrt(4*halfW*halfH);
		var iconID = idMaker.makeID(String.format("%s_clone", id));
		String transString = String.format("translate(%.5f,%.5f) scale(%.5f,%.5f)",
				pos.x-(scale.x*halfW*resizer), pos.y-(scale.y*halfH*resizer),
				scale.x*resizer, scale.y*resizer);
		if(rotationDegrees != 0) {
			transString += String.format(" rotate(%.5f,%.5f,%.5f)",
					rotationDegrees, -halfW, -halfH);
		}
		dom.getElementByID(MAIN_CONTENT_ID).orElseThrow()
				.appendElement(dom.newElement("use")
						.setAttribute("id", iconID)
						.setAttribute("xlink:href",String.format("#%s", id))
						.setAttribute("x", "0") // moving to position bby translation (better for scaling)
						.setAttribute("y", "0")
						.setAttribute("transform",transString)
				);
		return iconID;
	}

	public void writeToFile(Path filepath) throws IOException{
		dom.writeToFile(filepath);
	}

	public double getWidth() {
		return convertToPixelUnits(dom.getAttribute("width").orElseThrow());
	}
	public double getHeight() {
		return convertToPixelUnits(dom.getAttribute("height").orElseThrow());
	}

	public void appendPathToBGLayer(String pathSpec, String style) {
		dom.getElementByID(BG_CONTENT_ID).orElseThrow()
				.appendElement(dom.newElement("path")
						.setAttribute("style", style)
						.setAttribute("fill-rule", "evenodd")
						.setAttribute("d", pathSpec)
				);

	}
	public void appendPathToMainLayer(String pathSpec, String style) {
		dom.getElementByID(MAIN_CONTENT_ID).orElseThrow()
				.appendElement(dom.newElement("path")
						.setAttribute("style", style)
						.setAttribute("fill-rule", "evenodd")
						.setAttribute("d", pathSpec)
				);

	}
	public void appendPathToFGLayer(String pathSpec, String style) {
		dom.getElementByID(FG_CONTENT_ID).orElseThrow()
				.appendElement(dom.newElement("path")
						.setAttribute("style", style)
						.setAttribute("fill-rule", "evenodd")
						.setAttribute("d", pathSpec)
				);

	}

	private static class IDMaker{
		private final Set<String> countTracker = new HashSet<>();
		public IDMaker(){
			//
		}
		
		public String makeID(String baseID){
			baseID = baseID.strip();
			var base = new StringBuilder();
			for(int i = 0; i < baseID.length(); i++){
				int cp = baseID.codePointAt(i);
				i += (Character.charCount(cp) - 1);
				if(Character.isLetterOrDigit(cp)){
					base.append(Character.toString(cp));
				}
			}
			int endIndex = base.length()-1;
			var baseStr = base.toString();
			while(Character.isDigit(baseStr.indexOf(endIndex))){
				endIndex--;
			}
			if(endIndex <= 0) baseStr = "_";
			int i = 2;
			String idStr = baseStr;
			while(countTracker.contains(idStr)){
				idStr = String.format("%s%s", baseStr, i++);
			}
			countTracker.add(idStr);
			return idStr;
		}
	}

	public static double convertToPixelUnits(String svgNumber) throws IllegalArgumentException{
		try {
			svgNumber = svgNumber.strip();
			int split = svgNumber.length();
			while (split > 0) {
				--split;
				char c = svgNumber.charAt(split);
				if (c == '.' || Character.isDigit(c)) {
					break;
				}
			}
			split += 1;
			double num = Double.parseDouble(svgNumber.substring(0, split));
			final String unit = svgNumber.substring(split, svgNumber.length()).strip();
			if(unit.length()==0) return num; // no units
			switch (unit){
				// well-defined units
				case "px":
					return num;
				case "in":
					return num*96;
				case "cm":
					return num*96/2.54;
				case "mm":
					return num*96/25.4;
				case "pt":
					return num*96/72;
				case "pc":
					return num*12*96/72;
				// fuzzy units
				case "em":
					// assume common font size of 12 pt
					return num*12*96/72;
				case "ex":
					// assume terminal monospace common font shape (8x12 dimensions) at 12 pt font
					return num*8*96/72;
				case "%":
					// assume common screen size, assuming square screen, assuming some margin
					// so.... 100% = 1024 (good enough, I hope)
					return num * 10.24;
				default:
					throw new IllegalArgumentException(String.format("Could not parse '%s' ('%s' is not a supported unit)", svgNumber, unit));
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("Could not parse '%s' (not a number)", svgNumber), e);
		}
	}




}
