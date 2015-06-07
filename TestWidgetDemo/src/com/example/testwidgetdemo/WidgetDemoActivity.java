package com.example.testwidgetdemo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class WidgetDemoActivity extends Activity {
	//界面布局组件变量声明
	private TextView textViewA = null;
	private Button btnA = null;
	private EditText editText = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //绑定界面组件
        textViewA = (TextView)findViewById(R.id.textViewA);
        btnA = (Button)this.findViewById(R.id.buttonA);
        editText = (EditText)this.findViewById(R.id.editTextA);
        
        btnA.setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "ButtonA 被用户点击了.", Toast.LENGTH_SHORT).show();
				Toast toast = Toast.makeText(getApplicationContext(), "ButtonA被用户点击了", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
					
				textViewA.setTextColor(Color.RED);
				textViewA.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
				textViewA.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
				textViewA.setText("设置TextView的字体");		
			}  	
        });
        
        editText.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String text = editText.getText().toString();

				textViewA.setTextColor(Color.BLUE);
				textViewA.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
				textViewA.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
			
				textViewA.setText(text);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
        	
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.widget_demo, menu);
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
}
