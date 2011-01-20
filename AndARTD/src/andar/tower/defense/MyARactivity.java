package andar.tower.defense;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.SurfaceHolder;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;

public class MyARactivity extends AndARActivity implements
		SurfaceHolder.Callback {

	private Model enemy1;

	private Model[] models = null;
	private ProgressDialog waitDialog;
	private Resources res;
	public static final boolean DEBUG = false;
	ARToolkit artoolkit;

	public MyARactivity() {
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
		if (models == null) {
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

		public ModelLoader() {
			super();
			fileUtil = new AssetsFileUtil(getResources().getAssets());
			fileUtil.setBaseFolder("models/");
			parser = new ObjParser(fileUtil);
		}

		private String modelName2patternName(String modelName) {
			// the designated map-marker
			String patternName = "marker_fisch16";

			if (modelName.equals("plant.obj")) {
				patternName = "marker_rupee16";
			} else if (modelName.equals("tank3.obj")) {
				patternName = "marker_rupee16";
			} else if (modelName.equals("superman.obj")) {
				patternName = "marker_fisch16";
			} else if (modelName.equals("tower.obj")) {
				patternName = "marker_peace16";
			} else if (modelName.equals("bench.obj")) {
				patternName = "marker_at16";
			} else if (modelName.equals("towergreen.obj")) {
				patternName = "marker_hand16";
			}

			return patternName;
		}

		@Override
		protected Void doInBackground(Void... params) {

			Model model;
			String[] towerModels = { "tower.obj", "towergreen.obj", "bench.obj", "tank3.obj" };
			String[] enemyModels = { "superman.obj" }; // also bullets...
			models = new Model[towerModels.length + enemyModels.length];

			for (int i = 0; i < towerModels.length; i++) {
				model = loadModelFromFile(towerModels[i]);
				models[i] = model;
			}

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
			for (int i = 0; i < enemyModels.length; i++) {
				model = loadModelFromFile(enemyModels[i]);
				model.way = way;
				models[towerModels.length + i] = model;
			}

			return null;

		}

		private Model loadModelFromFile(String modelFileName) {
			// read the model file:
			Model model = null;
			if (modelFileName.endsWith(".obj")) {
				try {
					if (Config.DEBUG)
						Debug.startMethodTracing("AndObjViewer");

					if (fileUtil != null) {
						fileReader = fileUtil.getReaderFromName(modelFileName);
						if (fileReader != null) {
							model = parser.parse(modelFileName.substring(0,
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

			// register model
			try {
				if (models != null) {
					for (Model model : models) {
						artoolkit.registerARObject(model.model3D);
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
