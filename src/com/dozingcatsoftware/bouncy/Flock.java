package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

public class Flock {
	
	ArrayList<Ball> balls;
	ArrayList<Body> ballBodies;
	Camera cam;
	Vector3 touchPoint;
	Vector3 worldPoint;
	
	public Flock(Camera cam){
		balls = new ArrayList<Ball>();
		ballBodies = new ArrayList<Body>();
		this.cam = cam;
		touchPoint = new Vector3();
	}
	
	void addBall(Ball b){
		balls.add(b);
		ballBodies.add(b.body);
	}
	
	void run(GLFieldRenderer renderer){
		for(Ball b : balls){
			b.run(balls, renderer);
		}
		
		if(Gdx.input.isTouched()) {
			worldPoint = screenToViewport(Gdx.input.getX(), Gdx.input.getY());
			for(Ball b : balls){
				b.attract(worldPoint.x, worldPoint.y);
			}
		}
	}
	
	void removeBall(Body ball){
		int index = ballBodies.indexOf(ball);
		ballBodies.remove(ball);
		balls.remove(index);
		
		
	}
	
	public Vector3 screenToViewport (float x, float y) {
		cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		return touchPoint;
	}

}
