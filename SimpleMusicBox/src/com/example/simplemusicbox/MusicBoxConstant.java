package com.example.simplemusicbox;
/*
 * 常量信息
 */
public class MusicBoxConstant {
	//music box status flag;
	//0x01:没有播放;0x02:正在播放;0x03:暂停
	public static final int IDLE = 0x01;
	public static final int PLAYING = 0x02;
	public static final int PAUSING = 0x03;
	public static String[] titles = new String[] {
		"兄弟一条命",
		"同桌的你",
		"最炫名族风"
	};
	public static String[] authors = new String[]{
		"简红",
		"老狼",
		"凤凰传奇"
	};
	public static final String ACTION_CTL = "action_control";
	public static final String ACTION_UPDATE = "action_update";
	public static final String TOKEN_CURRENT = "token_current";
	public static final String TOKEN_UPDATE = "token_update";
	
	public static final String MUSIC_SERVICE="come.example.simplemusicbox.MUSIC_SERVICE";
}
