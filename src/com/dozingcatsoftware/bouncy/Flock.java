
package com.dozingcatsoftware.bouncy;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

public class Flock {

	ArrayList<Ball> balls;
	ArrayList<Body> ballBodies;
	Camera cam;
	Vector3 touchPoint;
	Vector3 worldPoint;

	public Flock (Camera cam) {
		balls = new ArrayList<Ball>();
		ballBodies = new ArrayList<Body>();
		this.cam = cam;
		touchPoint = new Vector3();
	}

	void addBall (Ball b) {
		balls.add(b);
		ballBodies.add(b.body);
	}

	void run (GLFieldRenderer renderer) {
		for (Ball b : balls) {
			// if (Gdx.input.isTouched()) {
			worldPoint = screenToViewport(Gdx.input.getX(), Gdx.input.getY());
			b.attract(Field.player.body.getPosition().x, Field.player.body.getPosition().y);
			// }
			b.run(balls, renderer);
			checkBallsOutOfBounds();
		}

	}

	void removeBalls () {
		for (int i = 0; i < balls.size(); i++) {
			Ball ball = balls.get(i);
			if (ball.destroy) {
				ballBodies.remove(ball.body);
				balls.remove(ball);
				Player.score++;
			}
		}
	}

	public Vector3 screenToViewport (float x, float y) {
		cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		return touchPoint;
	}

	public void checkBallsOutOfBounds () {
		for (Ball b : balls) {
			if (b.OutOfBounds()) {
				b.destroy = true;
			}
		}

	}
}
