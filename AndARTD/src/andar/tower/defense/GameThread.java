package andar.tower.defense;

public class GameThread extends Thread {

	// Game objects:
	private GameCenter center;
	private boolean running = true;

	// time
	long prevTime;
	long currTime;
	public Model3D tower;

	// game area limits
	public static final float UPPERLIMITX = 200;
	public static final float LOWERLIMITX = -200;
	public static final float UPPERLIMITY = 150;
	public static final float LOWERLIMITY = -150;

	// score

	/**
	 * 
	 * @param ball
	 * @param paddle1
	 * @param paddle2
	 */
	public GameThread(GameCenter center) {
		this.center = center;
		setDaemon(true);
		start();
	}

	@Override
	public synchronized void run() {
		super.run();
		setName("GameThread");
		prevTime = System.nanoTime();
		long td;
		yield();
		boolean collision = false;
		while (running) {
			if (tower != null) {
				currTime = System.nanoTime();
				td = currTime - prevTime;
				prevTime = currTime;

				center.update(td);

				// update all position
				tower.update(td, center);

				// check for collisions
				collision = false;
				// if(ball.getVx() > 0) {
				// //Ball heading to paddle1 ... so we don't care about paddle2
				// if((ball.getOldX()+ball.radius<=GameThread.UPPERLIMITX)&&(ball.getX()+ball.radius>GameThread.UPPERLIMITX))
				// {
				// //Kollision mit Paddel 1
				// if((ball.getY()+ball.radius>
				// paddle1.getY())&&(ball.getY()-ball.radius<
				// paddle1.getY()+paddle1.getWidth())) {
				// ball.bounceX((paddle2.getY()-paddle2.getOldY())/(td/100));
				// ball.setX(GameThread.UPPERLIMITX-ball.radius);
				// collision = true;
				// }
				// }
				// } else {
				// //Ball heading to paddle2 ... so we don't care about paddle1
				// if((ball.getOldX()-ball.radius>=GameThread.LOWERLIMITX)&&(ball.getX()-ball.radius<GameThread.LOWERLIMITX))
				// {
				// //Kollision mit Paddel 2
				// if((ball.getY()+ball.radius>
				// paddle2.getY())&&(ball.getY()-ball.radius<
				// paddle2.getY()+paddle2.getWidth())) {
				// ball.bounceX((paddle2.getY()-paddle2.getOldY())/(td/100));
				// ball.setX(GameThread.LOWERLIMITX+ball.radius);
				// collision = true;
				// }
				// }
				// }

				// if(!collision) {
				// if(ball.getX()+ball.radius>GameThread.UPPERLIMITX) {
				// //score
				// score.incComputerScore();
				// ball.reset();
				// } else if (ball.getX()-ball.radius<GameThread.LOWERLIMITX) {
				// score.incPlayerScore();
				// //score
				// ball.reset();
				// }
				// }

			}
			yield();
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
