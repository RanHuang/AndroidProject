package hrylab.xjtu.wifip2papp;

import java.util.ArrayList;
import java.util.List;

import hrylab.xjtu.wifip2papp.wifidirect.WifiConnectionInfoListener;
import hrylab.xjtu.wifip2papp.wifidirect.WifiConnector;
import hrylab.xjtu.wifip2papp.wifidirect.WifiDirectBroadcastReceiver;
import hrylab.xjtu.wifip2papp.wifidirect.WifiPeerListListener;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiDirectTestActivity extends ListActivity {
	
	private static final String TAG = "WifiP2pDemo WifiDirectTestActivity";
	private static final boolean D = true;
		
	private WifiP2pManager mManager = null;
	private Channel mChannel = null;
	private BroadcastReceiver mReceiver = null;
	private WifiPeerListListener mPeerListListener= null;
	private IntentFilter mIntentFilter = null;
	private WifiConnector mWifiConnector = null;
	private WifiConnectionInfoListener mWifiConnectionInfoListener = null;
	
	private List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
    
	//Listen for the change of peer list
	private PeerAvailableBroadcastReceiver selfPeerAvailableBroadcastReceiver = null;
	private ConnectionInfoAvailableBroadcastReceiver selfConnectionInfoAvailableReceiver = null;
	private static final String SelfPeerListAvailabeAction = "hrylab.xjtu.wifip2papp.WifiDirectTestActivity.PeerListAvailable";
	private static final String SelfConnectionInfoAvailableAction = "hrylab.xjtu.wifip2papp.WifiDirectTestActivity.ConnectionInfoAvailable";
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_test);
	        
	        if(D) Log.i(TAG, "+++ ON CREATE +++");
	       
	        //Create an intent filter and add the same intents that your broadcast receiver checks for
	        mIntentFilter = new IntentFilter();
	        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	        
	        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
	        mChannel = mManager.initialize(this, getMainLooper(), null);
	        
	        mWifiConnector = new WifiConnector(this, mManager, mChannel, true);
	        mPeerListListener = new WifiPeerListListener(mWifiConnector);
	        mWifiConnectionInfoListener = new WifiConnectionInfoListener(mWifiConnector);
	        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, mWifiConnector, mPeerListListener, mWifiConnectionInfoListener);
	       	        
	        setListAdapter(new WifiPeerListAdapter(this, R.layout.wifip2p_peer_info, peersList));
	        
	        selfPeerAvailableBroadcastReceiver = new PeerAvailableBroadcastReceiver();
	        selfConnectionInfoAvailableReceiver = new ConnectionInfoAvailableBroadcastReceiver();
	        
	        this.findViewById(R.id.button_cancelConnected).setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mWifiConnector.disconnectFromRemoveGroup();
					mWifiConnector.startDiscoverPeers();
				}
	        	
	        });
	        this.findViewById(R.id.button_fileTransfer).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(WifiDirectTestActivity.this, "Start a Client...", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					startActivityForResult(intent, FileTransferService.CHOOSE_FILE_RESULT_CODE);
				}
			});
	    }
	 @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data){
			// User has picked an image. Transfer it to group owner i.e peer using
	        // FileTransferService.
	    	//TODO: check requestCode and resultCode
			Uri uri = data.getData();
			TextView statusText = (TextView)WifiDirectTestActivity.this.findViewById(R.id.text_fileTransfer);
			statusText.setText("Sending: " + uri);
			Intent serviceIntent = new Intent(WifiDirectTestActivity.this, FileTransferService.class);
			serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
			serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
			serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,mWifiConnector.getWifiP2pInfo().groupOwnerAddress.getHostAddress());
			serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 9000);
			Log.d(WifiDirectTestActivity.TAG, "Before start FileTransferService...");
			startService(serviceIntent);
		} 
	@Override
	public void onStart(){
		super.onStart();
	  	if(D) Log.i(TAG, "++ ON START ++");
	}
	@Override
	public void onResume(){
		super.onResume();
		if(D) Log.i(TAG, "++ ON RESUME ++");
		
		registerReceiver(mReceiver, mIntentFilter);
		//self broadcast
		registerReceiver(selfPeerAvailableBroadcastReceiver, new IntentFilter(SelfPeerListAvailabeAction));
		registerReceiver(selfConnectionInfoAvailableReceiver, new IntentFilter(SelfConnectionInfoAvailableAction));
	}
	@Override
	public void onPause(){
		super.onPause();
		if(D) Log.i(TAG, "++ ON PAUSE ++");		
	}
	@Override
	public void onStop(){
	 	super.onStop();
	   	if(D) Log.i(TAG, "++ ON STOP ++");
	    
	   	unregisterReceiver(mReceiver);
	    //self broadcast
	   	unregisterReceiver(selfPeerAvailableBroadcastReceiver);
	   	unregisterReceiver(selfConnectionInfoAvailableReceiver);
	}
	    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	   // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.test, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle action bar item clicks here. The action bar will
	    // automatically handle clicks on the Home/Up button, so long
	    // as you specify a parent activity in AndroidManifest.xml.
	    int id = item.getItemId();
	    switch(id){
	    case R.id.action_startScanPeers:
	    	mWifiConnector.startDiscoverPeers();
	    	return true;
	    case R.id.action_back:
	      	finish();
	       	return true;
	    case R.id.action_settings:
	       	Toast.makeText(this, "Click the settings in actionbar...", Toast.LENGTH_SHORT).show();
	       	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
/*
 * 异步接收消息，在peer list 更新时进行调用，刷新UI list 信息
 */
	private class PeerAvailableBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			peersList.clear();
			peersList.addAll(mWifiConnector.getAllPeersWifiP2pDevices());
			((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
		}
		
	}
	public void sendSelfPeerListAvailableBroadcast(){
		Intent intent = new Intent(SelfPeerListAvailabeAction);
		sendBroadcast(intent);
	}
	
	private class ConnectionInfoAvailableBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			WifiP2pInfo wifiP2pInfo = mWifiConnector.getWifiP2pInfo();
			
			TextView textView = (TextView)findViewById(R.id.text_fileTransfer);
			Button button = (Button)findViewById(R.id.button_fileTransfer);
			
			if(wifiP2pInfo == null) {
				button.setVisibility(View.INVISIBLE);
				textView.setVisibility(View.INVISIBLE);
				return;
			}
			if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
				//If we are the group owner
				button.setVisibility(View.INVISIBLE);
				textView.setVisibility(View.VISIBLE);
				textView.setText("Server");
				Toast.makeText(WifiDirectTestActivity.this, "Connection is formed. The GroupOwner is: " + 
						wifiP2pInfo.groupOwnerAddress, Toast.LENGTH_SHORT).show();
				
				//Start a Server	
				new FileServerAsyncTask(WifiDirectTestActivity.this, 
						WifiDirectTestActivity.this.findViewById(R.id.text_fileTransfer)).execute();
				
			}else if(wifiP2pInfo.groupFormed){
				//If we are not the group owner
				textView.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
				((TextView)WifiDirectTestActivity.this.findViewById(R.id.text_fileTransfer))
					.setText(getResources().getString(R.string.button_fileTransfer));
			}
			
			
		}
		
	}
	public void sendSelfConnectionInfoAvailableReceiver(){
		Intent intent = new Intent(SelfConnectionInfoAvailableAction);
		sendBroadcast(intent);
	}
	/*
	 * 跟新UI上自设备信息
	 */
	public void updateThisDeviceStatus(){
		WifiP2pDevice thisDevice = mWifiConnector.getThisDevice();
		
		if(thisDevice == null) return;
		
		TextView view = (TextView)this.findViewById(R.id.self_name);
		view.setText(thisDevice.deviceName);
		view = (TextView)this.findViewById(R.id.self_status);
    	view.setText(getDeviceStatus(thisDevice.status));
    	   	
    	Button buttonCancelConnected = (Button)this.findViewById(R.id.button_cancelConnected);
    	switch(thisDevice.status){
    	case WifiP2pDevice.CONNECTED:
    		buttonCancelConnected.setVisibility(View.VISIBLE);
    		break;
    		default:
    			buttonCancelConnected.setVisibility(View.GONE);
    	}
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
    /*
     * 点击列表项目选择设备进行连接
     */
    @Override
	public void onListItemClick(ListView l, View v, int position, long id){
		WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
		Toast.makeText(this, device.toString(), Toast.LENGTH_SHORT).show();
		mWifiConnector.connect(device);
	}
    
}
