package com.nali.mrfcore.util;

public class ReflectObj {
	
	private int i;
	private final String name;
	
	public ReflectObj() {
		this.name = null;
	}
	
	public ReflectObj(int i, String name) {
		this.i = i;
		this.name = name;
	}
	
	public int getI() {
		return this.i;
	}
	
	public void setI(int i) {
		this.i = i;
	}

	public String getName() {
		return name;
	}
}
