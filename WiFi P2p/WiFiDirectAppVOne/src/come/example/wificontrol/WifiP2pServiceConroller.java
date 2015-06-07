package come.example.wificontrol;

import com.example.wifidirectappvone.MainActivity;

public class WifiP2pServiceConroller implements Runnable {

	private static final int MIN_DISCOVERY_INTERVAL = 30;
	private static final int MAX_DISCOVERY_INTERVAL = 300;
	private static int DISCOVERY_INTERVAL = 30;
	
	private WifiP2pServiceDiscovery mWifiP2pServiceDiscovery;
	
	public WifiP2pServiceConroller(WifiP2pServiceDiscovery wifiP2pServiceDiscovery){
		this.mWifiP2pServiceDiscovery = wifiP2pServiceDiscovery;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			if(WifiP2pDeviceStatus.isConnected()){
				if(!WifiP2pDeviceStatus.isConnectionInfoRequestSent()){
					
					mWifiP2pServiceDiscovery.requestConnectionInfo();
					//
					WifiP2pDeviceStatus.setConnectionInfoRequestSent();
				}
				DISCOVERY_INTERVAL += 30;
				if(DISCOVERY_INTERVAL > MAX_DISCOVERY_INTERVAL) {
					DISCOVERY_INTERVAL = MIN_DISCOVERY_INTERVAL;
				}
			}else {
				mWifiP2pServiceDiscovery.discover();
			}
						
			try{
				Thread.sleep(DISCOVERY_INTERVAL * 1000);
			}catch(InterruptedException e){
				MainActivity.logToScreen(e.toString());
			}
			
		}
		
	}

}
