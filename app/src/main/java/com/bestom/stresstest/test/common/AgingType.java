package com.bestom.stresstest.test.common;

public enum AgingType {

	CPU("cpu"),
	GPU("gpu"),
	VPU("vpu"),
	MEM("mem");
	
	private String type;
	
	private AgingType(String type){
		this.type = type;
	}
	
	public static AgingType getType(String typ){
		if(typ==null) return null;
		for(AgingType at : AgingType.values()){
			if(typ.equals(at.type)){
				return at;
			}
		}
		return null;
	}

	public String getType() {
		return type;
	}
	
	
}
