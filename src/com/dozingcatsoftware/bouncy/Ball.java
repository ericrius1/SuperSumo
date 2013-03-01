
package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.dozingcatsoftware.bouncy.elements.Box2DFactory;

public class Ball {
	SpriteBatch batch;
	Body body;
	public int id;
	Vector2 velocity;
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
	private float neighborDist = 2.0f;
	private float cohesionMultiplier = 1.0f;
	private float maxspeed = 10.0f;
	static final float FRAME_RATE = 5;
	float desiredSeparation = 1.0f;
	float maxforce = 1.0f;
	boolean destroy = false;
	Texture enemyTexture;
	private static final int[] DIRECTION_TO_ANIMATION_MAP = {3, 1, 0, 2};
	private float currentFrame = 0;
	float xSpeed;
	float ySpeed;
	Vector2 bodyPrevPosition;
	float scaleFactor = 30.0f;
	private int numEnemySprites = 4;
	Random RAND = new Random();
	private float boundryLaxity = 1;
	float density = 4.0f;

	private int width;
	private int height;
	private static final int REGION_COLUMNS = 3;
	private static final int REGION_ROWS = 4;
	private float attractForce = 300 / density;

	public Ball (float x, float y) {
		batch = new SpriteBatch();
		String fileName = "data/red";
		fileName = fileName.concat(String.valueOf(RAND.nextInt(numEnemySprites) + 1));
		fileName = fileName.concat(".png");
		enemyTexture = new Texture(fileName);
		this.width = enemyTexture.getWidth() / REGION_COLUMNS;
		this.height = enemyTexture.getHeight() / REGION_ROWS;
		RAND = new Random();
		body = Box2DFactory.createCircle(Field.world, x, y, radius, false, density);
		body.setBullet(true);
		acceleration = new Vector2(0, 0);
		bodyPrevPosition = new Vector2(body.getPosition().x, body.getPosition().y);
	}

	void applyForce (Vector2 force) {
		body.applyForce(force, body.getWorldCenter());
		// Use this if you want flock to be affected by gravity
		// body.applyForce(new Vector2(force.x, Field.world.getGravity().y), body.getWorldCenter());
	}

	void run (ArrayList<Ball> balls, GLFieldRenderer renderer) {
		flock(balls);
		update(Gdx.graphics.getDeltaTime());
		// borders();
		render(renderer);
	}

	public void update (float deltaTime) {

		xSpeed = (body.getPosition().x - bodyPrevPosition.x) / deltaTime;
		ySpeed = (bodyPrevPosition.y - body.getPosition().y) / deltaTime;
		System.out.println("x Pos : " + body.getPosition().x);
		System.out.println("x Previous Pos : " + bodyPrevPosition.x);
		bodyPrevPosition = new Vector2(body.getPosition().x, body.getPosition().y);
		// current frame = ++ currentFrame % 3
		currentFrame += FRAME_RATE * deltaTime;
		// use this to keep precision of where we are since deltaTime is a float
		while (currentFrame > 3.0f) {
			currentFrame -= 3.0f;
		}

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
				difference.div(d); // weight by distance
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
		worldTarget = worldTarget.mul(attractForce);
		// Now apply it to the body's center of mass
		applyForce(worldTarget);
	}

	void borders () {
		float x = body.getPosition().x;
		float y = body.getPosition().y;
		if (x < -radius) body.setTransform(new Vector2(Field.width + radius, y), body.getAngle());
		if (y > Field.height + radius) body.setTransform(new Vector2(x, 0 - radius), body.getAngle());
		if (x > Field.width + radius) body.setTransform(new Vector2(0 - radius, y), body.getAngle());
		if (y < 0 - radius) body.setTransform(new Vector2(x, Field.height + radius), body.getAngle());

	}

	private void render (GLFieldRenderer renderer) {
		int srcX = (int)currentFrame * width;
		int srcY = (getAnimationRow()) * height;
		CircleShape shape = (CircleShape)body.getFixtureList().get(0).getShape();
		renderer.fillCircle(body.getPosition().x, body.getPosition().y, shape.getRadius(), 200, 50, 200);
		batch.begin();
		batch.draw(enemyTexture, body.getPosition().x * scaleFactor - width * .5f, body.getWorldCenter().y * scaleFactor, width,
			height, srcX, srcY, width, height, false, false);
		batch.end();

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

	public boolean OutOfBounds () {
		return (body.getPosition().x < -boundryLaxity || body.getPosition().x > Field.layout.getWidth() + boundryLaxity
			|| body.getPosition().y < 0 - boundryLaxity || body.getPosition().y > Field.layout.getHeight() + boundryLaxity);
	}

	private int getAnimationRow () {
		double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
		int direction = (int)Math.round(dirDouble) % REGION_ROWS;
		return DIRECTION_TO_ANIMATION_MAP[direction];
	}
}
