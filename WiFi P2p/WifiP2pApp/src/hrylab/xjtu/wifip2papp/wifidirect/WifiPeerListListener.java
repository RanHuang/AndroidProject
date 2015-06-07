package hrylab.xjtu.wifip2papp.wifidirect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
/*
 * @author NickHuang 
 */
public class WifiPeerListListener implements PeerListListener{
	
	private static final String TAG = "WifiPeerListListener";
	private static final boolean D = true;
	
	private WifiConnector mConnector;
	private ExecutorService mExecutor;
	
	public WifiPeerListListener(WifiConnector mConnector){
		this.mConnector = mConnector;
		
		mExecutor = Executors.newCachedThreadPool();
	}
	
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		// TODO Auto-generated method stub
		mExecutor.submit(new UpdateAvailableWifiDevices(peers));
	}
	
	private class UpdateAvailableWifiDevices implements Runnable{

		private WifiP2pDeviceList wifiP2pDeviceList;
		
		public UpdateAvailableWifiDevices(WifiP2pDeviceList wifiP2pDeviceList){
			this.wifiP2pDeviceList = wifiP2pDeviceList;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(D) Log.d(TAG, "Peers available. Count = " + wifiP2pDeviceList.getDeviceList().size());
			mConnector.clearPeersWifiP2pDevices();
			/*
			for(WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()){
				Log.d(TAG, "Available device: " + device.deviceName);
				mConnector.addWifiP2pDevice(device);
			}
			*/
			mConnector.addAllPeersWifiP2pDevices(wifiP2pDeviceList.getDeviceList());
	        mConnector.sendPeersAvaliableBroadcast();
		}
		
	}
}
