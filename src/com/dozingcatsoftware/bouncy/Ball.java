
package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.dozingcatsoftware.bouncy.elements.Box2DFactory;

public class Ball {

	Body body;
	public int id;
	Vector2 velocity;
	Random RAND;
	private float radius = 0.4f;
	private Vector2 acceleration;
	private Vector2 sum;
	private Vector2 desired;
	private Vector2 steer;
	private Vector2 difference;
	Vector2 worldTarget;
	private ScreenTranslationUtils screenUtil;;
	private Vector2 bodyVector;
	Vector2 screenPosition;
	private Vector2 tempPosVector;
	private Vector2 tempOtherPosVector;
	private float mouseForceMultiplier = 10.0f;
	private float neighborDist = 2.0f;
	private float cohesionMultiplier = 1.0f;
	private float maxspeed = 10.0f;
	float desiredSeparation = 1.0f;
	float maxforce = 1.0f;

	public Ball (float x, float y) {
		RAND = new Random();
		body = Box2DFactory.createCircle(Field.world, x, y, radius, false);
		body.setBullet(true);
		acceleration = new Vector2(0, 0);
		screenUtil = new ScreenTranslationUtils(10);
	}

	void applyForce (Vector2 force) {
		body.applyForce(force, body.getWorldCenter());
		// Use this if you want flock to be affected by gravity
		// body.applyForce(new Vector2(force.x, Field.world.getGravity().y), body.getWorldCenter());
	}

	void run (ArrayList<Ball> balls, GLFieldRenderer renderer) {
		flock(balls);
		borders();
		render(renderer);
	}

	void flock (ArrayList<Ball> balls) {
		// Vector2 cohesion = cohesion(balls);
		Vector2 separation = separation(balls);
		// cohesion = cohesion.mul(cohesionMultiplier);
		separation = separation.mul(cohesionMultiplier);
		// applyForce(cohesion);
		applyForce(separation);

	}

	Vector2 seek (Vector2 target) {
		desired = target.sub(body.getWorldCenter());
		desired.nor();
		desired.mul(maxspeed);
		desired.sub(body.getLinearVelocity());
		// body.getLinearVelocity().sub(body.getWorldCenter());
		System.out.println(desired);
		desired = limit(desired, maxforce);
		return desired;
	}

	Vector2 separation (ArrayList<Ball> balls) {
		steer = new Vector2(0, 0);
		int count = 0;
		tempPosVector = new Vector2(body.getWorldCenter().x, body.getWorldCenter().y);
		// For every ball in system, check if it's too close
		for (Ball other : balls) {
			float d = body.getWorldCenter().dst(other.body.getWorldCenter());
			if ((d > 0) && (d < desiredSeparation)) {
				tempOtherPosVector = new Vector2(other.body.getWorldCenter().x, other.body.getWorldCenter().y);
				difference = tempPosVector.sub(tempOtherPosVector);
				difference.nor();
				// difference.div(d); // weight by distance
				steer.add(difference);
				count++;
			}
		}
		// Average - divide by how many
		if (count > 0) {
			return seek((steer.div((float)count)));
		}
		return steer;

	}

	Vector2 cohesion (ArrayList<Ball> balls) {
		sum = new Vector2(0, 0);
		int count = 0;
		for (Ball other : balls) {
			float d = body.getWorldCenter().dst(other.body.getWorldCenter());
			if ((d > 0) && (d < neighborDist)) {
				sum.add(other.body.getWorldCenter());
				count++;

			}
		}
		if (count > 0) {
			return seek(sum.div((float)count));
		}
		return sum;
	}

	void attract (float x, float y) {
		// worldTarget = screenUtil.coordPixelsToWorld(x , y);
		worldTarget = new Vector2(x, y);
		// first find the vector going from this body to the specified point
		worldTarget = worldTarget.sub(body.getWorldCenter());
		// Then scale vector to specified force
		worldTarget = worldTarget.nor();
		worldTarget = worldTarget.mul(mouseForceMultiplier);
		// Now apply it to the body's center of mass
		applyForce(worldTarget);
	}

	void borders () {
		float x = body.getPosition().x;
		float y = body.getPosition().y;
		if (x < -radius) body.setTransform(new Vector2(Field.height + radius, y), body.getAngle());
		if (y > Field.height + radius) body.setTransform(new Vector2(x, 0 - radius), body.getAngle());
		if (x > Field.width + radius) body.setTransform(new Vector2(0 - radius, y), body.getAngle());
		if (y < 0 - radius) body.setTransform(new Vector2(x, Field.height + radius), body.getAngle());

	}

	private void render (GLFieldRenderer renderer) {
		screenPosition = screenUtil.getBodyPixelCoord(body);
		CircleShape shape = (CircleShape)body.getFixtureList().get(0).getShape();
		renderer.fillCircle(body.getPosition().x, body.getPosition().y, shape.getRadius(), 200, 50, 200);
	}

	Vector2 limit (Vector2 vec, float max) {
		Vector2 tempVec = vec;
		if (mag(tempVec) > max) {
			tempVec = tempVec.nor();
			tempVec = tempVec.mul(max);
		}
		return tempVec;
	}

	float mag (Vector2 vec) {
		return (float)Math.sqrt((vec.x * vec.x + vec.y + vec.y));
	}

}
