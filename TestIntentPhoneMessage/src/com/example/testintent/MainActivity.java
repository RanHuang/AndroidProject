package com.example.testintent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button button = null;
	private EditText editText = null;
	private EditText smsText = null;
	private Button buttonSend = null;
	private EditText editTextRecvMan = null;
	private EditText editTextMsm = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        button = (Button)findViewById(R.id.button);       
        smsText = (EditText)findViewById(R.id.smsText);
        buttonSend = (Button)findViewById(R.id.buttonSend);
               
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editText = (EditText)findViewById(R.id.editText);
				String tel = editText.getText().toString();
				//ͨ��Intent.ACTION_CALL����һ�����в��ŵ�Intent����
//				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
				//�������Ű�ť֮����ֱ�Ӻ��У���������AndroidϵͳĬ�ϵĲ��ų����û�ӵ�н�һ��������һ��������Ȩ��
				Intent intent  = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
				startActivity(intent); //ֱ������Androidϵͳ���ų�����к���
			}
        	
        });
        
        smsText.setText("Waiting...");
        
        buttonSend.setOnClickListener(new buttonSendListener()); //δ���Ͱ�ť��Ӽ�����
    }
    private class buttonSendListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			editTextRecvMan = (EditText)findViewById(R.id.editTextRecvMan);
	        editTextMsm = (EditText)findViewById(R.id.editTextMsm);
	        String number = editTextRecvMan.getText().toString(); //��ȡ�ֻ�����
	        String message = editTextMsm.getText().toString(); //��ȡ��������
	        if(number.equals("") || message.equals("")){
	        	Toast.makeText(MainActivity.this, "������������...", Toast.LENGTH_LONG).show();
	        } else {
	        	SmsManager sms = SmsManager.getDefault();
	        	sms.sendTextMessage(number, null, message, null, null);
	        	Toast.makeText(MainActivity.this, "���ŷ��ͳɹ�", Toast.LENGTH_LONG).show();
	        }
	        
		}
    	
    }
}
