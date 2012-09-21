package com.sebastian.glass;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.EdgeShape;

public class Edge extends Obstacle {

	public Edge(Body b, ObstacleType oType) {
		super(b, oType);
	}
	
	public Edge(World w, Vector2 one, Vector2 two, ObstacleType oType) {
		super(oType);
		
		EdgeShape tempShape = new EdgeShape();
		//tempShape.setAsEdge(one, two);
		tempShape.set(one, two);
		FixtureDef tempFixtureDef = new FixtureDef();
		tempFixtureDef.shape = tempShape;
		tempFixtureDef.friction = 0.3f;
		
		BodyDef tempBodyDef = new BodyDef();
		Body b = w.createBody(tempBodyDef);
		b.createFixture(tempFixtureDef);
		tempShape.dispose();
		setBody(b);
	}
}
