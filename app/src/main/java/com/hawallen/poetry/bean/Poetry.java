package com.hawallen.poetry.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Poetry  implements Serializable{

	public String produceDate = "";
	public String favorDate = "";
	public String title = "";
	public String[] poetryStrings = new String[2];

}
