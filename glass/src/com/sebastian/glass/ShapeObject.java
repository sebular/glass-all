package com.sebastian.glass;

import com.badlogic.gdx.math.Vector2;

public class ShapeObject extends GameObject {
	
	public ShapeObject(Glass game, Vector2 position, Vector2[] vertices) {
		super(game, position, vertices);
	}

	@Override
	public void init() {
		initRender();
	}

	@Override
	public void update() {
		renderComponent.render();
	}
	
	protected void initRender() {
		this.addComponent(ComponentType.RenderComponent);
		renderComponent.addMeshPlus(
			new MeshPlus(
				vertices,
				255, 255, 255, 255,
				0, 0, 0, 0
			)
		);
	}

	@Override
	public void highlightVertex(int vertex) {
		
	}

	@Override
	protected void initPhysics() {
		
	}

}
