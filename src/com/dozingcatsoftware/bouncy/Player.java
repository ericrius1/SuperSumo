
package com.dozingcatsoftware.bouncy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Player {
	SpriteBatch batch;
	Vector3 spritePosition = new Vector3();
	private int width;
	private int height;
	private static final int REGION_COLUMNS = 3;
	private static final int REGION_ROWS = 4;
	private Texture playerTexture;
	private float currentFrame = 0;
	private float xSpeed = 0.0f;
	private float ySpeed = 0.0f;
	private float x = 10;
	private float y = 10;

	private static final int[] DIRECTION_TO_ANIMATION_MAP = {3, 1, 0, 2};

	public Player () {
		// create a sprite batch with which to render sprite
		batch = new SpriteBatch();
		playerTexture = new Texture("data/player.png");
		this.width = playerTexture.getWidth() / REGION_COLUMNS;
		this.height = playerTexture.getHeight() / REGION_ROWS;

	}

	public void render () {
		int srcX = (int)currentFrame * width;
		int srcY = getAnimationRow() * height;
		batch.begin();
		batch.draw(playerTexture, x, y, width, height, srcX, srcY, width, height, false, false);
		batch.end();

		// If sprite is down, set the sprite's x/y coordinates
		if (Gdx.input.isTouched()) {
			Field.cam.unproject(spritePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		}

	}

	private int getAnimationRow () {
		double dirDouble = (Math.atan2(xSpeed, ySpeed) / Math.PI / 2) + 2;
		int direction = (int)Math.round(dirDouble) % REGION_ROWS;
		return DIRECTION_TO_ANIMATION_MAP[direction];
	}

}
