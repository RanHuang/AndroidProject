package come.example.wificontrol;

import com.example.wifidirectappvone.MainActivity;

import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.util.Log;

public class WifiP2pConnector {
	
	private final static String TAG = "WifiP2pConnector";
	private static final boolean D = true;
	
	private WifiP2pManager mWifiP2pManager;
    private Channel mChannel;
    WifiManager mWifiManager;
    
    private ConnectionTimeOut mConnectionTimeOut= null;
    
	
	public WifiP2pConnector(WifiP2pManager manager, Channel channel, WifiManager wifiManager){
		if(D) Log.d(TAG, "WifiP2pConnector");
		
		this.mWifiP2pManager = manager;
    	this.mChannel = channel;
    	this.mWifiManager = wifiManager;
    	
    	WifiP2pDeviceStatus.initiate();
    	
	}
	
	public void connectP2p(final WifiP2pDevice device) {
		// TODO Auto-generated method stub
		if(D) Log.d(TAG, "WifiP2p Connect to: " + device.deviceName);
		
		if(WifiP2pDeviceStatus.canDiscover()){
			WifiP2pDeviceStatus.setConnecting();
			 WifiP2pConfig config = new WifiP2pConfig();
	         config.deviceAddress = device.deviceAddress;
	         MainActivity.logToScreen("====" + device.deviceAddress);
	         //config.wps.setup = WpsInfo.PBC;
	         config.wps.setup = WpsInfo.DISPLAY;
	         config.wps.pin = "12345678";
//	         config.groupOwnerIntent = WifiP2pDeviceStatus.getGOIntention();
	         
	         MainActivity.logToScreen("Connecting to " + device.deviceName);
	         mWifiP2pManager.clearServiceRequests(mChannel, new MyActionListener("Clear WifiP2p Service Requsets"));
	         
	         mWifiP2pManager.connect(mChannel, config, new ActionListener(){

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					WifiP2pDeviceStatus.setConnecting();
					startTimeOut();
					MainActivity.logToScreen("Wifi P2p connecting...");
				}

				@Override
				public void onFailure(int reason) {
					// TODO Auto-generated method stub
					MainActivity.logToScreen("Connection error:" + reason);
					WifiP2pDeviceStatus.setDisconnected();
				}
	        	 
	         });
		}
	}
	
	private void startTimeOut() {
		// TODO Auto-generated method stub
		mConnectionTimeOut = new ConnectionTimeOut();
		mConnectionTimeOut.execute();
	}
	
	public void stopTimeOut(){
		if(mConnectionTimeOut != null){
			mConnectionTimeOut.cancel(true);
		}
	}
	
	private class ConnectionTimeOut extends AsyncTask<Object, Object, Object>{
		private static final int TIMEOUT = 38;
		private static final String TAG = "Connection Time Out";
		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(TIMEOUT * 1000);
			}catch(InterruptedException e){
				if(D) Log.i(TAG, "Connection Timeout Cancelled.");
			}
			return null;			
		}
		@Override
		protected void onPostExecute(Object result) {
			if (!WifiP2pDeviceStatus.isConnected()) {
				Log.i(TAG, "Connection Timed Out");
				mWifiP2pManager.cancelConnect(mChannel, null);
				WifiP2pDeviceStatus.setDisconnected();
			}
		}
		
	}

	public void disconnectFromGroup() {
		// TODO Auto-generated method stub
		if(WifiP2pDeviceStatus.isConnected() && mWifiP2pManager != null && mChannel != null){
			mWifiP2pManager.removeGroup(mChannel, new MyActionListener("Wifi P2p Disconnect From Group"));
		}
	}
	
	
	public void resetWifi() {
		Log.d(TAG, "Resetting wifi");
		mWifiManager.setWifiEnabled(false);
		mWifiManager.setWifiEnabled(true);
	}
	
	public void setWifiOn(boolean on){
		if(on) {
			if(mWifiManager.isWifiEnabled()) return;
			mWifiManager.setWifiEnabled(true);
		}
		else mWifiManager.setWifiEnabled(false);
	}

	
}
