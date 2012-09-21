package com.sebastian.glass;

import java.util.ArrayList;
import java.util.Timer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Glass implements ApplicationListener, InputProcessor {
	private OrthographicCamera camera;
	private Box2DDebugRenderer renderer;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	private int SCREEN_WIDTH, SCREEN_HEIGHT;
	private World world;
	private Array<Mesh> meshes;
	
	private long start = 0;
	private float end = 0;
	
	private boolean currentShape = false;
	
	private Array<GameObject> gameObjects = new Array<GameObject>();
	private PhysicalShapeObject currentGameObject;
	
	
	@Override
	public void create() {		
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(10, (h/w) * 10);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		
		world = new World(new Vector2(0, 0), true);
		renderer = new Box2DDebugRenderer();
		Gdx.input.setInputProcessor(this);
		meshes = new Array<Mesh>();
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		world.step(Gdx.app.getGraphics().getDeltaTime(), 8, 3);
		//camera.update();
		for (GameObject g : gameObjects) {
			g.update();
		}
		if(currentGameObject != null) {
			currentGameObject.update();
		}
		renderer.render(world, camera.combined);
		
		/*
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		batch.end();
		*/
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		System.out.println(keycode);
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
		
		
		return false;
	}
	
	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		long elapsedTime = System.nanoTime() - start;
		double seconds = (double)elapsedTime / 1000000000.0;
		System.out.println(seconds);
		
		
		if (seconds > 1) {
			// Start new shape
			if (!currentShape) {
				System.out.println("beginning");
				Vector2 center = screenToWorld(new Vector2(x,y));
				currentGameObject = new PhysicalShapeObject(world, center, new Vector2[0]);
				currentShape = true;
			}
			else {
				System.out.println("ending");
				currentGameObject.initPhysics();
				gameObjects.add(currentGameObject);
				
				currentShape = false;
				currentGameObject = null;
			}
		}
		else {
			if (currentShape) {
				System.out.println("adding");
				currentGameObject.addVertex(screenToWorld(new Vector2(x,y)).sub(currentGameObject.center));
			}
		}
		
		
		return false;
	}
	
	private Vector2 screenToWorld(Vector2 coords) {
		float x = ((coords.x / (float)Gdx.graphics.getWidth()) - .5f) * camera.viewportWidth;
		float y = (-(coords.y / (float)Gdx.graphics.getHeight()) + .5f) * camera.viewportHeight;
		return new Vector2(x,y);
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		if (!currentShape) {
			for (GameObject g : gameObjects) {
				g.unHighlight();
				for (Fixture f : g.physicsComponent.body.getFixtureList()) {
					if (f.testPoint(screenToWorld(new Vector2(x,y)))) {
						System.out.println("heyo");
						g.highlight();
						
					}
				}
			}
		}
		
		if (currentShape && currentGameObject.vertices.length >= 3) {
			Vector2[] currentVertices = currentGameObject.vertices;
			currentVertices[currentVertices.length - 1] = screenToWorld(new Vector2(x,y)).sub(currentGameObject.center);
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
