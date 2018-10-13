package objects;

import managers.MainManager;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.MainScene;
import android.content.Context;
import android.util.Log;

import com.visivaemobile.minescape.R;

public class Village extends Entity {
	
	protected MainManager mm = MainManager.getInstance();
	protected VertexBufferObjectManager vbom = mm.vbom;
	public int[] itemsQuantity = new int[5];
    public Text[] itemsQuantityText = new Text[5];
    private int[] itemsCost = new int[5];
    private Text[] itemsCostText = new Text[5];
    public int villageLevel;
    private Text villageLevelNum;
    public Sprite upgradeButton;
    public Rectangle closeDialogRect;
    private Sprite[] forestArea = new Sprite[4];
    public boolean touchableVillage = true;
    public Text shareVillageLevel;
	
	public Village(float pX, float pY) {
		super(pX, pY);
		createChildren();
		registerEntityModifier(new ScaleModifier(0, 1, 0));
	}
	
	private void createChildren() {
		
		villageLevel = mm.activity.getSharedPreferences(MainScene.VILLAGE_LEVEL, Context.MODE_PRIVATE).getInt(MainScene.VILLAGE_LEVEL, 0);
		
		Rectangle villageBack = new Rectangle(0, 0, 800, 480, vbom);
    	villageBack.setColor(0.1f, 0.1f, 0.1f);
    	attachChild(villageBack);
    	
    	Sprite villageSprite = new Sprite(0, 0, mm.villageTextureRegion, vbom);
    	attachChild(villageSprite);
    	
    	forestArea[0] = new Sprite(-55, 96, mm.villageForestRegion, vbom);
    	forestArea[1] = new Sprite(145, 60, mm.villageForestRegion, vbom);
    	forestArea[2] = new Sprite(-71, 8, mm.villageForestRegion, vbom);
    	forestArea[3] = new Sprite(113, -28, mm.villageForestRegion, vbom);
    	for(int i = 0; i < 4; i++) {
    		forestArea[i].setAnchorCenter(0, 0);
    		attachChild(forestArea[i]);
    	}
    	
    	for(int i = 1; i < MainScene.MAX_VILLAGE_LEVELS + 1; i++) {
    		if(i <= villageLevel) buildVillage(i);
    	}
    	
    	Rectangle villageMenu = new Rectangle(340, 25, 116, 426, vbom);
    	villageMenu.setColor(0.5f, 0.5f, 0.5f, 0.75f);
    	attachChild(villageMenu);
    	villageMenu.setZIndex(10);
    	
    	Text resourcesText = new Text(340, 220, mm.bitlightFont, mm.activity.getResources().getString(R.string.resources), vbom);
    	attachChild(resourcesText);
    	resourcesText.setZIndex(10);
    	
    	for(int i = 0; i < 5; i++) {
    		TiledSprite itemSprite = new TiledSprite(295, 160 - i * 50, mm.itemsTiledTexture, vbom);
    		itemSprite.setCurrentTileIndex(i);
    		itemSprite.setAnchorCenterX(0);
    		attachChild(itemSprite);
    		itemSprite.setZIndex(10);
    		
    		//TODO: voltar quantidade ao normal
    		itemsQuantity[i] = 999;mm.activity.getSharedPreferences(MainScene.ITEMS_TAGS[i], Context.MODE_PRIVATE).getInt(MainScene.ITEMS_TAGS[i], 0);
			itemsQuantityText[i] = new Text(330, 160 - i * 50, mm.bitlightFont, "x" + itemsQuantity[i], 6, vbom);
			itemsQuantityText[i].setAnchorCenterX(0);
			attachChild(itemsQuantityText[i]);
			itemsQuantityText[i].setZIndex(10);
    	}
    	
    	Text villageLevelText = new Text(340, -100, mm.bitlightFont, mm.activity.getResources().getString(R.string.village_level), vbom);
    	attachChild(villageLevelText);
    	villageLevelText.setZIndex(10);
    	
    	villageLevelNum = new Text(340, -150, mm.greenscrFont, Integer.toString(villageLevel), 2, vbom);
    	attachChild(villageLevelNum);
    	villageLevelNum.setZIndex(10);
    	
    	Rectangle bottomRect = new Rectangle(0, -215, 796, 46, vbom);
    	bottomRect.setColor(0.5f, 0.5f, 0.5f, 0.75f);
    	attachChild(bottomRect);
    	bottomRect.setZIndex(10);
    	
    	upgradeButton = new Sprite(320, -215, mm.buttonTexture, vbom) {
    		@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
    			switch(pTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					if(touchableVillage) {
						mm.buttonSound.play();
						if(villageLevel < MainScene.MAX_VILLAGE_LEVELS) {
							touchableVillage = false;
							boolean hasEnoughResources = true;
							for(int i = 0; i < 5; i++) {
								if(itemsCost[i] > itemsQuantity[i]) {
									hasEnoughResources = false;
									break;
								}
							}
							if(hasEnoughResources) {
								mm.mainDialogScene.setParameters(1, mm.activity.getResources().getString(R.string.village_title), mm.activity.getResources().getString(R.string.village_message), mm.activity.getResources().getString(R.string.yes), mm.activity.getResources().getString(R.string.no), 0);
								mm.mainDialogScene.show();
							}
							else {
								mm.activity.toastNoResources.show();
								touchableVillage = true;
							}
							
							break;
						}
						else mm.activity.toastNoUpdates.show();
	    			}					
			    }
    			return false;
		    }
    	};
    	upgradeButton.setScale(0.5f);
    	attachChild(upgradeButton);
    	upgradeButton.setZIndex(10);
    	
