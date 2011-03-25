package andar.tower.defense.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import andar.tower.defense.parser.Group;
import andar.tower.defense.parser.Material;
import andar.tower.defense.util.BaseFileUtil;

public class ParsedObjModel implements Serializable {
	
	public String name;
	private static final String tag = "ParsedObjModel";
	public float scale = 4f;
	
	public int STATE = STATE_DYNAMIC;
	public static final int STATE_DYNAMIC = 0;
	public static final int STATE_FINALIZED = 1;
	
	private Vector<Group> groups = new Vector<Group>();
	/**
	 * all materials
	 */
	protected HashMap<String, Material> materials = new HashMap<String, Material>();

	public ParsedObjModel(String name) {
		// add default material
		this.name = name;
		materials.put("default", new Material("default"));
	}
	
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

}
