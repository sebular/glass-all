package com.sebastian.glass;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PhysicalShapeObject extends GameObject {
	
	public PhysicalShapeObject(Glass game, Vector2 position, Vector2[] vertices) {
		super(game, position, vertices);
	}

	@Override
	public void init() {
		this.objectType = ObjectType.PhysicalShapeObject;
		if (vertices.length >= 3) {
			initRender();
		}
	}

	@Override
	public void update() {
		
	}

	@Override
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
	
	@Override
	public void initPhysics() {
		this.addComponent(ComponentType.PhysicsComponent);
		physicsComponent.createPolygonBody("shape", position.x, position.y, vertices, 1, BodyType.DynamicBody);
	}
	
}
