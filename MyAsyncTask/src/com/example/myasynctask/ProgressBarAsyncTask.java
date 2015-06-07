package com.example.myasynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 生成该类的对象，并调用execute方法之后
 * 首先执行的是onProExecute方法 
 * 其次执行doInBackgroup方法
 * @author Nick
 *
 */
public class ProgressBarAsyncTask extends AsyncTask<String, Integer, String> {

	private TextView textView;
	private ProgressBar progressBar;
	
	public ProgressBarAsyncTask(TextView textView, ProgressBar progressBar){
		super();
		this.progressBar = progressBar;
		this.textView = textView;
	}
	/**
	 * 这里的Integer参数对应AsyncTask中的第一个参数,String返回值对应AsyncTask的第三个参数
	 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
	 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作 
	 */
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		int i = 0;
		for(i=0; i<=100; i+=10){
			NetOperation.operator();
			publishProgress(i);
		}
		return new String("100% End." + params[0]);
	}
	
	//该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置 
	@Override
	protected void onPreExecute(){
		textView.setText("开始执行异步操作");
	}
	/**
	 * String参数对应AsyncTask中的第三个参数(接收doInBackground的返回值)
	 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
	 */
	@Override
	protected void onPostExecute(String result){
		textView.setText("异步操作执行结束: " + result);
	}
	/**  
     * 这里的Integer参数对应AsyncTask中的第二个参数  
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行  
     * onProgressUpdate是在UI线程中执行，所以可以对UI空间进行操作  
     */  
	@Override
	protected void onProgressUpdate(Integer...values){
		int value = values[0];
		progressBar.setProgress(value);
		textView.setText("开始执行异步操作: " + values[0] + "%");
	}
	//onCancelled方法用于在取消执行中的任务时更改UI 
	@Override
	protected void onCancelled(){
		textView.setText("cancelled");
		progressBar.setProgress(0); 
	}
}
