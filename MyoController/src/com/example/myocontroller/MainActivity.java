package com.example.myocontroller;

import java.util.ArrayList;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.util.Locale;

import java.io.*;

public class MainActivity extends ListActivity implements  OnInitListener{
   EditText selection;
   int location;
   private final String file = "list.txt";
   private TextToSpeech speaker;
   private OutputStreamWriter out;
   ArrayList<String> items = new ArrayList<String>( );
   ArrayAdapter<String> aa;
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
      //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);
       	setContentView(R.layout.activity_main);
	   selection=(EditText)findViewById(R.id.txtMsg);
	 
	  		try {
			String[] files = fileList();
			
				
				if(new File(getFilesDir().toString()+"/"+"list.txt").exists())
				{
					InputStream in = openFileInput(file);
					InputStreamReader isr = new InputStreamReader(in);
					BufferedReader reader = new BufferedReader(isr);
					String str = null;
					
					while ((str = reader.readLine()) != null) {
					      items.add(str);
					}
					 aa = new ArrayAdapter<String>(
						      this,
						      android.R.layout.simple_list_item_1,     //Android supplied List item format
						      items);
				    setListAdapter(aa);
					reader.close();
				}
			}catch (IOException e) {}
	  		
	  		 aa = new ArrayAdapter<String>(
				      this,
				      android.R.layout.simple_list_item_1,     //Android supplied List item format
				      items);
		    setListAdapter(aa); //connect ArrayAdapter to <ListView>
   }
 //speaks the contents of output
   public void speak(String output){
   	speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);
   }
   
   public void onInit(int status) {
       // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
       if (status == TextToSpeech.SUCCESS) {
           // Set preferred language to US english.
           // If a language is not be available, the result will indicate it.
           int result = speaker.setLanguage(Locale.US);
          
          //  int result = speaker.setLanguage(Locale.FRANCE);
           if (result == TextToSpeech.LANG_MISSING_DATA ||
               result == TextToSpeech.LANG_NOT_SUPPORTED) {
              // Language data is missing or the language is not supported.
               Log.e("Assignment4", "Language is not available.");
           } 
       } else {
           // Initialization failed.
           Log.e("Assignment4", "Could not initialize TextToSpeech.");
       }
   }
   
// on destroy
   public void onDestroy(){
   	
   	// shut down TTS engine
   	if(speaker != null){
   		speaker.stop();
   		speaker.shutdown();
   	}
   	super.onDestroy();
   }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String text = items.get(position);
		location = position;
		selection.setText(text);
		
	}
	
	
	final int PICK1 = Menu.FIRST + 1;
	final int PICK2 = Menu.FIRST + 2;
	final int PICK3 = Menu.FIRST + 3;
	final int PICK4 = Menu.FIRST + 4;
	final int PICK5 = Menu.FIRST + 5;
	
	  @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			
			super.onCreateOptionsMenu(menu);
			MenuItem item1 = menu.add(0, PICK1, Menu.NONE, "Save Number");
			MenuItem item2 = menu.add(0, PICK2, Menu.NONE, "Add Number");
			MenuItem item3 = menu.add(0, PICK3, Menu.NONE, "Update Number");
			MenuItem item4 = menu.add(0, PICK4, Menu.NONE, "Delete Number");
			MenuItem item5 = menu.add(0, PICK5, Menu.NONE, "Close App");
			item1.setShortcut('1', 's');
			item2.setShortcut('2', 'a');
			item3.setShortcut('3', 'u');
			item4.setShortcut('4', 'd');
			item5.setShortcut('5', 'e');
			
			return true;
		}
	    
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {		
			    
			    int itemID = item.getItemId();  //get id of menu item picked
			    
			    switch (itemID) {
			    case PICK1:
			    	try{
			    	out = new OutputStreamWriter(openFileOutput(file, MODE_PRIVATE)); 
			    	}
			    	catch (IOException e) {}
		    			
			    	for (String list:items){
			    		try {
			    			
			    			String line = list.toString().trim();
			    				out.write(line + " \n");
			    			}
			    		
			    		
			    		catch (IOException b) {
			    			Log.e("Assignment4", b.getMessage());
			    			
			    		}
			    	}
			    	
			    	try{
			    		out.close();
			    	}		    	
			    	catch (IOException a) {
		    			Log.e("Assignment4", a.getMessage());
		    			
		    		
			    	}
			    	return true;
		    	
			    case PICK2 :
			    	int index = items.size() + 1;
			    	//String textToSpeech = selection.getText().toString()+"is added";
			    	String textToSpeech = "Number is added";
			    	String text = index +". " + selection.getText().toString();
			        items.add(text);
			        selection.setText("");
			        aa.notifyDataSetChanged();
			     // if speaker is talking, stop it
					if(speaker.isSpeaking()){
						Log.i("Assignment4", "Speaker Speaking");
						speaker.stop();
					// else start speech
					} else {
						Log.i("Assignment4", "Speaker Not Already Speaking");
						speak(textToSpeech);
					} 
			    	return true; 
			    	
			    case PICK3 : 
			    	int position = location +1;
			    	String update = selection.getText().toString();
			    	int posPeriod =update.indexOf(".");
			    	if(posPeriod!=-1){
			    	update = position + ". " + update.substring(posPeriod+2);
			    	}
			    	else
			    		update = position + ". " + update.trim();
			    	items.remove(location);
			    	items.add(location, update);
			    	selection.setText("");
			    	aa.notifyDataSetChanged();
			    	return true;
			    	
			    case PICK4 : 
			    	String delete = selection.getText().toString();
			    	int post =delete.indexOf(".");
			    	//String value = delete.substring(post+1)+ "is removed";
			    	String value = "Number is deleted";
			        if(speaker.isSpeaking()){
						Log.i("Assignment4", "Speaker Speaking");
						speaker.stop();
					// else start speech
					} else {
						Log.i("Assignment4", "Speaker Not Already Speaking");
						speak(value);
					}
			        
			    	if(location == items.size()-1){
			         
			    	items.remove(location);
			    	}
			    	else
			    	{   
			    		 
			    		items.remove(location);
			    		//aa.notifyDataSetChanged();
			    		for(int i =0; i<items.size();i++){
			    			int pos = i+1;
			    			String val = items.get(i);
			    			int spc =val.indexOf(" ");
			    			val = pos+". "+val.substring(spc+1);
			    			items.set(i,val);

			    		}
				    	
			    	}
			    	aa.notifyDataSetChanged();
		    		selection.setText("");
			    	return true;
			    
			    case PICK5 : 
			    	try{
				    	out = new OutputStreamWriter(openFileOutput(file, MODE_PRIVATE)); 
				    	}
				    	catch (IOException e) {}
			    			
				    	for (String list:items){
				    		try {
				    			
				    			String line = list.toString().trim();
				    				out.write(line + " \n");
				    			}
				    		
				    		
				    		catch (IOException b) {
				    			Log.e("Assignment4", b.getMessage());
				    			
				    		}
				    	}
				    	
				    	try{
				    		out.close();
				    	}		    	
				    	catch (IOException a) {
			    			Log.e("Assignment4", a.getMessage());
			    			
			    		
				    	}
			    	finish();
			    
			    default: super.onOptionsItemSelected(item);
			    }
			   		   
		    return false;
		}
      
}

