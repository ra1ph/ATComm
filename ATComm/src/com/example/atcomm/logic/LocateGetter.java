package com.example.atcomm.logic;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocateGetter implements LocationListener{

	public double longitude=0,latitude=0;
	public boolean isEnabled=false;
	public Context context;
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		longitude = arg0.getLongitude();
		latitude = arg0.getLatitude();
		Toast.makeText(context, "Location has been changed", Toast.LENGTH_SHORT).show();
		Log.d("myLog","GPS has been changed");
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		isEnabled = false;
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		isEnabled = true;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
