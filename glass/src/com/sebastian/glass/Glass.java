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
	OrthographicCamera camera;
	Array<GameObject> gameObjects = new Array<GameObject>();
	World world;
	static final float WORLD_TO_BOX=0.01f;
	static final float BOX_WORLD_TO=100f;
	
	private Box2DDebugRenderer renderer;
	private float SCREEN_WIDTH, SCREEN_HEIGHT;
	private LevelEditor levelEditor;
	boolean DEBUG = false;
	
	@Override
	public void create() {		
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		System.out.println(SCREEN_WIDTH);
		//camera = new OrthographicCamera(100, (SCREEN_HEIGHT / SCREEN_WIDTH) * 100);
		camera = new OrthographicCamera(33, 20.625f);
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
		GL10 gl = Gdx.graphics.getGL10();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		world.step(Gdx.app.getGraphics().getDeltaTime(), 8, 3);
		camera.update();
		camera.apply(gl);
		for (GameObject g : gameObjects) {
			g.updateObject();
		}
		levelEditor.update();
		if (DEBUG) renderer.render(world, camera.combined);
		
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
		float x = ((coords.x / SCREEN_WIDTH) - .5f) * camera.viewportWidth + camera.position.x;
		float y = (-(coords.y / SCREEN_HEIGHT) + .5f) * camera.viewportHeight + camera.position.y;
		return new Vector2(x,y);
	}
	
	public Vector2 worldToScreen(Vector2 coords) {
		float x = ((coords.x / camera.viewportWidth) * SCREEN_WIDTH) + (.5f / SCREEN_WIDTH) - camera.position.x;
		float y = ((coords.y) / camera.viewportHeight) * SCREEN_HEIGHT - (.5f / SCREEN_HEIGHT) - camera.position.y;
		return new Vector2(x,-y);
	}
	
	float ConvertToBox(float x){
	    return x*WORLD_TO_BOX;
	}
}
