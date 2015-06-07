package com.example.wifip2pactivescanexpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ListActivity {

public static final String TAG = "ActiveScanActivity";
	
	private static TextView textLog = null;
	static Handler mHandler = null;
	
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;
	private IntentFilter mIntentFilter = null;
	private BroadcastReceiver mReceiver = null;
	private WifiManager mWifiManager = null;
	private WifiP2pController mWifiP2pController = null;
	private WifiP2pPeerListListener mWifiP2pPeerListListener = null;
	private WifiP2pConnectionInfoListener mWifiP2pConnectionInfoListener = null;
	
	private WifiP2pDevice mWifiP2pDevice = null;
	private WifiP2pInfo mWifiP2pInfo = null;

	private List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
	
	private Button mBtnStart = null;
	private Button mBtnStop = null;
	private Button mBtnSetGO = null;
	
	private TextView textSelfName = null;
	private TextView textSelfStatus = null;
	private TextView textSelfRole = null;
	
	private String fileName = "timeLog.txt";
	private static long startScanTime;
	private static long connectedDelay;
	private static int numOfScan;
	private static long validLog;
	
	private static boolean isConnected;
	private static boolean isConnecting;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mHandler = new Handler(Looper.getMainLooper());
        setListAdapter(new WifiPeerListAdapter(this, R.layout.wifip2p_peer_info, peersList));
        
        numOfScan = 0;
        validLog = 0;
        this.deleteFile(fileName);
        
        uiIntialize();
        wifiP2pInitialize();
        
        String startContent = "\n%%Start%%" + getCurrentTime() + "\n";
        writeFile(fileName, startContent);
    }

    private void uiIntialize(){
    	textLog = (TextView)this.findViewById(R.id.text_log);
        textLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        textSelfName = (TextView)this.findViewById(R.id.text_selfName);
        textSelfStatus = (TextView)this.findViewById(R.id.text_selfStatus);
        textSelfRole = (TextView)this.findViewById(R.id.text_selfRole);
        
        mBtnStart = (Button)this.findViewById(R.id.button_startAutoScan);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "Start scan", Toast.LENGTH_SHORT).show();
				numOfScan ++;
				startScanTime = System.currentTimeMillis();
				mWifiP2pController.startWifiScan();
				isConnected = false;
				isConnecting = false;
				connectedDelay = 0;
			}
		});
        
        mBtnStop = (Button)this.findViewById(R.id.button_stopAutoScan);
        mBtnStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = String.valueOf(numOfScan) + " " + "AllDelay: " + String.valueOf(connectedDelay) + "\n";
				writeFile(fileName, content);				
				mWifiP2pController.disConnect();
				mWifiP2pController.stopWifiScan();
				logToScreen(content);
			}
		});
        
        mBtnSetGO = (Button)this.findViewById(R.id.button_setGO);
        mBtnSetGO.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWifiP2pController.setGroupOwner();
			}
		});
    }
    
    public static void logToScreen(final String text) {
		Log.i(TAG, text);
		
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				if(textLog != null){
					textLog.setText(textLog.getText() + "\n" + text);
				}
				Layout layout = textLog.getLayout();
				if(layout != null){
					final int scrollAmount = layout.getLineTop(textLog.getLineCount()) - textLog.getHeight();
					if(scrollAmount > 0){
						textLog.scrollTo(0, scrollAmount);
					} else { //there is no need to scroll
						textLog.scrollTo(0, 0);
					}
				}
			}
			
		});
	}
    
    private void wifiP2pInitialize(){
    	mWifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        if(mWifiManager != null && !mWifiManager.isWifiEnabled()){
        	mWifiManager.setWifiEnabled(true);
        }
        
        mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        mWifiP2pController = new WifiP2pController(mWifiP2pManager, mChannel);
        
        mWifiP2pPeerListListener = new WifiP2pPeerListListener();
        mWifiP2pConnectionInfoListener = new WifiP2pConnectionInfoListener();  
                
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);    
        mReceiver = new WifiDirectBroadcastReceiver();
    }
    
    @Override
    public void onStart(){
    	super.onStart();
    	Log.i(TAG, "++ ON START ++");	
    }
	
	@Override
	public void onPause(){ 	
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	@Override
	public void onResume(){
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
	}
	    
	@Override
	public void onStop(){
		super.onStop();
	    Log.i(TAG, "++ ON STOP ++");
	}
	    
	@Override
	public void onRestart(){
		super.onRestart();
	}
	    
	@Override
	public void onDestroy(){
		Log.i(TAG, "++ ON DESTROY ++");		
		String endContent = "TimeOfAllScan:" + String.valueOf(numOfScan) + " ValidLog: " + String.valueOf(validLog) + "\n";
		writeFile(fileName, endContent);
		
		String readFromFile = readFile(fileName);
		writeToSDCard(getCurrentTime() + "activeScanAllLog.txt",readFromFile);
	    super.onDestroy(); 	    	
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class WifiP2pPeerListListener implements PeerListListener{

		@Override
		public void onPeersAvailable(WifiP2pDeviceList peers) {
			// TODO Auto-generated method stub
			peersList.clear();
			
			peersList.addAll(peers.getDeviceList());
			((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
			
			if(!peersList.isEmpty() && !isConnecting){
				WifiP2pDevice device = peersList.get(0);
				mWifiP2pController.connectToDevice(device.deviceAddress);
				isConnecting = true;
			}
			
			Log.d(TAG, "PeersAvailable");
			logToScreen("PeersAvailable:" + peers.getDeviceList().size());
		}
    	
    }
    
    private class WifiP2pConnectionInfoListener implements ConnectionInfoListener{

		@Override
		public void onConnectionInfoAvailable(WifiP2pInfo info) {
			// TODO Auto-generated method stub
			Log.d(TAG, "ConnectionInfoAvailable");
			mWifiP2pInfo = info;			
			if(info != null && info.isGroupOwner){
				Log.d(TAG, "ConnectionInfoAvailable, is GO!");
				logToScreen("(^^)Is GO!");
			} else {
				
			}				
		}
    	
    }
    
    private class WifiDirectBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 String action = intent.getAction();
		        Log.d(TAG, action);
		        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
					// Check to see if Wi-Fi is enabled and notify appropriate activity
					int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
					if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
						
					} else {
						
					}
				} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
					// Call WifiP2pManager.requestPeers() to get a list of current peers
					Log.d(TAG, "Peers Changed Action");					
					mWifiP2pManager.requestPeers(mChannel, mWifiP2pPeerListListener);
					
				} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
		            if (mWifiP2pManager == null) { return; }		            
		            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
		            if (networkInfo.isConnected()) {
		            	if(!isConnected){
		            		validLog ++;
		            		connectedDelay = System.currentTimeMillis() - startScanTime;
		            	}		            	
		            	logToScreen("IS connected.");
		                Log.d(TAG, "Connected to p2p network. Requesting network details");
		                mWifiP2pManager.requestConnectionInfo(mChannel, mWifiP2pConnectionInfoListener);
		                isConnected = true;
		            } else {
		                // It's a disconnect
		            	isConnected = false;
		            	logToScreen("IS disconnected.");
		            }
		        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
		            WifiP2pDevice device = (WifiP2pDevice) intent
		                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
		            mWifiP2pDevice = device;
		            Log.d(TAG, "Device status -" + getDeviceStatus(device.status));
		        }
		        
		        updateSelfInfoUI();
		        
		}	
    }
    
    private void updateSelfInfoUI(){
    	if(mWifiP2pDevice != null){
    		textSelfName.setText(mWifiP2pDevice.deviceName);
			textSelfStatus.setText(getDeviceStatus(mWifiP2pDevice.status));
    	}
    	if(mWifiP2pInfo	!= null){
    		if(mWifiP2pInfo.isGroupOwner){
    			textSelfRole.setText("GO");
    		} else {
    			textSelfRole.setText("");
    		}
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
		Toast.makeText(this, "Is GO:" + String.valueOf(device.isGroupOwner()), Toast.LENGTH_SHORT).show();
//		mWifiP2pController.connectToDevice(device.deviceAddress);
	}
    
    private boolean writeFile(String file, String content){
    	try{
    		FileOutputStream outFile = openFileOutput(file, Context.MODE_APPEND);
    		
    		byte[] buffer = content.getBytes();
    		outFile.write(buffer);
    		outFile.flush();
    		outFile.close();
    		return true;
    	} catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    private String readFile(String file){
    	String result = "";
    	try{
    		FileInputStream inFile = openFileInput(file);
    		int len = inFile.available();
    		byte[] buffer = new byte[len];
    		inFile.read(buffer);
    		result = new String(buffer);    		
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    public boolean writeToSDCard(String fileName, String content){
    	File sdCardFile = Environment.getExternalStorageDirectory();
    	String dirPath = sdCardFile.getPath() + File.separator + "WifiP2pLogFiles";
    	File dirFile = new File(dirPath);
    	if(!dirFile.exists()){
    		dirFile.mkdir();
    	}
    	
    	try{
    		File file = new File(dirPath + File.separator + fileName);
    		OutputStream outputStream = new FileOutputStream(file);
    		
    		byte[] buffer = content.getBytes();
    		outputStream.write(buffer);
    		outputStream.flush();
    		outputStream.close();
    		return true;
    	} catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}    	
    }
    
    String getCurrentTime(){
    	Time time = new Time("GMT+8");
    	time.setToNow();   //获取系统时间
    	String result = time.toString();
    	return result;
    }
}
