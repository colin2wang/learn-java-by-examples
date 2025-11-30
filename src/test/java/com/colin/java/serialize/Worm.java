package com.colin.java.serialize;

import java.io.Serializable;

public class Worm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;

	public Worm(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}