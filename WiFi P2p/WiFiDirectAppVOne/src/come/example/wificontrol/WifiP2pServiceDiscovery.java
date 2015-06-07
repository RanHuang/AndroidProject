package come.example.wificontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.example.wifidirectappvone.MainActivity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;


public class WifiP2pServiceDiscovery {
	
	private static final String TAG = "WifiP2pServiceDiscovery";
	private static final boolean D = true;
	private static final boolean L = true;
	
	private WifiP2pManager mWifiP2pManager = null;
    private Channel mChannel = null;
    private WifiP2pConnector mWifiP2pConnector = null;
	
    private List<WifiP2pDevice> discoveredWifiP2pDevice;
    
    // TXT RECORD properties
    public static final String SERVICE_INSTANCE = "WifiP2pService20141102";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    
    // The service that broadcasts  relevant information
 	private WifiP2pDnsSdServiceInfo serviceInfo;
 	// The service that discovers other devices
 	private WifiP2pDnsSdServiceRequest serviceRequest;
 	
    
	public WifiP2pServiceDiscovery(WifiP2pManager wifiP2pManager, Channel channel, WifiP2pConnector wifiP2pConnector){
		this.discoveredWifiP2pDevice = new ArrayList<WifiP2pDevice>();
		this.mChannel = channel;
		this.mWifiP2pConnector = wifiP2pConnector;
		this.mWifiP2pManager = wifiP2pManager;
		
		discoverServiceInitialize();
		
	}
	
	private void discoverServiceInitialize(){
		if(L) MainActivity.logToScreen(TAG + " :discoverServiceInitialize()");
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
				if(D) Log.d(TAG, "DnsSdTxtRecord available -" + txtRecordMap.toString());
				String getRecordData = txtRecordMap.get(ConstantMessage.ROLE);
				if(getRecordData != null){
					discoveredWifiP2pDevice.add(srcDevice);
				}
						
				if(L) MainActivity.logToScreen("$%& A srcDevice is found. " + srcDevice.deviceName);	
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
		mWifiP2pConnector.resetWifi();		
	}
	 /**
     * Registers a wifi p2p service that broadcast some info.
     */
	private void registerServiceAndBroadcastInfo(){
//		 Map<String, String> record = new HashMap<String, String>();
//		 record.put("nickname", "Kitty " + (int)(Math.random() * 1000));
		//可以根据具体情况，设置不同的ROLE，然后接收端根据收到的内容进行不同的处理
		 Map<String, String> record = Collections.singletonMap(ConstantMessage.ROLE, ConstantMessage.IS_AVAILABLE);
		 
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
						if(L) MainActivity.logToScreen("!Succeed!: Add local WifiP2p Service");
					}

					@Override
					public void onFailure(int reason) {
						// TODO Auto-generated method stub
						if(reason == 2){ //If busy, try again after some seconds.
							if(D) MainActivity.logToScreen("*Failed*: Add local WifiP2p Service, Try Again after some time.");
							try{
								Thread.sleep(7 * 1000);
							}catch(InterruptedException e){
									
							}
							Thread threadAddLocalService = new threadAddLoclaService();
							threadAddLocalService.start();
						}
					}					
				});
		}
	}
	//Select a nearby device from the discoveredWifiP2pDevice list and connect to it.
	public void discover(){
			
		if(L) MainActivity.logToScreen(TAG + ":discover()");
		if(WifiP2pDeviceStatus.isConnected()){
			if(WifiP2pDeviceStatus.isGroupOwner()){
				MainActivity.logToScreen("-!-Is GO.");
				return;
			} else {
				MainActivity.logToScreen("-!-Is Client.");
				clearLocalServices();
			}
			
		} else {
			MainActivity.logToScreen("---Is Not Connected.");
			WifiP2pDevice destination = selectDestinationDevice(discoveredWifiP2pDevice);
			if(destination != null){
				mWifiP2pConnector.connectP2p(destination);
			}
			if(WifiP2pDeviceStatus.canDiscover()) {
				registerServiceAndBroadcastInfo();
				discoverNearByDevices();
			}	
		}
		
		discoveredWifiP2pDevice.clear();
		
	}
	
	//这个可能需要根据实际情况进行调整
	private WifiP2pDevice selectDestinationDevice(
			List<WifiP2pDevice> discoveredDevices) {
		// TODO Auto-generated method stub
		if(discoveredDevices.isEmpty()){
			return null;
		} else {
			return discoveredDevices.get(0);
		}
	}

	/**
	 * Searches for nearby devices broadcasting the info. Any devices
	 * found will be added to the discoveredWifiP2pDevice list.
	 */
	private void discoverNearByDevices(){
		if(L) MainActivity.logToScreen("discoverNearByDevices()");
		
		clearServiceRequests();
		
		mWifiP2pManager.addServiceRequest(mChannel, serviceRequest, new MyActionListener("Add WifiP2p Service Requset"));
		
		mWifiP2pManager.discoverServices(mChannel, new MyActionListener("WifiP2p Discover Service"));
	}
	/*
	private class discoverServices extends Thread {
		
	}
	 */	
	private void clearServiceRequests(){
		mWifiP2pManager.clearServiceRequests(mChannel, new MyActionListener("Clear WifiP2p Service Requsets"));
	}
	
	private void clearLocalServices(){
		mWifiP2pManager.clearLocalServices(mChannel, new MyActionListener("Clear local WifiP2p Services"));
	}

	public void requestConnectionInfo() {
		// TODO Auto-generated method stub
		mWifiP2pConnector.stopTimeOut();
		mWifiP2pManager.requestConnectionInfo(mChannel, new WifiP2pConnectionInfoListener(mWifiP2pConnector));
	}

}
