package net.plantabyte.cartopen.test;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import java.util.*;
import java.util.stream.Collectors;

public class Decorator {
	private final List<String> decorationIDs;
	private final Optional<String> svgStyle;
	private final float decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter;
	
	public Decorator(
			final Collection<String> decorationIDs, final String svgStyle,
			final float decorFrequency, final float size, final float cellSize,
			final float positionJitter, final float sizeJitter, final float distortionJitter
	) {
		this.decorationIDs = new ArrayList(decorationIDs.size());
		this.decorationIDs.addAll(decorationIDs);
		if(svgStyle == null || svgStyle.isBlank()){
			this.svgStyle = Optional.empty();
		} else {
			this.svgStyle = Optional.of(svgStyle);
		}
		this.decorFrequency = decorFrequency;
		this.size = size;
		this.cellSize = cellSize;
		this.positionJitter = positionJitter;
		this.sizeJitter = sizeJitter;
		this.distortionJitter = distortionJitter;
	}
	public Decorator(
			final Collection<String> decorationIDs
	) {
		this(decorationIDs, null, 1F, 1F, 1F, 0F, 0F, 0F);
	}
	public Decorator(
			final Collection<String> decorationIDs, String svgStyle
	) {
		this(decorationIDs, svgStyle, 1F, 1F, 1F, 0F, 0F, 0F);
	}

	private Decorator( // essentially a move constructor
			final List<String> decorationIDs, final Optional<String> svgStyle,
			final float decorFrequency, final float size, final float cellSize,
			final float positionJitter, final float sizeJitter, final float distortionJitter
	) {
		this.decorationIDs = decorationIDs;
		this.svgStyle = svgStyle;
		this.decorFrequency = decorFrequency;
		this.size = size;
		this.cellSize = cellSize;
		this.positionJitter = positionJitter;
		this.sizeJitter = sizeJitter;
		this.distortionJitter = distortionJitter;
	}

	public Decorator withStyle(final String svgStyle){
		return new Decorator(decorationIDs, Optional.ofNullable(svgStyle), decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter);
	}
	public Decorator withDecorFrequency(final float decorFrequency){
		return new Decorator(decorationIDs, svgStyle, decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter);
	}
	public Decorator withIconSize(final float size){
		return new Decorator(decorationIDs, svgStyle, decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter);
	}
	public Decorator withCellSize(final float cellSize){
		return new Decorator(decorationIDs, svgStyle, decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter);
	}
	public Decorator withPositionJitter(final float positionJitter){
		return new Decorator(decorationIDs, svgStyle, decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter);
	}
	public Decorator withSizeJitter(final float sizeJitter){
		return new Decorator(decorationIDs, svgStyle, decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter);
	}
	public Decorator withDistortionJitter(final float distortionJitter){
		return new Decorator(decorationIDs, svgStyle, decorFrequency, size, cellSize, positionJitter, sizeJitter, distortionJitter);
	}

	public String toJSON(){
		var sb = new StringBuilder();
		sb.append("{\n");
		sb.append("  \"decorationIDs\": ").append('[');
		boolean notFirst = false;
		for(var id : decorationIDs){
			if(notFirst) sb.append(", ");
			sb.append('"').append(esc(id)).append('"');
			notFirst = true;
		}
		sb.append("],\n");
		sb.append("  \"svgStyle\": ").append('"').append(esc(svgStyle.orElse(""))).append('"').append(",\n");
		sb.append("  \"decorFrequency\": ").append(String.format("%.5f", decorFrequency)).append(",\n");
		sb.append("  \"size\": ").append(String.format("%.5f", size)).append(",\n");
		sb.append("  \"cellSize\": ").append(String.format("%.5f", cellSize)).append(",\n");
		sb.append("  \"positionJitter\": ").append(String.format("%.5f", positionJitter)).append(",\n");
		sb.append("  \"sizeJitter\": ").append(String.format("%.5f", sizeJitter)).append(",\n");
		sb.append("  \"distortionJitter\": ").append(String.format("%.5f", distortionJitter)).append("\n"); // last onw must not have trailing comma
		sb.append('}');
		return sb.toString();
	}

	public static Decorator fromJSON(String jsonStr) throws JsonParserException {
		var map = JsonParser.object().from(jsonStr);
		var idList = map.getArray("decorationIDs", new JsonArray()).stream().map(String::valueOf).collect(Collectors.toList());
//		Decorator(final Collection<String> decorationIDs, final String svgStyle,
//		final float decorFrequency, final float size, final float cellSize,
//		final float positionJitter, final float sizeJitter, final float distortionJitter)
		return new Decorator(
				idList,
				map.getString("svgStyle", ""),
				map.getFloat("decorFrequency", 1F),
				map.getFloat("size", 1F),
				map.getFloat("cellSize", 1F),
				map.getFloat("positionJitter", 0F),
				map.getFloat("sizeJitter", 0F),
				map.getFloat("distortionJitter", 0F)
		);
	}

	private static String esc(String s){
		return s.replace("\\", "\\\\")
				.replace("\t", "\\t")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\"", "\\\"");
	}

	public void addDecorationID(String id){
		this.decorationIDs.add(id);
	}
	public void removeDecorationID(String id){
		this.decorationIDs.remove(id);
	}
	
	public String[] getDecorationIDs(){
		return this.decorationIDs.toArray(new String[this.decorationIDs.size()]);
	}
	
	public Optional<String> getStyle(){
		return svgStyle;
	}
	
	public float getDecorFrequency() {
		return decorFrequency;
	}
	
	public float getPositionJitter() {
		return positionJitter;
	}
	
	public float getSizeJitter() {
		return sizeJitter;
	}
	
	public float getDistortionJitter() {
		return distortionJitter;
	}

	public float getCellSize() { return cellSize; }

	public float getIconSize() { return size; }
}
