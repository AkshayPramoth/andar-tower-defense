package andar.tower.defense;

import andar.tower.defense.model.Enemy;
import andar.tower.defense.model.ModelPool;

public class GameContext {

	private int health;
	private long score;

	public GameCenter gameCenter;
	public ModelPool modelPool;

	public GameContext(int health, GameCenter gameCenter) {
		this.health = health;
		this.score = 0;
		this.gameCenter = gameCenter;
	}

	public void createEnemy() {
		modelPool.getTank();
	}

	public void enemyKilled(Enemy enemy) {
		score += enemy.getScoringPoints();
	}

	public void enemyReachesDestination(int hitpoints) {
		health -= hitpoints;
	}
}
