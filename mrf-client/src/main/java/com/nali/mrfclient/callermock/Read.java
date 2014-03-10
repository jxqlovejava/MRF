package com.nali.mrfclient.callermock;

public class Read {
	
	private int id;
	private String reader;
	
	public Read() {
		
	}
	
	public Read(int id, String reader) {
		this.id = id;
		this.reader = reader;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getReader() {
		return reader;
	}
	public void setReader(String reader) {
		this.reader = reader;
	}

}
