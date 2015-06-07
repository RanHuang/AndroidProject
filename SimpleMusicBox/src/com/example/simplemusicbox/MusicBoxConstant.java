package com.example.simplemusicbox;
/*
 * ������Ϣ
 */
public class MusicBoxConstant {
	//music box status flag;
	//0x01:û�в���;0x02:���ڲ���;0x03:��ͣ
	public static final int IDLE = 0x01;
	public static final int PLAYING = 0x02;
	public static final int PAUSING = 0x03;
	public static String[] titles = new String[] {
		"�ֵ�һ����",
		"ͬ������",
		"���������"
	};
	public static String[] authors = new String[]{
		"���",
		"����",
		"��˴���"
	};
	public static final String ACTION_CTL = "action_control";
	public static final String ACTION_UPDATE = "action_update";
	public static final String TOKEN_CURRENT = "token_current";
	public static final String TOKEN_UPDATE = "token_update";
	
	public static final String MUSIC_SERVICE="come.example.simplemusicbox.MUSIC_SERVICE";
}
