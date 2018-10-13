package com.visivaemobile.minescape;

import gplay.GBaseGameActivity;
import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.io.IOException;

import managers.MainManager;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.ScreenCapture;
import org.andengine.entity.util.ScreenCapture.IScreenCaptureCallback;
import org.andengine.util.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.InterstitialAd;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class MainActivity extends GBaseGameActivity {
	
	// Note: Your consumer key and secret should be obfuscated in your source code before shipping.
	private static final String TWITTER_KEY = "UzxXAcGaCC8lXliw0dC1mFEFS";
	private static final String TWITTER_SECRET = "hyeVyeLzKwN1oTh2u1yzPUML70ssp12WpO9OkoUYmLx5FpA1uL";
	
	private Camera camera;
	public int CAMERA_WIDTH = 800;
	public int CAMERA_HEIGHT = 480;
	private float splashTime = 3.0f;
	private boolean SceneLoaded = false;
	
	private MainManager mm;
	
	public Toast toastNoResources;
	public Toast toastComingSoon;
	public Toast toastNoUpdates;
	
    private static final int RC_UNUSED = 5001;
    private static final String MAX_DISTANCE = "maxDistance";
    private static final String MAX_MONSTERS = "maxMonsters";
    private static final String ACHIEVEMENT_DISTANCE_INCREMENT = "AchievementDistanceIncrement";
    private static final String ACHIEVEMENT_MONSTER_INCREMENT = "AchievementMonsterIncrement";
    private static final String ACHIEVEMENT_RESOURCES_INCREMENT = "AchievementResourcesIncrement";
    
    private static final int REWARDED_DIAMONDS = 3;
    private static final float REWARD_FACTOR = 1.12f;
    private static final int TWEET_COMPOSER_REQUEST_CODE = 100;

    @SuppressLint("ShowToast")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(this, new Twitter(authConfig));
		Fabric.with(this, new TweetComposer());
        mHelper.mConnectOnStart = false;
        HeyzapAds.start("399834f5f3b013fd1fa07b661babfe51", this);
        
        //TODO: desabilitar propagandas indesejadas nesta parte?
        if(!getSharedPreferences("firstLaunch", Context.MODE_PRIVATE).getBoolean("firstLaunch", true)) {
        	InterstitialAd.display(this);
			splashTime = 1.0f;
        }
		
		toastNoResources = Toast.makeText(getApplicationContext(), R.string.not_enough_resources, Toast.LENGTH_SHORT);
		toastComingSoon = Toast.makeText(getApplicationContext(), R.string.coming_soon, Toast.LENGTH_SHORT);
		toastNoUpdates = Toast.makeText(getApplicationContext(), R.string.upgrade_completed, Toast.LENGTH_SHORT);
    }

	@Override
	public EngineOptions onCreateEngineOptions() {
		this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);
	    engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
	    return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
		MainManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		mm = MainManager.getInstance();
		MainManager.getInstance().createSplashResources(pOnCreateResourcesCallback);
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
		mm.createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback)	throws IOException {
		mm.createMainScene();
		this.mEngine.registerUpdateHandler(new TimerHandler(splashTime, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                mEngine.setScene(mm.mainScene);
                SceneLoaded = true;
                
                //Ap?s carregar a cena...
                mm.mainScene.aboutEntity.updateOverallScore();
                mm.mainScene.setChildScene(mm.mainDialogScene);
                mm.mainScene.showMenu();
                if(mm.introMusic.isPlaying()) mm.introMusic.pause();
                else mm.introMusic.play();
                
                getSharedPreferences("firstLaunch", Context.MODE_PRIVATE).edit().putBoolean("firstLaunch", true).commit();
                
                if(getSharedPreferences("autoSignIn", Context.MODE_PRIVATE).getBoolean("autoSignIn", true)) {
                	getSharedPreferences("autoSignIn", Context.MODE_PRIVATE).edit().putBoolean("autoSignIn", true).commit();
            		beginUserInitiatedSignIn();
                }
                
                runOnUiThread(new Runnable(){ @Override public void run() { resources.AppRater.app_launched(mm.activity); }});
            }
		}));		
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	public boolean villageEntityIsOnTop = false;
    public boolean aboutEntityIsOnTop = false;
    public boolean creditsEntityIsOnTop = false;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(!mm.mainDialogScene.isShown) {
	    		if(villageEntityIsOnTop) mm.mainScene.villageEntity.hideVillageEntity();
	    		else if(aboutEntityIsOnTop) mm.mainScene.aboutEntity.hideAboutEntity();
	    		else if(creditsEntityIsOnTop) mm.mainScene.creditsEntity.hideCreditsEntity();
		    	else {
		    		mm.mainScene.touchable = false;
		    		mm.mainDialogScene.setParameters(1, getResources().getString(R.string.exit_title), getResources().getString(R.string.exit_msg), getResources().getString(R.string.yes), getResources().getString(R.string.no), 1);
					mm.mainDialogScene.show();
		    	}
	    	}
	    }
	    return false; 
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();	        
	    if (this.isGameLoaded()) {
	        System.exit(0);
	    }
	}
	
	public void mainDialogCallback(int callback, boolean action) {
    	if(callback == 0) {
    		if(action) {
    			mm.mainScene.villageEntity.upgradeVillage();
    			
    			if(isOnline()) {
    				float diamonds = REWARDED_DIAMONDS;
    				for(int i = 1; i < mm.mainScene.villageEntity.villageLevel; i++) {
    					diamonds = diamonds * REWARD_FACTOR;
    				}
    				int intDiam = (int)diamonds;
    				
    				mm.mainDialogScene.setParameters(
    						1,
    						getResources().getString(R.string.share_dialog_title),
    						
    						getResources().getString(R.string.share_dialog_msg_a)
    						+ Integer.toString(intDiam)
    						+ getResources().getString(R.string.share_dialog_msg_b),
    						
    						getResources().getString(R.string.yes),
    						getResources().getString(R.string.no),
    						6);
    	     	   	mm.mainDialogScene.show();
    			}
    			else mm.mainScene.villageEntity.touchableVillage = true;
    		}
    		else mm.mainScene.villageEntity.touchableVillage = true;
    	}
    	else if(callback == 1) {
    		if(action) System.exit(0);
    		mm.mainScene.touchable = true;
    	}
    	else if(callback == 2) {
    		mm.mainScene.aboutEntity.touchableAbout = true;
    	}
    	else if(callback == 3) {
    		if(action) {
    			getSharedPreferences("autoSignIn", Context.MODE_PRIVATE).edit().putBoolean("autoSignIn", true).commit();
        		beginUserInitiatedSignIn();
    		}
    		mm.mainScene.touchable = true;
    	}
    	else if(callback == 4) {
    		if(action) {
    			getSharedPreferences("autoSignIn", Context.MODE_PRIVATE).edit().putBoolean("autoSignIn", false).commit();
    			mm.mainScene.googlePlaySprite.setCurrentTileIndex(0);
        		signOut();
    		}
    		mm.mainScene.touchable = true;
    	}
    	else if(callback == 5) {
    		if(action) {
    			getSharedPreferences("autoSignIn", Context.MODE_PRIVATE).edit().putBoolean("autoSignIn", true).commit();
        		beginUserInitiatedSignIn();
    		}
    		mm.mainScene.aboutEntity.touchableAbout = true;
    	}
    	else if(callback == 6) {
    		if(action) {
    			mm.mainScene.villageEntity.shareVillageLevel.setVisible(true);
    			tweetScreenshot();
    		}
    		else mm.mainScene.villageEntity.touchableVillage = true;
    	}
    	//retorno do compartilhamento no twitter
    	else if(callback == 7) {
    		mm.mainScene.villageEntity.touchableVillage = true;
    	}
    	else if(callback == 8) {
    		mm.mainScene.villageEntity.touchableVillage = true;
    	}
    }
	
    public void openAchievements() {
    	runOnUiThread(new Runnable(){ 
    		@Override public void run() { 
    			if (isSignedIn()) {
    	            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), RC_UNUSED);
    	        } else {
    	        	ShowDialogToLogIn();
    	        }
    		}
    	});
    }
    
    public void openLeaderboards() {
    	runOnUiThread(new Runnable(){ 
    		@Override public void run() {
    			if (isSignedIn()) {
    		        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), RC_UNUSED);
    		    } else {
    		    	ShowDialogToLogIn();
    		    }
    		}
    	});
    }
    
    private void updateGooglePlayServices() {
    	runOnUiThread(new Runnable(){ 
    		@Override public void run() {
    			if(isSignedIn()) {
    	    		mm.mainScene.googlePlaySprite.setCurrentTileIndex(1);
    	    		updateLeaderboards();
    	    		updateAchievements();
    			}   			
    			else {
    				mm.mainScene.googlePlaySprite.setCurrentTileIndex(0);
    			}
    		}
    	});
    }
    
    public void updateLeaderboards() {
    	int distance = getSharedPreferences(MAX_DISTANCE, Context.MODE_PRIVATE).getInt(MAX_DISTANCE, 0);
		if(distance > 0) Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_distance), distance);
		
		int monsters = getSharedPreferences(MAX_MONSTERS, Context.MODE_PRIVATE).getInt(MAX_MONSTERS, 0);
		if(monsters > 0) Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_monsters), monsters);
		
		int score = mm.mainScene.aboutEntity.overallScore;
		if(score > 0) Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_score), score);
    }
    
    public void updateAchievements() {
    	//recupera os valores incrementais da ?ltima jogada
    	int distance_increment = getSharedPreferences(ACHIEVEMENT_DISTANCE_INCREMENT, Context.MODE_PRIVATE).getInt(ACHIEVEMENT_DISTANCE_INCREMENT, 0);
    	int monsters_increment = getSharedPreferences(ACHIEVEMENT_MONSTER_INCREMENT, Context.MODE_PRIVATE).getInt(ACHIEVEMENT_MONSTER_INCREMENT, 0);
    	int resources_increment = getSharedPreferences(ACHIEVEMENT_RESOURCES_INCREMENT, Context.MODE_PRIVATE).getInt(ACHIEVEMENT_RESOURCES_INCREMENT, 0);
    	
    	//passa os valores para o Google Play Services
    	if(distance_increment > 0) {
    		Games.Achievements.increment(getApiClient(), getString(R.string.achievements_going_far), distance_increment);
        	Games.Achievements.increment(getApiClient(), getString(R.string.achievements_the_marathonist), distance_increment);
    	}
    	if(monsters_increment > 0) {
    		Games.Achievements.increment(getApiClient(), getString(R.string.achievements_slayer), monsters_increment);
        	Games.Achievements.increment(getApiClient(), getString(R.string.achievements_kill_them_all), monsters_increment);
    	}
    	if(resources_increment > 0) {
    		Games.Achievements.increment(getApiClient(), getString(R.string.achievements_the_miner), resources_increment);
        	Games.Achievements.increment(getApiClient(), getString(R.string.achievements_megalomaniac), resources_increment);
    	}
    	
    	//zera os achievements increments ap?s pass?-los para o Google Play Services
    	getSharedPreferences(ACHIEVEMENT_DISTANCE_INCREMENT, Context.MODE_PRIVATE).edit().putInt(ACHIEVEMENT_DISTANCE_INCREMENT, 0).commit();
		getSharedPreferences(ACHIEVEMENT_MONSTER_INCREMENT, Context.MODE_PRIVATE).edit().putInt(ACHIEVEMENT_MONSTER_INCREMENT, 0).commit();
		getSharedPreferences(ACHIEVEMENT_RESOURCES_INCREMENT, Context.MODE_PRIVATE).edit().putInt(ACHIEVEMENT_RESOURCES_INCREMENT, 0).commit();
    }
    
    private void ShowDialogToLogIn() {
    	mm.mainScene.aboutEntity.touchableAbout = false;
    	mm.mainDialogScene.setParameters(1, getResources().getString(R.string.sign_in_title), getResources().getString(R.string.sign_in_msg), getResources().getString(R.string.yes), getResources().getString(R.string.no), 5);
 	   	mm.mainDialogScene.show();
    }

	@Override
	public void onSignInFailed() {
		if(SceneLoaded && !isSignedIn()) {
			mm.mainScene.googlePlaySprite.setCurrentTileIndex(0);
		}
	}

	@Override
	public void onSignInSucceeded() {
		if(SceneLoaded)	{
			updateGooglePlayServices();
			mm.mainScene.googlePlaySprite.setCurrentTileIndex(1);
		}
		
		//Implement some confirmation message?
	}
	
	public void signInOrOut() {
		if(!isSignedIn()) {
     	   	mm.mainDialogScene.setParameters(1, getResources().getString(R.string.sign_in_title), getResources().getString(R.string.sign_in_msg), getResources().getString(R.string.yes), getResources().getString(R.string.no), 3);
     	   	mm.mainDialogScene.show();
		}
		else {
			mm.mainDialogScene.setParameters(1, getResources().getString(R.string.sign_out_title), getResources().getString(R.string.sign_out_msg), getResources().getString(R.string.yes), getResources().getString(R.string.no), 4);
     	   	mm.mainDialogScene.show();
		}
	}
	
	public void tweetScreenshot() {
		final ScreenCapture screenCapture = new ScreenCapture();
		mm.mainScene.attachChild(screenCapture);
		
		final int viewWidth = mRenderSurfaceView.getWidth();
		final int viewHeight = mRenderSurfaceView.getHeight();

		FileUtils.ensureDirectoriesExistOnExternalStorage(this, "");

		screenCapture.capture(viewWidth, viewHeight, Environment.getExternalStorageDirectory().toString() + "/minescape.jpg", new IScreenCaptureCallback() {
			@Override
			public void onScreenCaptured(final String pFilePath) {
				
				Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/minescape.jpg"));
	    		Intent intent = new TweetComposer.Builder(mm.activity)
	    			.text(getResources().getString(R.string.share_village_text_a) + Integer.toString(mm.mainScene.villageEntity.villageLevel) + getResources().getString(R.string.share_village_text_b))
	    			.image(uri)
	    			.createIntent();
	    		startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);
	    		
				mm.mainScene.villageEntity.shareVillageLevel.setVisible(false);
				mm.mainScene.villageEntity.touchableVillage = true;
				
			}

			@Override
			public void onScreenCaptureFailed(final String pFilePath, final Exception pException) {
				
				mm.mainScene.villageEntity.shareVillageLevel.setVisible(false);
				mm.mainScene.villageEntity.touchableVillage = true;
				
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		//resultado do login no google play
		if(requestCode == 9001) {
			if(resultCode == RESULT_CANCELED) {
				getSharedPreferences("autoSignIn", Context.MODE_PRIVATE).edit().putBoolean("autoSignIn", false).commit();
			}
		}
		
		//resultado do compartilhamento no twitter	
		if(requestCode == TWEET_COMPOSER_REQUEST_CODE) {
			
			mm.mainScene.villageEntity.touchableVillage = false;
			
			if(resultCode == RESULT_OK) {
				//calcula quantos diamantes serão dados de recompensa
				float diamonds = REWARDED_DIAMONDS;
				for(int i = 1; i < mm.mainScene.villageEntity.villageLevel; i++) {
					diamonds = diamonds * REWARD_FACTOR;
				}
				int intDiam = (int)diamonds;
				
				//soma os diamantes da recompensa
				mm.mainScene.villageEntity.itemsQuantity[4] += intDiam;
				mm.mainScene.villageEntity.itemsQuantityText[4].setText("x" + mm.mainScene.villageEntity.itemsQuantity[4]);
				
				mm.mainDialogScene.setParameters(
						0,
						getResources().getString(R.string.share_success_title),
						getResources().getString(R.string.share_success_text),
						getResources().getString(R.string.yes),
						"",
						7);
	     	   	mm.mainDialogScene.show();
			}
			else {
				mm.mainDialogScene.setParameters(
						0,
						getResources().getString(R.string.share_fail_title),
						getResources().getString(R.string.share_fail_text),
						getResources().getString(R.string.yes),
						"",
						8);
	     	   	mm.mainDialogScene.show();
			}
		}
	}
	    
	   
	   
	
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	protected int getLayoutID() {
		return R.layout.main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.SurfaceViewId;
	}

}
