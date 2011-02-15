package andar.tower.defense;

import andar.tower.defense.model.Enemy;
import andar.tower.defense.model.Tower;
import android.os.Message;

public class GameThread extends Thread {

	// Game objects:
	private GameContext gameContext;
	private boolean running = true;

	// time
	long prevTime;
	long currTime;
	private GameActivityHandler gameActivityHandler;
	public boolean loadingDone = false;

	// game area limits
	public static final float UPPERLIMITX = 200;
	public static final float LOWERLIMITX = -200;
	public static final float UPPERLIMITY = 150;
	public static final float LOWERLIMITY = -150;

	// score

	/**
	 * 
	 * @param myARactivity 
	 * @param ball
	 * @param paddle1
	 * @param paddle2
	 */
	public GameThread(GameActivityHandler gameActivityHandler, GameContext gameContext) {
		this.gameActivityHandler = gameActivityHandler;
		this.gameContext = gameContext;
		setDaemon(true);
		start();
	}
	
	public void updateHUD(float x, float y) {
		Message msg = Message.obtain(gameActivityHandler, gameActivityHandler.UPDATE_X_Y, (int)x, (int)y);
		msg.sendToTarget();
	}

	@Override
	public synchronized void run() {
		super.run();
		setName("GameThread");
		prevTime = System.nanoTime();
		long deltaTime;
		yield();
		
		while (running ) {
			if (loadingDone ) {
				currTime = System.nanoTime();
				deltaTime = currTime - prevTime;
				prevTime = currTime;

				gameContext.gameCenter.update(deltaTime);

				// update all positions
				for (Enemy enemy : gameContext.modelPool.getActiveEnemies()) {
					enemy.positionUpdate(gameContext);
					yield(); //necesarry?
				}
				
				// update positioning of towers 
				int i = 0;
				for (Tower tower : gameContext.modelPool.getActiveTowers()) {
					tower.model3D.update(deltaTime, gameContext.gameCenter);
					int minDistance = tower.updateNearestEnemyInRange(gameContext.enemyList);
					tower.attack();
					if (i == 0) {
						updateHUD(tower.model3D.getX(), minDistance);
//						updateHUD(tower.model3D.getX(), tower.model3D.getY());
					}
					i++;
				}
				
			}
			yield();
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
