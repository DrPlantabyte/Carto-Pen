package net.plantabyte.cartopen.test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SVGManager {
	
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
		;
	}

	public String importAsDef(Path svgSrc) throws IOException {
		String filename = svgSrc.getName();
		String id = idMaker.makeID(filename.substring(0,filename.lastIndexOf(".")));
		// TODO: add in defs as group and return ID
		throw new UnsupportedOperationException("Not implemented yet");
		//return id;
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
			return idStr;
		}
	}





}
