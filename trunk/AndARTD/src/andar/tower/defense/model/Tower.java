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
	public double Quadrat (double x){
		return x*x;
	}
	private double getDistance(Enemy enemy) {

		double distance = Math.sqrt(Quadrat(enemy.xpos - model3D.getX())+Quadrat(enemy.ypos - model3D.getY()));
		return distance;
	}	

	public void attack() {
		// TODO Auto-generated method stub
		
	}

}
