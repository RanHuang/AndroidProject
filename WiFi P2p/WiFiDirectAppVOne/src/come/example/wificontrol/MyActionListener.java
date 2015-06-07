package come.example.wificontrol;

import com.example.wifidirectappvone.MainActivity;

import android.net.wifi.p2p.WifiP2pManager;

public final class MyActionListener implements WifiP2pManager.ActionListener  {
	private String action = null;
	
	public MyActionListener(String string){
		action = string;
	}
	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		MainActivity.logToScreen("Succeed!: " + action);
	}

	@Override
	public void onFailure(int reason) {
		// TODO Auto-generated method stub
		MainActivity.logToScreen("Failed*: " + action + " " + reason);
	}

}
