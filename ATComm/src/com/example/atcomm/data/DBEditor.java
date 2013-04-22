package com.example.atcomm.data;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;

import com.example.atcomm.Main;
import com.example.atcomm.logic.GPSPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DBEditor {
	
	private Context context;
	private String appFld;
	
	private SQLiteDatabase db;
	
	public Main parent;
	public int DB_IS_EXIST = 1;
	public int UNKNOWN_ERROR = 2;
	public static String dropFolder = "//Android//data//com.dropbox.android//files//scratch//ATCommNew";
	
	private String DB_NAME = "database.db";

	public DBEditor(Context context){
		this.context = context;
		appFld = String.format("//data//data//%s//", context.getPackageName());
		db = context.openOrCreateDatabase(DB_NAME, 0, null);		
	}
	
	
	public void createTable(String tbl_name){
		db.execSQL("DROP TABLE IF EXISTS " + tbl_name);
		db.execSQL("CREATE TABLE " + tbl_name + " (_id INTEGER PRIMARY KEY, latitude DOUBLE, longitude DOUBLE, level DOUBLE);");
	}
	
	public void addPoint(GPSPoint point, String tbl_name){
		ContentValues val = new ContentValues();
		val.put("latitude",point.latitude);
		val.put("longitude",point.longitude);
		val.put("level",point.level);
		db.insert(tbl_name, null, val);
		
		Log.d("myLog", Double.toString(point.latitude) + "   " + Double.toString(point.longitude) + "   " + Double.toString(point.level));
		Toast.makeText(context, Double.toString(point.latitude) + "   " + Double.toString(point.longitude) + "   " + Double.toString(point.level), Toast.LENGTH_SHORT).show();
		parent.writeLog(Double.toString(point.latitude) + "   " + Double.toString(point.longitude) + "   " + Double.toString(point.level));
	}
	
	public void close(){
		db.close();
	}
}