    	Text upgradeText = new Text(320, -220, mm.bitlightFont, mm.activity.getResources().getString(R.string.upgrade), vbom);
    	attachChild(upgradeText);
    	upgradeText.setZIndex(10);
    	
    	Text nextLevelCost = new Text(-390, -220, mm.bitlightFont, mm.activity.getResources().getString(R.string.upgrade_cost), vbom);
    	nextLevelCost.setAnchorCenterX(0);
    	attachChild(nextLevelCost);
    	nextLevelCost.setZIndex(10);
    	
    	for(int i = 0; i < 5; i++) {
    		TiledSprite itemCostSprite = new TiledSprite(nextLevelCost.getX() + nextLevelCost.getWidth() + 10 + i * 100, -220, mm.itemsTiledTexture, vbom);
    		itemCostSprite.setCurrentTileIndex(i);
    		itemCostSprite.setAnchorCenterX(0);
    		attachChild(itemCostSprite);
    		itemCostSprite.setZIndex(10);
    		
    		if(villageLevel >= MainScene.MIN_LEVEL_TO_CHARGE[i]) itemsCost[i] = MainScene.COST_FACTOR[i] * (villageLevel + 1 - MainScene.MIN_LEVEL_TO_CHARGE[i]);
    		else itemsCost[i] = 0;
    		
    		if(villageLevel < MainScene.MAX_VILLAGE_LEVELS)
    			itemsCostText[i] = new Text(nextLevelCost.getX() + nextLevelCost.getWidth() + 42 + i * 100, -220, mm.bitlightFont, "x" + itemsCost[i], 6, vbom);
    		else
    			itemsCostText[i] = new Text(nextLevelCost.getX() + nextLevelCost.getWidth() + 42 + i * 100, -220, mm.bitlightFont, "---", 6, vbom);
    		itemsCostText[i] = new Text(nextLevelCost.getX() + nextLevelCost.getWidth() + 42 + i * 100, -220, mm.bitlightFont, "x" + itemsCost[i], 6, vbom);
    		itemsCostText[i].setAnchorCenterX(0);
    		attachChild(itemsCostText[i]);
    		itemsCostText[i].setZIndex(10);
    	}
    	
