package com.example.simplecalc.info;

import java.util.HashMap;
import java.util.Map;

public class ConstantInfo {
	public static final String addition="�ӷ�";
	public static final String minus="����";
	public static final String multiply="�˷�";
	public static final String divider="����";
	//����Java���͵Ķ��� typeMap
	public static final Map<Integer,String> typeMap = new
			HashMap<Integer,String>();
	public static final Map<Integer,String> infoMap = new
			HashMap<Integer,String>();
	static { //��̬���룬�����ʼ����ʱ��ִ��
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
