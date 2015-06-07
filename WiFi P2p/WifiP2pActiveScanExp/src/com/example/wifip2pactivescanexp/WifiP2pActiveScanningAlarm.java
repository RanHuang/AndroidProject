package com.example.wifip2pactivescanexp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class WifiP2pActiveScanningAlarm extends BroadcastReceiver{
	
	private static final String TAG = "WifiP2pActiveScanningAlarm";
	private static final String WAKE_LOCK = "WifiP2pActiveScanAlarmWakeLock";
	private static WakeLock wakeLock;
	private static PendingIntent alarmIntent;
	
	private static WifiP2pController mWifiP2pController = null;
	private Context mContext = null;
//	private static long mInterval;
	
	public WifiP2pActiveScanningAlarm(){}
	
	public WifiP2pActiveScanningAlarm(Context context, WifiP2pController wifiP2pController){
		mContext = context;
		mWifiP2pController = wifiP2pController;
	}
	
	public void startActiveScan(long interval){
//		mInterval = interval;
		scheduleScanning(mContext, interval);
	}
	
	public void scheduleScanning(Context context, long interval) {

		Log.d(TAG, "scheduling a new WifiP2p scanning in " + Long.toString(System.currentTimeMillis()));
		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, WifiP2pActiveScanningAlarm.class);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, alarmIntent);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "start a scan at " + String.valueOf(System.currentTimeMillis()));
		
		mWifiP2pController.startWifiScan();
		
	}
	
	public static void stopScanning(Context context) {
		AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		if(alarmMgr != null){
			Log.d(TAG, "alarm cancelled");
			Intent intent = new Intent(context, WifiP2pActiveScanningAlarm.class);
			alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarmMgr.cancel(alarmIntent);
		}
		releaseWakeLock();
	}
	
	public static void getWakeLock(Context context){
		releaseWakeLock();

		PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK , WAKE_LOCK); 
		wakeLock.acquire();
	}
	
	public static void releaseWakeLock(){
		if(wakeLock != null && wakeLock.isHeld()){
			wakeLock.release();
		}		
	}
	
}
