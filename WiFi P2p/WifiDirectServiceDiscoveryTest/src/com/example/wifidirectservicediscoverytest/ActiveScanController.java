package com.example.wifidirectservicediscoverytest;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class ActiveScanController {
	
	private static final String TAG = "ActiveScanController";
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;

	public ActiveScanController(WifiP2pManager wifiP2pManager, Channel channel) {
		// TODO Auto-generated constructor stub
		mWifiP2pManager = wifiP2pManager;
		mChannel = channel;
	}

	
	public void setGroupOwner(boolean set){
		if(set){
			mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener(){

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					Log.d(TAG, "createGroup success");
					ActiveScanActivity.logToScreen("createGroup success!");
				}

				@Override
				public void onFailure(int reason) {
					// TODO Auto-generated method stub
					Log.d(TAG, "createGroup failure" + reason);
					ActiveScanActivity.logToScreen("createGroup failure");
				}
				
			});
		}
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
				ActiveScanActivity.logToScreen("discovery success");
			/*	
				final Handler discoveryHandler = new Handler();
				discoveryHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						stopWifiScan();
					}
				}, 30000); //The delay time need to be set carefully.
			*/	
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.d(TAG, "discovery fail");
				ActiveScanActivity.logToScreen("discovery fail");
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
	public void connectToServer(String mac){
		Log.d(TAG, "connect to:" + mac);	
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = mac;
		config.wps.setup = WpsInfo.PBC;
		
		mWifiP2pManager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onSuccess() {
			    Log.d(TAG, "connectWifiServer success");
			    ActiveScanActivity.logToScreen("connectWifiServer success");
			}

			@Override
			public void onFailure(int reason) {
				Log.d(TAG, "connectWifiServer failure:" + reason);
				ActiveScanActivity.logToScreen("connectWifiServer success");
				ActiveScanActivity.setForConnection(true);
			}
		});
	}
	
	//disconnectFromGroup
	public void disConnect(){ 
		if (mWifiP2pManager != null && mChannel != null) {
			mWifiP2pManager.removeGroup(mChannel, new ActionListener() {

				@Override
				public void onSuccess() {
					ActiveScanActivity.setIsConnected(false);
				}

				@Override
				public void onFailure(int reason) {
					Log.w(TAG, "Disconnect failed: " + reason);
					
				}
			});
		}
	}
	
	
}
