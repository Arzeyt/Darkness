package com.arzeyt.darkness.effectObject;

public class effectEntry {

	private int effectID;
	private String effectName;
	
	public effectEntry(int effectID, String effectName) {
		this.effectID = effectID;
		this.effectName = effectName;
	}

	public int getEffectID() {
		return effectID;
	}

	public String getEffectName() {
		return effectName;
	}
	
}
