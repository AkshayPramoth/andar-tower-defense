package andar.tower.defense;

import andar.tower.defense.model.Enemy;
import andar.tower.defense.model.ModelPool;
import android.os.Bundle;
import android.os.Message;

public class GameContext {

	private int health;
	private long score;

	public GameCenter gameCenter;
	public ModelPool modelPool;
	private GameActivityHandler gameActivityHandler;

	public GameContext(int health, GameCenter gameCenter, GameActivityHandler gameActivityHandler) {
		this.gameActivityHandler = gameActivityHandler;
		this.health = health;
		this.score = 0;
		this.gameCenter = gameCenter;
	}

	public void sendToMainThread(int what) {
		Message msg = Message.obtain (gameActivityHandler,what);
		msg.sendToTarget();
	}
	
	public void sendBulletToMainThread(Bundle data) {
		Message msg = Message.obtain (gameActivityHandler,gameActivityHandler.GET_BULLET, data);
		msg.sendToTarget();
	}
	
	public void createEnemy() {
		sendToMainThread(gameActivityHandler.GET_TANK);
//		modelPool.getTank();
	}

	public void enemyKilled(Enemy enemy) {
		score += enemy.getScoringPoints();
	}

	public void enemyReachesDestination(int hitpoints) {
		health -= hitpoints;
	}
}
