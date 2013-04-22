package com.example.atcomm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import com.example.atcomm.logic.ATParser;
import com.example.atcomm.logic.GPSPoint;
import com.example.atcomm.logic.LocateGetter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ReadConsole extends AsyncTask<Void, String, Void> {
	public LocateGetter loc;
	Context context;
	public long time_sleep = 300;
	public boolean isReading = true;
	public String dev_name;
	public Main parent;
	public int level = 0;
	public GPSPoint point = new GPSPoint(0, 0, 0);

	public ReadConsole(Context context, String dev_name, Main parent) {
		loc = new LocateGetter();
		loc.context = context;
		this.context = context;
		this.dev_name = dev_name;
		this.parent = parent;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		// Looper.prepare();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DataOutputStream os = new DataOutputStream(process.getOutputStream());

		try {
			os.writeBytes("cat " + dev_name);
			os.flush();
			os.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		InputStream is;
		is = process.getInputStream();
		while (true) {
			try {

				String ln = "";
				byte[] buffer = new byte[1024];
				int read = 0;
				read = is.read(buffer, 0, 1024);
				parent.comDeliver = true;
				while (read > 0) {
					ln += new String(buffer, 0, read);
					Log.d("myLog", ln + "OUT");
					/*
					 * if(ln.contains("AT+CSQ")||ln.equals("SQ")){
					 * Toast.makeText(context, "Попробуйте другое устройство",
					 * Toast.LENGTH_SHORT).show(); Log.d("myLog",
					 * "Change device please"); Looper.myLooper().quit(); return
					 * null; }
					 */
					if ((!isReading) || (!parent.isReading)) {
						Log.d("myLog", "Read console stopped");
						process.destroy();
						/*
						 * DataOutputStream os2 = new
						 * DataOutputStream(process.getOutputStream());
						 * os2.writeBytes("kill %1"); os2.flush(); os2.close();
						 */
						Log.d("myLog", "Read console stopped");
						// Looper.myLooper().quit();
						return null;
					}
					if (ln.contains("+CSQ")) {
						this.publishProgress(ln);
						// Thread.sleep(time_sleep);
					}
					ln = "";
					read = is.read(buffer, 0, 1024);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /*
			 * catch (RuntimeException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); parent.writeLog("Device not responding");
			 * return null; }
			 */
		}
		// process.destroy();
		// Looper.loop();
		// return null;
	}

	@Override
	protected void onProgressUpdate(String... strings) {
		level = parseReplyDb(strings[0]);
		point = new GPSPoint(loc.latitude, loc.longitude, level);
		parent.writeLog("New level!");
	}

	protected void onPostExecute(Void reply) {
		parent.isReading = false;
	}

	public int parseReplyDb(String reply) {
		try {
			String data = reply.substring(reply.indexOf("CSQ:") + 5);
			Log.d("myLog", "Data " + data);
			String level = data.split(",")[0];
			Log.d("myLog", "Data " + level);
			int l = Integer.parseInt(level);
			return l;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}