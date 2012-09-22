package com.sebastian.glass;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MeshPlus implements Comparable<Object> {
	Mesh mesh;
	Vector2[] vertices;
	Array<Vector2[]> triangles;
	int r, g, b, a;
	float x, y, z, rotation;
	
	public MeshPlus(Vector2[] triangle, int r, int g, int b, int a, float x, float y, float z, float rotation) {
		this.r = r; this.g = g; this.b = b; this.a = a;
		this.x = x; this.y = y; this.z = z;
		this.rotation = rotation;
		this.triangles = new Array<Vector2[]>();
		
		if (triangle != null) {
			if (triangle.length == 3) {
				this.addTriangle(triangle);
			}
			else if (triangle.length > 3) {
				this.vertices = triangle;
				triangulate();
			}
		}
	}
	
	public MeshPlus(int r, int g, int b, int a, float x, float y, float z, float rotation) {
		this(null, r, g, b, a, x, y, z, rotation);
	}
	
	public void addTriangle(Vector2[] triangle) {
		triangles.add(triangle);
	}
	
	public void update() {
		
	}
	
	public void setPosition(float x, float y, float rotation) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	public void triangulate() {
		Triangulator t = new Triangulator();
		for (Vector2 v : vertices) {
			t.addPolyPoint(v.x, v.y);
		}
		
		if (t.triangulate()) {
			for (int i = 0; i < t.getTriangleCount(); i++) {
				float[] one = t.getTrianglePoint(i, 0);
				float[] two = t.getTrianglePoint(i, 1);
				float[] three = t.getTrianglePoint(i, 2);
				
				Vector2[] triVerts = new Vector2[3];
				triVerts[0] = new Vector2(one[0], one[1]);
				triVerts[1] = new Vector2(two[0], two[1]);
				triVerts[2] = new Vector2(three[0], three[1]);
				this.addTriangle(triVerts);
			}
		}
	}
	
	public void buildMesh() {
		int nVertexAttributes = 6;
		if (mesh != null) {
			mesh.dispose();
		}
		mesh = new Mesh(true, triangles.size * 3, triangles.size * 3, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
		float[] meshVertices = new float[triangles.size * nVertexAttributes * 3];
		short[] meshIndices = new short[triangles.size * 3];
		
		float maxY = 0;
		float minY = Float.MAX_VALUE;
		
		for (int i = 0; i < triangles.size; i++) {
			for (int j = 0; j < 3; j++) {
				float currentY = triangles.get(i)[j].y;
				if (currentY > maxY) maxY = currentY;
				if (currentY < minY) minY = currentY;
			}
		}
		
		float diff = maxY - minY;
		
		for (int i = 0; i < triangles.size; i++) {
			int row = i * nVertexAttributes * 3;
			Vector2[] triangle = triangles.get(i);
			
			for (int point = 0; point < 3; point++) {
				int vertsIncrement =  nVertexAttributes * point;
				float colorshift = ((maxY - triangle[point].y) / (diff));
				colorshift *= .2f;
				colorshift = 1 - colorshift;
				
				// This makes colorshift not happen.
				colorshift = 1;
				
				// these 6 correspond to nVertexAttributes
				meshVertices[row + vertsIncrement + 0] = triangle[point].x;
				meshVertices[row + vertsIncrement + 1] = triangle[point].y;
				meshVertices[row + vertsIncrement + 2] = 0;
				meshVertices[row + vertsIncrement + 3] = Color.toFloatBits((int)(r * colorshift), (int)(g * colorshift), (int)(b * colorshift), a);
				//meshVertices[row + vertsIncrement + 3] = Color.toFloatBits((int)(r + colorshift), (int)(g + colorshift), (int)(b + colorshift), a);
				meshVertices[row + vertsIncrement + 4] = (triangle[point].x) * (float)(1.0 / 16.0);
				meshVertices[row + vertsIncrement + 5] = (triangle[point].y) * (float)(1.0 / 16.0);
				
				int indexIncrement = (i * 3) + point;
				meshIndices[indexIncrement] = (short)(indexIncrement);
			}
		}

		mesh.setVertices(meshVertices);
		mesh.setIndices(meshIndices);
	}

	@Override
	public int compareTo(Object o) {
		MeshPlus other = (MeshPlus)o;
		if (this.z > other.z) return 1;
		if (other.z > this.z) return -1;
		return 0;
	}
}
