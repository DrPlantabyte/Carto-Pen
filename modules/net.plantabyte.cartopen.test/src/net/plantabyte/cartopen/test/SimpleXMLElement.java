package net.plantabyte.cartopen.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleXMLElement {
	private final String name;
	private final Map<String,String> attributes;
	private final LinkedList<SimpleXMLElement> children;

	private SimpleXMLElement(){
		// for use by TextElement
		name = null;
		attributes = null;
		children = null;
	}
	private static boolean isValidName(String n){
		char first = n.charAt(0);
		if(!(first == '_' || Character.isLetter(first))) return false;
		if(n.toLowerCase(Locale.ENGLISH).startsWith("xml")) return false;
		for(int i = 1; i < n.length(); i++){
			if(Character.isWhitespace(n.charAt(i))) return false;
		}
		return true;
	}
	public SimpleXMLElement(String name){
		if(!isValidName(name)) throw new IllegalArgumentException(name+" is not a valid element name");
		this.name = name;
		this.attributes = new HashMap<>();
		this.children = new LinkedList<>();
	}
	public static SimpleXMLElement newElement(String name){
		return new SimpleXMLElement(name);
	}
	public SimpleXMLElement appendText(String text){
		this.appendChild(new TextElement(text));
		return this;
	}
	public SimpleXMLElement appendChild(SimpleXMLElement c){
		this.children.add(c);
		return this;
	}
	public void appendChildren(SimpleXMLElement... childNodes){
		for(var c : childNodes){
			this.appendChild(c);
		}
	}
	public SimpleXMLElement removeChild(SimpleXMLElement c){
		this.children.remove(c);
		return this;
	}
	public SimpleXMLElement removeChildren(SimpleXMLElement... childNodes){
		for(var c : childNodes){
			this.removeChild(c);
		}
		return this;
	}

	public SimpleXMLElement setAttribute(String key, String value){
		if(!isValidName(key)) throw new IllegalArgumentException(key+" is not a valid attribute name");
		attributes.put(key, value);
		return this;
	}

	public SimpleXMLElement removeAttribute(String key){
		attributes.remove(key);
		return this;
	}

	public Optional<String> getAttribute(String key){
		return Optional.ofNullable(attributes.get(key));
	}

	public List<SimpleXMLElement> getAllChildren(){
		return this.children;
	}

	public List<SimpleXMLElement> getChildElements(){
		return Collections.unmodifiableList(
				this.children.stream().filter((var e) -> e.isElement()).collect(Collectors.toList())
		);
	}

	public boolean isText(){return false;}
	public boolean isElement(){return true;}


	@Override public final String toString(){
		return toString(0);
	}

	public static String XMLHeader(){
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	}

	public void writeTo(OutputStream out) throws IOException {
		var w = new OutputStreamWriter(out, StandardCharsets.UTF_8);
		w.write(XMLHeader());
		w.write(this.toString());
		// does not close the stream (intended behavior)
	}
	public void writeTo(Path filepath) throws IOException{
		try(var fout = Files.newOutputStream(filepath)){
			this.writeTo(fout);
		}
	}
	public String writeToString(){
		return XMLHeader().concat(this.toString());
	}

	private static String escape(String input){
		return String.valueOf(input)
				.replace("&", "&amp;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}
	public String toString(int indent){
		var sb = new StringBuilder();
		var attributeStr = new StringBuilder();
		var indentStr = new StringBuilder();
		for(int i = 0; i < indent; i++) indentStr.append(' ');
		sb.append(indentStr);
		for(var e : this.attributes.entrySet()){
			attributeStr.append(' ')
					.append(String.valueOf(e.getKey()))
					.append("=\"")
					.append(escape(e.getValue()))
					.append("\"");
		}
		if(children.size() == 0) {
			// no children
			sb.append(String.format("<%s%s />", this.name, attributeStr));
		} else {
			sb.append(String.format("<%s%s>\n", this.name, attributeStr));
			for(var c : children) sb.append(c.toString(indent+1));
			sb.append(indentStr);
			sb.append(String.format("</%s>", this.name));
		}
		sb.append('\n');
		return sb.toString();
	}


	private static class TextElement extends SimpleXMLElement{
		String content;
		public TextElement(String text){
			super();
			this.content = text;
		}

		@Override
		public String toString(int indent) {
			var sb = new StringBuilder();
			for(int i = 0; i < indent; i++) sb.append(' ');
			sb.append(escape(content));
			sb.append('\n');
			return sb.toString();
		}

		@Override public SimpleXMLElement appendText(String text){
			this.content = this.content.concat(text);
			return this;
		}
		@Override public SimpleXMLElement appendChild(SimpleXMLElement c){
			throw new UnsupportedOperationException("Cannot add children to text");
		}
		@Override public SimpleXMLElement removeChild(SimpleXMLElement c){
			// no-op
			return this;
		}

		@Override public SimpleXMLElement setAttribute(String key, String value){
			throw new UnsupportedOperationException("Cannot add attributes to text");
		}

		@Override public SimpleXMLElement removeAttribute(String key){
			// no-op
			return this;
		}

		@Override public Optional<String> getAttribute(String key){
			return Optional.empty();
		}

		@Override public List<SimpleXMLElement> getAllChildren(){
			return Collections.EMPTY_LIST;
		}


		@Override public boolean isText(){return true;}
		@Override public boolean isElement(){return false;}
	}
}
