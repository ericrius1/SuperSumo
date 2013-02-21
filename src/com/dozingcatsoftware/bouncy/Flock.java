package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.Body;

public class Flock {
	
	ArrayList<Ball> balls;
	
	public Flock(){
		balls = new ArrayList<Ball>();
	}
	
	void addBall(Ball b){
		balls.add(b);
	}
	
	void run(){
		for(Ball b : balls){
			b.run(balls);
		}
	}

}
