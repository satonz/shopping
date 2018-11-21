package com.zhang.utils;

import java.util.UUID;

public class CommonsUtils {

	//生成uuid方法
	public static String getUUID(){
		String s = UUID.randomUUID().toString();
		String replace = s.replace("-", "");
		return replace;
	}

	public static void main(String[] args) {
		System.out.println(getUUID());
	}
	
}
