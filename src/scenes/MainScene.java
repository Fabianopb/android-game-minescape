package scenes;

import managers.MainManager;
import objects.About;
import objects.Credits;
import objects.Village;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseBackIn;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseCircularIn;
import org.andengine.util.modifier.ease.EaseCircularOut;

import android.content.Intent;
import android.net.Uri;

import com.visivaemobile.minescape.GameActivity;
import com.visivaemobile.minescape.MainActivity;
import com.visivaemobile.minescape.R;

public class MainScene extends Scene {
	
	protected Engine engine;
    protected MainActivity activity;
    protected MainManager mm;
    protected VertexBufferObjectManager vbom;
    protected Camera camera;
    
    //variáveis do jogo
    public static final String[] ITEMS_TAGS = { "woodQty", "stoneQty", "ironQty", "goldQty", "diamondQty" };
    public static final String VILLAGE_LEVEL = "VillageLevel";
    public static final String MAX_DISTANCE = "maxDistance";
    public static final String MAX_MONSTERS = "maxMonsters";
    public static final String TOTAL_DISTANCE = "totalDistance";
    public static final String OVERALL_SCORE = "overallScore";
    public static final int ACHIEVEMENTS_NUM = 8;
    public static final String[] ACHIEVEMENTS_TAGS = {"warmUp", "dontStop", "itsSerious", "endurance", "ironWarrior", "goldWarrior", "diamondWarrior", "obsidianWarrior"};
    public static final String TUTORIAL_TAGS = "tutorialActivated";
    
    public static final float[] ACHIEVEMENTS_MULTIPLIER = {0.1f, 0.2f, 0.5f, 1.0f, 0.3f, 0.6f, 0.9f, 1.2f};
    public static final int MAX_VILLAGE_LEVELS = 20;
    public static final int[] MIN_LEVEL_TO_CHARGE = {0, 3, 5, 8, 12};
    public static final int[] COST_FACTOR = {16, 9, 5, 3, 2};
    
    public MainScene() {
        this.mm = MainManager.getInstance();
        this.engine = mm.engine;
        this.activity = mm.activity;
        this.vbom = mm.vbom;
        this.camera = mm.camera;
        createScene();
    }
    
    public void createScene() {
    	mm.loadMainResources();
    	mm.createMusic();
    	mm.createSoundEffects();
    	createBackground();
    	createButtons();
    	
    	mm.loadVillageResources();
    	createVillageEntity();
    	createAboutEntity(villageEntity.villageLevel);
    	createCreditsEntity();
    	
    	mm.createMainDialogScene();
    }
    
    public Sprite logoSprite;
    public boolean touchable = false;
    private void createBackground() {
    	
    	AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0.4f, 0.6f, 1.0f, 5);
		setBackground(autoParallaxBackground);
    	
