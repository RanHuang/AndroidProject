package hrylab.xjtu.wifip2papp.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

public class WifiController {

	private static final String TAG = "WifiP2pDemo WifiController";
	private static final boolean D = true;
	
	private WifiConnector mWifiConnector = null;
	
	public WifiController(WifiConnector mWifiConnector) {
		// TODO Auto-generated constructor stub
		this.mWifiConnector = mWifiConnector;
	}

	public void startWifiScan(boolean isStart){
		if(isStart){
			if(mWifiConnector.isWifiP2pEnabled()){
				mWifiConnector.startDiscoverPeers();
			}			
		} else {
			mWifiConnector.stopPeerDiscovery();
		}
	}
	
	public void setGroupOwner(){
		//Set the GO as the Server
		mWifiConnector.setGroupOwner();
		//Add your Server Service
		
	}
	
	public boolean connectToWifiServer(){
		if(D) Log.v(TAG, "WifiP2pDemo WifiController : connectToWifiServer");
		WifiP2pDevice wifiP2pServer = null;
		for(WifiP2pDevice device : mWifiConnector.getAllPeersWifiP2pDevices()){
			if(device.isGroupOwner()){
				wifiP2pServer = device;
			}
		}
		if(wifiP2pServer != null){
			mWifiConnector.connect(wifiP2pServer);
			return true;
		}
		return false;
	}
	
	public void sendToWifiDevice(){
		
	}

	public void connect() {
		// TODO Auto-generated method stub
		WifiP2pDevice wifiP2pDevice = null;
		for(WifiP2pDevice device : mWifiConnector.getAllPeersWifiP2pDevices()){
			if(device != null){
				wifiP2pDevice = device;
				break;
			}
		}
		if(wifiP2pDevice != null){
			mWifiConnector.connect(wifiP2pDevice);
		}	
	}
		
}
