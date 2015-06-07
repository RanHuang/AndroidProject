package hrylab.xjtu.wifip2papp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class FileServerAsyncTask extends AsyncTask<Void, Void, String>{
	//Debugging
	public static final String TAG = "FileServerAsyncTask";
	
	private Context context = null;
	private TextView statusText = null;
	/**
     * @param context
     * @param statusText
     */
    public FileServerAsyncTask(Context context, View statusText) {
    	Log.d(FileServerAsyncTask.TAG, "Go into FileServerAsyncTask...");
        this.context = context;
        this.statusText = (TextView) statusText;
    }
	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		Log.d(FileServerAsyncTask.TAG, "Go into doInBackground...");
		try{
			ServerSocket serverSocket = new ServerSocket(9000);
			Log.d(FileServerAsyncTask.TAG, "Server: Socket opened");
			Socket client = serverSocket.accept();
			Log.d(FileServerAsyncTask.TAG, "Server: connection done");
			final File file = new File(Environment.getExternalStorageDirectory() + "/"
					+ context.getPackageName() + "/wifip2pshared" + System.currentTimeMillis()
					+ ".jpg");
			File dirs = new File(file.getParent());
			if(!dirs.exists()){
				dirs.mkdirs();
			}
			file.createNewFile();
			Log.d(FileServerAsyncTask.TAG, "server: copying files " + file.toString());
			
			InputStream inputStream = client.getInputStream();
			copyFile(inputStream, new FileOutputStream(file));
			serverSocket.close();
			return file.getAbsolutePath();
		} catch (IOException e){
			Log.e(FileServerAsyncTask.TAG, e.getMessage());
			return null;
		}
	}
	
	private boolean copyFile(InputStream inputStream,
			OutputStream outputStream) {
		// TODO Auto-generated method stub
		Log.d(FileServerAsyncTask.TAG, "Go into copyFile...");
		byte buf[] = new byte[1024];
		int len;
		try{
			while((len = inputStream.read(buf)) != -1){
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch(IOException e){
			Log.d(FileServerAsyncTask.TAG, e.toString());
			return false;
		}
		return true;
	}
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String result){
		if(result != null){
			Log.d(FileServerAsyncTask.TAG, "File copied" + result);
			statusText.setText("File copied" + result);
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + result), "image/*");
			context.startActivity(intent);
		}
	}
	@Override
	protected void onPreExecute(){
		Log.d(FileServerAsyncTask.TAG, "Openning a server socket");
		statusText.setText("Openning a server socket");
	}

}
