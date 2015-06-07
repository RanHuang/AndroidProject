package com.example.testintent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver{
	StringBuilder sms = new StringBuilder();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[])bundle.get("pdus");
		SmsMessage[] msgs = new SmsMessage[pdus.length];
		for(int i=0; i<pdus.length; i++){
			msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
		}
		for(SmsMessage msg : msgs){
			sms.append("发信人:\n");
			sms.append(msg.getDisplayOriginatingAddress());
			sms.append("\n信息内容:\n");
			sms.append(msg.getDisplayMessageBody());
		}
		Toast.makeText(context, sms.toString(), Toast.LENGTH_LONG).show();
	}	
}
