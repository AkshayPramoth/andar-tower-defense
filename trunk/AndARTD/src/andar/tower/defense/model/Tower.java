package andar.tower.defense.model;

import java.util.ArrayList;

import andar.tower.defense.GameContext;
import android.util.Log;

public class Tower extends Model {

	/* Tower can attack Enemies in this radius */
	private int actionRadius = 500;
	private Enemy nearestEnemyInRange;
	private String tag = "Tower";

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

		int distance = (int) Math.sqrt(quadrat((int) (enemy.xpos - model3D.getX()))
				+ quadrat((int) (enemy.ypos - model3D.getY())));
//		Log.i(tag , "enemx: " + enemy.xpos + " -modlgetX: " +model3D.getX()+ "=x: " + (enemy.xpos - model3D.getX()) + " Distance: " + distance);
		return distance;
	}

	public void attack() {
		// TODO Auto-generated method stub

	}
	

	/**
	 * get hit/shot: a model that aims at this one reaches its destination
	 * @param hitpoints how hard it got hit
	 * @param gameContext
	 */
	@Override
	protected void hit(int hitpoints, GameContext gameContext) {
		// towers don't get hit
	}

}
