package com.example.simpledict;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ResultActivity extends Activity{
	private ListView listView = null;
	private Button button_close = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
		//获取ListView组件
		listView = (ListView)this.findViewById(R.id.list);
		button_close = (Button)this.findViewById(R.id.button_close);
		Intent intent = this.getIntent(); //获取当前Activity的Intent
		Bundle data = intent.getExtras();
		//从Bundle对象中将数据反序列化出来
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = (List<Map<String, String>>)data.getSerializable("data");
		//创建ListView的Adapter
		SimpleAdapter adapter = new SimpleAdapter(ResultActivity.this, list, R.layout.search_result,
				new String[]{"word", "detail"}, new int[]{R.id.editText_searchWrod, R.id.editText_searchExplain});
		//更新title信息
		this.setTitle(DictConstant.matchedResult + "(" + list.size() + ")");
		listView.setAdapter(adapter);
		
		button_close.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
	}

}
