package com.example.simplecalc.info;

import java.util.HashMap;
import java.util.Map;

public class ConstantInfo {
	public static final String addition="加法";
	public static final String minus="减法";
	public static final String multiply="乘法";
	public static final String divider="除法";
	//基于Java泛型的定义 typeMap
	public static final Map<Integer,String> typeMap = new
			HashMap<Integer,String>();
	public static final Map<Integer,String> infoMap = new
			HashMap<Integer,String>();
	static { //静态代码，在类初始化的时候执行
		typeMap.put(1, addition);
		typeMap.put(2, minus);
		typeMap.put(3, multiply);
		typeMap.put(4, divider);
		infoMap.put(1, "+");
		infoMap.put(2, "-");
		infoMap.put(3, "*");
		infoMap.put(4, "/");
	}	
}
