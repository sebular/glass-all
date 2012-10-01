package com.sebastian.glass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
	protected Glass game;
	ObjectType objectType;
	
	PhysicsComponent physicsComponent;
	RenderComponent renderComponent;
	InputComponent inputComponent;
	
	boolean isHighlighted = false;
	float rotation = 0;
	float scale = 1;
	int highlightedVertex = -1;
	
	Vector2 position;
	Vector2[] unrotatedVertices;
	Vector2[] vertices;
	
	GL10 gl = Gdx.graphics.getGL10();
	ImmediateModeRenderer10 ir = new ImmediateModeRenderer10();
	
	public abstract void init();
	public abstract void update();
	protected abstract void initRender();
	protected abstract void initPhysics();
	
	
	public GameObject(Glass game, Vector2 position, Vector2[] vertices) {
		this.game = game;
		this.vertices = vertices.clone();
		this.unrotatedVertices = vertices.clone();
		this.position = position;
		init();
	}
	
	public void updateObject() {
		this.update();
		this.matchRenderToPhysics();
		this.renderShape();
		if (!this.hasComponent(ComponentType.PhysicsComponent) || game.DEBUG) {
			this.renderShapeOutline();
		}
		this.renderHighlightedVertex();
	}
	
	public Component addComponent(ComponentType componentType) {
		switch (componentType) {
		case RenderComponent:
			RenderComponent rc = new RenderComponent();
			rc.setGameObject(this);
			renderComponent = rc;
			return rc;
		case PhysicsComponent:
			PhysicsComponent pc = new PhysicsComponent(game.world);
			pc.setGameObject(this);
			physicsComponent = pc;
			return pc;
		case InputComponent:
			InputComponent ic = new InputComponent();
			ic.setGameObject(this);
			inputComponent = ic;
			return ic;
		default:
			return null;
		}
	}
	
	public boolean hasComponent(ComponentType componentType) {
		switch (componentType) {
			case RenderComponent:
				return renderComponent != null;
			case PhysicsComponent:
				return physicsComponent != null;
			case InputComponent:
				return inputComponent != null;
			default:
				return false;
		}
	}
	
	public void removeComponent(ComponentType componentType) {
		switch (componentType) {
			case RenderComponent:
				renderComponent.clearItems();
				renderComponent = null;
				break;
			case PhysicsComponent:
				physicsComponent.destroyBody();
				physicsComponent = null;
				break;
			case InputComponent:
				inputComponent = null;
				break;
			default:
				break;
		}
	}
	
	public void highlightVertex(int index) {
		highlightedVertex = index;
	}
	
	public void highlight() {
		isHighlighted = true;
	}
	
	public void unHighlight() {
		isHighlighted = false;
	}
	
	protected void renderShape() {
		if (this.hasComponent(ComponentType.RenderComponent)) {
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
	}
	
	protected void renderHighlightedVertex() {
		if (highlightedVertex >= 0) {
			gl.glPointSize(5);
			gl.glColor4f(.85f, 0, .85f, 1);
			gl.glPushMatrix();
			gl.glTranslatef(position.x, position.y, 0);
			ir.begin(GL10.GL_POINTS);
			ir.vertex(vertices[highlightedVertex].x, vertices[highlightedVertex].y, 0);
			ir.end();
			gl.glPopMatrix();
		}
	}
	
	protected void renderShapeOutline() {
		// render the position point
		gl.glPointSize(5);
		gl.glColor4f(1, 0, 0, 1);
		ir.begin(GL10.GL_POINTS);
		ir.vertex(position.x, position.y, 0);
		ir.end();
		
		// slightly different edge color if highlighted
		gl.glLineWidth(3);
		gl.glColor4f(.3f, .6f, 1, 1);
					
		// render the edges of the shape
		gl.glPushMatrix();
		gl.glTranslatef(position.x, position.y, 0);
		ir.begin(GL10.GL_LINE_STRIP);
					
		for (int i = 0; i < vertices.length; i++) {
			ir.vertex(vertices[i].x, vertices[i].y, 0);
		}
		if (vertices.length >= 3) {
			ir.vertex(vertices[0].x, vertices[0].y, 0);
		}
					
		ir.end();
		gl.glPopMatrix();
		gl.glLineWidth(1);
	}
	
	protected void matchRenderToPhysics() {
		if (this.hasComponent(ComponentType.PhysicsComponent) && this.hasComponent(ComponentType.RenderComponent)) {
			position.x = physicsComponent.body.getPosition().x;
			position.y = physicsComponent.body.getPosition().y;
			rotation = physicsComponent.body.getAngle();
			renderComponent.x = position.x;
			renderComponent.y = position.y;
			renderComponent.rotation = rotation;
			rotateVertices();
		}
	}
	
	protected void rotateVertices() {
		for (int i = 0; i < vertices.length; i++) {
			vertices[i] = unrotatedVertices[i].cpy().rotate(rotation * MathUtils.radiansToDegrees);
		}		
	}
	
	protected void updateVertices(Vector2[] newVertices) {
		if (this.hasComponent(ComponentType.PhysicsComponent)) {
			this.removeComponent(ComponentType.PhysicsComponent);
			rotation = 0;
			renderComponent.rotation = 0;
		}
		
		this.vertices = newVertices.clone();
		this.unrotatedVertices = newVertices.clone();
		if (this.vertices.length >= 3) {
			if (!this.hasComponent(ComponentType.RenderComponent)) {
				initRender();
			}
			renderComponent.setMeshPlus(
				new MeshPlus(
					this.unrotatedVertices,
					255, 255, 255, 255,
					0, 0, 0, 0
				)
			);
		}
	}
	
	protected void addVertex(Vector2 vertex) {
		Vector2[] newVertices = new Vector2[unrotatedVertices.length + 1];
		for (int i = 0; i < unrotatedVertices.length; i++) {
			newVertices[i] = unrotatedVertices[i];
		}
		newVertices[unrotatedVertices.length] = vertex;
		updateVertices(newVertices);
	}
	
	public int getCloseVertex(Vector2 mousePosition) {
		if (this.hasComponent(ComponentType.PhysicsComponent)) {
			Vector2 localMousePoint = physicsComponent.body.getLocalPoint(game.screenToWorld(mousePosition));
			
			int closestVertexIndex = -1;
			Float minDistance = Float.MAX_VALUE;
			for (int i = 0; i < unrotatedVertices.length; i++) {
				Float distanceToMouse = localMousePoint.dst(unrotatedVertices[i]);
				if (distanceToMouse < minDistance) {
					minDistance = distanceToMouse;
					closestVertexIndex = i;
				}
			}
			if (minDistance <= .25)
				return closestVertexIndex;
		}
		return -1;
	}
}
