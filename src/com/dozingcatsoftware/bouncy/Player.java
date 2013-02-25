
package com.dozingcatsoftware.bouncy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
	SpriteBatch batch;
	Vector2 spritePosition = new Vector2();
	Vector2 spritePreviousPosition = new Vector2();
	private int width;
	private int height;
	private static final int REGION_COLUMNS = 3;
	private static final int REGION_ROWS = 4;
	private Texture playerTexture;
	private float currentFrame = 0;
	private float xSpeed = 20.0f;
	private float ySpeed = 0.0f;
	private float x = 10;
	private float y = 10;
	static final float FRAME_RATE = 5;
	private float centerScreenY;
	float screenHeight;
	float screenWidth;

	private static final int[] DIRECTION_TO_ANIMATION_MAP = {3, 1, 0, 2};

	public Player () {
		// create a sprite batch with which to render sprite
		batch = new SpriteBatch();
		playerTexture = new Texture("data/player.png");
		this.width = playerTexture.getWidth() / REGION_COLUMNS;
		this.height = playerTexture.getHeight() / REGION_ROWS;
		spritePosition.x = 10;
		spritePosition.y = 10;
		spritePreviousPosition = spritePosition;
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();

	}

	public void render () {
		int srcX = (int)currentFrame * width;
		int srcY = getAnimationRow() * height;
		batch.begin();
		batch.draw(playerTexture, spritePosition.x, spritePosition.y, width, height, srcX, srcY, width, height, false, false);
		batch.end();

		// If sprite is down, set the sprite's x/y coordinates
		if (Gdx.input.isTouched()) {
			float screenY = ScreenTranslationUtils.map(Gdx.input.getY(), screenHeight, 0, 0, screenHeight);
			spritePosition.set(Gdx.input.getX(), screenY);

		}

	}

	public void update (float deltaTime) {

		xSpeed = (spritePosition.x - spritePreviousPosition.x) / deltaTime;
		ySpeed = (spritePreviousPosition.y - spritePosition.y) / deltaTime;

		spritePreviousPosition = new Vector2(spritePosition.x, spritePosition.y);

		// current frame = ++ currentFrame % 3
		currentFrame += FRAME_RATE * deltaTime;
		// use this to keep precision of where we are since deltaTime is a float
		while (currentFrame > 3.0f) {
			currentFrame -= 3.0f;
		}

	}

	private int getAnimationRow () {
		double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
		int direction = (int)Math.round(dirDouble) % REGION_ROWS;
		return DIRECTION_TO_ANIMATION_MAP[direction];
	}

	private boolean isCollision (float x2, float y2) {
		return (x2 > x && x2 < x + width && y2 > y && y2 < y + height);

	}

}
