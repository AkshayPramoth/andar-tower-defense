package andar.tower.defense.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import andar.tower.defense.GameContext;
import andar.tower.defense.parser.Group;
import andar.tower.defense.parser.Material;
import andar.tower.defense.util.BaseFileUtil;

public class Model implements Serializable {
	// position/rotation/scale
	private static final String tag = "Model";
	public String name;
	
	private ParsedObjModel parsedObjModel; // the meshes and textures
	public Model3D model3D;
	public float xrot = 90;
	public float yrot = 0;
	public float zrot = 0;
	/* in normal view distance you can see a model 
	 * on screen in a range of x/y: (-30..+30)/(-30..+30) */
	public float xpos = 0;
	public float ypos = 0;
	public float zpos = 0;
	/* rotate/scale a model to "normal" at start */
	private float defaultXRot;
	private float defaultYRot;
	private float defaultScale;
	public float scale = 4f;
	
	public static final int STATE_DYNAMIC = 0;
	public static final int STATE_FINALIZED = 1;
	
	
	// timestamp of last position-update in milliseconds
	private double lastPosUpdate = 0; 

	private Vector<Group> groups = new Vector<Group>();
	/**
	 * all materials
	 */
	protected HashMap<String, Material> materials = new HashMap<String, Material>();

	public Model(ParsedObjModel parsedObjModel, String patternName) {
		this.name = parsedObjModel.name;
		setParsedObjModel(parsedObjModel);
		Model3D model3d = new Model3D(this, parsedObjModel, patternName);
		this.model3D = model3d;
		adjustModel(0f,0f,0f);
	}
	
	/**
	 * calculate new position on path depending on velocity
	 */
	public void positionUpdate() {
	}
	
	/**
	 * Rotate/scale a model to "normal" at start.
	 * @param defaultXRot
	 * @param defaultYRot
	 * @param defaultScale
	 */
	public void adjustModel(float defaultXRot, float defaultYRot, float defaultScale) {
		this.defaultXRot = defaultXRot;
		this.defaultYRot = defaultYRot;
		this.defaultScale = defaultScale;
	}

	public void setScale(float f) {
		this.scale = defaultScale * f;
		if (this.scale < 0.0001f)
			this.scale = 0.0001f;
	}

	public float getScale() {
		return scale;
	}
	
	public void setXrot(float dY) {
		this.xrot = defaultXRot + dY;
	}

	public void setYrot(float dX) {
		this.yrot = defaultYRot + dX;
	}

	public void addXpos(float f) {
		this.xpos += f;
	}

	public void addYpos(float f) {
		this.ypos += f;
	}
	
	/**
	 * get hit/shot: a model that aims at this one reaches its destination
	 * @param hitpoints how hard it got hit
	 * @param gameContext
	 */
	protected void hit(int hitpoints, GameContext gameContext) {
		/* implementation for center:
		 * the enemy reaches the center before getting destroyed
		 */
		gameContext.enemyReachesDestination(hitpoints);
	}

	public ParsedObjModel getParsedObjModel() {
		return parsedObjModel;
	}

	public void setParsedObjModel(ParsedObjModel parsedObjModel) {
		this.parsedObjModel = parsedObjModel;
	}

}
