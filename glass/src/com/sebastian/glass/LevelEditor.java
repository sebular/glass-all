package com.sebastian.glass;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

public class LevelEditor implements InputProcessor {
	
	private long start = 0;
	private boolean currentShape = false;
	private PhysicalShapeObject currentGameObject;
	private GameObject selectedGameObject = null;
	private Vector2 dragVector;
	private Glass game;
	
	public LevelEditor(Glass game) {
		this.game = game;
	}
	
	public void update() {
		if(currentGameObject != null) {
			currentGameObject.update();
		}
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// start the click timer
		start = System.nanoTime();
		selectedGameObject = null;
		for (GameObject g : game.gameObjects) {
			if (g.isHighlighted) {
				selectedGameObject = g;
				dragVector = g.physicsComponent.body.getTransform().getPosition().cpy().sub(game.screenToWorld(new Vector2(x,y)));
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		long elapsedTime = System.nanoTime() - start;
		double seconds = (double)elapsedTime / 1000000000.0;
		System.out.println(seconds);
		
		if (selectedGameObject == null) {
		
			if (seconds > 1) {
				// Start new shape
				if (!currentShape) {
					System.out.println("beginning");
					Vector2 center = game.screenToWorld(new Vector2(x,y));
					Vector2[] verts = { new Vector2(0,0) };
					currentGameObject = new PhysicalShapeObject(game.world, center, verts);
					currentShape = true;
				}
				else {
					System.out.println("ending");
					currentGameObject.initPhysics();
					game.gameObjects.add(currentGameObject);
					
					currentShape = false;
					currentGameObject = null;
				}
			}
			else {
				if (currentShape) {
					System.out.println("adding");
					currentGameObject.addVertex(game.screenToWorld(new Vector2(x,y)).sub(currentGameObject.center));
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if (selectedGameObject != null) {
			Vector2 touchPosition = game.screenToWorld(new Vector2(x,y));
			selectedGameObject.physicsComponent.body.setAwake(true);
			selectedGameObject.physicsComponent.body.setTransform(touchPosition.cpy().add(dragVector), selectedGameObject.physicsComponent.body.getTransform().getRotation());
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if (!currentShape) {
			for (GameObject g : game.gameObjects) {
				g.unHighlight();
				for (Fixture f : g.physicsComponent.body.getFixtureList()) {
					if (f.testPoint(game.screenToWorld(new Vector2(x,y)))) {
						g.highlight();
					}
				}
			}
		}
		
		if (currentShape && currentGameObject.vertices.length >= 3) {
			Vector2[] currentVertices = currentGameObject.vertices;
			currentVertices[currentVertices.length - 1] = game.screenToWorld(new Vector2(x,y)).sub(currentGameObject.center);
			currentGameObject.updateVertices(currentVertices);
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
