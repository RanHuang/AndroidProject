package com.example.wifidirectservicediscoverytest;

import java.util.ArrayList;
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
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

	private static final String TAG = "WifiDirectServiceDiscoveryTest MainActivity";
	//Set for test
//	public static final boolean isGO = true;
	public static final boolean isGO = false;
	
	private Button buttonStartService = null;
	private Button buttonStopService = null;
	private Button buttonStartRequest = null;
	private Button buttonStopRequest = null;
	
	private static TextView textLog = null;
	
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;
	private IntentFilter mIntentFilter = null;
	private BroadcastReceiver mReceiver = null;
	private WifiManager mWifiManager;
	
	private ArrayList<WifiP2pDevice> wifiDeviceList = null;
	private WifiDiscoveryService mWifiDiscoveryService = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mWifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        if(mWifiManager != null && !mWifiManager.isWifiEnabled()){
        	mWifiManager.setWifiEnabled(true);
        }
        
        textLog = (TextView)this.findViewById(R.id.text_log);
        textLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        buttonStartService = (Button)this.findViewById(R.id.button_startService);
        buttonStartService.setOnClickListener(new BtnStartServiceListener());
        buttonStopService = (Button)this.findViewById(R.id.button_stopService);
        buttonStopService.setOnClickListener(new BtnStopServiceListener());
        buttonStartRequest = (Button)this.findViewById(R.id.button_startRequest);
        buttonStartRequest.setOnClickListener(new BtnStartRequestListener());
        buttonStopRequest = (Button)this.findViewById(R.id.button_stopRequest);
        buttonStopRequest.setOnClickListener(new BtnStopRequestListener());
        
        mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        wifiDeviceList = new ArrayList<WifiP2pDevice>();
        mWifiDiscoveryService = new WifiDiscoveryService(mWifiP2pManager, mChannel, wifiDeviceList);
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);    
        mReceiver = new WiFiDirectBroadcastReceiver();
        
                
    }
    
    public static void logToScreen(final String text) {
		Log.i(TAG, text);
		
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable(){

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
    public void onPause(){ 	
    	super.onPause();
    	unregisterReceiver(mReceiver);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if(isGO){
    		MainActivity.logToScreen("This device is set as GO!");
    	} else {
    		MainActivity.logToScreen("This device is not to be GO.");
    	}
    	registerReceiver(mReceiver, mIntentFilter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent callIntent = null;
        switch(id){
        case R.id.action_startActiveScanTest:
        	callIntent = new Intent(MainActivity.this, ActiveScanActivity.class);
        	startActivity(callIntent);
        	return true;
        case R.id.action_startServiceDiscoveryTest:
        	callIntent = new Intent(MainActivity.this, ServiceDiscoveryActivity.class);
        	startActivity(callIntent);
        	return true;
        case R.id.action_back:
        	finish();
        	System.exit(0);
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 String action = intent.getAction();
		        Log.d(TAG, action);
		        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

		            if (mWifiP2pManager == null) {
		                return;
		            }

		            NetworkInfo networkInfo = (NetworkInfo) intent
		                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

		            if (networkInfo.isConnected()) {

		                // we are connected with the other device, request connection
		                // info to find group owner IP
		                Log.d(TAG, "Connected to p2p network. Requesting network details");
//		                mWifiP2pManager.requestConnectionInfo(mChannel, null);
		            } else {
		                // It's a disconnect
		            }
		        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
		                .equals(action)) {

		            WifiP2pDevice device = (WifiP2pDevice) intent
		                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
		            Log.d(TAG, "Device status -" + device.status);
		            logToScreen("Device status -" + getDeviceStatus(device.status));
		        }
		}	
    }
    
    /*
     * Button onClickListener
     */
    private class BtnStartServiceListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiDiscoveryService.registerServiceAndBroadcastInfo();
		}   	
    }
    
    private class BtnStopServiceListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiDiscoveryService.clearLocalServices();
		}   	
    }
    private class BtnStartRequestListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiDiscoveryService.discoverNearByDevices();
		}   	
    }
    
    private class BtnStopRequestListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiDiscoveryService.clearServiceRequests();
		}   	
    }
    
    private static String getDeviceStatus(int statusCode) {
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }
    
}
