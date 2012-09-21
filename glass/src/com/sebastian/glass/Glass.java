package com.sebastian.glass;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Glass implements ApplicationListener { //, InputProcessor {
	private OrthographicCamera camera;
	private Box2DDebugRenderer renderer;

	private float SCREEN_WIDTH, SCREEN_HEIGHT;
	
	private LevelEditor levelEditor;
	
	Array<GameObject> gameObjects = new Array<GameObject>();
	World world;
	
	@Override
	public void create() {		
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(10, (SCREEN_HEIGHT / SCREEN_WIDTH) * 10);
		world = new World(new Vector2(0, 0), true);
		renderer = new Box2DDebugRenderer();
		levelEditor = new LevelEditor(this);
		Gdx.input.setInputProcessor(levelEditor);
	}

	@Override
	public void dispose() {
		
		
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
		levelEditor.update();
		renderer.render(world, camera.combined);
		
	}

	@Override
	public void resize(int width, int height) {
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public Vector2 screenToWorld(Vector2 coords) {
		float x = ((coords.x / SCREEN_WIDTH) - .5f) * camera.viewportWidth;
		float y = (-(coords.y / SCREEN_HEIGHT) + .5f) * camera.viewportHeight;
		return new Vector2(x,y);
	}
}
