package com.sebastian.glass;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class RenderComponent extends Component {
	Array<MeshPlus> items;
	float x = 0;
	float y = 0;
	float rotation = 0;
	
	public RenderComponent() {
		items = new Array<MeshPlus>();
	}
	
	public MeshPlus addMeshPlus(MeshPlus meshPlus) {
		items.add(meshPlus);
		meshPlus.buildMesh();
		return meshPlus;
	}
	
	public MeshPlus setMeshPlus(MeshPlus meshPlus) {
		clearItems();
		System.out.println("cleared!");
		return addMeshPlus(meshPlus);
	}
	
	public void clearItems() {
		items = new Array<MeshPlus>();
	}
	
	public void render() {
		GL10 gl = Gdx.graphics.getGL10();
		
		for (MeshPlus ri : items) {
			gl.glPushMatrix();
			gl.glTranslatef(x, y, 0);
			gl.glRotatef((ri.rotation + rotation) * MathUtils.radiansToDegrees, 0, 0, 1);
			gl.glTranslatef(ri.x, ri.y, 0);
			
			ri.mesh.render(GL10.GL_TRIANGLES, 0, ri.mesh.getMaxVertices());
			gl.glPopMatrix();
		}
		
	}
	
	public void setPosition(float x, float y, float rotation) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
}
