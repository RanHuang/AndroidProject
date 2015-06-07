package com.example.wifip2pactivescanexpa;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class WifiP2pController {
	
	private static final String TAG = "ActiveScanController";
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;

	public WifiP2pController(WifiP2pManager wifiP2pManager, Channel channel) {
		// TODO Auto-generated constructor stub
		mWifiP2pManager = wifiP2pManager;
		mChannel = channel;
	}
	
	/**
	 * start a scan, isStart to check if it is to start or to cancel a scan
	 */
	public void startWifiScan(){
		// start wifi manager and call the discoverpeers method
		mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "discovery success");
				MainActivity.logToScreen("discovery success");
				
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.d(TAG, "discovery fail");
				MainActivity.logToScreen("discovery fail");
			}
		});
	}
	
	public void stopWifiScan(){
		Log.d(TAG, "wifip2p scanning stopped");
		mWifiP2pManager.stopPeerDiscovery(mChannel, null);
	}
	
	/**
	 * connect to a device
	 * add necessary parameters here
	 */
	public void connectToDevice(String mac){
		Log.d(TAG, "connect to:" + mac);	
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = mac;
		config.wps.setup = WpsInfo.PBC;
		
		mWifiP2pManager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onSuccess() {
			    Log.d(TAG, "connect to Device success");
			    MainActivity.logToScreen("connect to Device success");
			}

			@Override
			public void onFailure(int reason) {
				Log.d(TAG, "connect to Device failure:" + reason);
				MainActivity.logToScreen("connect to Device success");
			}
		});
	}
	
	//disconnectFromGroup
	public void disConnect(){ 
		if (mWifiP2pManager != null && mChannel != null) {
			mWifiP2pManager.removeGroup(mChannel, new ActionListener() {

				@Override
				public void onSuccess() {
					
				}

				@Override
				public void onFailure(int reason) {
					Log.w(TAG, "Disconnect failed: " + reason);
					
				}
			});
		}
	}

	public void setGroupOwner() {
		// TODO Auto-generated method stub
		mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener(){

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.i(TAG, "Setting this Device as Group Owner initialized... ");
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				Log.i(TAG, "Set this Device as Group Owner Failed : " + reason);
			}
			
		});
	}
	
}
