package com.example.wifidirectdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.Toast;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiDirectActivity mActivity;
    
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
    		WifiDirectActivity wifiDirectActivity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = wifiDirectActivity;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		 if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
	            // Check to see if Wi-Fi is enabled and notify appropriate activity
			 	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			 	if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
			 		//Wifi P2P is enabled
			 		mActivity.setIsWifiP2pEnabled(true);
			 	} else {
			 		//Wifi P2P is not enabled
			 		mActivity.setIsWifiP2pEnabled(false);
			 	}
	        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
	            // Call WifiP2pManager.requestPeers() to get a list of current peers
	        	/*
	        	 * If the discovery process succeeds and detects peers, the system broadcasts 
	        	 * the WIFI_P2P_PEERS_CHANGED_ACTION intent, which you can listen for in a broadcast 
	        	 * receiver to obtain a list of peers. When your application receives the 
	        	 * WIFI_P2P_PEERS_CHANGED_ACTION intent, you can request a list of the discovered peers 
	        	 * with requestPeers(). 
	        	 */
	        	if(mManager != null){
	        		mManager.requestPeers(mChannel, mActivity.peerListListener);
	        	}
	        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
	            // Respond to new connection or disconnections
	        	if(mManager == null){
	        		return;
	        	}
	        	NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
	        	if(networkInfo.isConnected()){
	        		Toast.makeText(mActivity, "Wifi Direct is connected.", Toast.LENGTH_LONG).show();
	        		mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
	        	} else {
	        		//It's disconnect.
	        		Toast.makeText(mActivity, "Wifi Direct is disconnected.", Toast.LENGTH_SHORT).show();
	        	}
	        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
	            // Respond to this device's wifi state changing
	        	mActivity.updateDeviceStatus((WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
	        }
		
	}

}
