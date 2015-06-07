package com.example.wifidirectdemo;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.Channel;
/**
 * An interface-callback for the activity to listen to wifi connection interaction
 * events.
 */
public interface DeviceActionListener {
	void connect(Channel channel, WifiP2pDevice device);
	void disconnect(Channel channel);
}
