package com.sebastian.glass;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PhysicsComponent extends Component {
	public World world;
	public Body body;
	public BodyDef bodyDef;
	
	public PhysicsComponent(World world) {
		this.world = world;
	}
	
	public PhysicsComponent(World world, BodyDef bodyDef, Array<Shape> shapes) {
		this.world = world;
	}
	
	public void setBodyDef(BodyDef bodyDef) {
		this.bodyDef = bodyDef;
	}
	
	public Body getBody() {
		return body;
	}
	
	public boolean createBody() {
		if (world != null && bodyDef != null) {
			this.body = this.world.createBody(this.bodyDef);
			if (this.body != null)
				return true;
		}
		return false;
	}
	
	public void destroyBody() {
		world.destroyBody(body);
	}
	
	public void createSquareBody(String gameObjectName, float x, float y, float rotation, float hx, float hy, float density, BodyType bodyType) {
		bodyDef = new BodyDef();
		bodyDef.position.x = x;
		bodyDef.position.y = y;
		bodyDef.angle = 0;
		bodyDef.type = bodyType;
		
		body = world.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(hx, hy);
		Fixture f = body.createFixture(shape, density);
		f.setUserData(gameObjectName);
		shape.dispose();
	}
	
	public void createCircleBody(String gameObjectName, float x, float y, float radius, float density, BodyType bodyType) {
		bodyDef = new BodyDef();
		bodyDef.position.x = x;
		bodyDef.position.y = y;
		bodyDef.angle = 0;
		bodyDef.type = bodyType;
		
		body = world.createBody(bodyDef);
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		Fixture f = body.createFixture(shape, density);
		f.setUserData(gameObjectName);
		shape.dispose();
	}
	
	public void createPolygonBody(String gameObjectName, float x, float y, Vector2[] vertices, float density, BodyType bodyType) {
		bodyDef = new BodyDef();
		bodyDef.position.x = x;
		bodyDef.position.y = y;
		bodyDef.angle = 0;
		bodyDef.type = bodyType;
		
		body = world.createBody(bodyDef);
		
		if (vertices.length > 3) {
			createComplexPolygonBody(gameObjectName, x, y, vertices, density, bodyType);
		}
		else {
			
			PolygonShape shape = new PolygonShape();
			shape.set(vertices);
			Fixture f = body.createFixture(shape, density);
			f.setUserData(gameObjectName);
			shape.dispose();
		}
	}
	
	private void createComplexPolygonBody(String gameObjectName, float x, float y, Vector2[] vertices, float density, BodyType bodyType) {
		Triangulator t = new Triangulator();
		for (Vector2 v : vertices) {
			t.addPolyPoint(v.x, v.y);
		}
		if (t.triangulate()) {
			
			for (int i = 0; i < t.getTriangleCount(); i++) {
				float[] one = t.getTrianglePoint(i, 0);
				float[] two = t.getTrianglePoint(i, 1);
				float[] three = t.getTrianglePoint(i, 2);
				
				PolygonShape shape = new PolygonShape();
				
				shape.set( new Vector2[]{ new Vector2(one[0], one[1]), new Vector2(two[0], two[1]), new Vector2(three[0], three[1]) });
				System.out.println("shape added");
				Fixture f = body.createFixture(shape, density);
				f.setUserData(gameObjectName);
				shape.dispose();
			}
		}
	}
	
	public void addSquareToBody(String gameObjectName, float x, float y, float hx, float hy, float density) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(hx, hy, new Vector2(x, y), 0);
		Fixture f = body.createFixture(shape, density);
		f.setUserData(gameObjectName);
		shape.dispose();
	}
	
	public void addCircleToBody(String gameObjectName, float x, float y, float radius, float density) {
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		shape.setPosition(new Vector2(x, y));
		Fixture f = body.createFixture(shape, density);
		f.setUserData(gameObjectName);
		shape.dispose();
	}
	
	public void addPolygonToBody(String gameObjectName, Vector2[] vertices, float density, BodyType bodyType) {
		
		
		if (vertices.length > 3) {
			addComplexPolygonToBody(gameObjectName, vertices, density, bodyType);
		}
		else {
			PolygonShape shape = new PolygonShape();
			shape.set(vertices);
			Fixture f = body.createFixture(shape, density);
			f.setUserData(gameObjectName);
			shape.dispose();
		}
		
	}
	
	private void addComplexPolygonToBody(String gameObjectName, Vector2[] vertices, float density, BodyType bodyType) {
		Triangulator t = new Triangulator();
		for (Vector2 v : vertices) {
			t.addPolyPoint(v.x, v.y);
		}
		if (t.triangulate()) {
			
			for (int i = 0; i < t.getTriangleCount(); i++) {
				float[] one = t.getTrianglePoint(i, 0);
				float[] two = t.getTrianglePoint(i, 1);
				float[] three = t.getTrianglePoint(i, 2);
				
				PolygonShape shape = new PolygonShape();
				
				shape.set( new Vector2[]{ new Vector2(one[0], one[1]), new Vector2(two[0], two[1]), new Vector2(three[0], three[1]) });
				System.out.println("shape added");
				Fixture f = body.createFixture(shape, density);
				f.setUserData(gameObjectName);
				shape.dispose();
				//trianglePieces.add(tVerts);
			}
		}
	}
	
	public void onCollisionBegin(String otherType) {
		//gameObject.onCollisionBegin(otherType);
	}
		
}
