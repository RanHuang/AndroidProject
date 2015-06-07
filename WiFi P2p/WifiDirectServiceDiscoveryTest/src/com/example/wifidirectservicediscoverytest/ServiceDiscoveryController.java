package com.example.wifidirectservicediscoverytest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

public class ServiceDiscoveryController {

private static final String TAG = "WifiP2pServiceDiscovery";
	
	private WifiP2pManager mWifiP2pManager = null;
    private Channel mChannel = null;
    
    private ArrayList<WifiP2pDevice> wifiDeviceList;
    
    // TXT RECORD properties
    public static final String SERVICE_INSTANCE = "WifiP2pDiscoveryService20141118";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    
    private static final String ROLE = "my_role_is";
    
    // The service that broadcasts  relevant information
 	private WifiP2pDnsSdServiceInfo serviceInfo;
 	// The service that discovers other devices
 	private WifiP2pDnsSdServiceRequest serviceRequest;
 	
 	//Test Data
 	private static int allTimes;
 	private static int validTimes;
 	private long timeStamp;
 	private long timeDelay;
 	private static long avgDelay;
 	
	public ServiceDiscoveryController(WifiP2pManager wifiP2pManager, Channel channel, ArrayList<WifiP2pDevice> peersList) {
		// TODO Auto-generated constructor stub
		mWifiP2pManager = wifiP2pManager;
 		mChannel = channel;
 		wifiDeviceList = peersList;
 		
 		ServiceDiscoveryController.allTimes = 0;
 		ServiceDiscoveryController.validTimes = 0;
 		ServiceDiscoveryController.avgDelay = 0;
 		
 		discoverServiceInitialize();
	}
	
	public int getAllTimes(){ return ServiceDiscoveryController.allTimes; }
	public int getValidTimes(){ return ServiceDiscoveryController.validTimes; }
	public long getAverageTimeDelay(){ return ServiceDiscoveryController.avgDelay; }
	
	private void discoverServiceInitialize(){
		Log.d(TAG, "discoverServiceInitialize");
		DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {
			@Override
			public void onDnsSdTxtRecordAvailable(String fullDomainName,
					Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
				// TODO Auto-generated method stub
				Log.d(TAG, "DnsSdTxtRecord available -" + txtRecordMap.toString());
				String getRecordData = txtRecordMap.get(ROLE);
				if(getRecordData != null){
					//Maybe one item is in more common use. I think at a time a client will want to find one
					//server device bacause it can connect to only one server.
					validTimes ++;
					timeDelay = System.currentTimeMillis() - timeStamp;
					avgDelay = (avgDelay * (validTimes - 1) + timeDelay) / validTimes;
					
					wifiDeviceList.add(srcDevice);
					ServiceDiscoveryActivity.logToScreen("$%&Found:" + srcDevice.deviceName + "\n" + "validTimes: " + validTimes + "\n");
					Log.d(TAG, "$%& A srcDevice is found. " + srcDevice.deviceName);
				}		
			}
		};
		
		mWifiP2pManager.setDnsSdResponseListeners(mChannel, null, txtListener);
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();		
	}
	
	 /**
     * Registers a wifi p2p service that broadcast some info.
     */
	public void registerServiceAndBroadcastInfo(){
		 Map<String, String> record = Collections.singletonMap(ROLE, "is_available");
		 
		serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		 
		// Add the local service, sending the service info, network channel, and listener that will be used to indicate success or failure of the request.
		Thread threadAddLocalService = new threadAddLoclaService();
		threadAddLocalService.start();
	}
	
	private class threadAddLoclaService extends Thread{
		@Override
		public void run(){
			
				mWifiP2pManager.addLocalService(mChannel, serviceInfo, new ActionListener(){

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.d(TAG, "!Succeed!: Add local WifiP2p Service");
						ServiceDiscoveryActivity.logToScreen("!Succeed!: Add local WifiP2p Service");
					}

					@Override
					public void onFailure(int reason) {
						// TODO Auto-generated method stub
						if(reason == 2){ //If busy, try again after some seconds.
							ServiceDiscoveryActivity.logToScreen("*Failed*: AddLocalService:" + reason);
							Log.d(TAG, "*Failed*: Add local WifiP2p Service, Try Again after some time.");
							try{
								Thread.sleep(5 * 1000);
							}catch(InterruptedException e){
									
							}
							Thread threadAddLocalService = new threadAddLoclaService();
							threadAddLocalService.start();
						}
					}					
				});
		}
	}
	
	/**
	 * Searches for nearby devices broadcasting the info. Any devices
	 * found will be added to the discoveredWifiP2pDevice list.
	 */
	public void discoverNearByDevices(){
		Log.d(TAG, "discoverNearByDevices()");
		
		this.timeStamp = System.currentTimeMillis();
		ServiceDiscoveryController.allTimes ++;
		
		mWifiP2pManager.addServiceRequest(mChannel, serviceRequest, new MyActionListener("Add WifiP2p Service Requset"));
		
		mWifiP2pManager.discoverServices(mChannel, new MyActionListener("WifiP2p Discover Service"));
	}
	
	public void clearServiceRequests(){
		mWifiP2pManager.clearServiceRequests(mChannel, new MyActionListener("Clear WifiP2p Service Requsets"));
	}
	
	public void clearLocalServices(){
		mWifiP2pManager.clearLocalServices(mChannel, new MyActionListener("Clear local WifiP2p Services"));
	}

	private final class MyActionListener implements WifiP2pManager.ActionListener  {
		private String action = null;
		
		public MyActionListener(String string){
			action = string;
		}
		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			ServiceDiscoveryActivity.logToScreen("onSuccess: " + action);
			Log.d(TAG, "onSuccess: " + action);
		}

		@Override
		public void onFailure(int reason) {
			// TODO Auto-generated method stub
			ServiceDiscoveryActivity.logToScreen("onFailure: " + action + " " + reason);
			Log.d(TAG, "onFailure: " + action + " " + reason);
		}

	}


}
