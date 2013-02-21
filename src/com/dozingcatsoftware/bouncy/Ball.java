package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.dozingcatsoftware.bouncy.elements.Box2DFactory;

public class Ball {
	
	Body body;
	static int count = 0;
	public int id;
	Vector2 velocity;
	Random RAND;
	private float radius;
	
	public Ball(float x, float y){
	   RAND = new Random();
		radius = 0.4f;
		velocity = new Vector2((-3 + RAND.nextFloat()*3), (-3 + RAND.nextFloat()*3));
		id = count;
		
		body = Box2DFactory.createCircle(Field.world, x, y, radius, false);
		body.setBullet(true);
		body.setLinearVelocity(velocity);
		count++;
		
	}
	
	void run(ArrayList<Ball> balls, GLFieldRenderer renderer){
		render(renderer);
	}
	
	private void render(GLFieldRenderer renderer){
		CircleShape shape = (CircleShape)body.getFixtureList().get(0).getShape();
		renderer.fillCircle(body.getPosition().x, body.getPosition().y, shape.getRadius(), 200, 50, 200);
	}

}
