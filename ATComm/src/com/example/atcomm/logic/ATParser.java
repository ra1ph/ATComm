package com.example.atcomm.logic;

import android.util.Log;

public class ATParser {
public static int parseReplyDb(String reply){
	try{
	String data = reply.substring(reply.indexOf("CSQ:")+5);
	Log.d("myLog", "Data "+data);
	String level = data.split(",")[0];
	Log.d("myLog", "Data "+level);
	int l = Integer.parseInt(level);
	return l;}
	catch(NumberFormatException e){
		return -1;
	}
}
}