    	closeDialogRect = new Rectangle(-375, 215, 46, 46, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(touchableVillage) {
						mm.buttonSound.play();
						hideVillageEntity();
					}
					break;
			    }
    			return false;
		    }
		};
		closeDialogRect.setColor(0.3f, 0.3f, 0.3f);
		attachChild(closeDialogRect);
		closeDialogRect.setZIndex(10);
		
		Text closeDialogX = new Text(-375, 215, mm.greenscrFont, "X", vbom);
		attachChild(closeDialogX);
		closeDialogX.setZIndex(10);
		
		shareVillageLevel = new Text(0, 0, mm.greenscrFont, "LEVEL " + "0000", vbom);
		shareVillageLevel.setVisible(false);
    	attachChild(shareVillageLevel);
    	shareVillageLevel.setZIndex(10);
		
		sortChildren();
    	
	}
	
	public void upgradeVillage() {
		
		Log.v("TAG", "entrou no upgrade");
		
		villageLevel++;
		mm.activity.getSharedPreferences(MainScene.VILLAGE_LEVEL, Context.MODE_PRIVATE).edit().putInt(MainScene.VILLAGE_LEVEL, villageLevel).commit();
		
		villageLevelNum.setText(Integer.toString(villageLevel));
		shareVillageLevel.setText("LEVEL " + Integer.toString(villageLevel));
		
		for(int i = 0; i < 5; i++) {
			itemsQuantity[i] -= itemsCost[i];
			mm.activity.getSharedPreferences(MainScene.ITEMS_TAGS[i], Context.MODE_PRIVATE).edit().putInt(MainScene.ITEMS_TAGS[i], itemsQuantity[i]).commit();
			
			if(villageLevel >= MainScene.MIN_LEVEL_TO_CHARGE[i]) itemsCost[i] = MainScene.COST_FACTOR[i] * (villageLevel + 1 - MainScene.MIN_LEVEL_TO_CHARGE[i]);
    		else itemsCost[i] = 0;
			
			itemsQuantityText[i].setText("x" + itemsQuantity[i]);
			itemsCostText[i].setText("x" + itemsCost[i]);
			
		}
		
		buildVillage(villageLevel);
		
		sortChildren();
		
		mm.mainScene.aboutEntity.updateOverallScore();
		
		mm.activity.updateLeaderboards();
		
		Log.v("TAG", "acabou o upgrade");
	}
	
	
	private void buildVillage(int level) {
		
		if(level == 1) {
			Sprite level001Sprite = new Sprite(49, -104, mm.villageLevel001Region, vbom);
			level001Sprite.setAnchorCenter(0, 0);
			attachChild(level001Sprite);
		}
		if(level == 2) {
			Sprite level002Sprite = new Sprite(88, -129, mm.villageLevel002Region, vbom);
			level002Sprite.setAnchorCenter(0, 0);
			attachChild(level002Sprite);
		}
		if(level == 3) {
			Sprite level003Sprite = new Sprite(49, -80, mm.villageLevel003Region, vbom);
			level003Sprite.setAnchorCenter(0, 0);
			attachChild(level003Sprite);
		}
		if(level == 4) {
			Sprite level004Sprite = new Sprite(1, -68, mm.villageLevel004Region, vbom);
			level004Sprite.setAnchorCenter(0, 0);
			attachChild(level004Sprite);
		}
		if(level == 5) {
			Sprite level005Sprite = new Sprite(-16, -22, mm.villageLevel005Region, vbom);
			level005Sprite.setAnchorCenter(0, 0);
			attachChild(level005Sprite);
		}
		if(level == 6) {
			Sprite level006Sprite = new Sprite(113, -180, mm.villageLevel006Region, vbom);
			level006Sprite.setAnchorCenter(0, 0);
			attachChild(level006Sprite);
		}
		if(level == 7) {
			Sprite level007Sprite = new Sprite(-111, -84, mm.villageLevel007Region, vbom);
			level007Sprite.setAnchorCenter(0, 0);
			attachChild(level007Sprite);
		}
		if(level == 8) {
			Sprite level008Sprite = new Sprite(24, -140, mm.villageLevel008Region, vbom);
			level008Sprite.setAnchorCenter(0, 0);
			attachChild(level008Sprite);
		}
		if(level == 9) {
			Sprite level009Sprite = new Sprite(-21, -108, mm.villageLevel009Region, vbom);
			level009Sprite.setAnchorCenter(0, 0);
			attachChild(level009Sprite);
			
			Smoke chimney01 = new Smoke(new PointParticleEmitter(90, -37), 10, 20, 150, mm.villageSmokeRegion, mm.vbom);
			attachChild(chimney01);
			chimney01.setZIndex(5);
			
			Smoke chimney02 = new Smoke(new PointParticleEmitter(202, -37), 10, 20, 150, mm.villageSmokeRegion, mm.vbom);
			attachChild(chimney02);
			chimney02.setZIndex(5);
		}
		if(level == 10) {
			Sprite level010Sprite = new Sprite(-128, -163, mm.villageLevel010Region, vbom);
			level010Sprite.setAnchorCenter(0, 0);
			attachChild(level010Sprite);
		}
		if(level == 11) {
			Sprite level011Sprite = new Sprite(-128, -185, mm.villageLevel011Region, vbom);
			level011Sprite.setAnchorCenter(0, 0);
			attachChild(level011Sprite);
		}
		if(level == 12) {
			Sprite level012Sprite = new Sprite(-23, -228, mm.villageLevel012Region, vbom);
			level012Sprite.setAnchorCenter(0, 0);
			attachChild(level012Sprite);
		}
		if(level == 13) {
			Sprite level013aSprite = new Sprite(-343, -68, mm.villageLevel013aRegion, vbom);
			level013aSprite.setAnchorCenter(0, 0);
			attachChild(level013aSprite);
			
			Sprite level013bSprite = new Sprite(17, -176, mm.villageLevel013bRegion, vbom);
			level013bSprite.setAnchorCenter(0, 0);
			attachChild(level013bSprite);
		}
		if(level == 14) {
			Sprite level014Sprite = new Sprite(-240, -148, mm.villageLevel014Region, vbom);
			level014Sprite.setAnchorCenter(0, 0);
			attachChild(level014Sprite);
		}
		if(level == 15) {
			Sprite level015aSprite = new Sprite(-343, 33, mm.villageLevel015aRegion, vbom);
			level015aSprite.setAnchorCenter(0, 0);
			attachChild(level015aSprite);
			
			Sprite level015bSprite = new Sprite(-311, -92, mm.villageLevel015bRegion, vbom);
			level015bSprite.setAnchorCenter(0, 0);
			attachChild(level015bSprite);
			
			Sprite level015cSprite = new Sprite(17, -147, mm.villageLevel015aRegion, vbom);
			level015cSprite.setAnchorCenter(0, 0);
			attachChild(level015cSprite);
		}
		if(level == 16) {
			Sprite level016aSprite = new Sprite(-127, -203, mm.villageLevel016aRegion, vbom);
			level016aSprite.setAnchorCenter(0, 0);
			attachChild(level016aSprite);
			
			Sprite level016bSprite = new Sprite(97, -240, mm.villageLevel016bRegion, vbom);
			level016bSprite.setAnchorCenter(0, 0);
			attachChild(level016bSprite);
		}
		if(level == 17) {
			forestArea[3].setVisible(false);
			
			Sprite level017Sprite = new Sprite(201, -132, mm.villageLevel017Region, vbom);
			level017Sprite.setAnchorCenter(0, 0);
			attachChild(level017Sprite);
			
			Smoke chimney03 = new Smoke(new PointParticleEmitter(250, -61), 10, 20, 150, mm.villageSmokeRegion, mm.vbom);
			attachChild(chimney03);
			chimney03.setZIndex(5);
		}
		if(level == 18) {
			Sprite level018Sprite = new Sprite(-15, -47, mm.villageLevel018Region, vbom);
			level018Sprite.setAnchorCenter(0, 0);
			attachChild(level018Sprite);
			
			Smoke chimney04 = new Smoke(new PointParticleEmitter(90, 35), 10, 20, 150, mm.villageSmokeRegion, mm.vbom);
			attachChild(chimney04);
			chimney04.setZIndex(5);
		}
		if(level == 19) {
			Sprite level019aSprite = new Sprite(-247, -24, mm.villageLevel019aRegion, vbom);
			level019aSprite.setAnchorCenter(0, 0);
			attachChild(level019aSprite);
			
			Sprite level019bSprite = new Sprite(137, -240, mm.villageLevel019bRegion, vbom);
			level019bSprite.setAnchorCenter(0, 0);
			attachChild(level019bSprite);
		}
		if(level == 20) {
			forestArea[2].setVisible(false);
			
			Sprite level020Sprite = new Sprite(-95, -23, mm.villageLevel020Region, vbom);
			level020Sprite.setAnchorCenter(0, 0);
			attachChild(level020Sprite);
		}
	}
	
	public void showVillageEntity() {
    	registerEntityModifier(new ScaleModifier(0.2f, 0, 1));
    	mm.activity.villageEntityIsOnTop = true;
    	touchableVillage = true;
    }
    
    public void hideVillageEntity() {
    	touchableVillage = false;
    	mm.activity.villageEntityIsOnTop = false;
    	registerEntityModifier(new ScaleModifier(0.2f, 1, 0) {
    		@Override protected void onModifierFinished(IEntity pItem) {
    			mm.mainScene.showMenu();
    		}
    	});    	
    }

}
