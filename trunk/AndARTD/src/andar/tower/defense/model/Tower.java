package andar.tower.defense.model;

import java.util.ArrayList;

import andar.tower.defense.GameContext;
import android.graphics.Point;
import android.util.Log;

public class Tower extends Model {

	public Tower(GameContext gameContext, ParsedObjModel parsedObjModel,
			String patternName) {
		super(gameContext, parsedObjModel, patternName);
	}

	/* Tower can attack Enemies in this radius */
	private int actionRadius = 500;
	private Enemy nearestEnemyInRange;
	private String tag = "Tower";
	public ArrayList<Point> way;
	private boolean shoot = false;

	public int updateNearestEnemyInRange(ArrayList<Enemy> enemyList) {
		int distance = 0;
		int nearestDistance = actionRadius;
		Enemy nearestEnemy = null;
		for (Enemy enemy : enemyList) {
			if (enemy.model3D.isVisible()) {
				distance = getDistance(enemy);
				if (distance < nearestDistance) {
					nearestEnemy = enemy;
					nearestDistance = distance;
					shoot = true;
				}
			}
		}

		nearestEnemyInRange = nearestEnemy;
		if (nearestEnemy != null)
			Log.i(tag, nearestEnemy.name + " is at: " + nearestDistance);
		return nearestDistance;

	}

	public int quadrat(int x) {
		return x * x;
	}

	private int getDistance(Enemy enemy) {

		int distance = (int) Math.sqrt(quadrat((int) (enemy.xpos - model3D
				.getX()))
				+ quadrat((int) (enemy.ypos - model3D.getY())));
		// Log.i(tag , "enemx: " + enemy.xpos + " -modlgetX: " +model3D.getX()+
		// "=x: " + (enemy.xpos - model3D.getX()) + " Distance: " + distance);
		return distance;
	}

	public void attack() {
		if (shoot == true) {
			
			Point targetLocation = new Point((int) nearestEnemyInRange.xpos - 5,
					(int) nearestEnemyInRange.ypos - 5);
			Enemy Bullet = gameContext.modelPool.getBullet(targetLocation);
			Bullet.xpos = this.xpos + 5;
			Bullet.ypos = this.ypos + 5;

		}

	}

	/**
	 * get hit/shot: a model that aims at this one reaches its destination
	 * 
	 * @param hitpoints
	 *            how hard it got hit
	 * @param gameContext
	 */
	@Override
	protected void hit(int hitpoints, GameContext gameContext) {
		// towers don't get hit
	}

}
