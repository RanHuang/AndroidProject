package com.example.simplemusicbox;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.util.Log;


public class MusicService extends Service{ //继承Service类
	private static final String TAG = "MusicService";
	private MyReceiver serviceReceiver; //声明服务接收器对象
	private AssetManager assetManager; //声明资产管理器对象
	private String[] musics = new String[] {
			"brothers.mp3", "nations.mp3", "deskmate.mp3"
	};
	private MediaPlayer mediaPlayer;
	private int status = MusicBoxConstant.IDLE; //状态标识
	private int current = 0;
	
	@Override
	public IBinder onBind(Intent intent) {   //覆写Service方法，空方法体
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		this.unregisterReceiver(serviceReceiver);
		if(mediaPlayer != null){
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
	@Override
	public void onCreate(){ //覆写onCreate方法
		assetManager = this.getAssets(); //获取AssetManager实例
		serviceReceiver = new MyReceiver(); //创建接收器对象
		IntentFilter filter = new IntentFilter(); //创建消息过滤器
		filter.addAction(MusicBoxConstant.ACTION_CTL);//设置类型
		registerReceiver(serviceReceiver, filter); //注册服务监听器
		mediaPlayer = new MediaPlayer(); //创建媒体播放器
		mediaPlayer.setOnCompletionListener(new OnCompletionListener(){ //注册完成播放器

			@Override
			public void onCompletion(MediaPlayer mp) {  //当前歌曲播完之后，继续播放下一首
				// TODO Auto-generated method stub
				current ++;
				if(current > 3){
					current = 0;
				}
				Intent sendIntent = new Intent(MusicBoxConstant.ACTION_UPDATE);
				sendIntent.putExtra(MusicBoxConstant.TOKEN_CURRENT, current);
				sendBroadcast(sendIntent);
				prepareAndPlay(musics[current]);
			}
			
		});
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(MusicService.TAG, "Service started!");
		return 0;
	}
	//播放音乐
	private void prepareAndPlay(String music){
		try {
			/* 重置MediaPlayer */
            mediaPlayer.reset();
			AssetFileDescriptor assetFileDescriptor = assetManager.openFd(music);
			//FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
			//mediaPlayer.setDataSource(fileDescriptor, assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
			assetFileDescriptor.close();
			
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e){
			System.out.println("出现异常"); // Where does such message output?
		}		
	}

	//内部类，实现广播消息的接收和处理逻辑
	public class MyReceiver extends BroadcastReceiver {

		@Override         //覆写onReceive()方法，接收消息之后的处理逻辑
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int control = intent.getIntExtra("control", -1);
			System.out.println("onReceive: " + control);
			switch(control){
				//TO DO
			case 1: //单击播放暂停按钮
				if(status == MusicBoxConstant.IDLE) {
					status = MusicBoxConstant.PLAYING;
					prepareAndPlay(musics[current]);
				} else if(status == MusicBoxConstant.PLAYING) {
					mediaPlayer.pause();
					status = MusicBoxConstant.PAUSING;
				} else {
					mediaPlayer.start();
					status = MusicBoxConstant.PLAYING;
				}
				break;
			case 2: //单击停止按钮
				mediaPlayer.stop();
				status = MusicBoxConstant.IDLE;
				current = 0;
				break;
				default:
					status = MusicBoxConstant.IDLE;
			}
			//调试信息
			switch(status){
			case MusicBoxConstant.IDLE:
				Log.d(MusicService.TAG,"播放器状态:停止。");
				break;
			case MusicBoxConstant.PLAYING:
				Log.d(MusicService.TAG,"播放器状态:播放。");
				break;
			case MusicBoxConstant.PAUSING:
				Log.d(MusicService.TAG,"播放器状态:暂停。");
				break;
				default:
					Log.d(MusicService.TAG,"播放器状态:未知。");
			}
			Intent sendIntent = new Intent(MusicBoxConstant.ACTION_UPDATE);
			sendIntent.putExtra(MusicBoxConstant.TOKEN_CURRENT, current);
			sendIntent.putExtra(MusicBoxConstant.TOKEN_UPDATE, status);
			MusicService.this.sendBroadcast(sendIntent);
		}
		
	}
}
