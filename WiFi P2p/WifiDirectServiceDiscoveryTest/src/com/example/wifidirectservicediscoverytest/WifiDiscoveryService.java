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

public class WifiDiscoveryService {
	private static final String TAG = "WifiDiscoveryService";
	
	private WifiP2pManager mWifiP2pManager = null;
    private Channel mChannel = null;
    
    private ArrayList<WifiP2pDevice> wifiDeviceList;
    
    // TXT RECORD properties
    public static final String SERVICE_INSTANCE = "WifiP2pService20141102";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    
    public static final String ROLE = "my_role_is";
    
    // The service that broadcasts  relevant information
 	private WifiP2pDnsSdServiceInfo serviceInfo;
 	// The service that discovers other devices
 	private WifiP2pDnsSdServiceRequest serviceRequest;
 	
 	
 	long timeStamp;
 	long timeDelay;
 	
 	public WifiDiscoveryService(WifiP2pManager wifiP2pManager, Channel channel, ArrayList<WifiP2pDevice> peersList){
 		mWifiP2pManager = wifiP2pManager;
 		mChannel = channel;
 		wifiDeviceList = peersList;
 		
 		discoverServiceInitialize();
 	}
 	
 	public WifiDiscoveryService(WifiP2pManager wifiP2pManager, Channel channel){
 		mWifiP2pManager = wifiP2pManager;
 		mChannel = channel;
 		wifiDeviceList = new ArrayList<WifiP2pDevice>();
 		
 		discoverServiceInitialize();
 	}
 	
	private void discoverServiceInitialize(){
		Log.d(TAG, "discoverServiceInitialize");
		DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {
	        /* Callback includes:
	         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
	         * record: TXT record dta as a map of key/value pairs.
	         * device: The device running the advertised service.
	         */
			@Override
			public void onDnsSdTxtRecordAvailable(String fullDomainName,
					Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
				// TODO Auto-generated method stub
				Log.d(TAG, "DnsSdTxtRecord available -" + txtRecordMap.toString());
				String getRecordData = txtRecordMap.get(ROLE);
				if(getRecordData != null){
					//Maybe one item is in more common use. I think at a time a client will want to find one
					//server device bacause it can connect to only one server.
					
					timeDelay = System.currentTimeMillis() - timeStamp;
					wifiDeviceList.add(srcDevice);
					MainActivity.logToScreen("$%& A srcDevice is found. " + srcDevice.deviceName 
							+ "\nFound time:" + System.currentTimeMillis() + "\nDelay Time:" + timeDelay + "\n");					
					Log.d(TAG, "$%& A srcDevice is found. " + srcDevice.deviceName);
				}		
			}
		};
		/*
		DnsSdServiceResponseListener servListener = new DnsSdServiceResponseListener(){
			// A service has been discovered. Is this our app?
			@Override
			public void onDnsSdServiceAvailable(String instanceName,
					String registrationType, WifiP2pDevice srcDevice) {
				// TODO Auto-generated method stub
				MainActivity.logToScreen("$%& A Service is Discovered : " + instanceName + registrationType);
				if(instanceName.equalsIgnoreCase(SERVICE_INSTANCE)){
					
				}
			}
			
		};		
		mWifiP2pManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);
		*/
		mWifiP2pManager.setDnsSdResponseListeners(mChannel, null, txtListener);
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();		
	}
	
	 /**
     * Registers a wifi p2p service that broadcast some info.
     */
	public void registerServiceAndBroadcastInfo(){
//		 Map<String, String> record = new HashMap<String, String>();
//		 record.put("nickname", "Kitty " + (int)(Math.random() * 1000));
		//可以根据具体情况，设置不同的ROLE，然后接收端根据收到的内容进行不同的处理
		 Map<String, String> record = Collections.singletonMap(ROLE, "is_available");
		 
		// Service information. Pass it an instance name, service type _protocol._transportlayer , and the map containing
		// information other devices will want once they connect to this one.
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
						MainActivity.logToScreen("!Succeed!: Add local WifiP2p Service");
					}

					@Override
					public void onFailure(int reason) {
						// TODO Auto-generated method stub
						if(reason == 2){ //If busy, try again after some seconds.
							MainActivity.logToScreen("*Failed*: Add local WifiP2p Service");
//							Log.d(TAG, "*Failed*: Add local WifiP2p Service, Try Again after some time.");
//							try{
//								Thread.sleep(7 * 1000);
//							}catch(InterruptedException e){
//									
//							}
//							Thread threadAddLocalService = new threadAddLoclaService();
//							threadAddLocalService.start();
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
		
//		clearServiceRequests();
		
		this.timeStamp = System.currentTimeMillis();
		MainActivity.logToScreen("\n Start time:" + timeStamp);
		
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
			Log.d(TAG, "onSuccess: " + action);
			MainActivity.logToScreen("onSuccess: " + action);
		}

		@Override
		public void onFailure(int reason) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onFailure: " + action + " " + reason);
			MainActivity.logToScreen("onFailure: " + action + " " + reason);
		}

	}

}
