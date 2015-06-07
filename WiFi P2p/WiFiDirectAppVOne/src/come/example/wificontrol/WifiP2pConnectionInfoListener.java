package come.example.wificontrol;

import java.net.InetAddress;

import com.example.data.ClientMessageSender;
import com.example.data.ServerMessageReceiver;
import com.example.wifidirectappvone.MainActivity;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;

public class WifiP2pConnectionInfoListener implements ConnectionInfoListener {

	private final static String TAG = "WifiP2pConnectionInfoListener";
	private static final boolean D = true;
	
	private WifiP2pConnector mWifiP2pConnector;
	
	public WifiP2pConnectionInfoListener(WifiP2pConnector wifiP2pConnector){
		this.mWifiP2pConnector = wifiP2pConnector;
	}
	
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		InetAddress groupOwner = info.groupOwnerAddress;
		if (groupOwner != null) {
			
			String groupOwnerAddress = groupOwner.getHostAddress();

			String message = (info.isGroupOwner) ? "@@@@I am the group owner"
					: "@@@@Group owner is the other device";
			MainActivity.logToScreen(message);

			
			if(info.groupFormed){
				if(info.isGroupOwner){
					WifiP2pDeviceStatus.setGroupOwner(true);
					MainActivity.logToScreen("&(^^)&In ConnectionInfoListener: is GO!");
					//THIS Thread should be set in the WifiController, otherwise BindException occurred: Address already in use.
					new Thread(new ServerMessageReceiver()).start();
					
				} else {
					// Send the message if we are not the owner..
					WifiP2pDeviceStatus.setGroupOwner(false);
					MainActivity.logToScreen("&(^^)&In ConnectionInfoListener: is NOT GO*");
					
					//new Thread(new MessageSender(groupOwnerAddress, MessageReceiverServer.PORT,_connector), "MessageSender Thread").start();
					MainActivity.logToScreen("TO DO:A Client Thread Start here.");
					new Thread(new ClientMessageSender(groupOwnerAddress, ServerMessageReceiver.PORT)).start();
				}
			}
			
		} else {
			if(D) Log.d(TAG,"In ConnectionInfoListener: there is no groupOwner, disconnectFromGroup!");
			mWifiP2pConnector.disconnectFromGroup();
		}
	}

}
