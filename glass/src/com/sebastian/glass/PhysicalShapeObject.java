package com.sebastian.glass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicalShapeObject extends GameObject {
	Vector2 center;
	Vector2[] vertices;
	boolean hasPhysics = false;
	boolean renderable = false;
	
	
	public PhysicalShapeObject(World world, Vector2 center, Vector2[] vertices) {
		super(world);
		this.center = center;
		this.vertices = vertices;
		init();
	}

	@Override
	public void init() {
		if (vertices.length >= 3) {
			initRender();
			renderable = true;
		}
		//initPhysics();
	}

	@Override
	public void update() {
		// Update mesh to match physics.
		// Should only be doing this once it has a complete physics object.
		
		if (hasPhysics) { 
			renderComponent.x = physicsComponent.body.getPosition().x;
			renderComponent.y = physicsComponent.body.getPosition().y;
			renderComponent.rotation = physicsComponent.body.getAngle();
		}
		
		if (renderable) {
			renderComponent.render();
		}
		
		// render the center point
		GL10 gl = Gdx.graphics.getGL10();
		gl.glPointSize(5);
		gl.glColor4f(1, 0, 0, 1);
		ImmediateModeRenderer10 ir = new ImmediateModeRenderer10();
		ir.begin(GL10.GL_POINTS);
		ir.vertex(center.x, center.y, 0);
		ir.end();
		
		gl.glLineWidth(3);
		gl.glColor4f(.3f, .6f, 1, 1);
		if (isHighlighted) {
			gl.glColor4f(.3f, 1, .6f, 1);
		}
		ir.begin(GL10.GL_LINE_STRIP);
		for (int i = 0; i < vertices.length; i++) {
			ir.vertex(vertices[i].cpy().add(center).x, vertices[i].cpy().add(center).y, 0);
		}
		if (vertices.length >= 3)
			ir.vertex(vertices[0].cpy().add(center).x, vertices[0].cpy().add(center).y, 0);
		ir.end();
		
		gl.glLineWidth(1);
	}
	
	public void updateVertices(Vector2[] newVertices) {
		this.vertices = newVertices;
		if (this.vertices.length >= 3) {
			if (!renderable) {
				initRender();
				renderable = true;
			}
		
			renderComponent.setMeshPlus(
				new MeshPlus(
					this.vertices,
					255, 255, 255, 255,
					0, 0, 0, 0
				)
			);
		}
		System.out.println("set mesh plus");
		
	}
	
	public void addVertex(Vector2 vertex) {
		Vector2[] newVertices = new Vector2[vertices.length + 1];
		for (int i = 0; i < vertices.length; i++) {
			newVertices[i] = vertices[i];
		}
		newVertices[vertices.length] = vertex;
		updateVertices(newVertices);
	}
	
	
	
	private void initRender() {
		this.addComponent(ComponentType.RenderComponent);
		renderComponent.x = center.x;
		renderComponent.y = center.y;
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
		physicsComponent.createPolygonBody("shape", center.x, center.y, vertices, 1, BodyType.DynamicBody);
		hasPhysics = true;
	}
	
}
