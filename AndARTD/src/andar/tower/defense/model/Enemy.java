package andar.tower.defense.model;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Point;
import android.util.Log;

public class Enemy extends Model {

	/*
	 * In unity(model.xpos) per second negative values don't make sense here. In
	 * normal view distance you can see a model on screen in a range of:
	 * model.xpos/model.ypos: (-30..+30)/(-30..+30)
	 */
	private int velocity;

	public ArrayList<Point> way;

	// timestamp of last position-update in milliseconds
	private double lastPosUpdate = 0;

	// indicating the actual life energy as additional 3D-model
	private final Model energyModel = null;

	private int health, maxHealth;

	private String tag = "Enemy";

	public Enemy(Model energyModel, int health, int velocity) {
		way = null;
		this.velocity = velocity;
		this.health = health;
		this.maxHealth = this.health;
//		this.energyModel = energyModel;

//		energyModel.adjustModel(0, 0, 5f);
		hit(0);

	}

	/**
	 * how much point you get for killing this enemy
	 * 
	 * @return number of scoring points
	 */
	public long getScoringPoints() {
		return velocity * maxHealth;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * get hit/shot from tower -> update health
	 * 
	 * @param hitpoints
	 */
	private void hit(int hitpoints) {
		health -= hitpoints;

		// adjust model that displays actual healthpoints
//		energyModel.setScale(((float) health) / maxHealth);

		if (health <= 0) {
//			gameContext.enemyKilled(this);
		}

	}

	/**
	 * called by Model3D onDraw. draw additional 3D-models that belong to this
	 * model here.
	 */
	public void drawAdditionals(GL10 gl) {
//		energyModel.xpos = xpos;
//		energyModel.ypos = ypos;
//		energyModel.model3D.draw(gl);
	}

	/**
	 * calculate new position on path depending on velocity
	 */
	public void positionUpdate() {
		double timestamp = System.currentTimeMillis();

		if (way != null && lastPosUpdate != 0) {
			double deltaTime = timestamp - lastPosUpdate;
			double wayToGo = velocity * deltaTime / 1000;
			/* move in direction of next point on the way */
			Point point = way.get(0);
			double deltaX = point.x - xpos;
			double deltaY = point.y - ypos;
			Log.i(tag , "deltaXY: " + deltaX + " / " + deltaY);
			double hypo = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			float scale = (float) (wayToGo / hypo);
			if (deltaX < 0.1 && deltaY < 0.1) {
				// next step would exceed actual way point
				xpos = point.x;
				ypos = point.y;
				way.remove(0);
				if (way.size() == 0) {
					way = null;
				}
				Log.i(tag, "reachedpoint " + this + " to (x/y): " + this.xpos
						+ " / " + this.ypos);
			} else {
				addXpos((float) (deltaX * scale));
				addYpos((float) (deltaY * scale));
				Log.i(tag, "newxy " + this + " to (x/y): " + this.xpos + " / "
						+ this.ypos);
			}
		}

		lastPosUpdate = timestamp;

	}

}
