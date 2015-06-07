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
	//绑定组件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //绑定组件
        title = (TextView)this.findViewById(R.id.title);
        author = (TextView)this.findViewById(R.id.author);        
        
        //创建监听器
        MusicListener musicListener = new MusicListener();
        //获取play按钮
        play = (ImageButton)this.findViewById(R.id.play);
        play.setOnClickListener(musicListener); //注册按钮接收器
        stop = (ImageButton)this.findViewById(R.id.stop);
        stop.setOnClickListener(musicListener);
        
        actReceiver = new ActivityReceiver(); //创建广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicBoxConstant.ACTION_UPDATE);
        this.registerReceiver(actReceiver, filter); //注册广播监听器
        
        Intent actionIntent = new Intent();
        //设置消息类型
        actionIntent.setAction(MusicBoxConstant.MUSIC_SERVICE);
        
        this.startService(actionIntent);    
    }

    @Override 
    protected void onDestroy(){
    	 Intent actionIntent = new Intent();
         //设置消息类型
         actionIntent.setAction(MusicBoxConstant.MUSIC_SERVICE);
         this.stopService(actionIntent); //停止Service
         
    	if(actReceiver != null){
    		this.unregisterReceiver(actReceiver); //注销广播接收器
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
    
    //内部类
    public class ActivityReceiver extends BroadcastReceiver{
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//获取Intent中的update，update代表播放状态
			int status = intent.getIntExtra(MusicBoxConstant.TOKEN_UPDATE, -1);
			//获取Intent中的current，current代表当前正在播放的歌曲
			int current = intent.getIntExtra(MusicBoxConstant.TOKEN_CURRENT, -1);
			Toast.makeText(getApplicationContext(), "目前的播放状态：" + String.valueOf(status),
				     Toast.LENGTH_SHORT).show();
			if(current >= 0 ){
				//设置当前播放的歌手与歌曲
				title.setText(MusicBoxConstant.titles[current]);
				author.setText(MusicBoxConstant.authors[current]);
			}
			//根据当前的播放状态，调整播放器按钮的状态和图标类型
		}
    	
    }
    private class MusicListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MusicBoxConstant.ACTION_CTL);
			switch(v.getId()){ //根据事件源的按钮，设置Intent消息
			case R.id.play:
				intent.putExtra("control", 1); //用户单击播放，1
				Toast.makeText(getApplicationContext(), "单击播放暂停按钮",
					     Toast.LENGTH_SHORT).show();
				break;
			case R.id.stop:
				intent.putExtra("control", 2); //用户单击停止，2
				Toast.makeText(getApplicationContext(), "单击停止按钮",
					     Toast.LENGTH_SHORT).show();
				break;
			}
			//发送广播，将被Service组件中的BroadcastReceiver接收
			MusicBox.this.sendBroadcast(intent);
		}
    	
    }
}
