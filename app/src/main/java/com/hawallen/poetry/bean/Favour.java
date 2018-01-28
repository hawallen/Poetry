package com.hawallen.poetry.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Favour implements Serializable {

	public List<Poetry> poetries = new ArrayList<Poetry>();
	public String fileName;

}
