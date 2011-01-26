package andar.tower.defense;

import java.util.ArrayList;

import andar.tower.defense.model.Enemy;
import andar.tower.defense.model.Model;

public class GameContext {

	public ArrayList<Enemy> enemyList;

	public ArrayList<Model> towerList;

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
	}

	public void registerModel(Model model) {
		towerList.add(model);
	}

	public void deregisterModel(Model model) {
		towerList.remove(model);
	}
	
	public void registerEnemy(Enemy model) {
		enemyList.add(model);
	}
	
	public void deregisterEnemy(Enemy model) {
		enemyList.remove(model);
	}

	public Enemy getNearestEnemy(Model model) {
		return null;
	}

	public Model getNearestModel(Model model) {
		return null;
	}

	private void createEnemy() {
	}

//	public void enemyKilled(Enemy enemy) {
//		score += enemy.getScoringPoints();
//	}
//
//	public void enemyReachesDestination(Enemy enemy) {
//		health -= enemy.getMaxHealth();
//	}
}
