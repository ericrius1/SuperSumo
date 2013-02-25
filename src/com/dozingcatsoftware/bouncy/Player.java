
package com.dozingcatsoftware.bouncy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.dozingcatsoftware.bouncy.elements.Box2DFactory;

public class Player {
	SpriteBatch batch;
	private int width;
	private int height;
	private static final int REGION_COLUMNS = 3;
	private static final int REGION_ROWS = 4;
	private Texture playerTexture;
	private float currentFrame = 0;
	private float xSpeed = 0.0f;
	private float ySpeed = 0.0f;
	static final float FRAME_RATE = 5;
	private float centerScreenY;
	float screenHeight;
	float screenWidth;
	float touchRange;
	private Vector2 bodyPrevPosition;
	Body body;
	ScreenTranslationUtils screenUtils;
	Vector2 worldTarget;
	Vector3 touchPoint;
	Vector3 worldPoint;
	float density = 40.0f;
	float box2dW;
	static int score = 0;

	float attractStrength = density * 20;

	private static final int[] DIRECTION_TO_ANIMATION_MAP = {3, 1, 0, 2};

	public Player () {
		// create a sprite batch with which to render sprite
		batch = new SpriteBatch();
		playerTexture = new Texture("data/player.png");
		this.width = playerTexture.getWidth() / REGION_COLUMNS;
		this.height = playerTexture.getHeight() / REGION_ROWS;
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
		touchRange = width * 4;
		screenUtils = new ScreenTranslationUtils(30f);
		touchPoint = new Vector3();

		box2dW = screenUtils.scalarPixelsToWorld(width / 2);
		body = Box2DFactory.createCircle(Field.world, 10, 20, box2dW, false, density);
		body.setBullet(true);
		bodyPrevPosition = body.getPosition();
	}

	public void render () {
		int srcX = (int)currentFrame * width;
		int srcY = (getAnimationRow()) * height;
		CircleShape shape = (CircleShape)body.getFixtureList().get(0).getShape();
		Bouncy.renderer.fillCircle(body.getPosition().x, body.getPosition().y, shape.getRadius(), 50, 200, 20);
		batch.begin();
		batch.draw(playerTexture, body.getPosition().x * 20, body.getPosition().y * 30, width, height, srcX, srcY, width, height,
			false, false);
		batch.end();

		// If sprite is down, set the sprite's x/y coordinates
		if (Gdx.input.isTouched()) {
			float screenY = ScreenTranslationUtils.map(Gdx.input.getY(), screenHeight, 0, 0, screenHeight);
			worldPoint = screenToViewport(Gdx.input.getX(), Gdx.input.getY());
			attract(worldPoint.x, worldPoint.y);
		}
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

		borders();

	}

	void applyForce (Vector2 force) {
		body.applyForce(force, body.getWorldCenter());
		// Use this if you want flock to be affected by gravity
		// body.applyForce(new Vector2(force.x, Field.world.getGravity().y), body.getWorldCenter());
	}

	void attract (float x, float y) {
		// worldTarget = screenUtil.coordPixelsToWorld(x , y);
		worldTarget = new Vector2(x, y);
		// first find the vector going from this body to the specified point
		worldTarget = worldTarget.sub(body.getWorldCenter());
		// Then scale vector to specified force
		worldTarget = worldTarget.nor();
		worldTarget = worldTarget.mul(attractStrength);
		// Now apply it to the body's center of mass
		applyForce(worldTarget);
	}

	private int getAnimationRow () {
		double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
		int direction = (int)Math.round(dirDouble) % REGION_ROWS;
		return DIRECTION_TO_ANIMATION_MAP[direction];
	}

	public Vector3 screenToViewport (float x, float y) {
		Field.cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		return touchPoint;
	}

	void borders () {
		float x = body.getPosition().x;
		float y = body.getPosition().y;
		if (x < -box2dW) body.setTransform(new Vector2(Field.width + box2dW, y), body.getAngle());
		if (y > Field.height + box2dW) body.setTransform(new Vector2(x, 0 - box2dW), body.getAngle());
		if (x > Field.width + box2dW) body.setTransform(new Vector2(0 - box2dW, y), body.getAngle());
		if (y < 0 - box2dW) body.setTransform(new Vector2(x, Field.height + box2dW), body.getAngle());

	}

}
