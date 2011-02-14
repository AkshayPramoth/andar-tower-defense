package andar.tower.defense;

import java.util.ArrayList;

import andar.tower.defense.model.Enemy;
import andar.tower.defense.model.Model;
import andar.tower.defense.model.Tower;

public class GameContext {

	public ArrayList<Enemy> enemyList;
	public ArrayList<Enemy> bulletList;
	public ArrayList<Tower> towerList;

	private int health;

	private long score;

	public GameCenter gameCenter;

//	public Map myMap;
//	public PersistentGameData myPersistentGameData;
//	public Game myGame;
//	public Map myMap;
//	public Game myGame;

	public GameContext(int health, GameCenter gameCenter) {
		this.health = health;
		this.score = 0;
		this.gameCenter = gameCenter;
		towerList = new ArrayList<Tower>();
		enemyList = new ArrayList<Enemy>();
		bulletList = new ArrayList<Enemy>();
	}

	public void registerTower(Tower tower) {
		towerList.add(tower);
	}
	public void deregisterTower(Tower tower) {
		towerList.remove(tower);
	}
	
	public void registerEnemy(Enemy model) {
		enemyList.add(model);
	}
	public void deregisterEnemy(Enemy model) {
		enemyList.remove(model);
	}
	
	public void registerBullet(Enemy model) {
		enemyList.add(model);
	}
	public void deregisterBullet(Enemy model) {
		enemyList.remove(model);
	}

	public Enemy getNearestEnemy(Model model) {
		return null;
	}

	private void createEnemy() {
	}

	public void enemyKilled(Enemy enemy) {
		score += enemy.getScoringPoints();
	}

	public void enemyReachesDestination(int hitpoints) {
		health -= hitpoints;
	}
}
