package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dozingcatsoftware.bouncy.elements.Box2DFactory;

public class Ball {
	
	public Body body;
	Vector2 velocity;
	Random RAND;
	
	public Ball(float x, float y){
	   RAND = new Random();
		float radius = 0.4f;
		velocity = new Vector2((-1 + RAND.nextFloat()*2), (-1 + RAND.nextFloat()*2));
		
		
		body = Box2DFactory.createCircle(Field.world, x, y, radius, false);
		body.setBullet(true);
		body.setLinearVelocity(velocity);
		
	}
	
	void run(ArrayList<Ball> balls){
		render();
	}
	
	private void render(){
		
	}

}
