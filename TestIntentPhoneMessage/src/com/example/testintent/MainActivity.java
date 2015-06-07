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
				//通过Intent.ACTION_CALL建立一个进行拨号的Intent请求
//				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
				//单击拨号按钮之后不再直接呼叫，而是允许Android系统默认的拨号程序，用户拥有进一步决定下一步操作的权限
				Intent intent  = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
				startActivity(intent); //直接启动Android系统拨号程序进行呼叫
			}
        	
        });
        
        smsText.setText("Waiting...");
        
        buttonSend.setOnClickListener(new buttonSendListener()); //未发送按钮添加监听器
    }
    private class buttonSendListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			editTextRecvMan = (EditText)findViewById(R.id.editTextRecvMan);
	        editTextMsm = (EditText)findViewById(R.id.editTextMsm);
	        String number = editTextRecvMan.getText().toString(); //获取手机号码
	        String message = editTextMsm.getText().toString(); //获取短信内容
	        if(number.equals("") || message.equals("")){
	        	Toast.makeText(MainActivity.this, "输入有误，请检查...", Toast.LENGTH_LONG).show();
	        } else {
	        	SmsManager sms = SmsManager.getDefault();
	        	sms.sendTextMessage(number, null, message, null, null);
	        	Toast.makeText(MainActivity.this, "短信发送成功", Toast.LENGTH_LONG).show();
	        }
	        
		}
    	
    }
}
