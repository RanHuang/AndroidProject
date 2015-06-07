package hrylab.xjtu.datatest;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import android.util.Log;

public class ServerMessageReceiver implements Runnable {

	private static final String TAG = "ServerMessageReceiver";
	private static final boolean D = true;
	
	public static final int PORT = 8765;
	
	public ServerMessageReceiver(){
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(D) Log.i(TAG, "Started Message Receive Server task...");
		ServerSocket serverSocket = null;
		try{
			serverSocket = new ServerSocket(ServerMessageReceiver.PORT);
			while(true){
				Socket socket = serverSocket.accept();
				new Thread(new SingleClientHandler(socket), "Single Client Server").start();				
			}
		} catch(Exception e){
			if(D) Log.w(TAG, e);
//			MainActivity.logToScreen(e.toString());
		} finally{
			if(serverSocket != null){
				try{
					serverSocket.close();
				} catch(Exception e){
					if(D) Log.w(TAG, "Exception when closing server socket", e);
				}
			}
		}
		
		
	}
	
	private class SingleClientHandler implements Runnable {
		
		private Socket mSocket;
		public SingleClientHandler(Socket socket){
			mSocket = socket;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			MainActivity.logToScreen("#^^#Received a connection from a Client:");
			try{
				Log.d(TAG, "Client IP:" + mSocket.getRemoteSocketAddress().toString());
				DataOutputStream dataOut = new DataOutputStream(mSocket.getOutputStream());
				dataOut.writeUTF("Hello Kitty, I am the Server! Ha Ha Ha...");
				dataOut.close();
				mSocket.close();
			} catch(Exception e){
				Log.d(TAG, "An error occurred in the Server socket.");
			}			
		}
	}
	
}