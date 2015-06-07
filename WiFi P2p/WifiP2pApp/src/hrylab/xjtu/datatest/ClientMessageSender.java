package hrylab.xjtu.datatest;

import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.util.Log;

public class ClientMessageSender implements Runnable {

	private static final String TAG = "ClientMessageSender";
	private static final boolean D = true;
	
	private String host;
	private int port;
	public ClientMessageSender( String host, int port){
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(D) Log.d(TAG, "Starting Message Sender Client...");
		Socket socket = new Socket();
		try{
			//Just for test, wait some time for the server to create socket.
			Thread.sleep(2 * 1000);
			
			socket.bind(null);
			socket.connect((new InetSocketAddress(host, port)), 0);
			
//			MainActivity.logToScreen("Connected to server: " + host + port);
			
			DataInputStream dataIn = new DataInputStream(socket.getInputStream());
			Log.d(TAG, "The received string: " + dataIn.readUTF());
//			MainActivity.logToScreen("The received string: " + dataIn.readUTF());
			Log.d(TAG, "Server IP:" + socket.getRemoteSocketAddress().toString());
			
			dataIn.close();
			socket.close();
			
		} catch(Exception e){
			if(D) Log.w(TAG, "Exception occured when sending message", e);
//			MainActivity.logToScreen(e.toString());
			//If error, try again!
			new Thread(new ClientMessageSender(host, port)).start();
		}
	}

}
