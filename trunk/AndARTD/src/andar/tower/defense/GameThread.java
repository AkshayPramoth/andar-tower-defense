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
	long prevLongTime;
	long currTime;
	private GameActivityHandler gameActivityHandler;
	public boolean loadingDone = false;

	// game area limits
	public static final float UPPERLIMITX = 200;
	public static final float LOWERLIMITX = -200;
	public static final float UPPERLIMITY = 150;
	public static final float LOWERLIMITY = -150;

	public GameThread(GameActivityHandler gameActivityHandler,
			GameContext gameContext) {
		this.gameActivityHandler = gameActivityHandler;
		this.gameContext = gameContext;
		setDaemon(true);
		start();
	}

	public void updateHUD(float x, float y) {
		Message msg = Message.obtain(gameActivityHandler,
				gameActivityHandler.UPDATE_X_Y, (int) x, (int) y);
		msg.sendToTarget();
	}

	@Override
	public synchronized void run() {
		super.run();
		setName("GameThread");
		prevTime = System.currentTimeMillis();
		long deltaTime;
		long deltaLongTime = 15 * 1000;// nanoseconds
		long maxDeltaLong = 15 * 1000;
		yield();

		while (running) {
			if (loadingDone) {
				currTime = System.currentTimeMillis();
				deltaTime = currTime - prevTime;
				deltaLongTime = currTime - prevLongTime;
				// update monsters all 15 seconds
				if (deltaLongTime > maxDeltaLong) {
					gameContext.createEnemy();
					prevLongTime = currTime;
				}

				prevTime = currTime;

				gameContext.gameCenter.update(deltaTime);

				// update all positions
				synchronized (gameContext.modelPool) {
					for (Enemy enemy : gameContext.modelPool.getActiveEnemies()) {
						enemy.positionUpdate(gameContext);
					}
				}
//				synchronized (gameContext.modelPool) {
//					for (Enemy bullet : gameContext.modelPool
//							.getActiveBullets()) {
//						bullet.positionUpdate(gameContext);
//					}
//				}

				// update positioning of towers
				int i = 0;
				for (Tower tower : gameContext.modelPool.getActiveTowers()) {
					tower.model3D.update(deltaTime, gameContext.gameCenter);
					int minDistance = tower
							.updateNearestEnemyInRange(gameContext.modelPool
									.getActiveEnemies());
					tower.attack();
					if (i == 0) {
						updateHUD(tower.model3D.getX(), minDistance);
						// updateHUD(tower.model3D.getX(),
						// tower.model3D.getY());
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
