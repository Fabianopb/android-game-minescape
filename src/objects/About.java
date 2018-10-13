package objects;

import managers.MainManager;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.MainScene;
import android.content.Context;

import com.visivaemobile.minescape.R;

public class About extends Entity {
	
	protected MainManager mm = MainManager.getInstance();
	protected VertexBufferObjectManager vbom = mm.vbom;
	public boolean touchableAbout = true;
	public Rectangle closeDialogRect;
	private int index;
	public TiledSprite[] achievementsSprite = new TiledSprite[8];
	private String[] achievementsString = new String[8];
	private String[] achievementsStringTitle = new String[8];
	private float overallScoreMultiplier;
	private int maxDistance;
	private int maxMonsters;
	public int overallScore;
	private Text overallScoreText;
	public Rectangle activateTutorialRect;
	private Rectangle activateTutorialRectInner;
	private boolean tutorialActivated = true;
	public Sprite achievementsSpriteDeco;
	public Sprite overallSprite;
	
	public About(float pX, float pY) {
		super(pX, pY);
		createChildren();
		registerEntityModifier(new ScaleModifier(0, 1, 0));
	}
	
	private void createChildren() {
		
		Rectangle aboutBack = new Rectangle(0, 0, 800, 480, vbom);
    	aboutBack.setColor(0.1f, 0.1f, 0.1f);
    	attachChild(aboutBack);
    	
    	closeDialogRect = new Rectangle(-375, 215, 46, 46, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(touchableAbout) {
						mm.buttonSound.play();
						hideAboutEntity();
					}
					break;
			    }
    			return false;
		    }
		};
		closeDialogRect.setColor(0.3f, 0.3f, 0.3f);
		attachChild(closeDialogRect);
		
		Text closeDialogX = new Text(-375, 215, mm.greenscrFont, "X", vbom);
		attachChild(closeDialogX);
    	
    	Text totalsTitle = new Text(0, 210, mm.greenscrFont, mm.activity.getResources().getString(R.string.about), vbom);
    	attachChild(totalsTitle);
    	
    	Text markerText = new Text(-350, 100, mm.greenscrFont, ">\n\n>", vbom);
    	attachChild(markerText);
    	
    	maxDistance = mm.activity.getSharedPreferences(MainScene.MAX_DISTANCE, Context.MODE_PRIVATE).getInt(MainScene.MAX_DISTANCE, 0);
    	Text distanceCoveredText = new Text(-320, 160, mm.bitlightFont, mm.activity.getResources().getString(R.string.totals_distance_max) + " " + maxDistance + "m", vbom);
    	distanceCoveredText.setAnchorCenterX(0);
    	attachChild(distanceCoveredText);
    	
    	maxMonsters = mm.activity.getSharedPreferences(MainScene.MAX_MONSTERS, Context.MODE_PRIVATE).getInt(MainScene.MAX_MONSTERS, 0);
    	Text totalMonstersCoveredText = new Text(-320, 130, mm.bitlightFont, mm.activity.getResources().getString(R.string.totals_monsters_max) + " " + maxMonsters, vbom);
    	totalMonstersCoveredText.setAnchorCenterX(0);
    	attachChild(totalMonstersCoveredText);
    	
    	Text gPlayAchievementsText = new Text(300, 150, mm.bitlightFont, mm.activity.getResources().getString(R.string.more_achievements), vbom);
    	attachChild(gPlayAchievementsText);
    	
    	achievementsSpriteDeco = new Sprite(300, 80, mm.gPlayAchievementsTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(touchableAbout) {
						mm.buttonSound.play();
						mm.activity.openAchievements();
					}
					break;
			    }
    			return false;
		    }
    	};
    	attachChild(achievementsSpriteDeco);
    	
    	Text achievementsText = new Text(-320, 60, mm.bitlightFont, mm.activity.getResources().getString(R.string.totals_achievements), vbom);
    	achievementsText.setAnchorCenterX(0);
    	attachChild(achievementsText);
    	
    	Text achievementsSubText = new Text(-320, 30, mm.bitlightFont, mm.activity.getResources().getString(R.string.totals_achievements_sub), vbom);
    	achievementsSubText.setAnchorCenterX(0);
    	attachChild(achievementsSubText);
    	
    	overallScoreMultiplier = 1.0f;
    	
    	achievementsString[0] = mm.activity.getResources().getString(R.string.achievements_100m);
    	achievementsString[1] = mm.activity.getResources().getString(R.string.achievements_200m);
    	achievementsString[2] = mm.activity.getResources().getString(R.string.achievements_500m);
    	achievementsString[3] = mm.activity.getResources().getString(R.string.achievements_1000m);
    	achievementsString[4] = mm.activity.getResources().getString(R.string.achievements_iron);
    	achievementsString[5] = mm.activity.getResources().getString(R.string.achievements_gold);
    	achievementsString[6] = mm.activity.getResources().getString(R.string.achievements_diamond);
    	achievementsString[7] = mm.activity.getResources().getString(R.string.achievements_obsidian);
    	
    	achievementsStringTitle[0] = mm.activity.getResources().getString(R.string.achievements_100m_title);
    	achievementsStringTitle[1] = mm.activity.getResources().getString(R.string.achievements_200m_title);
    	achievementsStringTitle[2] = mm.activity.getResources().getString(R.string.achievements_500m_title);
    	achievementsStringTitle[3] = mm.activity.getResources().getString(R.string.achievements_1000m_title);
    	achievementsStringTitle[4] = mm.activity.getResources().getString(R.string.achievements_iron_title);
    	achievementsStringTitle[5] = mm.activity.getResources().getString(R.string.achievements_gold_title);
    	achievementsStringTitle[6] = mm.activity.getResources().getString(R.string.achievements_diamond_title);
    	achievementsStringTitle[7] = mm.activity.getResources().getString(R.string.achievements_obsidian_title);
    	
    	for(index = 0; index < 4; index++) {
    		achievementsSprite[index] = new TiledSprite(-280 + 100 * index, -20, mm.achievementsTiledTexture, vbom) {
    			int achievementsIndex = index;
    			@Override
    			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    				switch(pTouchEvent.getAction()) {			
    				case TouchEvent.ACTION_UP:
    					if(touchableAbout) {
    						touchableAbout = false;
    						mm.buttonSound.play();
    						mm.mainDialogScene.setParameters(0, achievementsStringTitle[achievementsIndex], achievementsString[achievementsIndex], mm.activity.getResources().getString(R.string.yes), mm.activity.getResources().getString(R.string.no), 2);
    						mm.mainDialogScene.show();
    					}
    					break;
    			    }
        			return false;
    		    }
    		};
    		if(mm.activity.getSharedPreferences(MainScene.ACHIEVEMENTS_TAGS[index], Context.MODE_PRIVATE).getBoolean(MainScene.ACHIEVEMENTS_TAGS[index], false)) {
    			achievementsSprite[index].setCurrentTileIndex(index + MainScene.ACHIEVEMENTS_NUM);
    			overallScoreMultiplier += MainScene.ACHIEVEMENTS_MULTIPLIER[index];
    		}
    		else {
    			achievementsSprite[index].setCurrentTileIndex(index);
    		}
    		attachChild(achievementsSprite[index]);
    	}
    	
    	for(index = 4; index < 8; index++) {
    		achievementsSprite[index] = new TiledSprite(-280 + 100 * (index - 4), -80, mm.achievementsTiledTexture, vbom) {
    			int achievementsIndex = index;
    			@Override
    			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    				switch(pTouchEvent.getAction()) {			
    				case TouchEvent.ACTION_UP:
    					if(touchableAbout) {
    						touchableAbout = false;
    						mm.buttonSound.play();
    						mm.mainDialogScene.setParameters(0, achievementsStringTitle[achievementsIndex], achievementsString[achievementsIndex], mm.activity.getResources().getString(R.string.yes), mm.activity.getResources().getString(R.string.no), 2);
    						mm.mainDialogScene.show();
    					}
    					break;
    			    }
        			return false;
    		    }
    		};
    		if(mm.activity.getSharedPreferences(MainScene.ACHIEVEMENTS_TAGS[index], Context.MODE_PRIVATE).getBoolean(MainScene.ACHIEVEMENTS_TAGS[index], false)) {
    			achievementsSprite[index].setCurrentTileIndex(index + MainScene.ACHIEVEMENTS_NUM);
    			overallScoreMultiplier += MainScene.ACHIEVEMENTS_MULTIPLIER[index];
    		}
    		else {
    			achievementsSprite[index].setCurrentTileIndex(index);
    		}
    		attachChild(achievementsSprite[index]);
    	}
    	
    	overallScoreText = new Text(-320, 100, mm.bitlightFont, "", 50, vbom);
    	overallScoreText.setAnchorCenterX(0);
    	attachChild(overallScoreText);
    	
    	Text gPlayLeaderboardsText = new Text(300, -20, mm.bitlightFont, mm.activity.getResources().getString(R.string.position_leaderboard), vbom);
    	attachChild(gPlayLeaderboardsText);
    	
    	overallSprite = new Sprite(300, -90, mm.gPlayLeaderboardsTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(touchableAbout) {
						//touchableAbout = false;
						mm.buttonSound.play();
						mm.activity.openLeaderboards();
					}
					break;
			    }
    			return false;
		    }
    	};
    	attachChild(overallSprite);
    	
    	tutorialActivated = mm.activity.getSharedPreferences(MainScene.TUTORIAL_TAGS, Context.MODE_PRIVATE).getBoolean(MainScene.TUTORIAL_TAGS, true);
		activateTutorialRect = new Rectangle(-300, -200, 40, 40, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(touchableAbout) {
						mm.buttonSound.play();
						if(tutorialActivated) {
							tutorialActivated = false;
						}
						else {
							tutorialActivated = true;
						}
						activateTutorialRectInner.setVisible(tutorialActivated);
						mm.activity.getSharedPreferences(MainScene.TUTORIAL_TAGS, Context.MODE_PRIVATE).edit().putBoolean(MainScene.TUTORIAL_TAGS, tutorialActivated).commit();
					}
					break;
			    }
    			return false;
		    }
		};
		activateTutorialRect.setColor(0.3f, 0.3f, 0.3f);
		attachChild(activateTutorialRect);
		
		activateTutorialRectInner = new Rectangle(-300, -200, 30, 30, vbom);
		activateTutorialRectInner.setColor(0.0f, 0.7f, 0.2f);
		attachChild(activateTutorialRectInner);
		activateTutorialRectInner.setVisible(tutorialActivated);
		
		Text tutorialText = new Text(-270, -200, mm.bitlightFont, mm.activity.getResources().getString(R.string.tutorial), vbom);
		tutorialText.setAnchorCenterX(0);
		attachChild(tutorialText);
    	
	}
	
	public void updateOverallScore() {
		overallScore = (int) ((overallScoreMultiplier + mm.mainScene.villageEntity.villageLevel) * (maxDistance + maxMonsters));
		overallScoreText.setText(mm.activity.getResources().getString(R.string.overall_score) + " " + overallScore);
		
	}
	
	public void showAboutEntity() {
    	registerEntityModifier(new ScaleModifier(0.2f, 0, 1));
    	mm.activity.aboutEntityIsOnTop = true;
    	touchableAbout = true;
    }
    
    public void hideAboutEntity() {
    	touchableAbout = false;
    	mm.activity.aboutEntityIsOnTop = false;
    	registerEntityModifier(new ScaleModifier(0.2f, 1, 0) {
    		@Override protected void onModifierFinished(IEntity pItem) {
    			mm.mainScene.showMenu();
    		}
    	});    	
    }
}
