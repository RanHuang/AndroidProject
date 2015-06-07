package info.fshi.datamule;

import android.bluetooth.BluetoothDevice;
import android.view.View.OnClickListener;

public class BTDevice{
	private short rssi;
	private int connState;
	private BluetoothDevice btDevice;
	private OnClickListener btConnect;
	private long avgContactsDelay;
	private int contactTimes;
	private long connectionStartTime;
	private int retryCounter;
	
	/**
	 * init a self-defined bluetooth device using android bluetooth object
	 * @param device
	 */
	public BTDevice(BluetoothDevice device){
		this.btDevice = device;
		this.contactTimes = 0;
		this.avgContactsDelay = 0;
		this.retryCounter = 3;
	}
	
	/**
	 * set connection start time of the communication in order to calculate the time taken for each comm
	 * @param connectionTime
	 */
	public void setConnectionStartTime(long connectionTime){
		this.connectionStartTime = connectionTime;
	}
	
	/**
	 * if set retry counter, default is 3
	 */
	public void resetRetryCounter(){
		this.retryCounter = 3;
	}
	
	/**
	 * for each attempt, decrease the counter
	 */
	public void decRetryCounter(){
		this.retryCounter--;
	}

	/**
	 * return retry counter
	 * @return
	 */
	public int getRetryCounter(){
		return this.retryCounter;
	}
	
	/**
	 * return connection start time
	 * @return
	 */
	public long getConnectionStartTime(){
		return this.connectionStartTime;
	}
	
	/**
	 * return contact delay of this device
	 * @return
	 */
	public long getDelay(){
		return this.avgContactsDelay;
	}
	
	/**
	 * update the overall delay with the new delay value
	 * @param delay
	 */
	public void updateConnectionDelay(long delay){
		avgContactsDelay = ((contactTimes*avgContactsDelay) + delay) / (contactTimes + 1);
		contactTimes ++;
	}
	
	/**
	 * set the connection state of this device
	 * @param state
	 */
	public void setConnState(int state){
		this.connState = state;
	}
	
	/**
	 * return the connection state
	 * @return
	 */
	public int getConnState(){
		return connState;
	}
	
	/**
	 * set the action by clicking the button
	 * @param listener
	 */
	public void setOnClickListener(OnClickListener listener){
		this.btConnect = listener;
	}
	
	/**
	 * get the listener
	 * @return
	 */
	public OnClickListener getOnClickListener(){
		return btConnect;
	}

	/**
	 * get the bluetooth device
	 * @return
	 */
	public BluetoothDevice getRawDevice(){
		return btDevice;
	}
	
	/**
	 * rssi
	 * @param rssi
	 */
	public void setRssi(short rssi){
		this.rssi = rssi;
	}
	
	/**
	 * bt device name
	 * @return
	 */
	public String getName(){
		return btDevice.getName();
	}
	
	/**
	 * mac address
	 * @return
	 */
	public String getMAC(){
		return btDevice.getAddress();
	}
	
	/**
	 * return rssi
	 * @return
	 */
	public short getRssi(){
		return rssi;
	}
	
}
