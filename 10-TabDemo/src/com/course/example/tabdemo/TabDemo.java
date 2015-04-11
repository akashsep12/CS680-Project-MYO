
package com.course.example.tabdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebView;

public class TabDemo extends Activity {
	
	private EditText text;
	private WebView webView;
	private CheckBox check1, check2;
	private RadioGroup group;
	private TextView tview;
	private Button button, button2;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		tabs.setup();
				
		TabHost.TabSpec spec;
		
		// Initialize a TabSpec for tab1 and add it to the TabHost
		spec=tabs.newTabSpec("tag1");	//create new tab specification
		spec.setContent(R.id.tab1);    //add tab view content
		spec.setIndicator("Check");    //put text on tab
		tabs.addTab(spec);             //put tab in TabHost container
		
		check1 = (CheckBox)findViewById(R.id.check01);
		check2 = (CheckBox)findViewById(R.id.check02);
		
		//set listeners for tab1
		check1.setOnClickListener( new OnClickListener() {
				public void onClick(View v){
					TextView tv = (TextView)findViewById(R.id.check01);
					if (check1.isChecked()) tv.setText("I had lunch !");											  				
					 	else tv.setText("No time so far");					 						 	
				}			
		});
		
		check2.setOnClickListener( new OnClickListener() {
			public void onClick(View v){
				TextView tv = (TextView)findViewById(R.id.check02);
				if (check2.isChecked()) tv.setText("Looks promising");											  				
				 	else tv.setText("NO !!!");					 						 	
			}			
	});
		
		//-------------------------------------------------------------------------------------
		
		// Initialize a TabSpec for tab2 and add it to the TabHost
		spec=tabs.newTabSpec("tag2");		//create new tab specification
		spec.setContent(R.id.tab2);			//add view tab content
		spec.setIndicator("Web");
		tabs.addTab(spec);					//put tab in TabHost container
		
		button = (Button)findViewById(R.id.Button01);
		text = (EditText)findViewById(R.id.EditText01);
		webView = (WebView)findViewById(R.id.web);
		
		//set listeners for web tab
		button.setOnClickListener( new OnClickListener() {
			public void onClick(View v){
				webView.getSettings().setJavaScriptEnabled(true);
			    webView.loadUrl(text.getText().toString());

			}
		});
		
		text.setOnKeyListener(new OnKeyListener() { 
	         public boolean onKey(View view, int keyCode, KeyEvent event) {
	            if (keyCode == KeyEvent.KEYCODE_ENTER) {
	            	webView.getSettings().setJavaScriptEnabled(true);
				    webView.loadUrl(text.getText().toString());
	               return true;
	            }
	            return false;
	         }
	      });

	//-------------------------------------------------------------------------------------
	
	// Initialize a TabSpec for tab3 and add it to the TabHost
	spec=tabs.newTabSpec("tag3");		//create new tab specification
	spec.setContent(R.id.tab3);			//add tab view content
	spec.setIndicator("Radio");			//put text on tab
	tabs.addTab(spec); 					//put tab in TabHost container
	
	group = (RadioGroup)findViewById(R.id.radio);
	tview = (TextView)findViewById(R.id.TextView01);
	button2 = (Button)findViewById(R.id.Button02);
	
	//set listener for radio buttons
	group.setOnCheckedChangeListener( new 
			OnCheckedChangeListener(){
				public void onCheckedChanged(RadioGroup gp, int checkedId){
					if (checkedId != -1) {
						RadioButton rb = (RadioButton)findViewById(checkedId);
						if (rb != null) 
				            tview.setText("You chose " + rb.getText());
						else tview.setText("Choose Now !");
					}
				}
	});	
	
	//set listener for clear button
	button2.setOnClickListener( new OnClickListener() {
		public void onClick(View v){
			if (group != null) {
				group.clearCheck();
				tview.setText("Choose Now !");
				}

		}
	});
	
	}	
	
}