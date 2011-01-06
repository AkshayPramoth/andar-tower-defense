package amir.laribi.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.util.Date;

import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.AndARRenderer;
import edu.dhbw.andar.CameraPreviewHandler;
import edu.dhbw.andar.CameraStatus;
import edu.dhbw.andar.Config;
import edu.dhbw.andar.exceptions.AndARException;

import amir.laribi.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.net.ParseException;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.widget.Toast;


public class MyARactivity extends AndARActivity implements SurfaceHolder.Callback {
	
	
	

	private Model model;
	private Model3D model3d;
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
			
			
	    	@Override
	    	protected Void doInBackground(Void... params) {
	    		
				
				String modelFileName = null;
				BaseFileUtil fileUtil= null;
				File modelFile=null;
				fileUtil = new AssetsFileUtil(getResources().getAssets());
				fileUtil.setBaseFolder("models/");
					
				
				
				//read the model file:						
				if(modelFileName.endsWith(".obj")) {
					ObjParser parser = new ObjParser(fileUtil);
					try {
						if(Config.DEBUG)
							Debug.startMethodTracing("AndObjViewer");
				
						if(fileUtil != null) {
							BufferedReader fileReader = fileUtil.getReaderFromName(modelFileName);
							if(fileReader != null) {
								model = parser.parse("Model", fileReader);
								model3d = new Model3D(model);
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
				}
	    		return null;
	    	}
	    	@Override
	    	protected void onPostExecute(Void result) {
	    		super.onPostExecute(result);
	    		waitDialog.dismiss();
	    		
	    		//register model
	    		try {
	    			if(model3d!=null)
	    				artoolkit.registerARObject(model3d);
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

	


