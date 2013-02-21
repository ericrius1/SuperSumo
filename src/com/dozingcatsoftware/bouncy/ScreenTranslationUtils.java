package com.dozingcatsoftware.bouncy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;

public class ScreenTranslationUtils {
	
	public World world;
	
	//variables to keep track of translating between world and screen coordinates
	public float transX;
	public float transY;
	public float scaleFactor;
	public float yFlip;
	private float screenHeight;
	private float screenWidth;
	
	public ScreenTranslationUtils(float sf){
		transX = Gdx.graphics.getWidth()/2;
		transY = Gdx.graphics.getHeight()/2;
		scaleFactor = sf;
		yFlip = -1;
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
	}
	
	public void setScaleFactor(float scale){
		scaleFactor = scale;
	}
	
	//Box2D has its own pixel space and we have to move back and forth between them
	
	//Convert from box2D world to pixel space
	public Vector2 coordWorldToPixels(Vector2 world){
		return coordWorldToPixels(world.x, world.y);
		
	}
	
	public  Vector2 coordWorldToPixels(float worldX, float worldY){
		float pixelX = map(worldX, 0f, 1f, transX, transX + scaleFactor);
		float pixelY = map(worldY, 0f, 1f, transY, transY + scaleFactor);
		if(yFlip==-1) pixelY = map(pixelY, 0f, screenHeight, screenHeight, 0f);
		return new Vector2(pixelX, pixelY);
	}
	
	//convert Coordinate from pixel space to box2d world
	public Vector2 coordPixelsToWorld(Vector2 screen){
		return coordPixelsToWorld(screen.x, screen.y);
	}
	
	public Vector2 coordPixelsToWorld(float pixelX, float pixelY){
		float worldX = map(pixelX, transX, transX+scaleFactor, 0f, 1f);
		float worldY = pixelY;
		if(yFlip == -1.0f)worldY = map(pixelY, screenHeight, 0f, 0f, screenHeight);
		worldY = map(worldY, transY, transY+scaleFactor, 0f, 1f);
		return new Vector2(worldX, worldY);
	}
	
	//a common task: find the position of a body so we can draw it
	public Vector2 getBodyPixelCoord(Body b){
		Transform xf = b.getTransform();
		return coordWorldToPixels(xf.getPosition());
	}
	
	static public final float map(float value, float start1, float stop1, float start2, float stop2){
		return start2 + (stop2 - start2) * ((value - start1)/(stop1-start1));
	}

}