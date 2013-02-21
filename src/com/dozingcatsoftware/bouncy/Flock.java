package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.Body;

public class Flock {
	
	ArrayList<Ball> balls;
	ArrayList<Body> ballBodies;
	
	public Flock(){
		balls = new ArrayList<Ball>();
		ballBodies = new ArrayList<Body>();
	}
	
	void addBall(Ball b){
		balls.add(b);
		ballBodies.add(b.body);
	}
	
	void run(GLFieldRenderer renderer){
		for(Ball b : balls){
			b.run(balls, renderer);
		}
	}
	
	void removeBall(Body ball){
		int index = ballBodies.indexOf(ball);
		ballBodies.remove(ball);
		balls.remove(index);
		
		
	}

}
