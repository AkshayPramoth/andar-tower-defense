package andar.tower.defense;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import andar.tower.defense.model.Enemy;
import andar.tower.defense.model.Model;
import andar.tower.defense.model.Model3D;
import andar.tower.defense.parser.ObjParser;
import andar.tower.defense.util.AssetsFileUtil;
import andar.tower.defense.util.BaseFileUtil;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;

public class GameActivity extends AndARActivity implements
		SurfaceHolder.Callback {

	private Model enemy1;
	private static final String tag = "GameActivity";

	private Model[] models = null;
	private ProgressDialog waitDialog;
	private Resources res;
	public static final boolean DEBUG = false;
	ARToolkit artoolkit;

	private andar.tower.defense.GameThread gameThread;

	private GameActivityHandler handler;

	TextView hud_x;

	TextView hud_y;
	private GameContext gameContext;

	public GameActivity() {
		super(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		super.setNonARRenderer(new LightingRenderer());// or might be omited
		res = getResources();
		artoolkit = getArtoolkit();
		// getSurfaceView().setOnTouchListener(new TouchEventHandler());
		getSurfaceView().getHolder().addCallback(this);

		GameCenter gameCenter = new GameCenter("center", "marker_fisch16.patt",
				137.0, new double[] { 0, 0 });// 170
		try {
			artoolkit.registerARObject(gameCenter);
		} catch (AndARException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createHUD();
		gameContext = new GameContext(100, gameCenter);
		gameThread = new GameThread(handler, gameContext);

	}

	public void createHUD() {

		// add layout
		LayoutInflater controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.hud, null);
		LayoutParams layoutParamsControl = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(viewControl, layoutParamsControl);

		// Handler for UI callbacks
		hud_x = (TextView) findViewById(R.id.tower_x);
		hud_y = (TextView) findViewById(R.id.tower_y);

		handler = new GameActivityHandler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == UPDATE_X_Y) {
					int x = msg.arg1;
					int y = msg.arg2;
					hud_x.setText("Tower rel. x: " + x);
					hud_y.setText("Tower rel. y: " + y);
				} else {
					Log.i(tag, "Unknown handle with what-code: " + msg.what);
				}
			}
		};

	}

	/**
	 * Inform the user about exceptions that occurred in background threads.
	 */

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println("Exception Occured !!");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		// load the model
		// this is done here, to assure the surface was already created, so that
		// the preview can be started
		// after loading the model
		if (gameContext.towerList.size() == 0) {
			waitDialog = ProgressDialog.show(this, "", getResources().getText(
					R.string.loading), true);
			waitDialog.show();
			new ModelLoader().execute();
		}
	}

	private class ModelLoader extends AsyncTask<Void, Void, Void> {

		BaseFileUtil fileUtil;
		BufferedReader fileReader;
		ObjParser parser;
		private String tag = "ModelLoader";
		private Model center;

		public ModelLoader() {
			super();
			fileUtil = new AssetsFileUtil(getResources().getAssets());
			fileUtil.setBaseFolder("models/");
			parser = new ObjParser(fileUtil);
		}

		private String modelName2patternName(String modelName) {
			// the designated map-marker
			String patternName = "marker_fisch16";

			if (modelName.equals("Tower.obj")) {
				patternName = "marker_rupee16";
			} else if (modelName.equals("tank3.obj")) {
				patternName = "marker_peace16";
			} else if (modelName.equals("Airplane.obj")) {
				patternName = "marker_hand16";
			}

			return patternName;
		}

		@Override
		protected Void doInBackground(Void... params) {

			Model model;
			String centerModel = "energy.obj";
			// String[] towerModels = { "tower.obj", "towergreen.obj",
			// "bench.obj", "tank3.obj" };
			String[] enemyModels = { "Airplane.obj", "tank3.obj" }; // also
																	// bullets...
			int i = 0;

			// load red circle on centermarker
			center = new Model();
			loadModelFromFile(center, centerModel);
			center.name = "center";
			gameContext.registerModel(center);
			i++;

			String modelName = "Tower.obj";
			model = new Model();
			loadModelFromFile(model, modelName);
			model.name = modelName;
			gameContext.registerModel(model);

			// load enemies with pathes to go
			ArrayList<Point> way = new ArrayList<Point>();
			/*
			 * In normal view distance you can see a model on screen in a range
			 * of: model.xpos/model.ypos: (-30..+30)/(-30..+30)
			 */
			way.add(new Point(-30, -30));
			way.add(new Point(0, -10));
			way.add(new Point(20, 30));
			way.add(new Point(-30, -30));
			way.add(new Point(20, 30));
			way.add(new Point(-30, -30));
			way.add(new Point(20, 30));
			way.add(new Point(-30, -30));
			way.add(new Point(20, 30));
			way.add(new Point(-30, -30));
			way.add(new Point(20, 30));
			way.add(new Point(-30, -30));

			Enemy enemy = null;
			for (i = 0; i < enemyModels.length; i++) {
				enemy = new Enemy(null, 100, 10);
				loadModelFromFile(enemy, enemyModels[i]);
				enemy.name = enemyModels[i];
				gameContext.registerEnemy(enemy);
			}

			// only the last one moves
			if (enemy != null)
				enemy.way = way;

			return null;

		}

		private Model loadModelFromFile(Model model, String modelFileName) {
			// read the model file:
			if (modelFileName.endsWith(".obj")) {
				try {
					if (Config.DEBUG)
						Debug.startMethodTracing("AndObjViewer");

					if (fileUtil != null) {
						fileReader = fileUtil.getReaderFromName(modelFileName);
						if (fileReader != null) {
							parser.parse(model, modelFileName.substring(0,
									modelFileName.length() - 4), fileReader);
							Log.w(tag, "model3d = new Model3D(model, "
									+ modelName2patternName(modelFileName)
									+ ".patt");
							Model3D model3d = new Model3D(model,
									modelName2patternName(modelFileName)
											+ ".patt");
							model.model3D = model3d;
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
			return model;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			waitDialog.dismiss();

			// register models on markers
			try {
				if (gameContext.towerList != null) {
					for (Model model : gameContext.towerList) {
						artoolkit.registerARObject(model.model3D);
					}
					// nr 1 is rupee tower - just for testing 
					gameThread.tower = gameContext.towerList.get(1).model3D;
				}
				if (gameContext.enemyList != null) {
					for (Enemy enemy : gameContext.enemyList) {
						artoolkit.registerARObject(enemy.model3D);
					}
				}
			} catch (AndARException e) {
				Log.d("on PostExecute", "ERROR ");
				e.printStackTrace();
			}
			Log.d("Starting Preview", "Preview starting  ");
			startPreview();

		}
	}

	public class Config {
		public final static boolean DEBUG = false;
	}

}
