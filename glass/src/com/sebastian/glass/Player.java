package com.sebastian.glass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player extends GameObject {
	boolean boosting = false;
	boolean turningLeft = false;
	boolean turningRight = false;
	
	public Player(Glass game, Vector2 position) {
		super(game, position, new Vector2[3]);
	}

	@Override
	public void init() {
		this.scale = .5f;
		this.vertices[0] = new Vector2(-1 * scale, -1 * scale);
		this.vertices[1] = new Vector2(1 * scale, -1 * scale);
		this.vertices[2] = new Vector2(0 * scale, 2 * scale); 
		this.unrotatedVertices = this.vertices.clone();
		initRender();
		initPhysics();
	}

	@Override
	public void update() {
		float boostForce = 4;
		float rotationSpeed = 5 * MathUtils.degreesToRadians;
		
		if (this.hasComponent(ComponentType.PhysicsComponent)) {
			Body body = this.physicsComponent.body;
			
			boosting = Gdx.input.isKeyPressed(Input.Keys.UP);
			turningLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
			turningRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
			
			
			if (boosting) {
				body.applyForceToCenter(body.getWorldVector(new Vector2(0, boostForce)));
			}
			if (turningLeft) {
				body.setTransform(body.getPosition(), body.getAngle() + rotationSpeed);
			}
			if (turningRight) {
				body.setTransform(body.getPosition(), body.getAngle() - rotationSpeed);
			}
		}
	}
	
	
	
	protected void initRender() {
		this.addComponent(ComponentType.RenderComponent);
		renderComponent.x = position.x;
		renderComponent.y = position.y;
		renderComponent.addMeshPlus(
			new MeshPlus(
				unrotatedVertices,
				255, 255, 255, 255,
				0, 0, 0, 0
			)
		);
	}
	
	public void initPhysics() {
		this.addComponent(ComponentType.PhysicsComponent);
		physicsComponent.createPolygonBody("shape", position.x, position.y, vertices, 1, BodyType.DynamicBody);
		physicsComponent.body.setFixedRotation(true);
	}
}
