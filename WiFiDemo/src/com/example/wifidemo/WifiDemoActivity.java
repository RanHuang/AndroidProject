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
	//��������
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
        //�󶨽������
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
    
    //Wifi �򿪼�����
    class open_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);
    		wifiManager.setWifiEnabled(true);
    		System.out.println("wifi state --->" + wifiManager.getWifiState());
    		Toast.makeText(WifiDemoActivity.this, "��ǰ����״̬Ϊ: " + change(), Toast.LENGTH_SHORT).show();//A delay exists.
    	}
    }
    //Wifi �رռ�����
    class close_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);
    		wifiManager.setWifiEnabled(false);
    		System.out.println("wifi state --->" + wifiManager.getWifiState());
    		Toast.makeText(WifiDemoActivity.this, "��ǰ����״̬Ϊ: " + change(), Toast.LENGTH_SHORT).show(); //A delay exists.
    	}
    }
    //Wifi ����������
    class search_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);
    		/*
    		if(wifiManager.getWifiState() == 1){
    			Toast.makeText(WifiDemoActivity.this, "��ǰ����״̬Ϊ: " + change(), Toast.LENGTH_SHORT).show();
    			return;
    		}*/
    		wifiManager.startScan();
    		wifiList = wifiManager.getScanResults(); //�������ź��б�
    		wifiInfo = wifiManager.getConnectionInfo(); //��ǰ���ӵ�Wifi�ź���Ϣ
    		if(stringBuffer != null) stringBuffer = new StringBuffer();
    		//��ӡWifi�ź��б�
    		stringBuffer = stringBuffer
    				.append("Wifi ����").append("  ")
    				.append("Wifi ��ַ").append("  ")
    				.append("Wifi Ƶ��").append("  ")
    				.append("Wifi �ź�").append("\n");
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
    			//�ֽ���
    			stringBuffer = stringBuffer.append("-------------------------------------------------------------------").append("\n");
    			textView.setText(stringBuffer.toString());
    			//��ʾ��ǰWifi�ź���Ϣ
    			stringBuffer = stringBuffer
    					.append("��ǰWifi����SSID").append(":   ").append(wifiInfo.getSSID()).append("\n")
    					.append("��ǰWifi����BSSID").append(":   ").append(wifiInfo.getBSSID()).append("\n")
    					.append("��ǰWifi����HiddenSSID").append(":   ").append(wifiInfo.getHiddenSSID()).append("\n")
    					.append("��ǰWifi����IpAddress").append(":   ").append(wifiInfo.getIpAddress()).append("\n")
    					.append("��ǰWifi����LinkSpeed").append(":   ").append(wifiInfo.getLinkSpeed()).append("\n")
    					.append("��ǰWifi����MacAddress").append(":   ").append(wifiInfo.getMacAddress()).append("\n")
    					.append("��ǰWifi����NetworkID").append(":   ").append(wifiInfo.getNetworkId()).append("\n")
    					.append("��ǰWifi����RSSI").append(":   ").append(wifiInfo.getRssi()).append("\n")    					
    					.append("-------------------------------------------------------------------").append("\n")
    					.append("ȫ����ӡ�����ڱ���Wifi��Ϣ").append(":   ").append(wifiInfo.toString());
    			textView.setText(stringBuffer.toString());
    			
    			
    		}
    	}
    }
    //Wifi ��������
    class check_btListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		wifiManager = (WifiManager)WifiDemoActivity.this.getSystemService(Context.WIFI_SERVICE);    		
    		System.out.println("wifi state --->" + wifiManager.getWifiState());
    		Toast.makeText(WifiDemoActivity.this, "��ǰ����״̬Ϊ: " + change(), Toast.LENGTH_SHORT).show();
    	}
    }
    public String change(){
    	String temp = null;
    	switch(wifiManager.getWifiState()){
    	case WifiManager.WIFI_STATE_DISABLING:
    		temp = "Wifi ���ڹر� ING";
    		break;
    	case WifiManager.WIFI_STATE_DISABLED:
    		temp = "Wifi �Ѿ��ر�";
    		break;
    	case WifiManager.WIFI_STATE_ENABLING:
    		temp = "WiFi ���ڴ�ING";
    		break;
    	case WifiManager.WIFI_STATE_ENABLED:
    		temp = "Wifi �Ѿ���";
    		break;
    	default:
    		temp = "δ֪״̬������";    			
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
