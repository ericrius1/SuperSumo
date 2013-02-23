package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
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
	private float maxspeed;
	private float maxforce;
	private Vector2 acceleration;
	private Vector2 sum;
	private Vector2 desired;
	private Vector2 worldTarget;
	private ScreenTranslationUtils screenUtil;;
	private Vector2 bodyVector;
	Vector2 screenPosition;
	
	
	public Ball(float x, float y){
	   RAND = new Random();
		radius = 0.4f;
		id = count;

		body = Box2DFactory.createCircle(Field.world, x, y, radius, false);
		body.setBullet(true);
		acceleration = new Vector2(0, 0);
		
		screenUtil = new ScreenTranslationUtils(10);
		count++;
	}
	
	void applyForce(Vector2 force){
		body.applyForce(force, body.getWorldCenter());
	}

	  void attract(float x, float y){
		  //worldTarget = screenUtil.coordPixelsToWorld(x , y);
		  worldTarget = new Vector2(x, y);
		  //first find the vector going from this body to the specified point
		  bodyVector = body.getWorldCenter();
		  worldTarget = worldTarget.sub(bodyVector);
		  //Then scale vector to specified force
		  worldTarget = worldTarget.nor();
		  worldTarget = worldTarget.mul(10.0f);
		  //Now apply it to the body's center of mass
		  applyForce(worldTarget);
	  }
		
		Vector2 seek(Vector2 target){
			desired = target.sub(body.getWorldCenter());
			desired = desired.nor();
			desired = desired.mul(maxspeed);
			//System.out.println(desired);
			//desired = body.getLinearVelocity().sub(desired);
			desired = desired.sub(body.getLinearVelocity());
			System.out.println(desired);
			// = limit(desired, maxforce);
			return desired;
		}
		
	  
	  void flock(ArrayList<Ball> balls){
		Vector2 cohesion = cohesion(balls);
		cohesion = cohesion.mul(2.0f);
		
	   //Add the force vectors to acceleration
		applyForce(cohesion);
	}
	
	Vector2 cohesion(ArrayList<Ball> balls){
		float neighbordist = 200;
		sum = new Vector2(0, 0);
		int count = 0;
		for(Ball other: balls){
			float d = sum.dst(other.body.getWorldCenter());
			if((d> 0) && (d < neighbordist)) {
				sum.add(other.body.getWorldCenter());
				count++;
				
			}
		}
		if(count > 0){
			return seek(sum.div(count));
		}
		else {
			return new Vector2(0, 0);
		}
	}

	void run(ArrayList<Ball> balls, GLFieldRenderer renderer){
		flock(balls);
		update();
		borders();
		render(renderer);
	}
	
	void update(){

		
	}
	
	void borders(){
		float x = body.getPosition().x;
		float y = body.getPosition().y;
		if(x < -radius) body.setTransform(new Vector2(Field.height+radius,y), body.getAngle());
		if (y > Field.height + radius) body.setTransform(new Vector2(x, 0 - radius), body.getAngle());
		if(x >Field.width + radius) body.setTransform(new Vector2(0-radius, y), body.getAngle());
		if (y<0 -radius ) body.setTransform(new Vector2(x, Field.height+radius), body.getAngle());
		
	}
	
	private void render(GLFieldRenderer renderer){
		screenPosition = screenUtil.getBodyPixelCoord(body);
		CircleShape shape = (CircleShape)body.getFixtureList().get(0).getShape();
		renderer.fillCircle(body.getPosition().x, body.getPosition().y, shape.getRadius(), 200, 50, 200);
	}
	
	Vector2 limit(Vector2 vec, float max){
		Vector2 tempVec = vec;
		if(mag(tempVec) > max){
			tempVec = tempVec.nor();
			tempVec = tempVec.mul(max);
		}
		return tempVec;
	}
	
	float mag(Vector2 vec){
		return (float)Math.sqrt((vec.x*vec.x + vec.y+vec.y));
	}

}
