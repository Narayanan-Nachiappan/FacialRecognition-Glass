package com.glass.wis.picretriev.stub;

import java.util.List;

public class ImageRecogResponse {
	int age;
	
	List<Double> ageDist;

	List<List<String>> attrs;
	
	List<String> gender;
	
	String result;
	
	String lang;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<Double> getAgeDist() {
		return ageDist;
	}

	public void setAgeDist(List<Double> ageDist) {
		this.ageDist = ageDist;
	}

	public List<List<String>> getAttrs() {
		return attrs;
	}

	public void setAttrs(List<List<String>> attrs) {
		this.attrs = attrs;
	}

	public List<String> getGender() {
		return gender;
	}

	public void setGender(List<String> gender) {
		this.gender = gender;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

		
}
