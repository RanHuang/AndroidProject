package com.example.simpledict;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WordDatabaseHelper extends SQLiteOpenHelper{
	public static final String TB_NAME = "dictionary";
	
	private final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TB_NAME + "(_id integer primary key, word, detail)";
	
	public WordDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
		onCreate(db);
	}
	 //基于关键词查询生词
  	public Cursor queryForWords(String key){
  		//
  		return this.getReadableDatabase().rawQuery("select * from " + TB_NAME + " where word like ? or detail like ?", 
  				new String[] {"%" + key + "%", "%" + key + "%"});
  	}
  	//获取表中的生词总数，返回生词数量
  	public int getTotalNumOfRecord(){
  		Cursor cursor = this.getReadableDatabase().rawQuery("select count(*) from " + TB_NAME, null);
  		int totalNum = 0;
  		if(cursor.moveToNext()){    //Cursor中数据存在
  			totalNum = cursor.getInt(0);  //从Cursor中读取数据条数
  		}
  		return totalNum;
  	}
  	//插入生词--似乎没用到
  	public void insertWord(String[] clos){
  		this.getWritableDatabase().execSQL("insert into " + TB_NAME + "values(null, ?, ?)", clos);
  	}

}
