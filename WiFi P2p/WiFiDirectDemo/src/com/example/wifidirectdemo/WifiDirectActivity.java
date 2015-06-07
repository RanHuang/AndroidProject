package com.example.wifidirectdemo;
import java.util.ArrayList;
import java.util.List;

import com.example.wifidirectcommunication.FileServerAsyncTask;
import com.example.wifidirectcommunication.FileTransferService;


import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiDirectActivity extends ListActivity implements ChannelListener, DeviceActionListener{
	//Debugging
	public static final String TAG = "WifiDirectDemo";
	//private static final boolean D = true;
	
	//
	private WifiP2pManager mManager = null;
	private Channel mChannel = null;
	private BroadcastReceiver mReceiver = null;
	private IntentFilter mIntentFilter = null;
	
	private boolean isWifiP2pEnabled = false;
	private boolean retryChannel = false;
	
	//peerList
	private List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
	private WifiP2pDevice device = null;
	private WifiP2pInfo info = null;
	
	public PeerListListener peerListListener = null;
	public ConnectionInfoListener connectionInfoListener = null;
	
	public void setIsWifiP2pEnabled(boolean state){
    	this.isWifiP2pEnabled = state;
    }
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);
        
        Log.d(WifiDirectActivity.TAG, "onCreate");
        
        this.setListAdapter(new WifiPeerListAdapter(this, R.layout.wifip2p_peer_info, peersList));
        peerListListener = new PeerListListener(){        	
			@Override
			public void onPeersAvailable(WifiP2pDeviceList peers) {
				// TODO Auto-generated method stub
				// Out with the old, in with the new.
				peersList.clear();   //清空list，刷新
				peersList.addAll(peers.getDeviceList());
	            // If an AdapterView is backed by this data, notify it
	            // of the change.  For instance, if you have a ListView of available
	            // peers, trigger an update.
	            ((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
	            //如果peers.size()=0，则没有发现peer
	            if (peersList.size() == 0) {
	                Log.d(WifiDirectActivity.TAG, "No devices found");
	                return;
	            }
	            Log.d(WifiDirectActivity.TAG, "Devices found");
			}
        	
        };
        
        connectionInfoListener = new ConnectionInfoListener(){

			@Override
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				// TODO Auto-generated method stub
				WifiDirectActivity.this.info = info;
				TextView textView = (TextView)findViewById(R.id.self_role);
				Button button = (Button)findViewById(R.id.button_doSomething);
				
				if(info.groupFormed && info.isGroupOwner){
					button.setVisibility(View.GONE);
					textView.setVisibility(View.VISIBLE);
					textView.setText("Server");
					Toast.makeText(WifiDirectActivity.this, "Connection is formed. The GroupOwner is: " + 
								info.groupOwnerAddress, Toast.LENGTH_SHORT).show();
					//Start a Server
					new FileServerAsyncTask(WifiDirectActivity.this, 
							WifiDirectActivity.this.findViewById(R.id.status_text)).execute();
				}else if(info.groupFormed){
					textView.setVisibility(View.GONE);
					button.setVisibility(View.VISIBLE);
					((TextView)WifiDirectActivity.this.findViewById(R.id.status_text))
					.setText(getResources().getString(R.string.button_doSomething));
				} else{
					button.setVisibility(View.GONE);
					textView.setVisibility(View.GONE);
				}
			}
        	
        };
        this.findViewById(R.id.button_doSomething).setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(WifiDirectActivity.this, "Start a Client...", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, FileTransferService.CHOOSE_FILE_RESULT_CODE);
			}
        	
        });
        //Create an intent filter and add the same intents that your broadcast receiver checks for
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        /*
         * In your activity's onCreate() method, obtain an instance of WifiP2pManager and 
         * register your application with the Wi-Fi P2P framework by calling initialize(). 
         * This method returns a WifiP2pManager.Channel, which is used to connect your application
         * to the Wi-Fi P2P framework. You should also create an instance of your broadcast receiver
         * with the WifiP2pManager and WifiP2pManager.Channel objects along with a reference to your
         * activity. This allows your broadcast receiver to notify your activity of interesting events 
         * and update it accordingly. 
         * It also lets you manipulate the device's Wi-Fi state if necessary
         */
        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		// User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
    	//TODO: check requestCode and resultCode
		Uri uri = data.getData();
		TextView statusText = (TextView)WifiDirectActivity.this.findViewById(R.id.status_text);
		statusText.setText("Sending: " + uri);
		Intent serviceIntent = new Intent(WifiDirectActivity.this, FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 9000);
		Log.d(WifiDirectActivity.TAG, "Before start FileTransferService...");
		startService(serviceIntent);
	}
		
	/*
     * register the broadcast receiver with the intent values to be matched
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume(){
    	super.onResume();
    	registerReceiver(mReceiver, mIntentFilter);
    }
    /*
     * unregister the broadcast receiver
     * (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause(){
    	super.onPause();
    	unregisterReceiver(mReceiver);
    }
    /*
     * (non-Javadoc)加载自定义Action Bar
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	getMenuInflater().inflate(R.menu.menu_wifidirect, menu);
		return true;	
    }
    /*
     * (non-Javadoc)针对选择Action Bar 按键的处理逻辑
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.menu_item_scan:
    	  	if(!isWifiP2pEnabled){
        		Toast.makeText(WifiDirectActivity.this, "WifiP2p is not enabled!", Toast.LENGTH_LONG).show();
        		return true;
        	}
    	  	//搜索可连接设备
    		discoverPeers();
    		return true;
    	case R.id.menu_item_back:
    		disconnect(mChannel);
    		stopService(new Intent(WifiDirectActivity.this, FileTransferService.class));
    		finish();
    		System.exit(0);
    		return true;
    	}
    	return false;   	
    }
    /*
	 * @return this device
	 */
	public WifiP2pDevice getDevice(){
		return device;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
		Toast.makeText(this, device.toString(), Toast.LENGTH_SHORT).show();
		connect(mChannel, device);
	}
	
	/**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
	private class WifiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>{
		private List<WifiP2pDevice>peerList = null;

		public WifiPeerListAdapter(Context context, int resource,
				List<WifiP2pDevice> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			peerList = objects;
		}
		
		@SuppressLint("InflateParams") @Override
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;
			if(v == null){
				 LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 v = vi.inflate(R.layout.wifip2p_peer_info, null);
			}
			
			WifiP2pDevice device = peerList.get(position);
			
			if(device != null){
				TextView deviceName = (TextView)v.findViewById(R.id.device_name);
				TextView deviceAddress = (TextView)v.findViewById(R.id.device_status);
			
				if(deviceName != null){
					deviceName.setText(device.deviceName);
				}
				if(deviceAddress != null){
					deviceAddress.setText(device.deviceAddress);
				}
			}
			return v;
		}
	}
    /*
     * Discovering peers
     * To discover peers that are available to connect to, call discoverPeers() to detect available peers 
     * that are in range. The call to this function is asynchronous and a success or failure is communicated 
     * to your application with onSuccess() and onFailure() if you created a WifiP2pManager.ActionListener. 
     * The onSuccess() method only notifies you that the discovery process succeeded and does not provide 
     * any information about the actual peers that it discovered.
     */
    private void discoverPeers(){
  
    	mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(WifiDirectActivity.this, "Discovery Initiated", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				Toast.makeText(WifiDirectActivity.this, "Discovery Failed : " + reason, Toast.LENGTH_LONG).show();
			}
		});
    }
    /**
     * Update UI for this device.
     * 
     * @param device WifiP2pDevice object
     */
    public void updateDeviceStatus(WifiP2pDevice device){
    	this.device = device;
    	TextView view = (TextView)this.findViewById(R.id.self_name);
    	view.setText(device.deviceName);
    	view = (TextView)this.findViewById(R.id.self_status);
    	view.setText(getDeviceStatus(device.status));
    }
    private static String getDeviceStatus(int deviceStatus){
    	switch(deviceStatus){
    	case WifiP2pDevice.AVAILABLE:
    		return "Available";
    	case WifiP2pDevice.CONNECTED:
    		return "Connected";
    	case WifiP2pDevice.FAILED:
    		return "Failed";
    	case WifiP2pDevice.INVITED:
    		return "Invited";
    	case WifiP2pDevice.UNAVAILABLE:
    		return "Unavailable";
    	default:
    		return "Unknown";
    	}
    }

	@Override
	public void disconnect(Channel channel) {
		// TODO Auto-generated method stub
		mManager.removeGroup(channel, new ActionListener(){

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Disconnect failed. Reason : " + reason);
			}
			
		});
	}
	@Override
	public void onChannelDisconnected() {
		// TODO Auto-generated method stub
		// we will try once more
        if (mManager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            retryChannel = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
	}
	@Override
	public void connect(Channel channel, WifiP2pDevice device) {
		// TODO Auto-generated method stub
		 WifiP2pConfig config = new WifiP2pConfig();
         config.deviceAddress = device.deviceAddress;
         config.wps.setup = WpsInfo.PBC;
         mManager.connect(channel, config, new ActionListener(){

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
				Toast.makeText(WifiDirectActivity.this, "Connecting...",
                        Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				Toast.makeText(WifiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
			}
        	 
         });
	}
}
