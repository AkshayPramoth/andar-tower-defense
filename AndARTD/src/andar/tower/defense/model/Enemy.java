package andar.tower.defense.model;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import andar.tower.defense.GameContext;
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
	public static final int AIRPLANE = 0;
	public static final int TANK = 1;
	public static final int BULLET = 2;
	private int type;

	private int health, maxHealth;

	private String tag = "Enemy";

	/* whatever this Moveable is heading for: center, enemy ... */ 
	private Model target;

	public Enemy(GameContext gameContext, int type, ParsedObjModel parsedObjModel, String patternName, Model energyModel, Model target, int health, int velocity) {
		super(gameContext, parsedObjModel, patternName);
		way = null;
		this.type = type;
		this.velocity = velocity;
		this.health = health;
		this.maxHealth = this.health;
		this.target = target;
//		this.energyModel = energyModel;

//		energyModel.adjustModel(0, 0, 5f);
		hit(0, null);

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
	 * get hit/shot: a model that aims at this one reaches its destination
	 * @param hitpoints how hard it got hit
	 * @param gameContext
	 */
	@Override
	protected void hit(int hitpoints, GameContext gameContext) {
		/* get hit/shot from tower -> update health
		 */
		health -= hitpoints;

		// adjust model that displays actual healthpoints
//		energyModel.setScale(((float) health) / maxHealth);

		if (health <= 0) {
			gameContext.enemyKilled(this);
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
	public void positionUpdate(GameContext gameContext) {
		double timestamp = System.currentTimeMillis();

		if (way != null && lastPosUpdate != 0) {
			double deltaTime = timestamp - lastPosUpdate;
			double wayToGo = velocity * deltaTime / 1000;
			/* move in direction of next point on the way */
			Point point = way.get(0);
			double deltaX = point.x - xpos;
			double deltaY = point.y - ypos;
//			Log.i(tag , "deltaXY: " + deltaX + " / " + deltaY);
			double hypo = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			float scale = (float) (wayToGo / hypo);
			if (scale > 1) {
				// next step would exceed actual way point
				xpos = point.x;
				ypos = point.y;
				way.remove(0);
				if (way.size() == 0) {
					// reached final target
					way = null;
					if (target != null)
						target.hit(health, gameContext);
					this.dismiss();
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

	private void dismiss() {
		lastPosUpdate = 0;
		gameContext.modelPool.dismissEnemy(type, this);
	}

}
