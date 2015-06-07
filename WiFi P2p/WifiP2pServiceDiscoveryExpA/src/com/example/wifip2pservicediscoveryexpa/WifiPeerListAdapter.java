package com.example.wifip2pservicediscoveryexpa;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Array adapter for ListFragment that maintains WifiP2pDevice list.
 */
public class WifiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>{

	private List<WifiP2pDevice> peerList = null;
	private Context context = null;
	public WifiPeerListAdapter(Context context, int resource,
			List<WifiP2pDevice> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		peerList = objects;
	}
	
	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v == null){
			 LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
