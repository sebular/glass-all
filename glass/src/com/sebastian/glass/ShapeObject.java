package com.sebastian.glass;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ShapeObject extends GameObject {
	float x, y;
	Vector2[] vertices;
	
	public ShapeObject(World world, float x, float y, Vector2[] vertices) {
		super(world);
		this.x = x;
		this.y = y;
		this.vertices = vertices;
		init();
	}

	@Override
	public void init() {
		initRender();
	}

	@Override
	public void update() {
		renderComponent.render();
	}
	
	private void initRender() {
		this.addComponent(ComponentType.RenderComponent);
		renderComponent.addMeshPlus(
			new MeshPlus(
				vertices,
				255, 255, 255, 255,
				0, 0, 0, 0
			)
		);
	}

}
