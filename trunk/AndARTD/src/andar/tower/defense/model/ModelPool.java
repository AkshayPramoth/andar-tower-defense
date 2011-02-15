package andar.tower.defense.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.exceptions.AndARException;

import andar.tower.defense.GameContext;
import andar.tower.defense.GameActivity.Config;
import andar.tower.defense.parser.ObjParser;
import andar.tower.defense.util.AssetsFileUtil;
import andar.tower.defense.util.BaseFileUtil;
import android.graphics.Point;
import android.net.ParseException;
import android.os.Debug;
import android.util.Log;

/**
 * Manage a pool of models for reuse
 * 
 * @author jakob
 * 
 */
public class ModelPool {

	private GameContext gameContext;

	private ArrayList<Enemy> activeAirplanes = new ArrayList<Enemy>();
	private ArrayList<Enemy> inactiveAirplanes = new ArrayList<Enemy>();

	private ArrayList<Enemy> activeTanks = new ArrayList<Enemy>();
	private ArrayList<Enemy> inactiveTanks = new ArrayList<Enemy>();

	private ArrayList<Enemy> activeBullets = new ArrayList<Enemy>();
	private ArrayList<Enemy> inactiveBullets = new ArrayList<Enemy>();

	private ArrayList<Tower> activeTowers = new ArrayList<Tower>();
	
	private BaseFileUtil fileUtil;

	private ObjParser parser;

	public static final String CENTER_PATTERN = "marker_fisch16.patt";

	private final ParsedObjModel AIRPLANE_OBJMODEL, TANK_OBJMODEL,
			BULLET_OBJMODEL, TOWER_OBJMODEL;

	private Model center;
	private ARToolkit artoolkit;
	private static final String tag = "EnemyPool";

	public ModelPool(GameContext gameContext, ARToolkit artoolkit, BaseFileUtil fileUtil) {
		this.gameContext = gameContext;
		this.artoolkit = artoolkit;
		this.fileUtil = fileUtil;
		parser = new ObjParser(fileUtil);

		center = loadCenter();

		AIRPLANE_OBJMODEL = loadModelFromFile("Airplane.obj");
		TANK_OBJMODEL = loadModelFromFile("tank3.obj");
		BULLET_OBJMODEL = loadModelFromFile("bullet.obj");
		TOWER_OBJMODEL = loadModelFromFile("Tower.obj");

	}

