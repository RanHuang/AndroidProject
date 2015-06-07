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
	
	private Button selectBtn; //ѡ���������Button
	private Button calcBtn; //���м���Button
    private int calcType=-1;
    private TextView calcTypeText;
    
    private EditText firstText = null;
    private EditText secondText = null;
    
    //Ψһ��ʶ��������������û�����
    public static final int RequestCode=0x123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //��ȡ�����ϵ�Button����
        selectBtn=(Button)findViewById(R.id.selectType);
        calcBtn=(Button)findViewById(R.id.calculate);
        calcTypeText=(TextView)findViewById(R.id.calc_type);
        firstText = (EditText)findViewById(R.id.firstNumber);
        secondText = (EditText)findViewById(R.id.secondNumber);
        
        //ע��Button������¼�������
        selectBtn.setOnClickListener(new SelectCalculationTypeListener());
        calcBtn.setOnClickListener(new CalculationListener());
    }
    //��ȡ�û�ѡ��ļ�������
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	//�ж��Ƿ�Ϊ�ӵ�ǰActivity����ȥ�����󣬲��Ǵ�CalculationTypeActivity���ؽ��
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
    
    //selectBtn������
    class SelectCalculationTypeListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		//������ת��ѡ��������͵�Intent��Ϣ
    		Intent intent=new Intent(MainActivity.this, CalculationTypeActivity.class);
    		//��ѡ��������ͽ��������ɺ󣬷��ؽ����MainActivity
    		//���߼������ͽ�����Ҫ��ȡ�ڼ������ͽ������û�ѡ��ļ������͵�ֵ,RequestCode���û�Ψһ��ʶ��������������û�����
    		MainActivity.this.startActivityForResult(intent, RequestCode);
    	}
    }
    //calcBtn������
    class CalculationListener implements OnClickListener{
    	@Override
    	public void onClick(View view){
    		Intent intent = new Intent(MainActivity.this, ResultActivity.class); //������תIntent
    		
    		intent.putExtra("firstNumber", firstText.getText().toString()); //����Intent��firstNumber
    		intent.putExtra("secondNumber", secondText.getText().toString()); //����Intent��secondNumber
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
    			}  //����Ϊ0 �ļ򵥴���
    			break;
    		default:
    			result = 1;
    		}
    		intent.putExtra("result", result);  //������ֵ
//    		MainActivity.this.finishActivity(RequestCode); // ������startActivityForResult(intent, RequestCode)����������Activity
    		startActivity(intent);  //����������ת
    		MainActivity.this.finish(); // ������ǰActivity
    	}
    }
}
