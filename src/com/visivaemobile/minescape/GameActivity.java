package com.visivaemobile.minescape;

import java.io.IOException;

import managers.GameManager;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.LayoutGameActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.heyzap.sdk.ads.BannerAdView;
import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.IncentivizedAd;

public class GameActivity extends LayoutGameActivity {
	
	private BoundCamera camera;
	public int CAMERA_WIDTH = 800;
	public int CAMERA_HEIGHT = 480;
	
	private BannerAdView bannerAdView;
	//private AdView adView;
	
	private GameManager gm;
	
	public Toast toastNoResources;
	
	@SuppressLint("ShowToast")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        HeyzapAds.start("399834f5f3b013fd1fa07b661babfe51", this);
        IncentivizedAd.fetch();
        
        //TODO: take this to a point where you can reward the player - when he dies at the end of the game
        if (IncentivizedAd.isAvailable()) {
            IncentivizedAd.display(this);
        }
        
        bannerAdView = new BannerAdView(this);
        LinearLayout bannerWrapper = (LinearLayout) findViewById(R.id.LayoutForAds);
        bannerWrapper.addView(bannerAdView);
        bannerAdView.load();
        
        toastNoResources = Toast.makeText(getApplicationContext(), R.string.not_enough_resources, Toast.LENGTH_SHORT);
        /*
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-1575283421556519/5300895528");
        adView.setAdSize(AdSize.BANNER);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.LayoutForAds);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder()
        //.addTestDevice("E599782CD67A92B6DBA90157256B56A4")
        .build();
        adView.loadAd(adRequest);*/
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.camera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);
	    engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
	    return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws IOException {
		GameManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		gm = GameManager.getInstance();
		GameManager.getInstance().createSplashResources(pOnCreateResourcesCallback);
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)	throws IOException {
		gm.createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback)	throws IOException {
		gm.createGameScene();
		this.mEngine.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                mEngine.setScene(gm.gameScene);
                gm.gameScene.gameHud.setChildScene(gm.gameDialogScene);
                gm.gameScene.gameHud.setVisible(true);
            }
		}));		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(!gm.gameDialogScene.isShown) {
		    	gm.gameScene.setIgnoreUpdate(true);
		    	gm.gameDialogScene.setParameters(1, 0, 0, getResources().getString(R.string.quit_title), getResources().getString(R.string.quit_msg), getResources().getString(R.string.yes), getResources().getString(R.string.no), 0);
				gm.gameDialogScene.show();
	    	}
	    }
	    return false; 
	}
	
	@Override
	protected void onDestroy() {
		bannerAdView.destroy();
		//adView.destroy();
	    super.onDestroy();	        
	    if (this.isGameLoaded()) {
	        System.exit(0);
	    }
	}
	
	public void gameDialogCallback(int callback, boolean action) {
		if(callback == 0) {
    		if(action) {
    			getSharedPreferences("firstLaunch", Context.MODE_PRIVATE).edit().putBoolean("firstLaunch", false).commit();
    			Intent menuActivity = new Intent(gm.activity, MainActivity.class);
	     	   	startActivity(menuActivity);
	     	   	finish();
	     	   	android.os.Process.killProcess(android.os.Process.myPid());
    		}
    		else gm.gameScene.setIgnoreUpdate(false);
    	}
		else if(callback >= 1 && callback <= 4) {
			if(action) {
				gm.gameScene.craftingNewSword(callback - 1, true);
				gm.gameScene.checkCraftingAchievement();
			}
		}
		else if(callback >= 5 && callback <= 8) {
			if(action) gm.gameScene.craftingNewArmor(callback - 5, true);
			gm.gameScene.checkCraftingAchievement();
		}
		else if(callback == 9) {
			gm.gameScene.setIgnoreUpdate(false);
		}
		else if(callback == 10) {
			gm.gameScene.circle.setVisible(false);
			gm.gameScene.setIgnoreUpdate(false);
			gm.gameScene.touchEnabled = true;
		}
    }
	
	@Override
	protected void onResume() {
	    super.onResume();
	    //adView.resume();
	}

	@Override
	public void onPause() {
		//adView.pause();
	    super.onPause();
	}

	@Override
	protected int getLayoutID() {
		return R.layout.game;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.SurfaceViewId;
	}

}
