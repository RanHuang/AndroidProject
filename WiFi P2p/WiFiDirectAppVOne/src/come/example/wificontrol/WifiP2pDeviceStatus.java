package come.example.wificontrol;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Used for maintaining the current status of the native device.
 * 
 */
public final class WifiP2pDeviceStatus {

	private static boolean isGO = false;
	private static int groupOwnerIntention = 0;
	private static String myId = null;  //WifiP2pDevice.deviceAddress
	
	private static boolean isSent = true; //ConnectionInfoRequest
	
	private static enum Status {
		UNAVAILABLE, CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED
	}

	private static Status currentStatus = Status.UNAVAILABLE;

	
	public static synchronized void initiate(){
		currentStatus = Status.DISCONNECTED;
	}
	
	public static synchronized void setConnected(){
		currentStatus = Status.CONNECTED;
		isSent = false;
	}
	
	public static synchronized void setConnectionInfoRequestSent(){
		isSent = true;
	}
	
	public static synchronized boolean isConnectionInfoRequestSent(){
		return isSent;
	}
	
	public static synchronized void setDisconnected(){
			currentStatus = Status.DISCONNECTED;
	}
	
	public static synchronized void setGroupOwner(boolean go){
		isGO = go;
	}

	public static synchronized Status getCurrentStatus() {
		return currentStatus;
	}
	
	public static synchronized boolean isConnected(){
		return currentStatus == Status.CONNECTED;
	}
	
	public static synchronized void setConnecting(){
		currentStatus = Status.CONNECTING;
	}
	
	public static synchronized boolean isAvailable(){
		return currentStatus != Status.UNAVAILABLE;
	}
	
	public static synchronized void setUnavailable(){
		currentStatus = Status.UNAVAILABLE;
		isGO = false;
	}
	
	
	public static synchronized int getGOIntention(){
		return groupOwnerIntention;
	}
	
	
	public static synchronized boolean isGroupOwner(){
		return isGO;
	}

	public static boolean canDiscover() {
		// TODO Auto-generated method stub
		return currentStatus == Status.DISCONNECTED;
	}
	
	public static void updateThisDevice(WifiP2pDevice deviceInfo) {
		myId = deviceInfo.deviceAddress;
		if(deviceInfo.isGroupOwner()){
			WifiP2pDeviceStatus.setGroupOwner(true);
		} else {
			WifiP2pDeviceStatus.setGroupOwner(false);
		}			
	}
	
	public static String getMyId(){
		return myId;
	}

	public static void setGroupOwnerIntention(int i) {
		// TODO Auto-generated method stub
		if(i<0){
			groupOwnerIntention = 0;
		} else if(i>15) {
			groupOwnerIntention = 15;
		} else {
			groupOwnerIntention = i;
		}
	}
	
	public static void setGroupOwnerIntention() {
		// TODO Auto-generated method stub
		groupOwnerIntention = ((int)(Math.random() *100)) % 16;
	}

}
