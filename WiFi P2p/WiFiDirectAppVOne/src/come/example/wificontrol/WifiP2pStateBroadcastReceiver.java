package come.example.wificontrol;

import com.example.wifidirectappvone.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiP2pStateBroadcastReceiver extends BroadcastReceiver{
	
	private static final String TAG = "WifiP2pStateBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			// Check to see if Wi-Fi is enabled and notify appropriate activity
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				MainActivity.logToScreen("$^^$Wifi Direct is ON");
			} else {
				MainActivity.logToScreen("$^^$Wifi Direct is OFF");
			}
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// Call WifiP2pManager.requestPeers() to get a list of current peers
			Log.d(TAG, "Peers Changed Action");
			/*
        	 * If the discovery process succeeds and detects peers, the system broadcasts 
        	 * the WIFI_P2P_PEERS_CHANGED_ACTION intent, which you can listen for in a broadcast 
        	 * receiver to obtain a list of peers. When your application receives the 
        	 * WIFI_P2P_PEERS_CHANGED_ACTION intent, you can request a list of the discovered peers 
        	 * with requestPeers(). 
        	 */
			
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			// Respond to new connection or disconnections
			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			
			if (networkInfo.isConnected()) {	
				WifiP2pDeviceStatus.setConnected();
			
				MainActivity.logToScreen("$^^$ Is Connected.");
				// we are connected with the other device, request connection info to find group owner IP				
				
			} else {
				Log.d(TAG, "Received a disconnect action.");
				MainActivity.logToScreen("$^^$ Is Not Connected!");				
				WifiP2pDeviceStatus.setDisconnected();
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			// Respond to this device's wifi state changing
			WifiP2pDeviceStatus.updateThisDevice((WifiP2pDevice) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
			
			MainActivity.logToScreen("$^^$ Update this Device.");
		}
	}
}
