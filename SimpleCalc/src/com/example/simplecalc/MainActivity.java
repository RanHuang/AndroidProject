package com.example.simplecalc;

import com.example.simplecalc.info.ConstantInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	private Button selectBtn; //选择计算类型Button
	private Button calcBtn; //进行计算Button
    private int calcType=-1;
    private TextView calcTypeText;
    
    private EditText firstText = null;
    private EditText secondText = null;
    
    //唯一标识这个请求来自主用户界面
    public static final int RequestCode=0x123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //获取界面上的Button对象
        selectBtn=(Button)findViewById(R.id.selectType);
        calcBtn=(Button)findViewById(R.id.calculate);
        calcTypeText=(TextView)findViewById(R.id.calc_type);
        firstText = (EditText)findViewById(R.id.firstNumber);
        secondText = (EditText)findViewById(R.id.secondNumber);
        
        //注册Button对象的事件监听器
        selectBtn.setOnClickListener(new SelectCalculationTypeListener());
        calcBtn.setOnClickListener(new CalculationListener());
    }
    //获取用户选择的计算类型
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	//判断是否为从当前Activity发出去的请求，并是从CalculationTypeActivity返回结果
    	if(RequestCode==requestCode && CalculationTypeActivity.ResultCode==resultCode) {
    		Bundle bundle = data.getExtras();
 //   		calcType = data.getIntExtra("type1", -1);
    		calcType = bundle.getInt("type1");
    		calcTypeText.setText(ConstantInfo.typeMap.get(calcType));
//    		Log.d("calculationType in MainActivity", String.valueOf(calcType));
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    //selectBtn监听器
    class SelectCalculationTypeListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		//创建跳转到选择计算类型的Intent消息
    		Intent intent=new Intent(MainActivity.this, CalculationTypeActivity.class);
    		//在选择计算类型界面操作完成后，返回结果到MainActivity
    		//告诉计算类型界面需要获取在计算类型界面上用户选择的计算类型的值,RequestCode是用户唯一标识这个请求来自主用户界面
    		MainActivity.this.startActivityForResult(intent, RequestCode);
    	}
    }
    //calcBtn监听器
    class CalculationListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		Intent intent = new Intent(MainActivity.this, ResultActivity.class); //创建跳转Intent
    		
    		intent.putExtra("firstNumber", firstText.getText().toString()); //存入Intent的firstNumber
    		intent.putExtra("secondNumber", secondText.getText().toString()); //存入Intent的secondNumber
    		intent.putExtra("calcType", calcType);
    		int result = 0;
    		int firstNumber = Integer.parseInt(firstText.getText().toString());
    		int secondNumber = Integer.parseInt(secondText.getText().toString());
    		switch(calcType) {
    		case 1:
    			result = firstNumber + secondNumber;
    			break;
    		case 2:
    			result = firstNumber - secondNumber;
    			break;
    		case 3:
    			result = firstNumber * secondNumber;
    			break;
    		case 4:
    			if(secondNumber != 0) {
    				result = firstNumber / secondNumber;
    			} else {
    				result = 0;
    			}  //除数为0 的简单处理
    			break;
    		default:
    			result = 1;
    		}
    		intent.putExtra("result", result);  //存入结果值
//    		MainActivity.this.finishActivity(RequestCode); // 结束以startActivityForResult(intent, RequestCode)方法启动的Activity
    		startActivity(intent);  //启动界面跳转
    		MainActivity.this.finish(); // 结束当前Activity
    	}
    }
}
