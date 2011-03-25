package andar.tower.defense;

import andar.tower.defense.model.ModelPool;
import andar.tower.defense.util.AssetsFileUtil;
import andar.tower.defense.util.BaseFileUtil;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
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
		gameContext = new GameContext(100, gameCenter, handler);
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
				if (msg.getData().containsKey("test")) {
					int i = 1;
				}
				if (msg.what == UPDATE_X_Y) {
					int x = msg.arg1;
					hud_x.setText("Tower rel. x: " + x);
					hud_y.setText("Dist En-Tow: " + msg.arg2);
				} else if (msg.what == GET_TANK) {
					gameContext.modelPool.getTank();
				} else if (msg.what == GET_AIRPLANE) {
					gameContext.modelPool.getAirplane();
				} else if (msg.what == GET_BULLET) {
					Bundle data = msg.getData();
					Point startPoint = new Point(data.getInt("start_x"),data.getInt("start_y"));
					Point targetPoint = new Point(data.getInt("target_x"),data.getInt("target_y"));
					gameContext.modelPool.getBullet(startPoint, targetPoint);
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
		
		if (!gameThread.loadingDone) {
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

			gameContext.modelPool.getTower("marker_rupee16.patt");
			gameContext.modelPool.getTower("marker_peace16.patt");
			gameContext.modelPool.getTower("marker_at16.patt");
			gameContext.modelPool.getTower("marker_hand16.patt");

			/* loading finished */
			gameThread.loadingDone = true;
			return null;

		}


		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			waitDialog.dismiss();
			Log.d("Starting Preview", "Preview starting  ");
			startPreview();

		}
	}

	public class Config {
		public final static boolean DEBUG = false;
	}

}
