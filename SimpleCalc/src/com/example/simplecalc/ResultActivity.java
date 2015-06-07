package com.example.simplecalc;

import com.example.simplecalc.info.ConstantInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity{
	private Button backBtn;
	private TextView infoText;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_result);
		
		Intent intent = this.getIntent();
		//绑定界面对象
		infoText = (TextView)this.findViewById(R.id.resultText);
		backBtn = (Button)this.findViewById(R.id.back);
		
		StringBuilder builder = new StringBuilder();
		//构造表达式，firstNumber + secondNumber = result;
		builder.append(intent.getStringExtra("firstNumber"))
			.append(" ")
			.append(ConstantInfo.infoMap.get(intent.getIntExtra("calcType", 0)))
			.append(" ")
			.append(intent.getStringExtra("secondNumber"))
			.append(" ")
			.append("=")
			.append(intent.getIntExtra("result", -1));
		
		infoText.setText(builder.toString());
		backBtn.setOnClickListener(new BackListener()); //注册返回监听器	
	}
	
	class BackListener implements OnClickListener {
		@Override
		public  void onClick(View view) {
			Intent intent = new Intent(ResultActivity.this, MainActivity.class); // 创建跳转界面的Intent
			startActivity(intent);
			ResultActivity.this.finish(); // 销毁当前Activity
		}
	}
}
