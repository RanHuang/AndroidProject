package com.example.wifidirectdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceListActivity extends ListActivity implements PeerListListener{
	//Debugging
	private static final String TAG = "DeviceListActivity";
	//private static final boolean D = true;
	
	private List<WifiP2pDevice> peersList = new ArrayList<WifiP2pDevice>();
	private WifiP2pDevice device = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_direct);
		
		this.setListAdapter(new WifiPeerListAdapter(this, R.layout.wifip2p_peer_info, peersList));
	}
	
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		// TODO Auto-generated method stub
		peersList.clear();
		peersList.addAll(peers.getDeviceList());
		((WifiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
		if(peersList.size() == 0){
			findViewById(R.id.scan_select_title).setVisibility(View.INVISIBLE);
			Log.d(DeviceListActivity.TAG, "No device found.");
			return;				
		}
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
		Toast.makeText(this, device.toString(), Toast.LENGTH_LONG).show();
	}
	
	/**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
	/*
	 * 
	 */
	private class WifiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>{
		private List<WifiP2pDevice>peerList = null;

		public WifiPeerListAdapter(Context context, int resource,
				List<WifiP2pDevice> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			peerList = objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;
			if(v == null){
				 LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 v = vi.inflate(R.layout.wifip2p_peer_info, parent);
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
	
}