		Sprite cloudsSprite = new Sprite(0, 260, mm.cloudsTexture, vbom);
		cloudsSprite.setAnchorCenter(0, 0);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-2.0f, cloudsSprite));
		
    	Sprite background02Sprite = new Sprite(0, 135, mm.background02Texture, vbom);
    	background02Sprite.setAnchorCenter(0, 0);
    	attachChild(background02Sprite);
    	
    	Sprite background01Sprite = new Sprite(0, 0, mm.background01Texture, vbom);
    	background01Sprite.setAnchorCenter(0, 0);
    	attachChild(background01Sprite);
    	
    	Sprite foregroundSprite = new Sprite(0, 0, mm.foregroundTexture, vbom);
    	foregroundSprite.setAnchorCenter(0, 0);
    	attachChild(foregroundSprite);
    	
    	logoSprite = new Sprite(400, 390, mm.logoTexture, vbom);
    	logoSprite.setScale(0);
    	attachChild(logoSprite);
    }
    
    public Entity playButtonEntity;
    public Entity villageButtonEntity;
    public Entity aboutButtonEntity;
    public Entity creditsButtonEntity;
    public Entity likeShareRateButtonEntity;
    public TiledSprite googlePlaySprite;
    private void createButtons() {
    	//Play Button
    	playButtonEntity = new Entity(-200, 270);
    	
    	Sprite playButtonSprite = new Sprite(0, 0, mm.buttonTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if(touchable) {
		        	switch(pTouchEvent.getAction()) {			
					case TouchEvent.ACTION_UP:
						touchable = false;
						mm.buttonSound.play();
						fadeScreenToGame();
						break;
				    }
		        }
    			return false;
		    }
    	};
    	playButtonEntity.attachChild(playButtonSprite);
    	registerTouchArea(playButtonSprite);
    	
    	Text playButtonText = new Text(0, 0, mm.greenscrFont, activity.getResources().getString(R.string.play), vbom);
    	playButtonEntity.attachChild(playButtonText);
    	
    	attachChild(playButtonEntity);
    	
    	//Village Button
    	villageButtonEntity = new Entity(-200, 170);
    	
    	Sprite villageButtonSprite = new Sprite(0, 0, mm.buttonTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    			if(touchable) {
		        	switch(pTouchEvent.getAction()) {			
					case TouchEvent.ACTION_UP:
						mm.buttonSound.play();
						hideMenu(0);
						break;
				    }
		        }
    			return false;
		    }
    	};
    	villageButtonEntity.attachChild(villageButtonSprite);
    	registerTouchArea(villageButtonSprite);
    	
    	Text villageButtonText = new Text(0, 0, mm.greenscrFont, activity.getResources().getString(R.string.village), vbom);
    	villageButtonEntity.attachChild(villageButtonText);
    	
    	attachChild(villageButtonEntity);
    	
    	//About Button
    	aboutButtonEntity = new Entity(-200, 70);
    	
    	Sprite aboutButtonSprite = new Sprite(0, 0, mm.buttonTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    			if(touchable) {
		        	switch(pTouchEvent.getAction()) {			
					case TouchEvent.ACTION_UP:
						mm.buttonSound.play();
						hideMenu(1);
						break;
				    }
		        }
    			return false;
		    }
    	};
    	aboutButtonEntity.attachChild(aboutButtonSprite);
    	registerTouchArea(aboutButtonSprite);
    	
    	Text aboutButtonText = new Text(0, 0, mm.greenscrFont, activity.getResources().getString(R.string.about), vbom);
    	aboutButtonEntity.attachChild(aboutButtonText);
    	
    	attachChild(aboutButtonEntity);
    	
    	//Credits Button
    	creditsButtonEntity = new Entity(0, 0);
    	
    	Rectangle creditsRect = new Rectangle(0, 0, 120, 40, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    			if(touchable) {
		        	switch(pTouchEvent.getAction()) {			
					case TouchEvent.ACTION_UP:
						mm.buttonSound.play();
						hideMenu(2);
						break;
				    }
		        }
    			return false;
		    }
    	};
    	creditsRect.setAnchorCenter(0, 0);
    	creditsRect.setColor(0.1f, 0.1f, 0.1f);
    	creditsButtonEntity.attachChild(creditsRect);
    	registerTouchArea(creditsRect);
    	
    	Text creditsText = new Text(60, 20, mm.bitlightFont, activity.getResources().getString(R.string.credits), vbom);
    	creditsButtonEntity.attachChild(creditsText);
    	
    	attachChild(creditsButtonEntity);
    	
    	likeShareRateButtonEntity = new Entity(900, 120);
    	
    	Sprite shareSprite = new Sprite(10, 170, mm.shareTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		           if(touchable && pTouchEvent.isActionUp()) {
		        	   touchable = false;
		        	   mm.buttonSound.play();
		        	   Intent shareIntent = new Intent(Intent.ACTION_SEND);
		        	   shareIntent.setType("text/plain");
		        	   shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.visivaemobile.minescape");
		        	   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.share_text));
	                   mm.activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getString(R.string.share_title)));
		        	   touchable = true;
		           }
		           return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		    }
    	};
    	likeShareRateButtonEntity.attachChild(shareSprite);
    	registerTouchArea(shareSprite);
    	
    	Sprite fbLikeSprite = new Sprite(10, 90, mm.fbLikeTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		           if(touchable && pTouchEvent.isActionUp()) {
		        	   touchable = false;
		        	   mm.buttonSound.play();
		        	   Intent likeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/visivaemobile"));
		        	   mm.activity.startActivity(likeIntent);
		        	   touchable = true;
		           }
		           return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		    }
    	};
    	likeShareRateButtonEntity.attachChild(fbLikeSprite);
    	registerTouchArea(fbLikeSprite);
    	
    	Sprite rateSprite = new Sprite(10, 10, mm.rateTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		           if(touchable && pTouchEvent.isActionUp()) {
		        	   touchable = false;
		        	   mm.buttonSound.play();
		        	   Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.visivaemobile.minescape"));
		        	   mm.activity.startActivity(rateIntent);
		        	   
		        	   touchable = true;
		           }
		           return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		    }
    	};
    	likeShareRateButtonEntity.attachChild(rateSprite);
    	registerTouchArea(rateSprite);
    	
    	googlePlaySprite = new TiledSprite(10, -70, mm.googlePlayTiledTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		           if(touchable && pTouchEvent.isActionUp()) {
		        	   
		        	   touchable = false;
		        	   mm.buttonSound.play();
		        	   
		        	   activity.signInOrOut();		        	   
		           }
		           return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
		    }
    	};
    	likeShareRateButtonEntity.attachChild(googlePlaySprite);
    	registerTouchArea(googlePlaySprite);
    	
    	attachChild(likeShareRateButtonEntity);
    }
    
    public void showMenu() {
    	logoSprite.registerEntityModifier(new ScaleModifier(0.5f, 0, 1, EaseCircularIn.getInstance()));
    	creditsButtonEntity.registerEntityModifier(new MoveModifier(0.5f, 0, -40, 0, 0));
    	likeShareRateButtonEntity.registerEntityModifier(new MoveModifier(0.5f, 900, 120, 700, 120));
    	playButtonEntity.registerEntityModifier(new MoveModifier(0.2f, -200, 270, 400, 270, EaseBackOut.getInstance()) {
    		@Override protected void onModifierFinished(IEntity pItem) {
    			villageButtonEntity.registerEntityModifier(new MoveModifier(0.2f, -200, 170, 400, 170, EaseBackOut.getInstance()) {
    	    		@Override protected void onModifierFinished(IEntity pItem) {
    	    			aboutButtonEntity.registerEntityModifier(new MoveModifier(0.2f, -200, 70, 400, 70, EaseBackOut.getInstance()) {
    	    	    		@Override protected void onModifierFinished(IEntity pItem) {
    	    	    			touchable = true;
    	    	    		}
    	    	    	});
    	    		}
    	    	});
    		}
    	});
    }
    
    private void hideMenu(final int show) {
    	touchable = false;
    	logoSprite.registerEntityModifier(new ScaleModifier(0.5f, 1, 0, EaseCircularOut.getInstance()));
    	creditsButtonEntity.registerEntityModifier(new MoveModifier(0.5f, 0, 0, 0, -40));
    	likeShareRateButtonEntity.registerEntityModifier(new MoveModifier(0.5f, 700, 120, 900, 120));
    	playButtonEntity.registerEntityModifier(new MoveModifier(0.2f, 400, 270, -200, 270, EaseBackIn.getInstance()) {
    		@Override protected void onModifierFinished(IEntity pItem) {
    			villageButtonEntity.registerEntityModifier(new MoveModifier(0.2f, 400, 170, -200, 170, EaseBackIn.getInstance()) {
    	    		@Override protected void onModifierFinished(IEntity pItem) {
    	    			aboutButtonEntity.registerEntityModifier(new MoveModifier(0.2f, 400, 70, -200, 70, EaseBackIn.getInstance()) {
    	    	    		@Override protected void onModifierFinished(IEntity pItem) {
    	    	    			if(show == 0) villageEntity.showVillageEntity();
    	    	    			else if(show == 1) aboutEntity.showAboutEntity();
    	    	    			else if(show == 2) creditsEntity.showCreditsEntity();
    	    	    		}
    	    	    	});
    	    		}
    	    	});
    		}
    	});
    }
    
    private void fadeScreenToGame() {
    	
    	Rectangle coverScreen = new Rectangle(400, 240, 800, 480, vbom);
    	coverScreen.setColor(0.1f, 0.1f, 0.1f, 0.0f);
    	attachChild(coverScreen);
    	coverScreen.registerEntityModifier(new AlphaModifier(1, 0, 1) {
    		@Override
            protected void onModifierFinished(IEntity pItem) {
                    super.onModifierFinished(pItem);
                    Intent gameIntent = new Intent(activity, GameActivity.class);
             	   	activity.startActivity(gameIntent);
             	   	activity.finish();
             	   	android.os.Process.killProcess(android.os.Process.myPid());
    		}
    	});
    }
    
    public Village villageEntity;
    private void createVillageEntity() {    	
    	villageEntity = new Village(400, 240);
    	attachChild(villageEntity);
    	registerTouchArea(villageEntity.upgradeButton);
    	registerTouchArea(villageEntity.closeDialogRect);
    }
    
    public About aboutEntity;
    private void createAboutEntity(int vLevel) {
    	aboutEntity = new About(400, 240);
    	attachChild(aboutEntity);
    	registerTouchArea(aboutEntity.closeDialogRect);
    	registerTouchArea(aboutEntity.activateTutorialRect);
    	registerTouchArea(aboutEntity.achievementsSpriteDeco);
    	registerTouchArea(aboutEntity.overallSprite);
    	for(int i = 0; i < 8; i++) registerTouchArea(aboutEntity.achievementsSprite[i]);
    }
    
    public Credits creditsEntity;
    private void createCreditsEntity() {
    	creditsEntity = new Credits(400, 240);
    	attachChild(creditsEntity);
    	registerTouchArea(creditsEntity.closeDialogRect);
    }

}
