package andar.tower.defense;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.SurfaceHolder;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;


public class MyARactivity extends AndARActivity implements SurfaceHolder.Callback {
	
	
	

	private Model model;
	private Model model2;
	private Model model3;
	private Model model4;
	private Model model5;
	private Model3D model3d;
	private Model3D model3d2;
	private Model3D model3d3;
	private Model3D model3d4;
	private Model3D model3d5;
	private ProgressDialog waitDialog;
	private Resources res;
	public static final boolean DEBUG = false;
	ARToolkit artoolkit;
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setNonARRenderer(new LightingRenderer());//or might be omited
		res=getResources();
		artoolkit = getArtoolkit();		
		//getSurfaceView().setOnTouchListener(new TouchEventHandler());
		getSurfaceView().getHolder().addCallback(this);
	}
	
	

	/**
	 * Inform the user about exceptions that occurred in background threads.
	 */

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println("");
		
	}
	  @Override
	    public void surfaceCreated(SurfaceHolder holder) {
	    	super.surfaceCreated(holder);
	    	//load the model
	    	//this is done here, to assure the surface was already created, so that the preview can be started
	    	//after loading the model
	    	if(model == null) {
				waitDialog = ProgressDialog.show(this, "", 
		                getResources().getText(R.string.loading), true);
				waitDialog.show();
				new ModelLoader().execute();
			}
	    }
	    
		
	    
	  
	    
		private class ModelLoader extends AsyncTask<Void, Void, Void> {
			
			private String modelName2patternName (String modelName) {
				String patternName = "android";
				
				if (modelName.equals("plant.obj")) {
					patternName = "marker_rupee16";
				} else if (modelName.equals("chair.obj")) {
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
	    		
				
				String modelFileName = null;
				BaseFileUtil fileUtil= null;
				File modelFile=null;
				fileUtil = new AssetsFileUtil(getResources().getAssets());
				fileUtil.setBaseFolder("models/");
					
				
				
				//read the model file:						
//				if(modelFileName.endsWith(".obj")) {
					ObjParser parser = new ObjParser(fileUtil);
					try {
						if(Config.DEBUG)
							Debug.startMethodTracing("AndObjViewer");
				
						if(fileUtil != null) {

							BufferedReader fileReader = fileUtil.getReaderFromName(modelFileName);
							if(fileReader != null) {
								model = parser.parse("Model", fileReader);
								Log.w("ModelLoader", "model3d = new Model3D(model, " + modelName2patternName(modelFileName) + ".patt");
								model3d = new Model3D(model, modelName2patternName(modelFileName) + ".patt");
							}
							String modelFileName2 = "chair.obj";
							BufferedReader fileReader2 = fileUtil.getReaderFromName(modelFileName2);
							if(fileReader2 != null) {
								model2 = parser.parse("Chair", fileReader2);
								Log.w("ModelLoader", "model3d = new Model3D(model2, " + modelName2patternName(modelFileName2) + ".patt");
								model3d2 = new Model3D(model2, modelName2patternName(modelFileName2) + ".patt");
							} else {
								Log.w("ModelLoader", "no file reader");
							}
							String modelFileName3 = "towergreen.obj";
							BufferedReader fileReader3 = fileUtil.getReaderFromName(modelFileName3);
							if(fileReader3 != null) {
								model3 = parser.parse("towergreen", fileReader3);
								Log.w("ModelLoader", "model3d = new Model3D(model3, " + modelName2patternName(modelFileName3) + ".patt");
								model3d3 = new Model3D(model3, modelName2patternName(modelFileName3) + ".patt");
							} else {
								Log.w("ModelLoader", "no file reader");
							}
							String modelFileName4 = "tower.obj";
							BufferedReader fileReader4 = fileUtil.getReaderFromName(modelFileName4);
							if(fileReader4 != null) {
								model4 = parser.parse("tower", fileReader4);
								Log.w("ModelLoader", "model3d = new Model3D(model4, " + modelName2patternName(modelFileName4) + ".patt");
								model3d4 = new Model3D(model4, modelName2patternName(modelFileName4) + ".patt");
							} else {
								Log.w("ModelLoader", "no file reader");
							}
							String modelFileName5 = "plant.obj";
							BufferedReader fileReader5 = fileUtil.getReaderFromName(modelFileName5);
							if(fileReader5 != null) {
								model5 = parser.parse("Plant", fileReader5);
								Log.w("ModelLoader", "model3d = new Model3D(model5, " + modelName2patternName(modelFileName5) + ".patt");
								model3d5 = new Model3D(model5, modelName2patternName(modelFileName5) + ".patt");
							} else {
								Log.w("ModelLoader", "no file reader");
							}
						}
						if(Config.DEBUG)
							Debug.stopMethodTracing();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//				}
	    		return null;
	    	}
	    	@Override
	    	protected void onPostExecute(Void result) {
	    		super.onPostExecute(result);
	    		waitDialog.dismiss();
	    		
	    		//register model
	    		try {
	    			if(model3d!=null) {
	    				artoolkit.registerARObject(model3d);
	    				artoolkit.registerARObject(model3d2);
	    				artoolkit.registerARObject(model3d3);
	    				artoolkit.registerARObject(model3d4);
	    				artoolkit.registerARObject(model3d5);
	    			}
				} catch (AndARException e) {
					e.printStackTrace();
				}
				startPreview();
	    	}
	    }
		

		public class Config {
			public final static boolean DEBUG = false;
		}

		
	}

	


