package com.example.simpledict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class DictActivity extends Activity {
//	private static final String TAG= "DictActivity";
	private static String DB_NAME = "dict.db";
	private WordDatabaseHelper dbHelper; //声明数据库dbHelper
	private SQLiteDatabase db;
	
	private Button insert =null;
	private Button search = null;
	private EditText editWord = null;
	private EditText editDetail = null;
	private EditText editSearch = null;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //初始化数据库
        dbHelper = new WordDatabaseHelper(this, DB_NAME, null, 1);
        db = dbHelper.getReadableDatabase(); //打开数据库
        
        insert = (Button)this.findViewById(R.id.button_add);
        search = (Button)this.findViewById(R.id.button_search);
        editWord = (EditText)this.findViewById(R.id.edit_word);
        editDetail = (EditText)this.findViewById(R.id.edit_explain);
        
        editSearch = (EditText)this.findViewById(R.id.edit_serach);
        
        insert.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Log.v(DictActivity.TAG, "insert");
				String word = editWord.getText().toString();
				String detail = editDetail.getText().toString();
				
				long rowId = insertData(db, word, detail);
				
				if(rowId == -1){
					Log.i("DictActivity", "数据插入失败!");
				} else {
					Log.i("DictActivity", "数据插入成功" + rowId);
					//提示插入成功
					Toast.makeText(DictActivity.this, DictConstant.newWordInfo + word, Toast.LENGTH_LONG).show();
				}
				refreshStatus();
				editWord.setText("");
				editDetail.setText("");				
			}
        	
        });
       
        search.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//获取查询的关键词
				String key = editSearch.getText().toString();
				//查询，获取结果的cursor
//				Log.v(DictActivity.TAG, "before query");
				Cursor cursor = dbHelper.queryForWords(key);
//				Log.v(DictActivity.TAG, "after query");
				
				
				Bundle bundle = new Bundle();
				//将数据从cursor中提取出来，以List的形式放入bundle对象中
				bundle.putSerializable("data", convertCursorToList(cursor));
				
				Intent intent = new Intent(DictActivity.this, ResultActivity.class);
				intent.putExtras(bundle);
//				Toast.makeText(DictActivity.this, "点击搜索按键", Toast.LENGTH_SHORT).show();
				startActivity(intent);			
						
			}
        	
        });
    }
    //插入数据
    private long insertData(SQLiteDatabase db, String word, String detail){
    	ContentValues values = new ContentValues();
    	values.put("word", word.trim());
    	values.put("detail", detail.trim());
    	long rowid = db.insert(WordDatabaseHelper.TB_NAME, null, values);
    	
    	return rowid;
    }
    //更新界面上当前生词的条数
    private void refreshStatus(){
    	TextView wordNum = (TextView)this.findViewById(R.id.textView_WordNum);
    	int totalNum = this.dbHelper.getTotalNumOfRecord();
    	if(totalNum > 0){
    		wordNum.setText(DictConstant.totalNumDesp + totalNum);
    	} else {
    		wordNum.setText(DictConstant.emptyNum);
    	}
    }
    //查询当前数据库生词条数，并结果设置到界面文本上
    private ArrayList<Map<String, String>>convertCursorToList(Cursor cursor){
    	ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
    	while(cursor.moveToNext()){
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("word", cursor.getString(1));
    		map.put("detail", cursor.getString(2));
    		result.add(map);
    	}
    	cursor.close();
		return result;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dict, menu);
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
    //当DictActivity被销毁时，关闭数据库，释放资源
     @Override
     public void onDestroy(){
    	 super.onDestroy();
    	 if(dbHelper != null){
    		 dbHelper.close(); //关闭数据库，释放资源
    	 }
     }
 
}
