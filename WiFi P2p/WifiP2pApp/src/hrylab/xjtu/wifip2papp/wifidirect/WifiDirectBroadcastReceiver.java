package hrylab.xjtu.wifip2papp.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
/*
 * @author NickHuang 
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver{
	public static final String TAG = "WifiDirectBroadcastReceiver";
	
	private WifiP2pManager mManager;
    private Channel mChannel;
    //WifiConnector : For saving the update of the WifiPeersList in WifiPeerListListener
    private WifiConnector mConnector;
    private PeerListListener mPeerListListener;
    private ConnectionInfoListener mWifiConnectionInfoListener;
    
    public WifiDirectBroadcastReceiver(WifiP2pManager mManager, Channel mChannel, WifiConnector mConnector, 
    		PeerListListener mPeerListListener, ConnectionInfoListener mWifiConnectionInfoListener){
    	this.mManager = mManager;
    	this.mChannel = mChannel;
    	this.mConnector = mConnector;   	
    	this.mPeerListListener = mPeerListListener;
    	this.mWifiConnectionInfoListener = mWifiConnectionInfoListener;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			// Check to see if Wi-Fi is enabled and notify appropriate activity
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				Log.d(TAG, "Wifi Direct ON");
				mConnector.setWifiP2pEnabled(true);
			} else {
				Log.w(TAG, "Wifi Direct OFF");
				mConnector.setWifiP2pEnabled(false);
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
			if(mManager != null){
				mManager.requestPeers(mChannel, mPeerListListener);
        	}
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			// Respond to new connection or disconnections
			Log.d(TAG, "P2P Connection changed");
			if(mManager == null){
        		return;
        	}
			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			if (networkInfo.isConnected()) {

				String message = "Received request to connect to another device.";
				Log.v(TAG, message);
				// we are connected with the other device, request connection
				// info to find group owner IP
				mManager.requestConnectionInfo(mChannel, mWifiConnectionInfoListener);
			} else {
				Log.i(TAG, "Received a disconnect action.");
//				mConnector.disconnected();
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			// Respond to this device's wifi state changing
			mConnector.updateThisDevice((WifiP2pDevice)intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
		}
	}

}
