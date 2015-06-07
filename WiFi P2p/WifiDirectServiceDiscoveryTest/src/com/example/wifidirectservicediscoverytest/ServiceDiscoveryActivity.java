package com.example.wifidirectservicediscoverytest;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ServiceDiscoveryActivity extends Activity {
	public static final String TAG = "ServiceDiscoveryActivity";
	
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;
	private IntentFilter mIntentFilter = null;
	private BroadcastReceiver mReceiver = null;
	private WifiManager mWifiManager = null;
	private ServiceDiscoveryController mServiceController = null;
	private ArrayList<WifiP2pDevice> wifiDeviceList = null;
	
	private WakeLock mWakeLock = null;
	
	private static TextView textLog = null;
	Timer mTimer = null;
	static Handler mHandler = null;
	TimerTask testTask = null;
	TimerTask taskDiscoveryService = null;
	TimerTask taskDiscovery = null;
	
	protected static final int TIMERTEST = 0x101;
	protected static final int STARTDISCOVERYSERVICE = 0x201;
	protected static final int STARTDISCOVERY = 0x202;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicediscovery);
        
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(
				Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		if (mWakeLock != null) {
			if (mWakeLock.isHeld()) {
				mWakeLock.release();
			}
		}
		
		textLog = (TextView)this.findViewById(R.id.text_log);
        textLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        wifiInitialize();
        handlerInitialize();
        
//        mTimer.schedule(testTask, 2000, 3000);
         
        if(MainActivity.isGO){
        	//For broadcasting(GO)
        	mTimer.schedule(taskDiscovery, 5000, 120000);
        	mTimer.schedule(taskDiscoveryService, 1000);
        } else {
        	//For discovering(Not GO)
        	mTimer.schedule(taskDiscovery, 5000, 30000);
        }
        
        
        
        
	}
	
	private void wifiInitialize(){
		mWifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        if(mWifiManager != null && !mWifiManager.isWifiEnabled()){
        	mWifiManager.setWifiEnabled(true);
        }
        
        mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        wifiDeviceList = new ArrayList<WifiP2pDevice>();
        mServiceController = new ServiceDiscoveryController(mWifiP2pManager, mChannel, wifiDeviceList);
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);    
        mReceiver = new WifiDirectBroadcastReceiver();
	}
	
	private void handlerInitialize(){
		mHandler = new Handler(Looper.getMainLooper()){
			@Override
    		public void handleMessage(Message msg){
    			switch(msg.what){
    			case TIMERTEST:
    				Log.d(TAG, "TimerTest");
    				logToScreen("TimerTest");
    				break;
    			case STARTDISCOVERYSERVICE:
    				mServiceController.clearLocalServices();
    				
    				mHandler.postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mServiceController.registerServiceAndBroadcastInfo();
						}
    					
    				}, 3000);
    				break;
    			case STARTDISCOVERY:
    				mServiceController.clearServiceRequests();
//    				retSetWifi();    				
    				mHandler.postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mServiceController.discoverNearByDevices();
						}
    					
    				}, 2000);
    				break;
    			}
    			super.handleMessage(msg);
    		}
		};
		
		mTimer = new Timer();
		
		testTask = new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = TIMERTEST;
				mHandler.sendMessage(message);
			}
    		
    	};
    	
    	taskDiscoveryService = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = STARTDISCOVERYSERVICE;
				mHandler.sendMessage(message);
			}
    		
    	};
    	
    	taskDiscovery = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = STARTDISCOVERY;
				mHandler.sendMessage(message);
			}
    		
    	};
	}
	
	public static void logToScreen(final String text) {
		Log.i(TAG, text);
		
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(textLog != null){
					textLog.setText(textLog.getText() + "\n" + text);
				}
				Layout layout = textLog.getLayout();
				if(layout != null){
					final int scrollAmount = layout.getLineTop(textLog.getLineCount()) - textLog.getHeight();
					if(scrollAmount > 0){
						textLog.scrollTo(0, scrollAmount);
					} else { //there is no need to scroll
						textLog.scrollTo(0, 0);
					}
				}
			}
			
		});
	}
/*	
	private void retSetWifi(){
		if(mWifiManager != null){
			mWifiManager.setWifiEnabled(false);
			mWifiManager.setWifiEnabled(true);
		}
        
	}
*/	
	@Override
    public void onStart(){
    	super.onStart();
    	Log.i(TAG, "++ ON START ++");    		
    }
	
	 @Override
	    public void onPause(){ 	
	    	super.onPause();
	    	
	    	unregisterReceiver(mReceiver);
	    }
	    @Override
	    public void onResume(){
	    	super.onResume();
	    	
	    	registerReceiver(mReceiver, mIntentFilter);
	    }
	    
	    @Override
	    public void onStop(){
	    	super.onStop();
	    	Log.i(TAG, "++ ON STOP ++");
	   	
	    }
	    
	    @Override
	    public void onRestart(){
	    	super.onRestart();
	    }
	    
	    @Override
	    public void onDestroy(){
	    	Log.i(TAG, "++ ON DESTROY ++");
	    	mServiceController.clearLocalServices();
	    	mServiceController.clearServiceRequests();
	    	mTimer.cancel();
	    	
	    	if(mWakeLock != null){
	    		if (!mWakeLock.isHeld()) {
	    			mWakeLock.acquire();
	    		}
	    	}
	    	MainActivity.logToScreen("AverageDelayTime: " + mServiceController.getAverageTimeDelay());
	    	MainActivity.logToScreen("ValidTimes: " + mServiceController.getValidTimes() + ";AllTimes: " + mServiceController.getAllTimes());
	    	MainActivity.logToScreen("ValidTimes/AllTimes = " + mServiceController.getValidTimes() / mServiceController.getAllTimes());
	    	
	    	super.onDestroy(); 		    	
	    }
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.servicediscovery, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	        if(id == R.id.action_back){
	        	finish();
	        }
	        return super.onOptionsItemSelected(item);
	    }
	    
	    private class WifiDirectBroadcastReceiver extends BroadcastReceiver {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				 String action = intent.getAction();
			        Log.d(TAG, action);
			        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
						// Check to see if Wi-Fi is enabled and notify appropriate activity
						int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
						if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
							ServiceDiscoveryActivity.logToScreen("$^^$Wifi Direct is ON");
						} else {
							ServiceDiscoveryActivity.logToScreen("$^^$Wifi Direct is OFF");
						}
					} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
						// Call WifiP2pManager.requestPeers() to get a list of current peers
						Log.d(TAG, "Peers Changed Action");
						/*
			        	 * If the discovery process succeeds and detects peers, the system broadcasts 
			        	 * the WIFI_P2P_PEERS_CHANGED_ACTION intent, which you can listen for in a broadcast 
			        	 * receiver to obtain a list of peers. When your application receives the 
			        	 * WIFI_P2P_PEERS_CHANGED_ACTION intent, you can request a list of the discovered peers 
			        	 * with requestPeers(). 
			        	 */
						
					} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			            if (mWifiP2pManager == null) { return; }
			            
			            NetworkInfo networkInfo = (NetworkInfo) intent
			                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			            if (networkInfo.isConnected()) {
			                // we are connected with the other device, request connection
			                // info to find group owner IP
			                Log.d(TAG, "Connected to p2p network. Requesting network details");
			                //mWifiP2pManager.requestConnectionInfo(mChannel, null);
			            } else {
			                // It's a disconnect
			            }
			        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
			                .equals(action)) {

			            WifiP2pDevice device = (WifiP2pDevice) intent
			                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
			            Log.d(TAG, "Device status -" + device.status);
			        }
			}	
	    }
}
