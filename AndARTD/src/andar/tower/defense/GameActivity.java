package andar.tower.defense;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import andar.tower.defense.model.Enemy;
import andar.tower.defense.model.ModelPool;
import andar.tower.defense.model.Model;
import andar.tower.defense.model.Model3D;
import andar.tower.defense.model.ParsedObjModel;
import andar.tower.defense.model.Tower;
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

	private static final String tag = "GameActivity";

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

		GameCenter gameCenter = new GameCenter("center", ModelPool.CENTER_PATTERN,
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
					hud_x.setText("Tower rel. x: " + x);
					hud_y.setText("Dist En-Tow: " + msg.arg2);
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
		
		/* time measures in ms:
		 * ObjParser.parse:       3000-10000 ms
		 * new Model3D(...)        100 ms
		 * registerARObject         10 ms
		 */

		BaseFileUtil fileUtil;
		private String tag = "GameActivity";

		public ModelLoader() {
			super();
			fileUtil = new AssetsFileUtil(getResources().getAssets());
			fileUtil.setBaseFolder("models/");
		}

		@Override
		protected Void doInBackground(Void... params) {

			gameContext.modelPool = new ModelPool(gameContext, artoolkit, fileUtil);

			Tower tower = gameContext.modelPool.getTower("marker_rupee16.patt");
			Tower tower2 = gameContext.modelPool.getTower("marker_at16.patt");

			// load enemies with pathes to go
	


			/* loading finished */
			gameThread.loadingDone = true;
			return null;

		}


		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			waitDialog.dismiss();

			// register models on markers
//			try {
//				if (center != null) {
//					artoolkit.registerARObject(center.model3D);
//				}
//				if (gameContext.towerList != null) {
//					for (Tower tower : gameContext.towerList) {
//						artoolkit.registerARObject(tower.model3D);
//					}
//
//				}
//				if (gameContext.enemyList != null) {
//					for (Enemy enemy : gameContext.enemyList) {
//						artoolkit.registerARObject(enemy.model3D);
//					}
//				}
//			} catch (AndARException e) {
//				Log.d("on PostExecute", "ERROR ");
//				e.printStackTrace();
//			}
			Log.d("Starting Preview", "Preview starting  ");
			startPreview();

		}
	}

	public class Config {
		public final static boolean DEBUG = false;
	}

}
