package com.example.simplecalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class CalculationTypeActivity extends Activity{
	public static final int ResultCode=0x321;
	private int calculationType = 0;
	private RadioGroup group;
	private RadioButton additionBtn;
	private RadioButton minusBtn;
	private RadioButton multiplyBtn;
	private RadioButton dividerBtn;
	private Button okBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.calculate_type);
		//根据ID获取各个组件对象
		additionBtn = (RadioButton)findViewById(R.id.addition);
		minusBtn = (RadioButton)findViewById(R.id.minus);
		multiplyBtn = (RadioButton)findViewById(R.id.multiply);
		dividerBtn = (RadioButton)findViewById(R.id.divider);
		group = (RadioGroup)this.findViewById(R.id.group);
		
		group.setOnCheckedChangeListener(new GroupSelectionListener()); //注册选择的监听器
		okBtn = (Button)this.findViewById(R.id.ok);
		okBtn.setOnClickListener(new OkListener()); //注册确认监听器
	}
	class OkListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = getIntent();  //基于Activity获取Intent对象
//			Log.d("calculationType in CalculationTypeActivity", String.valueOf(calculationType));
			intent.putExtra("type1", calculationType); //存入计算类型
			CalculationTypeActivity.this.setResult(ResultCode, intent); //设置放回结果
			CalculationTypeActivity.this.finish(); //销毁当前Activity，返回
		}
	}
	class GroupSelectionListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			int radioButtonId = group.getCheckedRadioButtonId();
			RadioButton btn = (RadioButton)CalculationTypeActivity.this.findViewById(radioButtonId);
			if(btn.getId() == additionBtn.getId()) {
				calculationType = 1;
			} else if(btn.getId() == minusBtn.getId()) {
				calculationType = 2;
			}else if(btn.getId() == multiplyBtn.getId()) {
				calculationType = 3;
			}else if(btn.getId() == dividerBtn.getId()) {
				calculationType = 4;
			}else {
				calculationType = 100;
			}
		}
	}
}
