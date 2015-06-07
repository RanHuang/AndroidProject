package hrylab.xjtu.wifip2papp;

import hrylab.xjtu.datatest.ClientMessageSender;
import hrylab.xjtu.datatest.ServerMessageReceiver;
import hrylab.xjtu.wifip2papp.wifidirect.WifiConnector;
import hrylab.xjtu.wifip2papp.wifidirect.WifiController;
import hrylab.xjtu.wifip2papp.wifidirect.WifiDirectBroadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	private static final String TAG = "WifiP2pDemon MainActivity";
	private static final boolean D = true;
	
	private WifiManager mWifiManager;
	
	private MyWifiPeerListListener mPeerListListener= null;
	private MyWifiConnectionInfoListener mConnectionInfoListener = null;
	
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;
	private IntentFilter mIntentFilter = null;
	private BroadcastReceiver mReceiver = null;
	
	private WifiConnector mWifiConnector = null;
	private WifiController mWifiController = null;
//	private List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
	//For UI
	private TextView textSelfName = null;
	private TextView textSelfStatus = null;
	private TextView textSelfRole = null;
	private Button buttonConnect = null;
	private Button buttonDisconnect = null;
	private Button buttonSetGO = null;
	private Button buttonStartScan = null;
	private Button buttonDataTest = null;
	private boolean GOisExisted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(D) Log.i(TAG, "+++ ON CREATE +++");
        mWifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        if(mWifiManager != null && !mWifiManager.isWifiEnabled()){
        	mWifiManager.setWifiEnabled(true);
        }
        //For UI
        textSelfName = (TextView)this.findViewById(R.id.text_selfName);
        textSelfStatus = (TextView)this.findViewById(R.id.text_selfStatus);
        textSelfRole = (TextView)this.findViewById(R.id.text_selfRole);
        buttonConnect = (Button)this.findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(new BtnConnectListener());
        buttonDisconnect = (Button)this.findViewById(R.id.button_disconnect);
        buttonDisconnect.setOnClickListener(new BtnDisconnectListener());
        buttonSetGO = (Button)this.findViewById(R.id.button_setServer);
        buttonSetGO.setOnClickListener(new BtnSetGOListener());
        buttonStartScan = (Button)this.findViewById(R.id.button_startScan);
        buttonStartScan.setOnClickListener(new BtnStartScanListener());
        buttonDataTest = (Button)this.findViewById(R.id.button_dataTest);
        buttonDataTest.setOnClickListener(new BtnDataSendTestListener());
        
        ((Button)this.findViewById(R.id.button_updateUI)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateThisDeviceUI();
			}
		});
        
		//Create an intent filter and add the same intents that your broadcast receiver checks for
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mPeerListListener = new MyWifiPeerListListener();
        mConnectionInfoListener = new MyWifiConnectionInfoListener();
        
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        
        mWifiConnector = new WifiConnector(mWifiP2pManager, mChannel);
        mWifiController = new WifiController(mWifiConnector);
                
        mReceiver = new WifiDirectBroadcastReceiver(mWifiP2pManager, mChannel, mWifiConnector, mPeerListListener, mConnectionInfoListener);
        
    }

    @Override
    public void onStart(){
    	super.onStart();
    	if(D) Log.i(TAG, "++ ON START ++");
    	
    	if(mWifiManager.isWifiEnabled()){
    		Toast.makeText(this, "The wifi is enabled.", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(this, "The wifi is not enabled. Setting wifi enabled...", Toast.LENGTH_SHORT).show();
    		mWifiManager.setWifiEnabled(true);
    	}
    }
    
    @Override
    public void onPause(){ 	
    	super.onPause();
    	unregisterReceiver(mReceiver);
    }
    @Override
    public void onResume(){
    	super.onResume();
//    	mWifiConnector.startDiscoverPeers();
    	registerReceiver(mReceiver, mIntentFilter);
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	if(D) Log.i(TAG, "++ ON STOP ++");
   	
    }
    
    @Override
    public void onRestart(){
    	super.onRestart();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy(); 	
    	if(D) Log.i(TAG, "++ ON DESTROY ++");
    	
    	if(mWifiManager.isWifiEnabled()){
//    		mWifiManager.setWifiEnabled(false);
    	} 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
        case R.id.action_startTest:
        	Intent callIntent = new Intent(MainActivity.this, WifiDirectTestActivity.class);
        	startActivity(callIntent);
        	return true;
        case R.id.action_back:
        	finish();
    		System.exit(0);
        	return true;
        case R.id.action_settings:
        	Toast.makeText(this, "Click the settings in actionbar...", Toast.LENGTH_SHORT).show();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	/*
	 * 跟新UI上自设备信息
	 */
	public void updateThisDeviceUI(){
		WifiP2pDevice thisDevice = mWifiConnector.getThisDevice();
		WifiP2pInfo wifiP2pInfo = mWifiConnector.getWifiP2pInfo();
		
		final TextView textSelfInfo = (TextView) this.findViewById(R.id.text_selfInfo);
		textSelfInfo.setMovementMethod(ScrollingMovementMethod.getInstance()); //滚动显示信息
		
		if(thisDevice != null){
			textSelfName.setText(thisDevice.deviceName);
			textSelfStatus.setText(getDeviceStatus(thisDevice.status));
			if(wifiP2pInfo != null && wifiP2pInfo.isGroupOwner){
				textSelfInfo.setText("*(^^)*This is Group Owner!");
				mWifiP2pManager.requestGroupInfo(mChannel, new GroupInfoListener(){

					@Override
					public void onGroupInfoAvailable(WifiP2pGroup group) {
						// TODO Auto-generated method stub
						textSelfInfo.setText("*(^^)*This is Group Owner!\n" + group.toString());
					}
					
				});
				buttonDataTest.setVisibility(View.VISIBLE);
			} else if(wifiP2pInfo != null){
				buttonDataTest.setVisibility(View.VISIBLE);	
			} else{
				buttonDataTest.setVisibility(View.INVISIBLE);
				textSelfInfo.setText("(vv)");
			}
		} else {
			textSelfInfo.setText("No this Device information!");
			return;
		}
		
		if(GOisExisted){
			textSelfRole.setVisibility(View.VISIBLE);			
			textSelfRole.setText(getResources().getString(R.string.isClient));
			if(wifiP2pInfo != null && wifiP2pInfo.isGroupOwner){
				mWifiConnector.disconnectFromRemoveGroup();
			}
			buttonConnect.setVisibility(View.VISIBLE);
			buttonDisconnect.setVisibility(View.GONE);
		} else if(wifiP2pInfo != null && wifiP2pInfo.isGroupOwner){
			textSelfRole.setVisibility(View.VISIBLE);
			textSelfRole.setText(getResources().getString(R.string.isServer));
			buttonConnect.setVisibility(View.GONE);
			buttonDisconnect.setVisibility(View.VISIBLE);
		} else {
			textSelfRole.setVisibility(View.GONE);
			buttonConnect.setVisibility(View.GONE);
			buttonDisconnect.setVisibility(View.GONE);
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
    private class BtnSetGOListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiController.setGroupOwner();
		}
    	
    }
    private class BtnStartScanListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiController.startWifiScan(true);
		}
    	
    }
    private class BtnConnectListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiController.connectToWifiServer();
