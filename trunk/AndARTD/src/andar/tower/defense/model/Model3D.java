package andar.tower.defense.model;
import java.lang.Math;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import andar.tower.defense.GameCenter;
import andar.tower.defense.parser.Group;
import andar.tower.defense.parser.Material;
import android.opengl.GLUtils;
import android.util.Log;
import android.widget.TextView;
import edu.dhbw.andar.ARObject;
import edu.dhbw.andar.ARToolkit;

/**
 * represents a 3d model.
 * 
 * @author tobi
 * 
 */
public class Model3D extends ARObject implements Serializable {

	private Model model;
	private double[] invTrans = new double[12];
	private Group[] texturedGroups;
	private Group[] nonTexturedGroups;
	private HashMap<Material, Integer> textureIDs = new HashMap<Material, Integer>();
	private float x = 0;
	private float y = 0;
	private float z = 0;

	public Model3D(Model model, String patternName) {
		super("model", patternName, 80.0, new double[] { 0, 0 });
		this.model = model;
		model.finalize();
		// separate texture from non textured groups for performance reasons
		Vector<Group> groups = model.getGroups();
		Vector<Group> texturedGroups = new Vector<Group>();
		Vector<Group> nonTexturedGroups = new Vector<Group>();
		for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
			Group currGroup = iterator.next();
			if (currGroup.isTextured()) {
				texturedGroups.add(currGroup);
			} else {
				nonTexturedGroups.add(currGroup);
			}
		}
		this.texturedGroups = texturedGroups.toArray(new Group[texturedGroups
				.size()]);
		this.nonTexturedGroups = nonTexturedGroups
				.toArray(new Group[nonTexturedGroups.size()]);
	}
	
	public synchronized void update(long time, GameCenter center) {
//		Log.i(tag, "is model " + this.model.name + " visible? " + this.isVisible());
		if(this.isVisible() && center.isVisible()) {
			double[] transmat = this.getTransMatrix();
			double[] centerMat = center.getInvTransMat();
			double[] coordsInCenterCS = new double[12];
			ARToolkit.arUtilMatMul(centerMat, transmat, coordsInCenterCS);
			float halfWidth = 0;
			x = (float) coordsInCenterCS[3] - halfWidth;
			y = (float) coordsInCenterCS[7] - halfWidth;
			z = (float) coordsInCenterCS[11] - halfWidth;
			Log.i(tag, "coords of " + model.name + " to center x/y/z: " + x
					+ "/" + y + "/" + z);		
		} else {
			//is there any touchevent?
		}
	}
	
	@Override
	public void init(GL10 gl) {
		int[] tmpTextureID = new int[1];
		// load textures of every material(that has a texture):
		Iterator<Material> materialI = model.getMaterials().values().iterator();
		while (materialI.hasNext()) {
			Material material = (Material) materialI.next();
			if (material.hasTexture()) {
				// load texture
				gl.glGenTextures(1, tmpTextureID, 0);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, tmpTextureID[0]);
				textureIDs.put(material, tmpTextureID[0]);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,
						material.getTexture(), 0);
				material.getTexture().recycle();
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			}
		}

		// transfer vertices to video memory
	}

	private Writer log = new LogWriter();
	private String tag = "Model3D";

	@Override
	public void draw(GL10 gl) {
		super.draw(gl);

		// gl = (GL10) GLDebugHelper.wrap(gl,
		// GLDebugHelper.CONFIG_CHECK_GL_ERROR, log);
		// do positioning:
		gl.glScalef(model.scale, model.scale, model.scale);
		gl.glTranslatef(model.xpos, model.ypos, model.zpos);
		gl.glRotatef(model.xrot, 1, 0, 0);
		gl.glRotatef(model.yrot, 0, 1, 0);
		gl.glRotatef(model.zrot, 0, 0, 1);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		// first draw non textured groups
		gl.glDisable(GL10.GL_TEXTURE_2D);
		int cnt = nonTexturedGroups.length;
		for (int i = 0; i < cnt; i++) {
			Group group = nonTexturedGroups[i];
			Material mat = group.getMaterial();
			if (mat != null) {
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
						mat.specularlight);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
						mat.ambientlight);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
						mat.diffuselight);
				gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS,
						mat.shininess);
			}
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, group.vertices);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, group.normals);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
		}

		// now we can continue with textured ones
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		cnt = texturedGroups.length;
		for (int i = 0; i < cnt; i++) {
			Group group = texturedGroups[i];
			Material mat = group.getMaterial();
			if (mat != null) {
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
						mat.specularlight);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
						mat.ambientlight);
				gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
						mat.diffuselight);
				gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS,
						mat.shininess);
				if (mat.hasTexture()) {
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, group.texcoords);
					gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs.get(mat)
							.intValue());
				}
			}
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, group.vertices);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, group.normals);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
		}

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public synchronized double[] getInvTransMat() {
		double[] transmat = getTransMatrix();
		ARToolkit.arUtilMatInv(transmat, invTrans);
		return invTrans;
	}

	public synchronized void calculateDistance(double[] transmat) {
		if (this.isVisible()) {
			double[] centerMat = getInvTransMat();
			double[] coordsInCenterCS = new double[12];
			ARToolkit.arUtilMatMul(centerMat, transmat, coordsInCenterCS);
			float halfWidth = 0;
			float x = (float) coordsInCenterCS[3] - halfWidth;
			float y = (float) coordsInCenterCS[7] - halfWidth;
			float z = (float) coordsInCenterCS[11] - halfWidth;
			Log.i(tag, "coords of " + model.name + " to center x/y/z: " + x
					+ "/" + y + "/" + z);
		}

	}

	/**
	 * write stuff to Android log
	 * 
	 * @author Tobias Domhan
	 * 
	 */
	class LogWriter extends Writer {

		@Override
		public void close() {
			flushBuilder();
		}

		@Override
		public void flush() {
			flushBuilder();
		}

		@Override
		public void write(char[] buf, int offset, int count) {
			for (int i = 0; i < count; i++) {
				char c = buf[offset + i];
				if (c == '\n') {
					flushBuilder();
				} else {
					mBuilder.append(c);
				}
			}
		}

		private void flushBuilder() {
			if (mBuilder.length() > 0) {
				Log.e("OpenGLCam", mBuilder.toString());
				mBuilder.delete(0, mBuilder.length());
			}
		}

		private StringBuilder mBuilder = new StringBuilder();

	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void getNearestEnemy(ArrayList<Enemy> enemyList) {
		double distance = 0;
		for (Enemy enemy : enemyList) {
			distance = getDistance(enemy);
		}
		
	}

	private double getDistance(Enemy enemy) {
	double x=(double)(enemy.xpos+getX());
	double y= (double)(enemy.ypos+getY());
	double distance =Math.sqrt(x*x+y*y);
		return distance;
	}
}
