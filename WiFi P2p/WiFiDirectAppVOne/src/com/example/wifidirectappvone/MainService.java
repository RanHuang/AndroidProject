package com.example.wifidirectappvone;

import come.example.wificontrol.WifiController;
import come.example.wificontrol.WifiP2pConnector;
import come.example.wificontrol.WifiP2pServiceConroller;
import come.example.wificontrol.WifiP2pServiceDiscovery;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class MainService extends IntentService {

	private static final String TAG = "WifiP2pAppV1 MainService";
	private static final boolean D = true;
	
	private WakeLock _wakeLock;
	private static final String WAKE_LOCK_TAG = "WifiP2pAppV1 MainService";
	
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;
	
	private WifiP2pConnector mWifiP2pConnector = null;
	private WifiP2pServiceDiscovery mWifiP2pServiceDiscovery = null;
	
	private WifiController mWifiController = null;
	
	
	public MainService() {
		super("MainService");
	}
	
	public MainService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if(D) Log.i(TAG, "Custom Service Started...");
		return START_STICKY;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		/*
		 * Test the MainActivity.logToScreen()
		 * Thread threadDelay = new Thread(new DelayThread());
		   threadDelay.start();
		 */
		Notification notification = new Notification.Builder(getApplicationContext())
										.setContentTitle("Nick Wifi Direct Test App").build();
		startForeground(2014, notification);
		
		initialize();
	}
	
	private void initialize(){
		PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(
				Context.POWER_SERVICE);
		_wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
		if (!_wakeLock.isHeld()) {
			_wakeLock.acquire();
		}
        
        mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
        mWifiP2pConnector = new WifiP2pConnector(mWifiP2pManager, mChannel, wifiManager);
        mWifiP2pServiceDiscovery = new WifiP2pServiceDiscovery(mWifiP2pManager, mChannel, mWifiP2pConnector);
         
        mWifiController = new WifiController();
        //TO DO: If this Device is set to be Group Owner and Server
        //ADD SERVER THREAD HERE
        //mWifiController.setServer();
              
        new Thread(new WifiP2pServiceConroller(mWifiP2pServiceDiscovery), 
        		"WifiP2pServiceDiscoveryController Thread").start();
        
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MainActivity.logToScreen(">>>>MainService: onDestroy()");
		//服务启动之后，即调用onDestroy(),是否不能在此注销 BroadcastReceiver() ?，
		//事实上在initialize()中注册的Receiver根本没有起作用
		//unregisterReceiver(mBroadcastReceiver);
		if (_wakeLock != null) {
			if (_wakeLock.isHeld()) {
				_wakeLock.release();
			}
		}
	}

}
