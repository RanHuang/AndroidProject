package com.example.myasynctask;
/**
 * ģ�������ӳ�
 * @author Nick
 *
 */
public class NetOperation {
	public static void operator(){
		try{
			//����1s
			Thread.sleep(1000);
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}
