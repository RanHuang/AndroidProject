package hrylab.xjtu.wifip2papp.wifidirect;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
/*
 * @author NickHuang 
 */
public class WifiConnectionInfoListener implements ConnectionInfoListener{
	
	private static String TAG = "WifiConnectionInfoListener";
	
	private WifiConnector mConnector;
	
	public WifiConnectionInfoListener(WifiConnector mConnector){
		this.mConnector = mConnector;
		
	}
	
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		mConnector.setWifiP2pInfo(info);
		mConnector.sendConnectionInfoAvailableBroadcast();
		Log.i(TAG, "onConnectionInfoAvailable...");
		/*
		InetAddress groupOwnerAddress = info.groupOwnerAddress;
		if(groupOwnerAddress != null){
			//The wifi connection is formed.		

			String message = (info.isGroupOwner) ? "I am the group owner"
					:"Group owner is the other device";
			Log.i(TAG, message);
				
			mConnector.connected();
			if(info.groupFormed && !info.isGroupOwner){
				//If we are not the group owner
				String groupOwnerHostAddress = groupOwnerAddress.getHostAddress();
				new Thread(new MessageSender(groupOwnerAddress, MessageReceiverServer.PORT,
						_connector), "MessageSender Thread").start();
				
			} else if(info.groupFormed && info.isGroupOwner){
				//If we are the owner
				
			}
		} else {
			//mConnector.disconnect();
			mConnector.setWifiP2pInfo(null);
		}
		*/
	}

}
