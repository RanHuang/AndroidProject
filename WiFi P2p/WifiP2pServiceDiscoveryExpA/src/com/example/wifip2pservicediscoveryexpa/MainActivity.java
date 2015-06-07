package com.example.wifip2pservicediscoveryexpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
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
	
	public static final String TAG = "WifiP2pServiceDiscovery";
	
	// TXT RECORD properties
    public static final String SERVICE_INSTANCE = "WifiP2pDiscoveryService20141203";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    
    private static final String ROLE = "my_role_is";
    
    // The service that broadcasts  relevant information
 	private WifiP2pDnsSdServiceInfo serviceInfo;
 	// The service that discovers other devices
 	private WifiP2pDnsSdServiceRequest serviceRequest;

	private static TextView textLog = null;
	static Handler mHandler = null;
	
	private WifiP2pManager mWifiP2pManager = null;
	private Channel mChannel = null;
	private IntentFilter mIntentFilter = null;
	private BroadcastReceiver mReceiver = null;
	private WifiManager mWifiManager = null;
	private WifiP2pController mWifiP2pController = null;
	private WifiP2pConnectionInfoListener mWifiP2pConnectionInfoListener = null;
	
	private WifiP2pDevice mWifiP2pDevice = null;
	private WifiP2pInfo mWifiP2pInfo = null;

	private List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
	
	private Button mBtnRequestService = null;
	private Button mBtnClearRequests = null;
	private Button mBtnDisconnect = null;
	private Button mBtnStartService = null;
	private Button mBtnClearService = null;
	
	private TextView textSelfName = null;
	private TextView textSelfStatus = null;
	private TextView textSelfRole = null;
	
	private String fileName = "timeLog.txt";
	private static long startScanTime;
	private static long connectedDelay;
	private static int numOfScan;
	private static long validLog;
	
	private static boolean isConnecting;
	private static boolean isConnected;
	
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
        
        discoverServiceInitialize();
    }
    
    private void uiIntialize(){
    	textLog = (TextView)this.findViewById(R.id.text_log);
        textLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        textSelfName = (TextView)this.findViewById(R.id.text_selfName);
        textSelfStatus = (TextView)this.findViewById(R.id.text_selfStatus);
        textSelfRole = (TextView)this.findViewById(R.id.text_selfRole);
        
        mBtnRequestService = (Button)this.findViewById(R.id.button_requestService);
        mBtnRequestService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "Start Service Request", Toast.LENGTH_SHORT).show();
				numOfScan ++;
				startScanTime = System.currentTimeMillis();
				
				discoverNearByDevices();
				
				isConnected = false;
				isConnecting = false;
				connectedDelay = 0;
			}
		});
        
        mBtnClearRequests = (Button)this.findViewById(R.id.button_clearRequests);
        mBtnClearRequests.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub								
				clearServiceRequests();							
			}
		});
        
        mBtnDisconnect = (Button)this.findViewById(R.id.button_disconnect);
        mBtnDisconnect.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = String.valueOf(numOfScan) + " " + "connectedDelay: " + String.valueOf(connectedDelay) + "\n";
				writeFile(fileName, content);
				mWifiP2pController.disConnect();
				clearServiceRequests();
				logToScreen(content);
			}
		});
    
        mBtnStartService = (Button)this.findViewById(R.id.button_startService);
        mBtnStartService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				registerServiceAndBroadcastInfo();
			}
		});
        
        mBtnClearService = (Button)this.findViewById(R.id.button_clearService);
        mBtnClearService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clearServiceRequests();
				clearLocalServices();
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
		writeToSDCard(getCurrentTime() + "serviceAllLog.txt",readFromFile);
	    super.onDestroy(); 	    	
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
		Toast.makeText(this, device.deviceAddress, Toast.LENGTH_SHORT).show();
//		clearServiceRequests();
//		startConnectTime = System.currentTimeMillis();
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

    private void discoverServiceInitialize(){
		Log.d(TAG, "discoverServiceInitialize");
		DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {
			@Override
			public void onDnsSdTxtRecordAvailable(String fullDomainName,
					Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
				Log.d(TAG, "DnsSdTxtRecord available -" + txtRecordMap.toString());
				String getRecordData = txtRecordMap.get(ROLE);
				if(getRecordData != null){
					//Maybe one item is in more common use. I think at a time a client will want to find one
					//server device bacause it can connect to only one server.
					if(!isConnecting){
						mWifiP2pController.connectToDevice(srcDevice.deviceAddress);					
					}
					isConnecting = true;
					peersList.clear();
					peersList.add(srcDevice);
					((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
					logToScreen("$%&Found:" + srcDevice.deviceName + "\n");
					Log.d(TAG, "$%& A srcDevice is found. " + srcDevice.deviceName);
				}		
			}
		};
		
		mWifiP2pManager.setDnsSdResponseListeners(mChannel, null, txtListener);
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();		
	}
    
    /**
    * Registers a wifi p2p service that broadcast some info.
    */
	public void registerServiceAndBroadcastInfo(){
		 Map<String, String> record = Collections.singletonMap(ROLE, "is_available");
		 
		serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		 
		// Add the local service, sending the service info, network channel, and listener that will be used to indicate success or failure of the request.
		Thread threadAddLocalService = new threadAddLoclaService();
		threadAddLocalService.start();
	}
	
	private class threadAddLoclaService extends Thread{
		@Override
		public void run(){
			
				mWifiP2pManager.addLocalService(mChannel, serviceInfo, new ActionListener(){

					@Override
					public void onSuccess() {
						Log.d(TAG, "!Succeed!: Add local WifiP2p Service");
						MainActivity.logToScreen("!Succeed!: Add local WifiP2p Service");
					}

					@Override
					public void onFailure(int reason) {
						// TODO Auto-generated method stub
						if(reason == 2){ //If busy, try again after some seconds.
							MainActivity.logToScreen("*Failed*: AddLocalService:" + reason);
							Log.d(TAG, "*Failed*: Add local WifiP2p Service, Try Again after some time.");
						}
					}					
				});
		}
	}
	
	/**
	 * Searches for nearby devices broadcasting the info. Any devices
	 * found will be added to the discoveredWifiP2pDevice list.
	 */
	
	public void discoverNearByDevices(){
		Log.d(TAG, "discoverNearByDevices()");
		
		mWifiP2pManager.addServiceRequest(mChannel, serviceRequest, new MyActionListener("Add WifiP2p Service Requset"));
		
		mWifiP2pManager.discoverServices(mChannel, new MyActionListener("WifiP2p Discover Service"));
	}
	
	public void clearServiceRequests(){
		mWifiP2pManager.clearServiceRequests(mChannel, new MyActionListener("Clear WifiP2p Service Requsets"));
	}
	
	public void clearLocalServices(){
		mWifiP2pManager.clearLocalServices(mChannel, new MyActionListener("Clear local WifiP2p Services"));
	}

	private final class MyActionListener implements WifiP2pManager.ActionListener  {
		private String action = null;
		
		public MyActionListener(String string){
			action = string;
		}
		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			MainActivity.logToScreen("onSuccess: " + action);
			Log.d(TAG, "onSuccess: " + action);
		}

		@Override
		public void onFailure(int reason) {
			// TODO Auto-generated method stub
			MainActivity.logToScreen("onFailure: " + action + " " + reason);
			Log.d(TAG, "onFailure: " + action + " " + reason);
		}

	}
}
