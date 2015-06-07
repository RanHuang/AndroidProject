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
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
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

public class ActiveScanActivity extends Activity {
	
	public static final String TAG = "ActiveScanActivity";
	
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;
	private IntentFilter mIntentFilter = null;
	private BroadcastReceiver mReceiver = null;
	private WifiManager mWifiManager = null;
	private ActiveScanController mActiveScanController = null;
	private WifiP2pPeerListListener mWifiP2pPeerListListener = null;
	private WifiP2pConnectionInfoListener mWifiP2pConnectionInfoListener = null;
	@SuppressWarnings("unused")
	private WifiP2pDevice thisDevice = null;
	@SuppressWarnings("unused")
	private WifiP2pInfo mWifiP2pInfo = null;
	
	private ArrayList<WifiP2pDevice> wifiDeviceList = null;
	
	private WakeLock mWakeLock = null;
	
	private static TextView textLog = null;
	Timer mTimer = null;
	static Handler mHandler = null;
	TimerTask testTask = null;
	TimerTask taskDiscoverPeers = null;
	TimerTask taskStopDiscoverPeers = null;
	
	//Test Data
	private static boolean isForPeers;
	private static int allTimes;
	private static int validTimes;
	private static long timeStamp;
	private static long timeDelay;
	private static long avgDelay;	
	
	private static boolean isForConnection;
	private static boolean isConnected;
	private static int allConnectionTimes;
	private static int validConnectionTimes;
	private static long timeConnectionStamp;
	private static long timeConnectionDelay;
	private static long avgConnectionDelay;
	
	protected static final int TIMERTEST = 0x101;
	protected static final int STARTPEERSDISCOVERY = 0x201;
	protected static final int STOPPEERDISCOVERY = 0x301;
	protected static final int STARTCONNECTION = 0x202;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activescan);
       
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
        
