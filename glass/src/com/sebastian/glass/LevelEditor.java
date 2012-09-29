package com.sebastian.glass;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;

public class LevelEditor implements InputProcessor {
	
	private long start = 0;
	private boolean currentShape = false;
	private PhysicalShapeObject currentGameObject;
	private PhysicalShapeObject modifiedGameObject;
	private GameObject selectedGameObject = null;
	private Vector2 dragVector;
	private Glass game;
	float camX = 0;
	float camY = 0;
	int selectedVertex = -1;
	int mouseButton = -1;
	
	boolean camDown = false;
	boolean camUp = false;
	boolean camLeft = false;
	boolean camRight = false;
	
	Player player;
	
	
	public LevelEditor(Glass game) {
		this.game = game;
		player = new Player(game.world, new Vector2(0,0));
	}
	
	public void update() {
		if(currentGameObject != null) {
			currentGameObject.update();
		}
		
		player.update();
		updateCameraPosition();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == 51) {
			camUp = true;
		}
		if (keycode == 47) {
			camDown = true;
		}
		if (keycode == 29) {
			camLeft = true;
		}
		if (keycode == 32) {
			camRight = true;
		}
		if (keycode == 30) {
			game.DEBUG = !game.DEBUG;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == 51) {
			camUp = false;
		}
		if (keycode == 47) {
			camDown = false;
		}
		if (keycode == 29) {
			camLeft = false;
		}
		if (keycode == 32) {
			camRight = false;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// start the click timer
		start = System.nanoTime();
		mouseButton = button;
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
		// left click
		if (button == 0) {
			if (selectedGameObject == null) {
			
				if (seconds > .5) {
					if (!currentShape) { // Start new shape
						Vector2 center = game.screenToWorld(new Vector2(x,y));
						Vector2[] verts = { new Vector2(0,0) };
						currentGameObject = new PhysicalShapeObject(game, center, verts);
						currentShape = true;
					}
					else { // Complete a shape
						currentGameObject.initPhysics();
						game.gameObjects.add(currentGameObject);
						
						currentShape = false;
						currentGameObject = null;
					}
				}
				else {
					if (currentShape) {
						currentGameObject.addVertex(game.screenToWorld(new Vector2(x,y)).sub(currentGameObject.center));
					}
				}
			}
		}
		
		// right click
		else if (button == 1) {
			if (modifiedGameObject != null && !modifiedGameObject.hasPhysics) {
				modifiedGameObject.initPhysics();
				modifiedGameObject = null;
			}
		}
		mouseButton = -1;
		return false;
	}
	
	private void moveShape(int x, int y) {
		if (selectedGameObject != null) {
			Vector2 newPosition = game.screenToWorld(new Vector2(x,y)).add(dragVector);
			selectedGameObject.physicsComponent.body.setAwake(true);
			selectedGameObject.physicsComponent.body.setTransform(newPosition, selectedGameObject.physicsComponent.body.getTransform().getRotation());
		}
	}
	
	private void moveVertex(int x, int y) {
		if (selectedGameObject != null && selectedVertex != -1) {
			if (selectedGameObject.objectType == ObjectType.PhysicalShapeObject) {
				modifiedGameObject = (PhysicalShapeObject)selectedGameObject;
				Vector2[] currentVertices = modifiedGameObject.vertices;
				currentVertices[selectedVertex] = game.screenToWorld(new Vector2(x,y)).sub(modifiedGameObject.center);
				modifiedGameObject.updateVertices(currentVertices);
			}
			
		}
	}
	
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if (mouseButton == 0) {
			moveShape(x, y);
		}
		else if (mouseButton == 1) {
			moveVertex(x,y);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if (!currentShape) {
			for (GameObject g : game.gameObjects) {
				g.unHighlight();
				boolean isHighlighted = false;
				for (Fixture f : g.physicsComponent.body.getFixtureList()) {
					
					if (f.testPoint(game.screenToWorld(new Vector2(x,y)))) {
						// mouse is hovering over GameObject g
						g.highlight();
						isHighlighted = true;
						if (g.objectType == ObjectType.PhysicalShapeObject) {
							PhysicalShapeObject pso = (PhysicalShapeObject)g;
							selectedVertex = pso.getCloseVertex(new Vector2(x,y));
						}
					}
				}
				
				if (g.objectType == ObjectType.PhysicalShapeObject) {
					PhysicalShapeObject pso = (PhysicalShapeObject)g;
					if (isHighlighted)
						pso.highlightVertex(selectedVertex);
					else
						pso.highlightVertex(-1);
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
		
		return false;
	}
	
	private void updateCameraPosition() {
		float camSpeed = 1;
		
		if (camDown) {
			game.camera.position.y -= camSpeed;
		}
		if (camUp) {
			game.camera.position.y += camSpeed;
		}
		if (camLeft) {
			game.camera.position.x -= camSpeed;
		}
		if (camRight) {
			game.camera.position.x += camSpeed;
		}
	}
}