//			mWifiController.connect();
		}
    	
    }
    private class BtnDisconnectListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mWifiConnector.disconnectFromRemoveGroup();
		}
    	
    }
    
    private class BtnDataSendTestListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			WifiP2pInfo wifiP2pInfo = mWifiConnector.getWifiP2pInfo();
			if(wifiP2pInfo != null){
				if(wifiP2pInfo.isGroupOwner){
					new Thread(new ServerMessageReceiver()).start();
				} else {
					new Thread(new ClientMessageSender(wifiP2pInfo.groupOwnerAddress.getHostAddress(), ServerMessageReceiver.PORT)).start();
				}
			}
		}
    	
    }

    /*
     * PeerList Listener
     */
	private class MyWifiPeerListListener implements PeerListListener{
		
		private static final String TAG = "WifiPeerListListener";
		private static final boolean D = true;
		
		
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peers) {
			// TODO Auto-generated method stub
			if(D) Log.d(TAG, "Peers available. Count = " + peers.getDeviceList().size());
			GOisExisted = false;
			for(WifiP2pDevice device : peers.getDeviceList()){
				if(device.isGroupOwner()){
					GOisExisted = true;
				}
			}		
			if(GOisExisted){
				Toast.makeText(MainActivity.this, "A group owner is found.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(MainActivity.this, "No group owner is found.", Toast.LENGTH_LONG).show();
			}
						
			mWifiConnector.clearPeersWifiP2pDevices();
			mWifiConnector.addAllPeersWifiP2pDevices(peers.getDeviceList());
			
			updateThisDeviceUI();

		}

	}
	/*
	 * ConnectionInfoListener
	 */
	private class MyWifiConnectionInfoListener implements ConnectionInfoListener{

		private static final String TAG = "WifiConnectionInfoListener";
		
		@Override
		public void onConnectionInfoAvailable(WifiP2pInfo info) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onConnectionInfoAvailable...");
			
			mWifiConnector.setWifiP2pInfo(info);
			
			updateThisDeviceUI();
				
		}
	}
}
