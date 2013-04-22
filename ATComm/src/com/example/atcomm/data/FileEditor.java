package com.example.atcomm.data;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileEditor {
	Context context;
	String appFld;
	private String DB_NAME = "database.db";
	
	public int DB_IS_EXIST = 1;
	public int UNKNOWN_ERROR = 2;
	public static String dropFolder = "//Android//data//com.dropbox.android//files//scratch//busrouter";
	
	public FileEditor(Context context){
		this.context = context;
		appFld = String.format("//data//data//%s//", context.getPackageName());
	}
	
	public void copyDBToCard(){
		try{
		File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
        	//Log.d("myLog", "SD can write!");
            String currentDBPath = appFld+"databases//";
            String backupDBPath = DB_NAME;
            File currentDB = new File(currentDBPath, DB_NAME);
            File backupDB = new File(sd, backupDBPath);
            //Log.d("myLog", "DB path "+currentDB.getPath());
            //Log.d("myLog", "New DB path "+backupDB.getPath());
            if(backupDB!=null)Log.d("myLog", "New DB file created!");

            if (currentDB.exists()) {
            	//Log.d("myLog", "Current DB was created!");
            	FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                
                Process process = Runtime.getRuntime().exec("su");
				DataOutputStream os = new DataOutputStream(
						process.getOutputStream());
                os.write(("chmod 777 "+backupDB.getPath()).getBytes());
                os.close();
                
                src.close();
                dst.close();
            }
        }
		}
		catch (Exception e){
			
		}
	}
	
	public void writeLogToDropbox(String log){
		try{
		File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
        	//Log.d("myLog", "SD can write!");
            String backupDBPath = "log.txt";
            File file = new File(sd + dropFolder, backupDBPath);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
            
            if (bw!=null) {
            	Log.d("myLog", "Log file opened!");
            	
            	bw.write(log);
            	bw.flush();
            	bw.close();
            }
        }
		}
		catch (Exception e){
			
		}
	}
	
	public void copyDBToDropbox(){
		try{
		File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
        	//Log.d("myLog", "SD can write!");
            String currentDBPath = appFld+"databases//";
            String backupDBPath = DB_NAME;
            File currentDB = new File(currentDBPath, DB_NAME);
            File backupDB = new File(sd + dropFolder, backupDBPath);
            //Log.d("myLog", "DB path "+currentDB.getPath());
            //Log.d("myLog", "New DB path "+backupDB.getPath());
            if(backupDB!=null)Log.d("myLog", "New DB file created!");

            if (currentDB.exists()) {
            	//Log.d("myLog", "Current DB was created!");
            	FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                
                Process process = Runtime.getRuntime().exec("su");
				DataOutputStream os = new DataOutputStream(
						process.getOutputStream());
                os.write(("chmod 777 "+backupDB.getPath()).getBytes());
                os.close();
                
                src.close();
                dst.close();
            }
        }
		}
		catch (Exception e){
			
		}
	}
}
