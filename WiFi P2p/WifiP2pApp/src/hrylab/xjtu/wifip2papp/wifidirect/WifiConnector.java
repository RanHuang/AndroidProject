package hrylab.xjtu.wifip2papp.wifidirect;

import hrylab.xjtu.wifip2papp.WifiDirectTestActivity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class WifiConnector {
	
	private static final String TAG = "WifiP2pDemo WifiDirectTestActivity";
	private static final boolean D = true;
	
	private WifiP2pManager mManager = null;
    private Channel mChannel = null;
    
    private Set<WifiP2pDevice> peersWifiP2pDevices = null;
    
    private WifiP2pDevice thisDevice = null;
    private WifiP2pInfo mWifiP2pInfo = null;
    
    private boolean isWifiP2pEnabled = false;
    
    public WifiConnector(WifiP2pManager mManager, Channel mChannel){
    	this.mManager = mManager;
    	this.mChannel = mChannel;
    	if(D) Log.i(TAG, "new HashSet<WifiP2pDevice> to store the list of peers");
    	this.peersWifiP2pDevices = new HashSet<WifiP2pDevice>();
    }
    
    //code for test    
    private boolean isTestActivityRunning = false;
    public WifiDirectTestActivity mListActivity = null;
    public WifiConnector(WifiDirectTestActivity activity, WifiP2pManager mManager, Channel mChannel, boolean isTestActivityRunning){
    	this.mManager = mManager;
    	this.mChannel = mChannel;
    	if(D) Log.i(TAG, "new HashSet<WifiP2pDevice> to store the list of peers");
    	this.peersWifiP2pDevices = new HashSet<WifiP2pDevice>();
    	mListActivity = activity;
    	this.isTestActivityRunning = isTestActivityRunning;
    }
    public void sendPeersAvaliableBroadcast(){
    	mListActivity.sendSelfPeerListAvailableBroadcast();
    }
    public void sendConnectionInfoAvailableBroadcast(){
    	mListActivity.sendSelfConnectionInfoAvailableReceiver();
    }
    //end of code for test
	public void setWifiP2pEnabled(boolean isEnabled) {
		// TODO Auto-generated method stub
		isWifiP2pEnabled = isEnabled;
	}
	
	public boolean isWifiP2pEnabled(){
		return isWifiP2pEnabled;
	}
	
	public void clearPeersWifiP2pDevices(){
		peersWifiP2pDevices.clear();
	}
	
	public void addAllPeersWifiP2pDevices(Collection<WifiP2pDevice> peers){
		peersWifiP2pDevices.addAll(peers);
	}
	
	public void addWifiP2pDevice(WifiP2pDevice device){
		peersWifiP2pDevices.add(device);
	}
	
	public Collection<WifiP2pDevice> getAllPeersWifiP2pDevices(){
		return peersWifiP2pDevices;
	}
	
	public void updateThisDevice(WifiP2pDevice device){
		thisDevice = device;

		if(isTestActivityRunning){
			mListActivity.updateThisDeviceStatus();
		}
	}
	
	public WifiP2pDevice getThisDevice(){
		return thisDevice;
	}

	public void startDiscoverPeers() {
		// TODO Auto-generated method stub
    	mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(D) Log.i(TAG, "Discovery Initiated");
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				if(D) Log.i(TAG, "Discovery Failed : " + reason);
			}
		});
	}
	
	public void stopPeerDiscovery(){
		mManager.stopPeerDiscovery(mChannel, null);
	}
	public void connect(WifiP2pDevice device) {
		// TODO Auto-generated method stub
		 WifiP2pConfig config = new WifiP2pConfig();
         config.deviceAddress = device.deviceAddress;
         //config.wps.setup = WpsInfo.PBC;
         config.wps.setup = WpsInfo.INVALID;
         mManager.connect(mChannel, config, new ActionListener(){

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(D) Log.i(TAG, "Wifi P2p Connect initialized... ");
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				if(D) Log.i(TAG, "Wifi P2p Connect Failed : " + reason);
			}
        	 
         });
	}
	
	public void disconnectFromRemoveGroup() {
		// TODO Auto-generated method stub
		mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener(){

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(D) Log.i(TAG, "Wifi P2p Disconnect initialized... ");
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				if(D) Log.i(TAG, "Wifi P2p Disconnect Failed : " + reason);
			}
			
		});
	}
	
	public void setGroupOwner(){
		if(thisDevice != null && !thisDevice.isGroupOwner()){
			
			mManager.createGroup(mChannel, new WifiP2pManager.ActionListener(){

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					if(D) Log.i(TAG, "Setting this Device as Group Owner initialized... ");
				}

				@Override
				public void onFailure(int reason) {
					// TODO Auto-generated method stub
					if(D) Log.i(TAG, "Set this Device as Group Owner Failed : " + reason);
				}
				
			});
			
		}
	}
	
	public void setWifiP2pInfo(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		mWifiP2pInfo = info;
	}
	
	public WifiP2pInfo getWifiP2pInfo(){
		return mWifiP2pInfo;
	}
	
}
