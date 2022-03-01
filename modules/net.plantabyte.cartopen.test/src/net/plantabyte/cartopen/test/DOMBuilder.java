package net.plantabyte.cartopen.test;


import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * This class wraps the java.xml module classes and functions in a way that is much more ergonomic for simple DOM
 * manipulations
 */
public class DOMBuilder {
	private final Document doc;
	private final Element pos;

	public DOMBuilder(String rootElementName){
		try {
			doc = newDocument();
			pos = doc.createElement(rootElementName);
			doc.appendChild(pos);
		} catch (ParserConfigurationException e) {
			throw new XMLException(e);
		}
	}

	public Document document(){
		return doc;
	}

	public Element element(){
		return pos;
	}

	public DOMBuilder(Document d){
		this(d, d.getDocumentElement());
	}
	public DOMBuilder(Document d, Element e){
		doc = d;
		pos = e;
	}



	public static DOMBuilder fromFile(Path f) throws IOException, XMLException {
		try(var in = Files.newInputStream(f)){
			var doc = parseXML(in);
			var root = doc.getDocumentElement();
			return new DOMBuilder(doc, root);
		} catch (ParserConfigurationException  | SAXException e) {
			throw new XMLException(e);
		}
	}

	public DOMBuilder newElement(String name){
		var e = doc.createElement(name);
		return new DOMBuilder(doc, e);
	}

	public DOMBuilder setAttribute(String key, String value) {
		pos.setAttribute(key, value);
		return this;
	}

	public Optional<String> getAttribute(String key){
		if(this.pos.hasAttribute(key)){
			return Optional.of(pos.getAttribute(key));
		} else {
			return Optional.empty();
		}
	}
	public Optional<String> getAttributeNS(String namespaceURI, String key){
		if(this.pos.hasAttributeNS(namespaceURI, key)){
			return Optional.of(pos.getAttributeNS(namespaceURI, key));
		} else {
			return Optional.empty();
		}
	}

	public DOMBuilder removeAttribute(String key) {
		pos.removeAttribute(key);
		return this;
	}

	public DOMBuilder appendElement(DOMBuilder b){
		if(this.doc != b.doc){
			this.doc.adoptNode(b.pos);
		}
		pos.appendChild(b.pos);
		return this;
	}

	public DOMBuilder appendAll(DOMBuilder... b){
		for(var n : b){
			this.appendElement(n);
		}
		return this;
	}

	public DOMBuilder appendAll(Collection<DOMBuilder> b){
		for(var n : b){
			this.appendElement(n);
		}
		return this;
	}

	public DOMBuilder setText(String text){
		pos.setTextContent(text);
		return this;
	}

	public DOMBuilder root(){
		return new DOMBuilder(this.doc, this.doc.getDocumentElement());
	}

	public DOMBuilder appendElement(Element e){
		pos.appendChild(e);
		return this;
	}

	public Optional<DOMBuilder> getFirstElementByName(String name) {
		var list = pos.getElementsByTagName(name);
		if(list.getLength() <= 0) return Optional.empty();
		return Optional.of(new DOMBuilder(this.doc, (Element)list.item(0)));
	}

	public List<DOMBuilder> getElementsByName(String name) {
		var list = pos.getElementsByTagName(name);
		var out = new ArrayList<DOMBuilder>(list.getLength());
		final int len = list.getLength();
		for(int i = 0; i < len; i++){
			out.add(new DOMBuilder(this.doc, (Element)list.item(i)));
		}
		return Collections.unmodifiableList(out);
	}

	public List<DOMBuilder> getChildElements() {
		var list = pos.getChildNodes();
		var out = new ArrayList<DOMBuilder>(list.getLength());
		final int len = list.getLength();
		for(int i = 0; i < len; i++){
			var n = list.item(i);
			if(n instanceof Element) {
				out.add(new DOMBuilder(this.doc, (Element) n));
			}
		}
		return Collections.unmodifiableList(out);
	}

	public List<DOMBuilder> recursiveGetAllChildElements() {
		var list = pos.getChildNodes();
		var out = new LinkedList<DOMBuilder>();
		final int len = list.getLength();
		for(int i = 0; i < len; i++){
			var n = list.item(i);
			if(n instanceof Element) {
				var db = new DOMBuilder(this.doc, (Element) n);
				out.add(db);
				out.addAll(db.recursiveGetAllChildElements());
			}
		}
		return Collections.unmodifiableList(out);
	}



	public String toXMLString() {
		try {
			return XMLtoString(doc);
		} catch (TransformerException e) {
			throw new XMLException(e);
		}
	}

	public void writeToFile(Path path) throws IOException {
		try(var out = Files.newOutputStream(path)){
			var transformer = TransformerFactory.newInstance().newTransformer();
			var src = new DOMSource(doc);
			var dst = new StreamResult(out);
			transformer.transform(src, dst);
		} catch (TransformerException e) {
			throw new XMLException(e);
		}
	}

	public Optional<DOMBuilder> getElementByID(final String id) {
		var list = pos.getChildNodes();
		final int len = list.getLength();
		for(int i = 0; i < len; i++){
			var n = list.item(i);
			if(n instanceof Element) {
				var e = ((Element) n);
				if(e.hasAttribute("id") && id.equals(e.getAttribute("id"))){
					return Optional.of(new DOMBuilder(this.doc, e));
				} else {
					var db = new DOMBuilder(this.doc, (Element) n);
					var r = db.getElementByID(id);
					if(r.isPresent()) return r;
				}
			}
		}
		return Optional.empty();
	}

	public boolean hasAttribute(final String key) {
		return pos.hasAttribute(key);
	}


	public static class XMLException extends RuntimeException{
		public XMLException(Throwable cause){
			super(cause.getLocalizedMessage(), cause);
		}
		public XMLException(String msg, Throwable cause){
			super(msg, cause);
		}
	}

	private static Document newDocument() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}

	private static String XMLtoString(Document doc) throws TransformerException {
		var out = new ByteArrayOutputStream();
		var transformer = TransformerFactory.newInstance().newTransformer();
		var src = new DOMSource(doc);
		var dst = new StreamResult(out);
		transformer.transform(src, dst);
		return out.toString(StandardCharsets.UTF_8);
	}

	private static Document parseXML(InputStream in) throws ParserConfigurationException, IOException, SAXException {
		return javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
	}
}
