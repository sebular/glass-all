package com.sebastian.glass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends GameObject {
	boolean hasPhysics = false;
	float playerScale = .5f;
	float rotation = 0;
	boolean boosting = false;
	boolean turningLeft = false;
	boolean turningRight = false;
	Vector2 position;
	Vector2[] vertices;
	
	public Player(World world, Vector2 position) {
		super(world);
		this.position = position;
		
		init();
	}

	@Override
	public void init() {
		this.vertices = new Vector2[3];
		vertices[0] = new Vector2(-1 * playerScale, -1 * playerScale);
		vertices[1] = new Vector2(1 * playerScale, -1 * playerScale);
		vertices[2] = new Vector2(0 * playerScale, 2 * playerScale); 
		
		initRender();
		initPhysics();
	}

	@Override
	public void update() {
		float boostForce = 4;
		float rotationSpeed = 5 * MathUtils.degreesToRadians;
		
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
		if (hasPhysics) {
			matchRenderToPhysics();
		}
		renderComponent.render();
	}
	
	private void matchRenderToPhysics() {
			position.x = physicsComponent.body.getPosition().x;
			position.y = physicsComponent.body.getPosition().y;
			rotation = physicsComponent.body.getAngle();
			renderComponent.x = position.x;
			renderComponent.y = position.y;
			renderComponent.rotation = rotation;
	}
	
	private void initRender() {
		this.addComponent(ComponentType.RenderComponent);
		renderComponent.x = position.x;
		renderComponent.y = position.y;
		renderComponent.addMeshPlus(
			new MeshPlus(
				vertices,
				255, 255, 255, 255,
				0, 0, 0, 0
			)
		);
	}
	
	public void initPhysics() {
		this.addComponent(ComponentType.PhysicsComponent);
		physicsComponent.createPolygonBody("shape", position.x, position.y, vertices, 1, BodyType.DynamicBody);
		physicsComponent.body.setFixedRotation(true);
		hasPhysics = true;
	}
}
