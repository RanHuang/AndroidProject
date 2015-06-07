package com.example.wifidirectcommunication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService{
	public static final String TAG = "FileTransferService";
	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "com.example.wifidirectcommunication.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    //for file transfer
    public static final int CHOOSE_FILE_RESULT_CODE = 20;
    
	public FileTransferService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		Log.d(FileTransferService.TAG, "Go into  FileTransferService...");
	}
	public FileTransferService() {
        super("FileTransferService");
        Log.d(FileTransferService.TAG, "Go into  FileTransferService...");
    }
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(FileTransferService.TAG, "Go into  onHandleIntent...");
		// TODO Auto-generated method stub
		Context context = getApplicationContext();
		if(intent.getAction().equals(ACTION_SEND_FILE)){
			String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
			String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
			int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
			Socket socket = new Socket();
			
			try{
				Log.d(FileTransferService.TAG, "Opening client socket - ");
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
				
				Log.d(FileTransferService.TAG, "Client socket - " + socket.isConnected());
				
				OutputStream outputStream = socket.getOutputStream();
				ContentResolver cr = context.getContentResolver();
				InputStream inputStream = null;
				try{
					inputStream = cr.openInputStream(Uri.parse(fileUri));
				}catch(FileNotFoundException e){
					Log.d(FileTransferService.TAG, e.toString());
				}
				//copyfile
				copyFile(inputStream, outputStream);
			} catch(IOException e){
				Log.d(FileTransferService.TAG, e.toString());
			}finally{
				if(socket != null){
					if(socket.isConnected()){
						try{
							socket.close();
						}catch(IOException e){
							//Give up
							e.printStackTrace();
						}
					}
				}
			}
			
		}
	}
	private boolean copyFile(InputStream inputStream,
			OutputStream outputStream) {
		// TODO Auto-generated method stub
		Log.d(FileTransferService.TAG, "Go into copyFile...");
		byte buf[] = new byte[1024];
		int len;
		try{
			while((len = inputStream.read(buf)) != -1){
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch(IOException e){
			Log.d(FileTransferService.TAG, e.toString());
			return false;
		}
		return true;
	}
}
