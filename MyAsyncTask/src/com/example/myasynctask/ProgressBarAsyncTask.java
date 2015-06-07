package com.example.myasynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * ���ɸ���Ķ��󣬲�����execute����֮��
 * ����ִ�е���onProExecute���� 
 * ���ִ��doInBackgroup����
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
	 * �����Integer������ӦAsyncTask�еĵ�һ������,String����ֵ��ӦAsyncTask�ĵ���������
	 * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�
	 * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в��� 
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
	
	//�÷���������UI�̵߳���,����������UI�̵߳��� ���Զ�UI�ռ�������� 
	@Override
	protected void onPreExecute(){
		textView.setText("��ʼִ���첽����");
	}
	/**
	 * String������ӦAsyncTask�еĵ���������(����doInBackground�ķ���ֵ)
	 * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������
	 */
	@Override
	protected void onPostExecute(String result){
		textView.setText("�첽����ִ�н���: " + result);
	}
	/**  
     * �����Integer������ӦAsyncTask�еĵڶ�������  
     * ��doInBackground�������У���ÿ�ε���publishProgress�������ᴥ��onProgressUpdateִ��  
     * onProgressUpdate����UI�߳���ִ�У����Կ��Զ�UI�ռ���в���  
     */  
	@Override
	protected void onProgressUpdate(Integer...values){
		int value = values[0];
		progressBar.setProgress(value);
		textView.setText("��ʼִ���첽����: " + values[0] + "%");
	}
	//onCancelled����������ȡ��ִ���е�����ʱ����UI 
	@Override
	protected void onCancelled(){
		textView.setText("cancelled");
		progressBar.setProgress(0); 
	}
}
