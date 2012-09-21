package com.sebastian.glass;

import com.badlogic.gdx.utils.Array;

public class Level {
	Array<MeshPlus> renderSet = new Array<MeshPlus>();
	Array<GameObject> gameObjects;
	
	public void update() {
		for(GameObject g : gameObjects) {
			g.update();
		}
	}
}
