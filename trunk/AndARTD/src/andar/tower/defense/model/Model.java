package andar.tower.defense.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import andar.tower.defense.parser.Group;
import andar.tower.defense.parser.Material;
import andar.tower.defense.util.BaseFileUtil;
import android.graphics.Point;
import android.util.Log;

public class Model implements Serializable {
	// position/rotation/scale
	private static final String tag = "Model";
	public String name;
	
	public Model3D model3D;
	public float xrot = 90;
	public float yrot = 0;
	public float zrot = 0;
	/* in normal view distance you can see a model 
	 * on screen in a range of x/y: (-30..+30)/(-30..+30) */
	public float xpos = 0;
	public float ypos = 0;
	public float zpos = 0;
	public float scale = 4f;
	public int STATE = STATE_DYNAMIC;
	public static final int STATE_DYNAMIC = 0;
	public static final int STATE_FINALIZED = 1;
	
	public Model center;
	
	/* In unity(model.xpos) per second
	 * negative values don't make sense here.
	 * In normal view distance you can see a model 
	 * on screen in a range of:
	 * model.xpos/model.ypos: (-30..+30)/(-30..+30)
	 */
	public int velocity; 
	
	public ArrayList<Point> way;
	
	// timestamp of last position-update in milliseconds
	private double lastPosUpdate = 0; 

	private Vector<Group> groups = new Vector<Group>();
	/**
	 * all materials
	 */
	protected HashMap<String, Material> materials = new HashMap<String, Material>();

	public Model() {
		// add default material
		materials.put("default", new Material("default"));
		way = null;
		velocity = 6;
	}
	
//	/**
//	 * for movable objects:
//	 * overwrite to calculate new position depending on path and velocity
//	 */
//	public void positionUpdate() {};

	public void addMaterial(Material mat) {
		// mat.finalize();
		materials.put(mat.getName(), mat);
	}

	public Material getMaterial(String name) {
		return materials.get(name);
	}

	public void addGroup(Group grp) {
		if (STATE == STATE_FINALIZED)
			grp.finalize();
		groups.add(grp);
	}

	public Vector<Group> getGroups() {
		return groups;
	}

	public void setFileUtil(BaseFileUtil fileUtil) {
		for (Iterator iterator = materials.values().iterator(); iterator
				.hasNext();) {
			Material mat = (Material) iterator.next();
			mat.setFileUtil(fileUtil);
		}
	}

	public HashMap<String, Material> getMaterials() {
		return materials;
	}
	
	/**
	 * calculate new position on path depending on velocity
	 */
	public void positionUpdate() {
		double timestamp = System.currentTimeMillis();
		
		if (way != null && lastPosUpdate != 0) {
			double deltaTime = timestamp-lastPosUpdate;
			double wayToGo = velocity * deltaTime / 1000;
			/* move in direction of next point on the way */
			Point point = way.get(0);
			double deltaX = point.x - xpos;
			double deltaY = point.y - ypos;
			Log.i(tag, "deltaXY: " + deltaX + " / " + deltaY); 
			double hypo = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			float scale = (float) (wayToGo / hypo);
			if (deltaX < 0.1 && deltaY <0.1) {
				// next step would exceed actual way point
				xpos = point.x;
				ypos = point.y;
				way.remove(0);
				if (way.size() == 0) {
					way = null;
				}
				Log.i(tag, "reachedpoint " + this + " to (x/y): " + this.xpos + " / " + this.ypos);
			} else {
				addXpos((float) (deltaX * scale));
				addYpos((float) (deltaY * scale));
				Log.i(tag, "newxy " + this + " to (x/y): " + this.xpos + " / " + this.ypos);
			}
		}
		
		lastPosUpdate = timestamp; 
	}

	public void addScale(float f) {
		this.scale += f;
		if (this.scale < 0.0001f)
			this.scale = 0.0001f;
	}

	public void addXrot(float dY) {
		this.xrot += dY;
	}

	public void addYrot(float dX) {
		this.yrot += dX;
	}

	public void addXpos(float f) {
		this.xpos += f;
	}

	public void addYpos(float f) {
		this.ypos += f;
	}

	/**
	 * convert all dynamic arrays to final non alterable ones.
	 */
	public void finalize() {
		if (STATE != STATE_FINALIZED) {
			STATE = STATE_FINALIZED;
			for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
				Group grp = (Group) iterator.next();
				grp.finalize();
				grp.setMaterial(materials.get(grp.getMaterialName()));
			}
			for (Iterator<Material> iterator = materials.values().iterator(); iterator
					.hasNext();) {
				Material mtl = iterator.next();
				mtl.finalize();
			}
		}
	}

	/*
	 * get a google protocol buffers builder, that may be serialized
	 */
	/*
	 * public BufferModel getProtocolBuffer() {
	 * ModelProtocolBuffer.BufferModel.Builder builder =
	 * ModelProtocolBuffer.BufferModel.newBuilder();
	 * 
	 * return builder.build(); }
	 */

}