//      mTimer.schedule(testTask, 2000, 3000);
        allTimes = 0;
        validTimes = 0;
        timeStamp = 0;
        timeDelay = 0;
        avgDelay = 0;
        isForPeers = false;
        
        allConnectionTimes = 0;
    	validConnectionTimes = 0;
    	timeConnectionStamp = 0;
    	timeConnectionDelay = 0;
    	avgConnectionDelay = 0;  
        isConnected = false;
        
        if(MainActivity.isGO){
        	//For GO
        	mActiveScanController.setGroupOwner(true);
        	mTimer.schedule(taskDiscoverPeers, 1000, 120000);         	
        } else {
        	//For not GO
        	mTimer.schedule(taskDiscoverPeers, 1000, 60000);
        	mTimer.schedule(taskStopDiscoverPeers, 34000);//主动扫描在启动扫描30s之后停止        	
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
        mWifiP2pPeerListListener = new WifiP2pPeerListListener();
        mWifiP2pConnectionInfoListener = new WifiP2pConnectionInfoListener();
        
        mActiveScanController = new ActiveScanController(mWifiP2pManager, mChannel);
        
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
    			case STARTPEERSDISCOVERY:
    				mActiveScanController.stopWifiScan();
    				if(MainActivity.isGO){
    					isForConnection = false;
    				} else {
    					isForConnection = true;
    				}
    				mHandler.postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							isForPeers = true;							
							allTimes ++;
							timeStamp = System.currentTimeMillis();
							mActiveScanController.startWifiScan();
						}	
						
    				}, 3000);
    				
    				break;
    			case STARTCONNECTION:
    				if(isGroupOwnerExisted()){
    					isForConnection = false;
    					mHandler.post(new Runnable(){

    						@Override
    						public void run() {
    							// TODO Auto-generated method stub
    							allConnectionTimes ++;
    							timeConnectionStamp = System.currentTimeMillis();
    							mActiveScanController.connectToServer(getServerMAC());
    						}													
    					});
    				}		
    				break;
    			case STOPPEERDISCOVERY:
    				mActiveScanController.stopWifiScan();
    				break;
    			}
    			super.handleMessage(msg);
    		}

			
		};
		
		testTask = new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = TIMERTEST;
				mHandler.sendMessage(message);
			}
    		
    	};
    	
    	taskDiscoverPeers = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = STARTPEERSDISCOVERY;
				mHandler.sendMessage(message);
			}
    		
    	};
    	
    	taskStopDiscoverPeers = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = STOPPEERDISCOVERY;
				mHandler.sendMessage(message);
			}
    		
    	};
    	
		mTimer = new Timer();
	}
	
	private boolean isGroupOwnerExisted() {
		// TODO Auto-generated method stub
		for(WifiP2pDevice device : wifiDeviceList){
			if(device.isGroupOwner()) return true;
		}	
		return false;
	}
	
	private String getServerMAC() {
		// TODO Auto-generated method stub
		for(WifiP2pDevice device : wifiDeviceList){
			if(device.isGroupOwner()) return device.deviceAddress;
		}
		return null;
	}
	
	public static void setForConnection(boolean is){
		isForConnection = is;
	}
	
	public static void setIsConnected(boolean is){
		isConnected = is;
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
	    	
	    	mTimer.cancel();
	    	
	    	if(mWakeLock != null){
	    		if (!mWakeLock.isHeld()) {
	    			mWakeLock.acquire();
	    		}
	    	}
	    	/*
	    	 * For discovering peers
	    	 */
	    	MainActivity.logToScreen("AverageDelayTime: " + avgDelay);
	    	MainActivity.logToScreen("ValidTimes: " + validTimes + ";AllTimes: " + allTimes);
//	    	MainActivity.logToScreen("ValidTimes/AllTimes = " + validTimes / allTimes);
	    	/*
	    	 *For connection 
	    	 */
	    	MainActivity.logToScreen("AverageConnectionDelayTime: " + avgConnectionDelay);
	    	MainActivity.logToScreen("ValidConnectionTimes: " + validConnectionTimes + ";AllConnectionTimes: " + allConnectionTimes);
//	    	MainActivity.logToScreen("ValidTimes/AllTimes = " + validConnectionTimes / allConnectionTimes);
	    	
	    	super.onDestroy(); 	    	
	    }
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.activescan, menu);
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
	    
	    private class WifiP2pPeerListListener implements PeerListListener{

			@Override
			public void onPeersAvailable(WifiP2pDeviceList peers) {
				// TODO Auto-generated method stub
				Log.d(TAG, "PeersAvailable");
				wifiDeviceList.clear();
				if(peers.getDeviceList().size() > 0){
					if(isForPeers){
						validTimes ++;
						timeDelay = System.currentTimeMillis() - timeStamp;
						avgDelay = (avgDelay * (validTimes - 1) + timeDelay) / validTimes;
						isForPeers = false;
					}
					wifiDeviceList.addAll(peers.getDeviceList());
//					ActiveScanActivity.logToScreen("$%&Found:" + wifiDeviceList.size());
//					ActiveScanActivity.logToScreen("validTimes: " + validTimes + "\n");		
					if((!isConnected) && isForConnection){
						Message message = new Message();
						message.what = STARTCONNECTION;
						mHandler.sendMessage(message);
					}
				}
												
			}
	    	
	    }
	    
	    private class WifiP2pConnectionInfoListener implements ConnectionInfoListener{

			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				// TODO Auto-generated method stub
				Log.d(TAG, "ConnectionInfoAvailable");
				mWifiP2pInfo = info;
				if(info.isGroupOwner){
					Log.d(TAG, "ConnectionInfoAvailable, is GO!");
					ActiveScanActivity.logToScreen("(^^)Is GO!");
				} else {
					mActiveScanController.disConnect();
				}				
			}
	    	
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
							ActiveScanActivity.logToScreen("$^^$Wifi Direct is ON");
						} else {
							ActiveScanActivity.logToScreen("$^^$Wifi Direct is OFF");
						}
					} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
						// Call WifiP2pManager.requestPeers() to get a list of current peers
						Log.d(TAG, "Peers Changed Action");
						
						mWifiP2pManager.requestPeers(mChannel, mWifiP2pPeerListListener);
						
					} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			            if (mWifiP2pManager == null) { return; }
			            
			            NetworkInfo networkInfo = (NetworkInfo) intent
			                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			            if (networkInfo.isConnected()) {
			                // we are connected with the other device, request connection
			                // info to find group owner IP
			            	mActiveScanController.stopWifiScan();
			            	if(!isConnected && !isForConnection){
			            		validConnectionTimes ++;
			            		timeConnectionDelay = System.currentTimeMillis() - timeConnectionStamp;
			            		avgConnectionDelay = (avgConnectionDelay * (validConnectionTimes - 1) + timeConnectionDelay) / validConnectionTimes;
			            		isConnected = true;
			            	}
			            				            
			            	ActiveScanActivity.logToScreen("IS connected.");
			                Log.d(TAG, "Connected to p2p network. Requesting network details");
			                mWifiP2pManager.requestConnectionInfo(mChannel, mWifiP2pConnectionInfoListener);
			            } else {
			                // It's a disconnect
			            	isConnected = false;
			            	ActiveScanActivity.logToScreen("IS disconnected.");
			            }
			        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
			                .equals(action)) {

			            WifiP2pDevice device = (WifiP2pDevice) intent
			                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
			            Log.d(TAG, "Device status -" + device.status);
			            thisDevice = device;
			        }
			}	
	    }
}
