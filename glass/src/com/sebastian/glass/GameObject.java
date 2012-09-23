package com.sebastian.glass;

import com.badlogic.gdx.physics.box2d.World;

public abstract class GameObject {
	protected World world;
	private static Glass game;
	ObjectType objectType;
	
	PhysicsComponent physicsComponent;
	RenderComponent renderComponent;
	InputComponent inputComponent;
	
	boolean isHighlighted = false;
	
	public abstract void init();
	public abstract void update();
	
	public GameObject(World world) {
		this.world = world;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public static void setGame(Glass g) {
		game = g;
	}
	
	public static Glass getGame() {
		return game;
	}
	
	public Component addComponent(ComponentType componentType) {
		switch (componentType) {
		case RenderComponent:
			RenderComponent rc = new RenderComponent();
			rc.setGameObject(this);
			renderComponent = rc;
			return rc;
		case PhysicsComponent:
			PhysicsComponent pc = new PhysicsComponent(world);
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
	
	public void highlight() {
		isHighlighted = true;
	}
	
	public void unHighlight() {
		isHighlighted = false;
	}
	
}
