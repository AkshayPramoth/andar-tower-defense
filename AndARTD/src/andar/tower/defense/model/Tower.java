package andar.tower.defense.model;

import java.util.ArrayList;

public class Tower extends Model {

	/* Tower can attack Enemies in this radius */
	private int actionRadius = 20;
	private Enemy nearestEnemyInRange;

	public void updateNearestEnemyInRange(ArrayList<Enemy> enemyList) {
		double distance = 0;
		double nearestDistance = actionRadius;
		Enemy nearestEnemy = null;
		for (Enemy enemy : enemyList) {
			distance = getDistance(enemy);
			if (distance < nearestDistance) {
				nearestEnemy = enemy;
				nearestDistance = distance;
			}
		}

		nearestEnemyInRange = nearestEnemy;

	}

	private double getDistance(Enemy enemy) {
		double x = (double) (enemy.xpos + model3D.getX());
		double y = (double) (enemy.ypos + model3D.getY());
		double distance = Math.sqrt(x * x + y * y);
		return distance;
	}	

	public void attack() {
		// TODO Auto-generated method stub
		
	}

}
