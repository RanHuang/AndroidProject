package come.example.wificontrol;

public class WifiController {
	
	
	public WifiController() {
		// TODO Auto-generated constructor stub
		WifiP2pDeviceStatus.setGroupOwnerIntention();
	}

	public void setServer() {
		// TODO Auto-generated method stub
		WifiP2pDeviceStatus.setGroupOwnerIntention(15); //Tend to be a GO		
		//ADD SERVER THREAD HERE
		
		
	}

	//Add the send packet Thread or AsyncTask in WifiP2pStateBroadcastReceiver, while it is connected.
	public void sendPacket() {
		// TODO Auto-generated method stub
		if(!WifiP2pDeviceStatus.isConnected()){
			//ADD send packet Thread or AsyncTask HERE
			
		}
		
	}
	
	
}
