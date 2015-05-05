package com.example.myocontroller;

import com.example.myocontroller.GameBoard;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Game extends Activity {
	
	public static Game game; 
	private Handler frame = new Handler();
	private static final int delay = 200;
	private static boolean notpause = true;
	private final Context context = this;
	private int yo=4;
	public static Game getInstance(){
		   return   game;
		 }
	
	private class MyClickListener  {
		private int dir;
	
	    public MyClickListener(int dir) {
	       this.dir = dir;
	       Log.d("flow","In MyClickListener");
	       
	    }

	    
	    public void move(int i)
	    {
	    	((GameBoard)findViewById(R.id.gameboard)).direction = i;
	    	Log.d("flow","I am here");
	    }
	   
	    
	 }
	MyClickListener t; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        
      game=this;
        Log.d("flow","after button");
        
        Handler h = new Handler();
 
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
            	Log.d("flow","In run");
            	initGfx();
            }
        }, 1000);
        t=new MyClickListener(yo);
        makemove(yo);
        
       
        Log.d("flow","After test");
        
    }
    
    public void makemove(int i)
    {
    	t.move(i);
        yo=i;
    }
    
    synchronized public void initGfx() {
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, delay);
        Log.d("flow","In initGfx()");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newgame:
            	((GameBoard)findViewById(R.id.gameboard)).restart();
            	notpause = true;
            	initGfx();
                return true;
            case R.id.viewmaxscore:
            	int maxscore = this.getmaxscore();
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            	alertDialog.setTitle("Max Score");
            	alertDialog.setMessage(maxscore + " points");
            	alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            		@Override
            		public void onClick(DialogInterface dialog, int which) {
            			dialog.cancel();
            		}
            	});
            	alertDialog.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            		@Override
            		public void onClick(DialogInterface dialog, int which) {
            			savemaxscore(0);
            		}
            	});
            	alertDialog.create();
            	alertDialog.show();
            
        }
        return false;
    }

	
	
	public void showscore() {
		int score = ((GameBoard)findViewById(R.id.gameboard)).calculatescore();
		int maxscore = this.getmaxscore();
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
    	alertDialog.setTitle("Score");
    	
    	if(score > 0) {
	    	if(score >= maxscore) {
	    		alertDialog.setMessage("New Record!: " + score + " points");
	    		savemaxscore(score);
	    	}else {
	    		alertDialog.setMessage(score + " points");
	    	}
    	}else
    		alertDialog.setMessage("0 points");
    	
    	alertDialog.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
    		   ((GameBoard)findViewById(R.id.gameboard)).restart();
		       notpause = true;
		       initGfx();
    	   }
    	});
    	alertDialog.create();
    	alertDialog.show();
	}
	
	public int getmaxscore() {
		String score = "";
		String FILENAME = "maxscore";
		FileInputStream in = null;
		File file = new File(context.getFilesDir(), FILENAME);
		if(file.exists()) {
			try{
				in = new FileInputStream(file);
				char i;
	            while (in.available() > 0) {
	            	i = (char)in.read();
	            	System.out.println(i);
	                score = score + i;
	            }
			}catch(IOException ignored) {
				
			}finally {
				if(in != null)
					try{
						in.close();
					}catch(IOException ignored){}
					finally{}
			}
			return Integer.parseInt(score);
		}
		return 0;
	}
	
	public void savemaxscore(int score) {
		String sc = String.valueOf(score);
		String FILENAME = "maxscore";
		FileOutputStream out = null;
		File file = new File(context.getFilesDir(), FILENAME);
		try{
			out = new FileOutputStream(file);
			out.write(sc.getBytes());
        }catch(IOException ignored) {
        	
        }finally {
			if(out != null)
				try {
					out.close();
				}catch(IOException ignored){}
				finally{}
		}
			
	}
	
	private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
        	if(notpause){
	            frame.removeCallbacks(frameUpdate);
	            ((GameBoard)findViewById(R.id.gameboard)).invalidate();
	            if(((GameBoard)findViewById(R.id.gameboard)).getend()){
	            	notpause = false;
	            	showscore();
	            }
	            frame.postDelayed(frameUpdate, delay);
        	}
        }
   };
    
}
