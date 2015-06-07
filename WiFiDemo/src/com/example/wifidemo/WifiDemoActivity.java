package com.example.wifidemo;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class WifiDemoActivity extends Activity {
	//变量定义
	private Button bt_open, bt_close, bt_check, bt_search;
	private TextView textView;
	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
//	private ScrollView scrollView;
//  private List wifiConfiguration;
	private ScanResult scanResult;
	private List<ScanResult> wifiList;
	private StringBuffer stringBuffer = new StringBuffer();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_demo);
        //绑定界面对象
//        scrollView = (ScrollView)findViewById(R.id.mScrollView);
        bt_open = (Button)findViewById(R.id.bt_open);
        bt_close = (Button)findViewById(R.id.bt_close);
        bt_check = (Button)findViewById(R.id.bt_check);
        bt_search = (Button)findViewById(R.id.bt_search);
        textView = (TextView)findViewById(R.id.text);
        
        bt_open.setOnClickListener(new open_btListener());
        bt_close.setOnClickListener(new close_btListener());
        bt_search.setOnClickListener(new search_btListener());
        bt_check.setOnClickListener(new check_btListener());
    }
    
    //Wifi 打开监听器
    class open_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);
    		wifiManager.setWifiEnabled(true);
    		System.out.println("wifi state --->" + wifiManager.getWifiState());
    		Toast.makeText(WifiDemoActivity.this, "当前网络状态为: " + change(), Toast.LENGTH_SHORT).show();//A delay exists.
    	}
    }
    //Wifi 关闭监听器
    class close_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);
    		wifiManager.setWifiEnabled(false);
    		System.out.println("wifi state --->" + wifiManager.getWifiState());
    		Toast.makeText(WifiDemoActivity.this, "当前网络状态为: " + change(), Toast.LENGTH_SHORT).show(); //A delay exists.
    	}
    }
    //Wifi 搜索监听器
    class search_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);
    		/*
    		if(wifiManager.getWifiState() == 1){
    			Toast.makeText(WifiDemoActivity.this, "当前网络状态为: " + change(), Toast.LENGTH_SHORT).show();
    			return;
    		}*/
    		wifiManager.startScan();
    		wifiList = wifiManager.getScanResults(); //搜索的信号列表
    		wifiInfo = wifiManager.getConnectionInfo(); //当前链接的Wifi信号信息
    		if(stringBuffer != null) stringBuffer = new StringBuffer();
    		//打印Wifi信号列表
    		stringBuffer = stringBuffer
    				.append("Wifi 名称").append("  ")
    				.append("Wifi 地址").append("  ")
    				.append("Wifi 频率").append("  ")
    				.append("Wifi 信号").append("\n");
    		if(wifiList != null){
    			for(int i = 0; i < wifiList.size(); i++){
    				scanResult = wifiList.get(i);
    				stringBuffer = stringBuffer
    						.append(scanResult.SSID).append("  ")
    						.append(scanResult.BSSID).append("  ")
    						.append(scanResult.frequency).append("  ")
    						.append(scanResult.level).append("\n");
    				textView.setText(stringBuffer.toString());
    			}
    			//分界线
    			stringBuffer = stringBuffer.append("-------------------------------------------------------------------").append("\n");
    			textView.setText(stringBuffer.toString());
    			//显示当前Wifi信号信息
    			stringBuffer = stringBuffer
    					.append("当前Wifi——SSID").append(":   ").append(wifiInfo.getSSID()).append("\n")
    					.append("当前Wifi——BSSID").append(":   ").append(wifiInfo.getBSSID()).append("\n")
    					.append("当前Wifi——HiddenSSID").append(":   ").append(wifiInfo.getHiddenSSID()).append("\n")
    					.append("当前Wifi——IpAddress").append(":   ").append(wifiInfo.getIpAddress()).append("\n")
    					.append("当前Wifi——LinkSpeed").append(":   ").append(wifiInfo.getLinkSpeed()).append("\n")
    					.append("当前Wifi——MacAddress").append(":   ").append(wifiInfo.getMacAddress()).append("\n")
    					.append("当前Wifi——NetworkID").append(":   ").append(wifiInfo.getNetworkId()).append("\n")
    					.append("当前Wifi——RSSI").append(":   ").append(wifiInfo.getRssi()).append("\n")    					
    					.append("-------------------------------------------------------------------").append("\n")
    					.append("全部打印出关于本机Wifi信息").append(":   ").append(wifiInfo.toString());
    			textView.setText(stringBuffer.toString());
    			
    			
    		}
    	}
    }
    //Wifi 检查监听器
    class check_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);    		
    		System.out.println("wifi state --->" + wifiManager.getWifiState());
    		Toast.makeText(WifiDemoActivity.this, "当前网络状态为: " + change(), Toast.LENGTH_SHORT).show();
    	}
    }
    public String change(){
    	String temp = null;
    	switch(wifiManager.getWifiState()){
    	case WifiManager.WIFI_STATE_DISABLING:
    		temp = "Wifi 正在关闭 ING";
    		break;
    	case WifiManager.WIFI_STATE_DISABLED:
    		temp = "Wifi 已经关闭";
    		break;
    	case WifiManager.WIFI_STATE_ENABLING:
    		temp = "WiFi 正在打开ING";
    		break;
    	case WifiManager.WIFI_STATE_ENABLED:
    		temp = "Wifi 已经打开";
    		break;
    	default:
    		temp = "未知状态？？？";    			
    	}
    	return temp;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wifi_demo, menu);
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
}
