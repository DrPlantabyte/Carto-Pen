package net.plantabyte.cartopen.test;

import net.plantabyte.drptrace.geometry.Vec2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SVGManager {

	private static final String MAIN_CONTENT_ID = "mainLayer";

	private final DOMBuilder dom;
	private final int width;
	private final int height;
	private final IDMaker idMaker = new IDMaker();
	
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
				.appendElement(
						dom.newElement("g")
								.setAttribute("id", MAIN_CONTENT_ID)
				)
		;
	}

	public String importAsDef(Path svgSrc) throws IOException, DOMBuilder.XMLException {
		String filename = svgSrc.getFileName().toString();
		String id = idMaker.makeID(filename.substring(0,filename.lastIndexOf(".")));
		var loaded = DOMBuilder.fromFile(svgSrc);
		loaded.setAttribute("id", id);
		dom.getFirstElementByName("defs").orElseThrow().appendElement(loaded);
		return id;
	}

	public String placeIcon(String id, Vec2 pos, Vec2 scale, double rotationDegrees) {
		var viewBox = dom.getElementByID(id).orElseThrow()
				.getAttribute("viewBox").orElseThrow().split("[\\s,]+");
		var iconID = idMaker.makeID(String.format("%s_clone", id));
		dom.getElementByID(MAIN_CONTENT_ID).orElseThrow()
				.appendElement(dom.newElement("use")
						.setAttribute("id", iconID)
						.setAttribute("xlink:href",String.format("#%s", id))
						.setAttribute("x", String.valueOf(pos.x))
						.setAttribute("y", String.valueOf(pos.y))
						.setAttribute("transform",String.format("translate(%.5f,%.5f) rotate(%.5f,%.5f,%.5f) scale(%.5f,%.5f)",
								-0.5*Double.parseDouble(viewBox[2])*scale.x, -0.5*Double.parseDouble(viewBox[3])* scale.y,
								rotationDegrees, -0.5*Double.parseDouble(viewBox[2]), -0.5*Double.parseDouble(viewBox[3]),
								scale.x, scale.y))
				);
		return iconID;
	}

	public void writeToFile(Path filepath) throws IOException{
		dom.writeToFile(filepath);
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





}
