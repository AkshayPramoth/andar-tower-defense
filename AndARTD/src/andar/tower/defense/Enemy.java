package andar.tower.defense;

import java.util.ArrayList;

import android.graphics.Point;

public class Enemy extends Model {
	
	/* In unity(model.xpos) per second
	 * negative values don't make sense here.
	 * In normal view distance you can see a model 
	 * on screen in a range of:
	 * model.xpos/model.ypos: (-30..+30)/(-30..+30)
	 */
	public int velocity; 
	
	public ArrayList<Point> way;
	
	// timestamp of last position-update in milliseconds
	private double lastPosUpdate = 0; 
	

	public Enemy() {
		way = null;
		velocity = 6;
	}
	
	/**
	 * calculate new position on path depending on velocity
	 */
	public void positionUpdate() {
		double timestamp = System.currentTimeMillis();
		
		if (way != null && lastPosUpdate != 0) {
			double deltaTime = timestamp-lastPosUpdate;
			double wayToGo = velocity * deltaTime / 1000;
			/* move in direction of next point on the way */
			Point point = way.get(0);
			double deltaX = point.x - xpos;
			double deltaY = Math.abs(point.y - ypos);
			double hypo = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			float scale = (float) (wayToGo / hypo);
			if (scale > 1) {
				// next step would exceed actual way point
				xpos = point.x;
				ypos = point.y;
				way.remove(0);
				if (way.size() == 0) {
					way = null;
				}
			} else {
				xpos = (float) (deltaX * scale);
				ypos = (float) (deltaY * scale);
			}
		}
		
		lastPosUpdate = timestamp; 
	}

}
