package com.example.wifidirectappvone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

	private static final String TAG = "WifiP2pAppV1 MainActivity";
	private static final boolean D = true;
	
	private static TextView textLog;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(D) Log.i(TAG, "+++ ON CREATE +++");
        
        //设置TextView可以滚动
        textLog = (TextView)this.findViewById(R.id.text_log);
        textLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        startService(new Intent(this, MainService.class));
    }

    public static void logToScreen(final String text) {
		if(D) Log.i(TAG, text);
		
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(textLog != null){
					textLog.setText(textLog.getText() + "\n" + text);
				}
				
				Layout layout = textLog.getLayout();
				if(layout != null){
					final int scrollAmount = layout.getLineTop(textLog.getLineCount()) - textLog.getHeight();
					if(scrollAmount > 0){
						textLog.scrollTo(0, scrollAmount);
					} else { //there is no need to scroll
						textLog.scrollTo(0, 0);
					}
				}
			}
			
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		logToScreen("*!!*MainActivity: onDestroy() stopService.");
		stopService(new Intent(this, MainService.class));

	}
}