	private synchronized Enemy getEnemy(int type, int health, int velocity) {
		ArrayList<Enemy> activeList = null;
		ArrayList<Enemy> inactiveList = null;
		ParsedObjModel parsedObjModel = null;
		switch (type) {
		case Enemy.AIRPLANE:
			activeList = activeAirplanes;
			inactiveList = inactiveAirplanes;
			parsedObjModel = AIRPLANE_OBJMODEL;
			break;
		case Enemy.TANK:
			activeList = activeTanks;
			inactiveList = inactiveTanks;
			parsedObjModel = TANK_OBJMODEL;
			break;
		case Enemy.BULLET:
			activeList = activeBullets;
			inactiveList = inactiveBullets;
			parsedObjModel = BULLET_OBJMODEL;
			break;
		default:
			break;
		}
		Enemy enemy;
		if (inactiveList.size() == 0) {
			enemy = new Enemy(gameContext, type, parsedObjModel, CENTER_PATTERN, null, center,
					health, velocity);
			try {
				artoolkit.registerARObject(enemy.model3D);
			} catch (AndARException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			enemy = inactiveList.remove(0);
			enemy.setHidden(false);
		}
		activeList.add(enemy);
		return enemy;
	}

	public synchronized void dismissEnemy(int type, Enemy enemy) {
		enemy.setHidden(true);
		ArrayList<Enemy> activeList = null;
		ArrayList<Enemy> inactiveList = null;
		switch (type) {
		case Enemy.AIRPLANE:
			activeList = activeAirplanes;
			inactiveList = inactiveAirplanes;
			break;
		case Enemy.TANK:
			activeList = activeTanks;
			inactiveList = inactiveTanks;
			break;
		case Enemy.BULLET:
			activeList = activeBullets;
			inactiveList = inactiveBullets;
			break;
		default:
			break;
		}
		if (activeList.contains(enemy)) {
			activeList.remove(enemy);
			inactiveList.add(enemy);
		}
	}
	
	public Enemy getAirplane() { 
		Enemy enemy = getEnemy(Enemy.AIRPLANE, 20, 10);
		Point startPoint = randomWayPoint(30);
		enemy.xpos = startPoint.x;
		enemy.ypos = startPoint.y;
		ArrayList<Point> way = new ArrayList<Point>();
		way.add(new Point(0,0));
		enemy.way = way;
		return enemy;
	}
	public void dismissAirplane(Enemy enemy) {
		dismissEnemy(Enemy.AIRPLANE, enemy);
	}
	
	public Enemy getTank() { 
		Enemy enemy = getEnemy(Enemy.TANK, 100, 4);
		Point startPoint = randomWayPoint(30);
		enemy.xpos = startPoint.x;
		enemy.ypos = startPoint.y;
		ArrayList<Point> way = new ArrayList<Point>();
		way.add(new Point(0,0));
		enemy.way = way;
		return enemy;
	}
	public void dismissTank(Enemy enemy) {
		dismissEnemy(Enemy.TANK, enemy);
	}
	
	public Enemy getBullet(Point targetLocation) { 
		Enemy enemy = getEnemy(Enemy.BULLET, 10, 20);
		ArrayList<Point> way = new ArrayList<Point>();
		way.add(targetLocation);
		enemy.way = way;
		return enemy;
	}
	public void dismissBullet(Enemy enemy) {
		dismissEnemy(Enemy.BULLET, enemy);
	}
	
	public Tower getTower(String markerName) {
		Tower tower = new Tower(gameContext, TOWER_OBJMODEL, markerName);
		activeTowers.add(tower);
		try {
			artoolkit.registerARObject(tower.model3D);
		} catch (AndARException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tower;
	}
	
	private Point randomWayPoint(int distanceFromCenter) {
		/* In normal view distance you can see a model on screen in a range
		 * of: model.xpos/model.ypos: (-30..+30)/(-30..+30)
		 */
		
		// enemies head to center starting on a distance of 15
		int radius = distanceFromCenter;
		double maxX = Math.sqrt((radius*radius)/2);
		double deltaX = Math.signum(Math.random()-0.5) * Math.random() * maxX;
		double deltaY = Math.signum(Math.random()-0.5) * Math.sqrt(radius*radius - deltaX*deltaX);
		
		return new Point((int)deltaX, (int)deltaY);
		
	}
	
	private ParsedObjModel loadModelFromFile(String modelFileName) {
		// read the model file:
		ParsedObjModel parsedObjModel = null;
		if (modelFileName.endsWith(".obj")) {
			try {
				if (Config.DEBUG)
					Debug.startMethodTracing("AndObjViewer");

				if (fileUtil != null) {
					BufferedReader fileReader = fileUtil.getReaderFromName(modelFileName);
					if (fileReader != null) {
						parsedObjModel = new ParsedObjModel(modelFileName);
						parser.parse(parsedObjModel, modelFileName.substring(0,
								modelFileName.length() - 4), fileReader);
						Log.i(tag , "new Model3D: " + modelFileName);
					} else {
						Log.w("ModelLoader", "no file reader: "
										+ modelFileName);
					}
				}
				if (Config.DEBUG)
					Debug.stopMethodTracing();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return parsedObjModel;
	}

	public Model getCenter() {
		return center;
	}

	private Model loadCenter() {
		// load red circle on centermarker
		String centerModelName = "energy.obj";
		ParsedObjModel energyObjModel = loadModelFromFile(centerModelName);
		Model model = new Model(gameContext, energyObjModel, ModelPool.CENTER_PATTERN);
		model.name = "center";
		try {
			artoolkit.registerARObject(model.model3D);
		} catch (AndARException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

	public ArrayList<Enemy> getActiveEnemies() {
		ArrayList<Enemy> allActiveEnemies = new ArrayList<Enemy>();
		allActiveEnemies.addAll(activeAirplanes);
		allActiveEnemies.addAll(activeTanks);
		return allActiveEnemies;
	}
	
	public ArrayList<Enemy> getActiveBullets() {
		return activeBullets;
	}

	public ArrayList<Tower> getActiveTowers() {
		return activeTowers;
	}
}
