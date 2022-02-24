package net.plantabyte.cartopen.test;

import java.util.*;

public class Decorator {
	private final ArrayList<String> decorationIDs;
	private final String svgStyle;
	private final float decorFrequency, positionJitter, sizeJitter, distortionJitter;
	
	public Decorator(
			final Collection<String> decorationIDs, final String svgStyle,
			final float decorFrequency, final float positionJitter,
			final float sizeJitter, final float distortionJitter
	) {
		this.decorationIDs = new ArrayList(decorationIDs.size());
		this.decorationIDs.addAll(decorationIDs);
		this.svgStyle = svgStyle;
		this.decorFrequency = decorFrequency;
		this.positionJitter = positionJitter;
		this.sizeJitter = sizeJitter;
		this.distortionJitter = distortionJitter;
	}
	
	public void addDecorationID(String id){
		this.decorationIDs.add(id);
	}
	
	public String[] getDecorationIDs(){
		return this.decorationIDs.toArray(new String[this.decorationIDs.size()]);
	}
	
	public String getStyle(){return svgStyle;}
	
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
}
