package com.sebastian.glass;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Obstacle {
	Body body;
	ObstacleType obstacleType;
	Vector2 position;
	float relatedVariable;
	
	public Obstacle(ObstacleType oType) {
		//body = b;
		obstacleType = oType;
		//relatedVariable = 1; 
	}
	
	public Obstacle(Body b, ObstacleType oType) {
		body = b;
		obstacleType = oType;
		//relatedVariable = 1;
	}
	
	public Obstacle(Body b, ObstacleType oType, float relatedVariable) {
		body = b; 
		obstacleType = oType;
		this.relatedVariable = relatedVariable;
	}
	
	public void setBody(Body b) {
		this.body = b;
	}
}
