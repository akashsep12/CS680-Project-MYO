package com.example.myocontroller;

import java.util.ArrayList;
import java.util.Random;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GameBoard extends View{
	private Paint p;
	private int row;
	private int col;
	private int sizepoint;
	private ArrayList<Point> snake;
	private ArrayList<Point> apples;
	private Random r;
	private boolean end;
	private boolean startgame;
	public int direction;
	private int cont;
	
	public GameBoard(Context context, AttributeSet aSet){
		super(context, aSet);
		this.sizepoint = 10;
		this.p = new Paint();
		this.p.setAlpha(255);
		this.p.setStrokeWidth(this.sizepoint);
		this.direction = 1;
		this.snake = new ArrayList<Point>();
		this.apples = new ArrayList<Point>();
		this.r = new Random();
		this.end = false;
		this.startgame = true;
		Log.d("flow","In gameboard");
	}
	
	@Override
    synchronized public void onDraw(Canvas canvas){
		if(this.startgame){
			this.row = getHeight() / this.sizepoint - 1;
			this.col = getWidth() / this.sizepoint - 1;
			for(int i = 0; i < this.row - 5; i++){
				this.snake.add(new Point(10, i));
			}
			this.startgame = false;
			this.cont = 0;
		}

		this.movesnake();
		this.addapple();
		
		this.p.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight() - this.sizepoint, this.p);
		this.p.setAlpha(255);
		this.p.setColor(Color.BLACK);
		int lensnake = this.snake.size();
		for(int i = 0; i < lensnake; i++) {
			canvas.drawPoint(this.snake.get(i).x * this.sizepoint, this.snake.get(i).y * this.sizepoint, p);
			if(i % 2 != 0) this.p.setColor(Color.BLACK); else this.p.setColor(Color.GREEN);
		}
		
		this.p.setColor(Color.YELLOW);
		if(this.snake.contains(this.apples.get(0))){
			this.p.setColor(Color.WHITE);
		}
		canvas.drawCircle(this.apples.get(0).x * this.sizepoint, this.apples.get(0).y * this.sizepoint, 5, p);
	}
	
	public void addapple(){
		if(this.apples.size() < 1){
			this.apples.add(new Point(this.r.nextInt(this.col), this.r.nextInt(this.row)));
		}
	}
	
	public void restart(){
		this.direction = 1;
		this.snake = new ArrayList<Point>();
		this.apples = new ArrayList<Point>();
		this.startgame = true;
		this.end = false;
	}
	
	public void movesnake(){
		int x = this.snake.get(0).x;
		int y = this.snake.get(0).y;
		switch(this.direction){
		case 1:
			y -= 1; // UP
			if(y < 0){
				y = this.row;
			}
			break;
		case 2:
			y += 1; // DOWN
			if(y > this.row){
				y = 0;
			}
			break;
		case 4:
			x += 1; // RIGHT
			if(x > this.col){
				x = 0;
			}
			break;
		case 5:
			x -= 1; // LEFT
			if(x < 0){
				x = this.col;
			}
			break;	
		}
		Point aux = new Point(x, y);
		
		if(this.snake.contains(aux)){ 
			this.end = true;
			
		}
		this.snake.add(0, aux);
		if(this.apples.contains(aux)){ 
			this.apples.remove(aux);
			this.cont += 1;
		}else
			this.snake.remove(this.snake.size() - 1);	
	}
	
	public boolean getend(){
		return this.end;
	}
	
	public int calculatescore(){
		return this.cont * 7;
	}
	
}

