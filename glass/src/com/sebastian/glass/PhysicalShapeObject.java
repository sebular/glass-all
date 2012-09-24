package com.sebastian.glass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicalShapeObject extends GameObject {
	Glass game;
	Vector2 center;
	Vector2[] vertices;
	boolean hasPhysics = false;
	boolean renderable = false;
	int highlightedVertex = -1;
	float rotation;
	
	public PhysicalShapeObject(Glass game, Vector2 center, Vector2[] vertices) {
		super(game.world);
		this.game = game;
		this.objectType = ObjectType.PhysicalShapeObject;
		this.center = center;
		this.vertices = vertices;
		this.rotation = 0;
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
			center.x = physicsComponent.body.getPosition().x;
			center.y = physicsComponent.body.getPosition().y;
			rotation = physicsComponent.body.getAngle();
			renderComponent.x = center.x;
			renderComponent.y = center.y;
			renderComponent.rotation = rotation;
		}
		
		if (renderable) {
			if (isHighlighted) {
				for (MeshPlus mp : renderComponent.items) {
					mp.r = 150;
					mp.g = 150;
					mp.buildMesh();
				}
				renderComponent.render();
			}
			else {
				for (MeshPlus mp : renderComponent.items) {
					mp.r = 255;
					mp.g = 255;
					mp.buildMesh();
				}
				renderComponent.render();
			}
		}
		GL10 gl = Gdx.graphics.getGL10();
		ImmediateModeRenderer10 ir = new ImmediateModeRenderer10();
		if (!hasPhysics) {
			
			gl.glPointSize(5);
			gl.glColor4f(1, 0, 0, 1);
			
			
			// render the center point
			ir.begin(GL10.GL_POINTS);
			ir.vertex(center.x, center.y, 0);
			ir.end();
			
			// slightly different edge color if highlighted
			gl.glLineWidth(2);
			gl.glColor4f(.3f, .6f, 1, 1);
			
			// render the edges of the shape
			ir.begin(GL10.GL_LINE_STRIP);
			for (int i = 0; i < vertices.length; i++) {
				ir.vertex(vertices[i].cpy().add(center).x, vertices[i].cpy().add(center).y, 0);
			}
			if (vertices.length >= 3)
				ir.vertex(vertices[0].cpy().add(center).x, vertices[0].cpy().add(center).y, 0);
			ir.end();
			gl.glLineWidth(1);
		}
		
		//render the selected Vertex
		if (highlightedVertex >= 0) {
			gl.glPointSize(5);
			gl.glColor4f(.85f, 0, .85f, 1);
			gl.glPushMatrix();
			gl.glTranslatef(center.x, center.y, 0);
			gl.glRotatef(rotation * MathUtils.radiansToDegrees, 0, 0, 1);
			ir.begin(GL10.GL_POINTS);
			ir.vertex(vertices[highlightedVertex].x, vertices[highlightedVertex].y, 0);
			ir.end();
			gl.glPopMatrix();
		}
		
	}
	
	public int getCloseVertex(Vector2 mousePosition) {
		Vector2 localMousePoint = physicsComponent.body.getLocalPoint(game.screenToWorld(mousePosition));
		int closestVertexIndex = -1;
		Float minDistance = Float.MAX_VALUE;
		for (int i = 0; i < vertices.length; i++) {
			Float distanceToMouse = localMousePoint.dst(vertices[i]);
			if (distanceToMouse < minDistance) {
				minDistance = distanceToMouse;
				closestVertexIndex = i;
			}
		}
		if (minDistance <= .25)
			return closestVertexIndex;
		return -1;
	}
	
	public void highlightVertex(int index) {
		highlightedVertex = index;
	}
	
	public void updateVertices(Vector2[] newVertices) {
		if (hasPhysics) {
			physicsComponent.destroyBody();
			physicsComponent = null;
			hasPhysics = false;
		}
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
