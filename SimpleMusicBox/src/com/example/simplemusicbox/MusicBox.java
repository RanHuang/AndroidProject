package com.example.simplemusicbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MusicBox extends Activity {
	private ImageButton play = null;
	private ImageButton stop = null;
	private ActivityReceiver actReceiver = null;
	private TextView title = null;
	private TextView author = null;
	//�����
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //�����
        title = (TextView)this.findViewById(R.id.title);
        author = (TextView)this.findViewById(R.id.author);        
        
        //����������
        MusicListener musicListener = new MusicListener();
        //��ȡplay��ť
        play = (ImageButton)this.findViewById(R.id.play);
        play.setOnClickListener(musicListener); //ע�ᰴť������
        stop = (ImageButton)this.findViewById(R.id.stop);
        stop.setOnClickListener(musicListener);
        
        actReceiver = new ActivityReceiver(); //�����㲥������
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicBoxConstant.ACTION_UPDATE);
        this.registerReceiver(actReceiver, filter); //ע��㲥������
        
        Intent actionIntent = new Intent();
        //������Ϣ����
        actionIntent.setAction(MusicBoxConstant.MUSIC_SERVICE);
        
        this.startService(actionIntent);    
    }

    @Override 
    protected void onDestroy(){
    	 Intent actionIntent = new Intent();
         //������Ϣ����
         actionIntent.setAction(MusicBoxConstant.MUSIC_SERVICE);
         this.stopService(actionIntent); //ֹͣService
         
    	if(actReceiver != null){
    		this.unregisterReceiver(actReceiver); //ע���㲥������
    	}
    	super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_box, menu);
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
    
    //�ڲ���
    public class ActivityReceiver extends BroadcastReceiver{
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//��ȡIntent�е�update��update������״̬
			int status = intent.getIntExtra(MusicBoxConstant.TOKEN_UPDATE, -1);
			//��ȡIntent�е�current��current����ǰ���ڲ��ŵĸ���
			int current = intent.getIntExtra(MusicBoxConstant.TOKEN_CURRENT, -1);
			Toast.makeText(getApplicationContext(), "Ŀǰ�Ĳ���״̬��" + String.valueOf(status),
				     Toast.LENGTH_SHORT).show();
			if(current >= 0 ){
				//���õ�ǰ���ŵĸ��������
				title.setText(MusicBoxConstant.titles[current]);
				author.setText(MusicBoxConstant.authors[current]);
			}
			//���ݵ�ǰ�Ĳ���״̬��������������ť��״̬��ͼ������
		}
    	
    }
    private class MusicListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MusicBoxConstant.ACTION_CTL);
			switch(v.getId()){ //�����¼�Դ�İ�ť������Intent��Ϣ
			case R.id.play:
				intent.putExtra("control", 1); //�û��������ţ�1
				Toast.makeText(getApplicationContext(), "����������ͣ��ť",
					     Toast.LENGTH_SHORT).show();
				break;
			case R.id.stop:
				intent.putExtra("control", 2); //�û�����ֹͣ��2
				Toast.makeText(getApplicationContext(), "����ֹͣ��ť",
					     Toast.LENGTH_SHORT).show();
				break;
			}
			//���͹㲥������Service����е�BroadcastReceiver����
			MusicBox.this.sendBroadcast(intent);
		}
    	
    }
}
