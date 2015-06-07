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


public class MusicService extends Service{ //�̳�Service��
	private static final String TAG = "MusicService";
	private MyReceiver serviceReceiver; //�����������������
	private AssetManager assetManager; //�����ʲ�����������
	private String[] musics = new String[] {
			"brothers.mp3", "nations.mp3", "deskmate.mp3"
	};
	private MediaPlayer mediaPlayer;
	private int status = MusicBoxConstant.IDLE; //״̬��ʶ
	private int current = 0;
	
	@Override
	public IBinder onBind(Intent intent) {   //��дService�������շ�����
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
	public void onCreate(){ //��дonCreate����
		assetManager = this.getAssets(); //��ȡAssetManagerʵ��
		serviceReceiver = new MyReceiver(); //��������������
		IntentFilter filter = new IntentFilter(); //������Ϣ������
		filter.addAction(MusicBoxConstant.ACTION_CTL);//��������
		registerReceiver(serviceReceiver, filter); //ע����������
		mediaPlayer = new MediaPlayer(); //����ý�岥����
		mediaPlayer.setOnCompletionListener(new OnCompletionListener(){ //ע����ɲ�����

			@Override
			public void onCompletion(MediaPlayer mp) {  //��ǰ��������֮�󣬼���������һ��
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
	//��������
	private void prepareAndPlay(String music){
		try {
			/* ����MediaPlayer */
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
			System.out.println("�����쳣"); // Where does such message output?
		}		
	}

	//�ڲ��࣬ʵ�ֹ㲥��Ϣ�Ľ��պʹ����߼�
	public class MyReceiver extends BroadcastReceiver {

		@Override         //��дonReceive()������������Ϣ֮��Ĵ����߼�
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int control = intent.getIntExtra("control", -1);
			System.out.println("onReceive: " + control);
			switch(control){
				//TO DO
			case 1: //����������ͣ��ť
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
			case 2: //����ֹͣ��ť
				mediaPlayer.stop();
				status = MusicBoxConstant.IDLE;
				current = 0;
				break;
				default:
					status = MusicBoxConstant.IDLE;
			}
			//������Ϣ
			switch(status){
			case MusicBoxConstant.IDLE:
				Log.d(MusicService.TAG,"������״̬:ֹͣ��");
				break;
			case MusicBoxConstant.PLAYING:
				Log.d(MusicService.TAG,"������״̬:���š�");
				break;
			case MusicBoxConstant.PAUSING:
				Log.d(MusicService.TAG,"������״̬:��ͣ��");
				break;
				default:
					Log.d(MusicService.TAG,"������״̬:δ֪��");
			}
			Intent sendIntent = new Intent(MusicBoxConstant.ACTION_UPDATE);
			sendIntent.putExtra(MusicBoxConstant.TOKEN_CURRENT, current);
			sendIntent.putExtra(MusicBoxConstant.TOKEN_UPDATE, status);
			MusicService.this.sendBroadcast(sendIntent);
		}
		
	}
}
