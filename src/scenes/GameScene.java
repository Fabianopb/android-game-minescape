package scenes;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import managers.GameManager;
import objects.CraftingTable;
import objects.Creeper;
import objects.DroppedItems;
import objects.Enemies;
import objects.MapPiece;
import objects.Ore;
import objects.Steve;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseElasticIn;
import org.andengine.util.modifier.ease.EaseElasticOut;

import android.content.Context;
import android.content.Intent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.visivaemobile.minescape.GameActivity;
import com.visivaemobile.minescape.MainActivity;
import com.visivaemobile.minescape.R;

public class GameScene extends Scene implements IOnSceneTouchListener {
	
	protected Engine engine;
    protected GameActivity activity;
    protected GameManager gm;
    protected VertexBufferObjectManager vbom;
    protected BoundCamera camera;
    
    //Shared Preferences TAGs
    private static final String[] ITEMS_TAGS = { "woodQty", "stoneQty", "ironQty", "goldQty", "diamondQty" };
    private static final String MAX_DISTANCE = "maxDistance";
    private static final String MAX_MONSTERS = "maxMonsters";
    private static final String TOTAL_DISTANCE = "totalDistance";
    private static final String[] ACHIEVEMENTS_TAGS = {"warmUp", "dontStop", "itsSerious", "endurance", "ironWarrior", "goldWarrior", "diamondWarrior", "obsidianWarrior"};
    public static final String TUTORIAL_TAGS = "tutorialActivated";
    private static final String ACHIEVEMENT_DISTANCE_INCREMENT = "AchievementDistanceIncrement";
    private static final String ACHIEVEMENT_MONSTER_INCREMENT = "AchievementMonsterIncrement";
    private static final String ACHIEVEMENT_RESOURCES_INCREMENT = "AchievementResourcesIncrement";
    
    //variáveis do jogo
    public static final int MAP_PIECES = 6;
    public static final int JUMP_TOLERANCE = 25;
    private static final int PROB_ENEMIES_MAX = 80;
    private static final int PROB_ENEMIES_MIN = 20;
    private static final int DISTANCE_TO_INCREASE_ENEMIES_PROB = 1500;
    private static final float STARTING_SPEED = 5.0f;
    private static final int DISTANCE_TO_INCREASE_PLAYER_SPEED = 8000;
    private static final float SPEED_INCREMENT = 0.5f;
    private static final int ORE_PROBABILITY = 40;
    private static final int CRAFTING_PROBABILITY = ORE_PROBABILITY + 5;
    	//probabilidade dos monstros, soma 100 (SKELETON = 20)
    private static final int ZOMBIE_PROBABILITY = 30;
    private static final int SPIDER_PROBABILITY = ZOMBIE_PROBABILITY + 50;
    private static final int CREEPER_PROBABILITY = 70;
    public static final int DROP_BEEF_PROBABILITY = 30;
    public static final int DROP_IRON_SWORD_PROBABILITY = DROP_BEEF_PROBABILITY + 4;
    public static final int DROP_GOLD_SWORD_PROBABILITY = DROP_IRON_SWORD_PROBABILITY + 3;
    public static final int DROP_DIAMOND_SWORD_PROBABILITY = DROP_GOLD_SWORD_PROBABILITY + 2;
    public static final int DROP_OBSIDIAN_SWORD_PROBABILITY = DROP_DIAMOND_SWORD_PROBABILITY + 1;
    public static final int DROP_IRON_ARMOR_PROBABILITY = DROP_OBSIDIAN_SWORD_PROBABILITY + 4;
    public static final int DROP_GOLD_ARMOR_PROBABILITY = DROP_IRON_ARMOR_PROBABILITY + 3;
    public static final int DROP_DIAMOND_ARMOR_PROBABILITY = DROP_GOLD_ARMOR_PROBABILITY + 2;
    public static final int DROP_OBSIDIAN_ARMOR_PROBABILITY = DROP_DIAMOND_ARMOR_PROBABILITY + 1;
    	//ataque dos monstros
    private static final int MONSTERS_ATTACK = 20;
    public static final int CREEPER_ATTACK = 70;
    	//durabilidade e saúde dos minerais e inimigos, respectivamente
    private static final int WOOD_RESISTANCE = 3;
    private static final int COBBLESTONE_RESISTANCE = 6;
    private static final int IRON_RESISTANCE = 10;
    private static final int GOLD_RESISTANCE = 12;
    private static final int DIAMOND_RESISTANCE = 15;
    private static final int ZOMBIE_HEALTH = 7;
    private static final int SPIDER_HEALTH = 5;
    private static final int SKELETON_HEALTH = 10;
		//probabilidade dos itens, soma 100 (DIAMOND = 5)
    private static final int WOOD_PROBABILITY = 35;
    private static final int STONE_PROBABILITY = WOOD_PROBABILITY + 20; //35 a 54
    private static final int IRON_PROBABILITY = STONE_PROBABILITY + 25;	//55 a 79
    private static final int GOLD_PROBABILITY = IRON_PROBABILITY + 12;	//80 a 91
    	//força das armas
    public static final int HAND_STRENGTH = 1;
    public static final int IRON_SWORD_STRENGHT = 3;
    public static final int GOLD_SWORD_STRENGHT = 5;
    public static final int DIAMOND_SWORD_STRENGHT = 6;
    public static final int OBSIDIAN_SWORD_STRENGHT = 8;
    	//resistência das armas
    private static final int[] SWORD_RESISTANCE = {80, 80, 100, 100};
    	//amortecimento das armaduras
    public static final float NO_ARMOR_IMPACT = 0.0f;
    public static final float IRON_ARMOR_IMPACT = 0.2f;
    public static final float GOLD_ARMOR_IMPACT = 0.3f;
    public static final float DIAMOND_ARMOR_IMPACT = 0.4f;
    public static final float OBSIDIAN_ARMOR_IMPACT = 0.6f;
    	//resistência das armaduras
    private static final int[] ARMOR_RESISTANCE = {10, 12, 14, 18};
    	//custo das armas
    private static final int[][] SWORD_COST = {{3, 1, 0}, {1, 5, 0}, {2, 2, 4}, {5, 8, 5}};
    	//custo das armaduras
    private static final int[][] ARMOR_COST = {{5, 2, 0}, {2, 7, 0}, {2, 3, 6}, {8, 10, 8}};
    	//nome das armas
    private static final String[] SWORD_NAMES = new String[4]; 
    	//nome das armaduras
    private static final String[] ARMOR_NAMES = new String[4];
    
    
    public GameScene() {
        this.gm = GameManager.getInstance();
        this.engine = gm.engine;
        this.activity = gm.activity;
        this.vbom = gm.vbom;
        this.camera = gm.camera;
        createScene();
    }
    
    public void createScene() {
    	//carrega recursos
    	gm.loadScenarioResources();
    	gm.loadGameResources();
    	gm.createSoundEffects();
    	//cria a cena
    	setCameraAndPhysics();
    	createBackground();
    	createSteve();
    	createIterators();
    	createHUD();
    	gm.createGameDialogScene();
    	//registra o runtime do jogo
    	registerUpdateHandler(runtimeHandler);
    }
    
    private void createBackground() {
    	
    	final ParallaxBackground parallaxBG = new ParallaxBackground(0.4f, 0.6f, 1.0f) {
			public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	            this.setParallaxValue(camera.getCenterX() / 10.0f);
			}
		};
		setBackground(parallaxBG);
		
		final Sprite cloudSprite = new Sprite(0, 260, gm.backgroundClouds, vbom);
		cloudSprite.setAnchorCenter(0, 0);
		parallaxBG.attachParallaxEntity(new ParallaxEntity(-1.0f, cloudSprite));
		
		final Sprite parallaxBackSprite02 = new Sprite(0, 135, gm.background02Texture, vbom);
		parallaxBackSprite02.setAnchorCenter(0, 0);
		parallaxBG.attachParallaxEntity(new ParallaxEntity(-2.0f, parallaxBackSprite02));

		final Sprite parallaxBackSprite01 = new Sprite(0, 0, gm.background01Texture, vbom);
		parallaxBackSprite01.setAnchorCenter(0, 0);
		parallaxBG.attachParallaxEntity(new ParallaxEntity(-4.0f, parallaxBackSprite01));
		
    }
    
    public PhysicsWorld physicsWorld;
    private void setCameraAndPhysics() {
		camera.setBounds(0, 0, 2400, 480);
		camera.setBoundsEnabled(true);
		
		this.setOnSceneTouchListener(this);
		
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -12), false);
	    physicsWorld.setContactListener(contactListener());
	    this.registerUpdateHandler(physicsWorld);
	    
	    //TODO: DEBUG RENDERER
	    /*DebugRenderer dr = new DebugRenderer(physicsWorld, vbom);
	    dr.setZIndex(1000);
	    this.attachChild(dr);*/
	}
    
    public Steve steve;
    private void createSteve() {
		steve = new Steve(0, 120, vbom, camera, physicsWorld);
		attachChild(steve);
		steve.setZIndex(10);
	}
    
    private LinkedList<MapPiece> mapPieceLL;
	private LinkedList<MapPiece> mapPiecesToBeAdded;
	
	private LinkedList<Enemies> enemiesLL;
	private LinkedList<Enemies> enemiessToBeAdded;
	
	private LinkedList<DroppedItems> droppedItemsLL;
	private LinkedList<DroppedItems> droppedItemssToBeAdded;

	private LinkedList<Ore> oreLL;
	private LinkedList<Ore> oresToBeAdded;
	
	private void createIterators() {
		
		mapPieceLL = new LinkedList<MapPiece>();
		mapPiecesToBeAdded = new LinkedList<MapPiece>();
		
		enemiesLL = new LinkedList<Enemies>();
		enemiessToBeAdded = new LinkedList<Enemies>();
		
		droppedItemsLL = new LinkedList<DroppedItems>();
		droppedItemssToBeAdded = new LinkedList<DroppedItems>();

		oreLL = new LinkedList<Ore>();
		oresToBeAdded = new LinkedList<Ore>();
	}
	
	public HUD gameHud = new HUD();
	private int distance;
	public int monsters;
	public int resources;
	private Text distanceText;
	public TiledSprite[] healthTiledSprite = new TiledSprite[5];
	public Entity swordEntity;
	private Rectangle swordResistanceRect;
	public TiledSprite swordTiledSprite;
	public Entity armorEntity;
	private Rectangle armorResistanceRect;
	public TiledSprite armorTiledSprite;
	private TiledSprite[] itemsHudTiledSprite = new TiledSprite[5];
	private int[] itemsQuantity = new int[5];
	private Text[] itemsQuantityText = new Text[5];
	private Text[] materialsQuantityText = new Text[3];
	private Entity craftingTableEntity;
	private int craftIndex;
	private Entity gameOverEntity;
	private Text gameOverDistanceText;
	private Text monstersOverText;
	private Entity[] gameOverItemsEntity = new Entity[5];
	private TiledSprite[] gameOverItemsTiledSprite = new TiledSprite[5];
	private Text[] gameOverItemsQuantityText = new Text[5];
	boolean canFade = true;
	public boolean tutorialActivated;
	public Sprite circle;
	
	private void createHUD() {
		
		distance = 0;
		monsters = 0;
		resources = 0;
		
		distanceText = new Text(10, 460, gm.bitlightFont, activity.getResources().getString(R.string.distance) + " " + distance + "m", 20, vbom);
		distanceText.setAnchorCenterX(0);
		gameHud.attachChild(distanceText);
		
		for(int i = 0; i < 5; i++) {
			healthTiledSprite[i] = new TiledSprite(i * 35 + 10, 425, gm.healthTiledTexture, vbom);
			healthTiledSprite[i].setAnchorCenterX(0);
			gameHud.attachChild(healthTiledSprite[i]);
		}
		
		//ENTIDADE DA RESISTÊNCIA DA ESPADA
		swordEntity = new Entity(10, 380);
		
		swordTiledSprite = new TiledSprite(0, 0, gm.swordTiledTexture, vbom);
		swordTiledSprite.setAnchorCenterX(0);
		swordEntity.attachChild(swordTiledSprite);
		
		Rectangle backSwordRectangle = new Rectangle(50, 0, 130, 30, vbom);
		backSwordRectangle.setColor(0.2f, 0.2f, 0.2f);
		backSwordRectangle.setAnchorCenterX(0);
		swordEntity.attachChild(backSwordRectangle);
		
		swordResistanceRect = new Rectangle(54, 0, 122, 22, vbom);
		swordResistanceRect.setAnchorCenterX(0);
		swordEntity.attachChild(swordResistanceRect);
		
		gameHud.attachChild(swordEntity);
		swordEntity.registerEntityModifier(new ScaleModifier(0, 1, 0));
		
		//ENTIDADE DA RESISTÊNCIA DA ARMADURA
		armorEntity = new Entity(10, 330);
		
		armorTiledSprite = new TiledSprite(0, 0, gm.helmentTiledTexture, vbom);
		armorTiledSprite.setAnchorCenterX(0);
		armorEntity.attachChild(armorTiledSprite);
		
		Rectangle backArmorRectangle = new Rectangle(50, 0, 130, 30, vbom);
		backArmorRectangle.setColor(0.2f, 0.2f, 0.2f);
		backArmorRectangle.setAnchorCenterX(0);
		armorEntity.attachChild(backArmorRectangle);
		
		armorResistanceRect = new Rectangle(54, 0, 122, 22, vbom);
		armorResistanceRect.setAnchorCenterX(0);
		armorEntity.attachChild(armorResistanceRect);
		
		gameHud.attachChild(armorEntity);
		armorEntity.registerEntityModifier(new ScaleModifier(0, 1, 0));
		
		//SPRITES QUE MOSTRAM QUANTIDADES DE MATERIAL
		for(int i = 0; i < 5; i++) {
			itemsHudTiledSprite[i] = new TiledSprite(210, 455 - i * 31, gm.itemsTiledTexture, vbom);
			itemsHudTiledSprite[i].setAnchorCenterX(0);
			itemsHudTiledSprite[i].setCurrentTileIndex(i);
			itemsHudTiledSprite[i].setScale(0.75f);
			gameHud.attachChild(itemsHudTiledSprite[i]);
		}
		
		for(int i = 0; i < 5; i++) {
			itemsQuantity[i] = activity.getSharedPreferences(ITEMS_TAGS[i], Context.MODE_PRIVATE).getInt(ITEMS_TAGS[i], 0);
			itemsQuantityText[i] = new Text(240, 455 - i * 31, gm.bitlightFont, "x" + itemsQuantity[i], 6, vbom);
			itemsQuantityText[i].setAnchorCenterX(0);
			gameHud.attachChild(itemsQuantityText[i]);
		}
		
		//MESA DE MONTAR ESPADAS E ARMADURAS
		craftingTableEntity = new Entity(400, 240);
		
		Rectangle craftingTableBackRect = new Rectangle(0, 0, 700, 400, vbom);
		craftingTableBackRect.setColor(0.2f, 0.2f, 0.2f, 0.9f);
		craftingTableEntity.attachChild(craftingTableBackRect);
		
		Text craftingTableText = new Text(-320, 160, gm.greenscrFont, activity.getResources().getString(R.string.crafting_table), vbom);
		craftingTableText.setAnchorCenterX(0);
		craftingTableEntity.attachChild(craftingTableText);
		
		Rectangle closeDialogRect = new Rectangle(300, 160, 50, 50, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(!gm.gameDialogScene.isShown) {
						craftingTableEntity.registerEntityModifier(new ScaleModifier(0.5f, 1, 0));
						touchEnabled = true;
						gm.gameScene.setIgnoreUpdate(false);
						gm.buttonSound.play();
						break;
					}
			    }
    			return false;
		    }
		};
		closeDialogRect.setColor(0.1f, 0.1f, 0.1f);
		craftingTableEntity.attachChild(closeDialogRect);
		gameHud.registerTouchArea(closeDialogRect);
		
		Rectangle leftRect = new Rectangle(-175, -20, 320, 280, vbom);
		leftRect.setColor(0.4f, 0.4f, 0.4f);
		craftingTableEntity.attachChild(leftRect);
		
		Rectangle rightRect = new Rectangle(175, -20, 320, 280, vbom);
		rightRect.setColor(0.4f, 0.4f, 0.4f);
		craftingTableEntity.attachChild(rightRect);
		
		Text closeDialogX = new Text(300, 160, gm.greenscrFont, "X", vbom);
		craftingTableEntity.attachChild(closeDialogX);
		
		Text craftSwordText = new Text(-300, 100, gm.bitlightFont, activity.getResources().getString(R.string.new_sword), vbom);
		craftSwordText.setAnchorCenterX(0);
		craftingTableEntity.attachChild(craftSwordText);
		
		Text craftArmorText = new Text(50, 100, gm.bitlightFont, activity.getResources().getString(R.string.new_armor), vbom);
		craftArmorText.setAnchorCenterX(0);
		craftingTableEntity.attachChild(craftArmorText);
		
		SWORD_NAMES[0] = activity.getResources().getString(R.string.iron_sword);
		SWORD_NAMES[1] = activity.getResources().getString(R.string.gold_sword);
		SWORD_NAMES[2] = activity.getResources().getString(R.string.diamond_sword);
		SWORD_NAMES[3] = activity.getResources().getString(R.string.obsidian_sword);
		
		ARMOR_NAMES[0] = activity.getResources().getString(R.string.iron_armor);
		ARMOR_NAMES[1] = activity.getResources().getString(R.string.gold_armor);
		ARMOR_NAMES[2] = activity.getResources().getString(R.string.diamond_armor);
		ARMOR_NAMES[3] = activity.getResources().getString(R.string.obsidian_armor);
		
		for(craftIndex = 0; craftIndex < 4; craftIndex++) {
			
			for(int j = 0; j < 4; j++) {
				
				Rectangle tableCellA = new Rectangle(-290 + j * 60, 40 - craftIndex * 55, 55, 50, vbom);
				tableCellA.setColor(0.3f, 0.3f, 0.3f);
				craftingTableEntity.attachChild(tableCellA);
				
				Rectangle tableCellB = new Rectangle(60 + j * 60, 40 - craftIndex * 55, 55, 50, vbom);
				tableCellB.setColor(0.3f, 0.3f, 0.3f);
				craftingTableEntity.attachChild(tableCellB);
				
				if(j < 3) {
					Text tableValueA = new Text(-290 + j * 60, 40 - craftIndex * 55, gm.bitlightFont, Integer.toString(SWORD_COST[craftIndex][j]), vbom);
					craftingTableEntity.attachChild(tableValueA);
					Text tableValueB = new Text(60 + j * 60, 40 - craftIndex * 55, gm.bitlightFont, Integer.toString(ARMOR_COST[craftIndex][j]), vbom);
					craftingTableEntity.attachChild(tableValueB);					
				}
				
			}
			
			TiledSprite craftSwordSprite = new TiledSprite(-110, 40 - craftIndex * 55, gm.swordTiledTexture, vbom);
			craftSwordSprite.setCurrentTileIndex(craftIndex);
			craftingTableEntity.attachChild(craftSwordSprite);
			
			TiledSprite craftArmorSprite = new TiledSprite(240, 40 - craftIndex * 55, gm.helmentTiledTexture, vbom);
			craftArmorSprite.setCurrentTileIndex(craftIndex);
			craftingTableEntity.attachChild(craftArmorSprite);
			
			Sprite craftSwordNow = new Sprite(-50, 40 - craftIndex * 55, gm.craftingTableTexture, vbom) {
				int newSwordIndex = craftIndex + 1;
				@Override
				public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					switch(pTouchEvent.getAction()) {			
					case TouchEvent.ACTION_UP:
						if(!gm.gameDialogScene.isShown) {
							gm.buttonSound.play();
							boolean hasEnoughMaterial = false;							
							for(int i = 0; i < 3; i++) {
								if(SWORD_COST[newSwordIndex - 1][i] > itemsQuantity[i + 2]) {
									hasEnoughMaterial = false;
									break;
								}
								else hasEnoughMaterial = true;
							}							
							if(hasEnoughMaterial) {
								gm.gameDialogScene.setParameters(1, 0, 0, activity.getResources().getString(R.string.confirm_craft_title), activity.getResources().getString(R.string.confirm_craft_msg) + " " + SWORD_NAMES[newSwordIndex - 1] + "?", activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), newSwordIndex);
								gm.gameDialogScene.show();
							}
							else activity.toastNoResources.show();
						}
						break;
				    }
	    			return false;
			    }
			};
			craftingTableEntity.attachChild(craftSwordNow);
			gameHud.registerTouchArea(craftSwordNow);
			
			Sprite craftArmorNow = new Sprite(300, 40 - craftIndex * 55, gm.craftingTableTexture, vbom) {
				int newArmorIndex = craftIndex + 5;
				@Override
				public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					switch(pTouchEvent.getAction()) {			
					case TouchEvent.ACTION_UP:
						if(!gm.gameDialogScene.isShown)	{
							gm.buttonSound.play();
							boolean hasEnoughMaterial = false;							
							for(int i = 0; i < 3; i++) {
								if(ARMOR_COST[newArmorIndex - 5][i] > itemsQuantity[i + 2]) {
									hasEnoughMaterial = false;
									break;
								}
								else hasEnoughMaterial = true;
							}							
							if(hasEnoughMaterial) {
								gm.gameDialogScene.setParameters(1, 0, 0, activity.getResources().getString(R.string.confirm_craft_title), activity.getResources().getString(R.string.confirm_craft_msg) + " " + ARMOR_NAMES[newArmorIndex - 5] + "?", activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), newArmorIndex);
								gm.gameDialogScene.show();
							}
							else activity.toastNoResources.show();
						}
						break;
				    }
	    			return false;
			    }
			};
			craftingTableEntity.attachChild(craftArmorNow);
			gameHud.registerTouchArea(craftArmorNow);
			
		}
		
		for(int i = 0; i < 2; i++) {
			
			TiledSprite ironLingot = new TiledSprite(-300 + i * 350, 80, gm.itemsTiledTexture, vbom);
			ironLingot.setAnchorCenterX(0);
			ironLingot.setCurrentTileIndex(2);
			ironLingot.setScale(0.75f);
			craftingTableEntity.attachChild(ironLingot);
			
			TiledSprite goldLingot = new TiledSprite(-240 + i * 350, 80, gm.itemsTiledTexture, vbom);
			goldLingot.setAnchorCenterX(0);
			goldLingot.setCurrentTileIndex(3);
			goldLingot.setScale(0.75f);
			craftingTableEntity.attachChild(goldLingot);
			
			TiledSprite diamondGem = new TiledSprite(-180 + i * 350, 80, gm.itemsTiledTexture, vbom);
			diamondGem.setAnchorCenterX(0);
			diamondGem.setCurrentTileIndex(4);
			diamondGem.setScale(0.75f);
			craftingTableEntity.attachChild(diamondGem);
			
		}
		
		Text currentMaterialText = new Text(-320, -180, gm.bitlightFont, activity.getResources().getString(R.string.current_material), vbom);
		currentMaterialText.setAnchorCenterX(0);
		craftingTableEntity.attachChild(currentMaterialText);
		
		float materialX = currentMaterialText.getWidth() - 300;
		
		for(int i = 0; i < 3; i++) {
			TiledSprite materialSprite = new TiledSprite(materialX + i * 100, -180, gm.itemsTiledTexture, vbom);
			materialSprite.setAnchorCenterX(0);
			materialSprite.setCurrentTileIndex(i + 2);
			materialSprite.setScale(0.75f);
			craftingTableEntity.attachChild(materialSprite);
		}
		
		for(int i = 0; i < 3; i++) {
			materialsQuantityText[i] = new Text(materialX + 27 + i * 100, -180, gm.bitlightFont, "x" + itemsQuantity[i + 2], 6, vbom);
			materialsQuantityText[i].setAnchorCenterX(0);
			craftingTableEntity.attachChild(materialsQuantityText[i]);
		}
		
		craftingTableEntity.registerEntityModifier(new ScaleModifier(0, 1, 0));
		gameHud.attachChild(craftingTableEntity);
		
		//TELA DE GAME OVER
		gameOverEntity = new Entity(400, 240);
		
		Rectangle redBackRect = new Rectangle(0, 0, 800, 480, vbom);
		redBackRect.setColor(0.5f, 0.0f, 0.0f, 0.5f);
		gameOverEntity.attachChild(redBackRect);
		
		Rectangle gameOverBackRect = new Rectangle(0, 0, 700, 400, vbom);
		gameOverBackRect.setColor(0.2f, 0.2f, 0.2f, 0.9f);
		gameOverEntity.attachChild(gameOverBackRect);
		
		Rectangle gameOverInnerRect = new Rectangle(0, 20, 600, 200, vbom);
		gameOverInnerRect.setColor(0.3f, 0.3f, 0.3f);
		gameOverEntity.attachChild(gameOverInnerRect);
		
		Text gameOverText = new Text(0, 160, gm.greenscrFont, activity.getResources().getString(R.string.game_over), vbom);
		gameOverEntity.attachChild(gameOverText);
		
		gameOverDistanceText = new Text(-260, 80, gm.greenscrFont, "", 20, vbom);
		gameOverDistanceText.setAnchorCenterX(0);
		gameOverDistanceText.setScale(0.75f);
		gameOverEntity.attachChild(gameOverDistanceText);
		
		monstersOverText = new Text(-260, 20, gm.greenscrFont, "", 30, vbom);
		monstersOverText.setAnchorCenterX(0);
		monstersOverText.setScale(0.75f);
		gameOverEntity.attachChild(monstersOverText);
		
		for(int i = 0; i < 5; i++) {
			gameOverItemsEntity[i] = new Entity(120 * (i - 2), 0);
			
			gameOverItemsTiledSprite[i] = new TiledSprite(0, -40, gm.itemsTiledTexture, vbom);
			gameOverItemsTiledSprite[i].setCurrentTileIndex(i);
			gameOverItemsTiledSprite[i].setAnchorCenterX(1);
			gameOverItemsEntity[i].attachChild(gameOverItemsTiledSprite[i]);
			
			gameOverItemsQuantityText[i] = new Text(3, -40, gm.bitlightFont, "", 6, vbom);
			gameOverItemsQuantityText[i].setAnchorCenterX(0);
			gameOverItemsEntity[i].attachChild(gameOverItemsQuantityText[i]);
			
			gameOverItemsEntity[i].registerEntityModifier(new ScaleModifier(0, 1, 0));
			gameOverEntity.attachChild(gameOverItemsEntity[i]);
		}
		
		Text rewardVideoText = new Text(0, -100, gm.greenscrFont, activity.getResources().getString(R.string.reward_video), vbom);
		rewardVideoText.setScale(0.5f);
		gameOverEntity.attachChild(rewardVideoText);
		
		Sprite giveupButton = new Sprite(-200, -150, gm.buttonTexture, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(canFade) {
						canFade = false;
						gm.buttonSound.play();
						fadeToMenu();
					}
					break;
			    }
    			return false;
		    }
		};
		giveupButton.setScale(0.8f);
		gameHud.registerTouchArea(giveupButton);
		gameOverEntity.attachChild(giveupButton);
		
		Text giveupText = new Text(0, -150, gm.greenscrFont, activity.getResources().getString(R.string.giveup_game), vbom);
		giveupText.setScale(0.75f);
		gameOverEntity.attachChild(giveupText);
		
		Sprite continueButton = new Sprite(+200, -150, gm.buttonTexture, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(canFade) {
						canFade = false;
						gm.buttonSound.play();
						fadeToMenu();
					}
					break;
			    }
    			return false;
		    }
		};
		continueButton.setScale(0.8f);
		gameHud.registerTouchArea(continueButton);
		gameOverEntity.attachChild(continueButton);
		
		Text continueText = new Text(0, -150, gm.greenscrFont, activity.getResources().getString(R.string.continue_game), vbom);
		continueText.setScale(0.75f);
		gameOverEntity.attachChild(continueText);
		
		gameOverEntity.registerEntityModifier(new ScaleModifier(0, 1, 0));
		gameHud.attachChild(gameOverEntity);
		
		tutorialActivated = activity.getSharedPreferences(TUTORIAL_TAGS, Context.MODE_PRIVATE).getBoolean(TUTORIAL_TAGS, true);
		
		circle = new Sprite(0, 0, gm.circleTexture, vbom);
		gameHud.attachChild(circle);
		circle.setVisible(false);
		
		gameHud.setVisible(false);
		attachChild(gameHud);
		camera.setHUD(gameHud);
	}
	
	private boolean putFirstPieces = true;
	private float mapX0 = 1200;
	private int steveX0 = 150;
	private int enemiesProb = 5;
	public boolean canCreateCraftingTable = true;
	private int triggerSpeed = DISTANCE_TO_INCREASE_PLAYER_SPEED;
	public static float currentSpeed = STARTING_SPEED;
	private boolean firstMessage = true;
	private boolean firstEnemy = true;
	private boolean firstOre = true;
	public boolean firstCraftingTable = true;
	public boolean firstCreeper = true;
	private boolean firstBeef = true;
	private boolean firstResource = true;
	
	IUpdateHandler runtimeHandler = new IUpdateHandler() {
		@Override
		public void reset() {}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			
			if(putFirstPieces) {
				createPieceOfMap(0, 0, 0);
				createPieceOfMap(1, 1200, 0);
				putFirstPieces = false;
			}
			
			if(steve.getX() > steveX0 + 80) {
				steveX0 = steveX0 + 80;
				distance = steveX0 / 80;
				distanceText.setText(activity.getResources().getString(R.string.distance) + " " + distance + "m");
				checkDistanceAchievement();
				
				if(tutorialActivated && firstMessage && distance > 1) {
					firstMessage = false;
					showTutorialMessage(0);
				}
				
				if(steveX0 > triggerSpeed) {
					triggerSpeed += DISTANCE_TO_INCREASE_PLAYER_SPEED;
					currentSpeed = currentSpeed + SPEED_INCREMENT;
					Steve.playerSpeed = currentSpeed;
				}
				
				if(enemiesProb < PROB_ENEMIES_MAX && steveX0 > DISTANCE_TO_INCREASE_ENEMIES_PROB * PROB_ENEMIES_MIN) {
					enemiesProb = steveX0 / DISTANCE_TO_INCREASE_ENEMIES_PROB;
				}
				
				//adicionar enemies de acordo com a probabilidade
				int probEnemies = (new Random()).nextInt(100);
				if(probEnemies < enemiesProb) {
					createEnemies(steve.getX() + 640, 350);
				}
				
				//adicionar minerais no caminho
				int probOre = (new Random()).nextInt(100);
				if(probOre < ORE_PROBABILITY) {
					createOre(steve.getX() + 680, 350);
				}
				else if(canCreateCraftingTable && probOre < CRAFTING_PROBABILITY) {
					createCraftingTable(steve.getX() + 680, 350);
				}
			}
			
			if(steve.getY() < - 100) gameOver();
			
			Iterator<MapPiece> mapPieces = mapPieceLL.iterator();
			MapPiece _mapPiece;
			
			while (mapPieces.hasNext()) {
				_mapPiece = mapPieces.next();
				
				//se a peça do mapa já tiver saido da tela
				if(steve.getX() - _mapPiece.getX() > _mapPiece.getWidth() + 450) {
					
					//coloca peça nova
					mapX0 += _mapPiece.getWidth();
					int nextPiece = (new Random()).nextInt(MAP_PIECES);
					createPieceOfMap(nextPiece, mapX0, 0);
					
					//remove peça antiga
					_mapPiece.removeMapPiece();
					mapPieces.remove();
					
					//atualiza as fronteiras da camera
					camera.setBounds(steve.getX() - 450, 0, steve.getX() + 2000, 480);
					
					break;
				}
			}
			
			Iterator<Enemies> enemiess = enemiesLL.iterator();
			Enemies _enemies;
			// iterating over the enemiess
			while (enemiess.hasNext()) {
				_enemies = enemiess.next();
				
				if(tutorialActivated && firstEnemy && _enemies.getX() - steve.getX() < 200) {
					firstEnemy = false;
					circle.setPosition(_enemies.getX() - steve.getX() + 400, _enemies.getY());
					circle.setVisible(true);
					showTutorialMessage(1);
				}

				// if enemies passed the left edge of the screen, then remove it and call a fail
				if(_enemies.killThisEnemy || steve.getX() - _enemies.getX() > 500 || _enemies.getY() < -500) {					
					_enemies.removeEnemies();
					enemiess.remove();
					continue;
				}
				else if(!_enemies.ignoreEnemiesCollision && steve.collidesWith(_enemies)) {
					if(steve.isAttacking) {
						_enemies.loseHealth();
						break;
					}
					else if(!steve.isBeingDamaged) {
						steve.loseHealth(MONSTERS_ATTACK);
						_enemies.jumpBehind();
						break;
					}
				}
			}
			
			Iterator<Ore> ores = oreLL.iterator();
			Ore _ore;
			// iterating over the ores
			while (ores.hasNext()) {
				_ore = ores.next();
				
				if(tutorialActivated && firstOre && _ore.getX() - steve.getX() < 200) {
					firstOre = false;
					circle.setPosition(_ore.getX() - steve.getX() + 400, _ore.getY());
					circle.setVisible(true);
					showTutorialMessage(2);
				}

				// if ore passed the left edge of the screen, then remove it and call a fail
				if(_ore.crackThisOre || steve.getX() - _ore.getX() > 500 || _ore.getY() < -500) {					
					_ore.removeOre();
					ores.remove();
					continue;
				}
				else if(!_ore.ignoreOreCollision && steve.collidesWith(_ore)) {
					if(steve.isAttacking) {
						_ore.loseResistance();
					}
					break;
				}
			}
			
			Iterator<DroppedItems> droppedItemss = droppedItemsLL.iterator();
			DroppedItems _droppedItems;
			// iterating over the droppedItemss
			while (droppedItemss.hasNext()) {
				_droppedItems = droppedItemss.next();
				
				if(tutorialActivated && firstBeef && _droppedItems.getIndex() == 5 && _droppedItems.getX() - steve.getX() > 50) {
					firstBeef = false;
					circle.setPosition(_droppedItems.getX() - steve.getX() + 400, _droppedItems.getY());
					circle.setVisible(true);
					showTutorialMessage(3);
				}
				
				if(tutorialActivated && firstResource && _droppedItems.getIndex() < 5 && _droppedItems.getX() - steve.getX() > 50) {
					firstResource = false;
					circle.setPosition(_droppedItems.getX() - steve.getX() + 400, _droppedItems.getY());
					circle.setVisible(true);
					showTutorialMessage(4);
				}

				// if droppedItems passed the left edge of the screen, then remove it and call a fail
				if(steve.getX() - _droppedItems.getX() > 500 || _droppedItems.getY() < -500) {					
					_droppedItems.removeDroppedItems(false);
					droppedItemss.remove();
					continue;
				}
				else if(steve.collidesWith(_droppedItems)) {
					gm.popSound.play();
					_droppedItems.removeDroppedItems(true);
					droppedItemss.remove();
					break;
				}
			}

			mapPieceLL.addAll(mapPiecesToBeAdded);
			mapPiecesToBeAdded.clear();
			
			enemiesLL.addAll(enemiessToBeAdded);
			enemiessToBeAdded.clear();
			
			droppedItemsLL.addAll(droppedItemssToBeAdded);
			droppedItemssToBeAdded.clear();

			oreLL.addAll(oresToBeAdded);
			oresToBeAdded.clear();
		}
	};
    
	//FUNÇÃO DO CONTACT LISTENER
    public int steveIsOnFloor = 0;
    public int creeperIsOnFloor = 0;
    public ContactListener contactListener() {
	    ContactListener contactListener = new ContactListener() {
	    	
	        public void beginContact(Contact contact) {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();
	            
	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null) {
	                if (x1.getBody().getUserData().equals("steve") && x2.getBody().getUserData().equals("floor") ||
	                	x2.getBody().getUserData().equals("steve") && x1.getBody().getUserData().equals("floor")) {
	                	steveIsOnFloor = 0;
	                	gm.stepSound.play();
	                }
	                if (x1.getBody().getUserData().equals("creeper") && x2.getBody().getUserData().equals("floor") ||
		                x2.getBody().getUserData().equals("creeper") && x1.getBody().getUserData().equals("floor")) {
		                creeperIsOnFloor = 0;
		            }
	            }
	        }
	        public void endContact(Contact contact) {}
	        public void preSolve(Contact contact, Manifold oldManifold) {}
	        public void postSolve(Contact contact, ContactImpulse impulse) {}
	    };
	    return contactListener;
	}
    
    //FUNÇÃO DO TOUCH LISTENER DA CENA
    private float touchY;
    private float releaseY;
    public boolean touchEnabled = true;
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(touchEnabled) {
			switch(pSceneTouchEvent.getAction()) {
			case TouchEvent.ACTION_DOWN:
				touchY = pSceneTouchEvent.getY();
				break;
			case TouchEvent.ACTION_UP:
				releaseY = pSceneTouchEvent.getY();
				if(!steve.isBeingDamaged) {
					if(releaseY - touchY > JUMP_TOLERANCE) {
						if(steveIsOnFloor < 2) steve.jump();
					}
					else if (!steve.isAttacking) {
						gm.attackSound.play();
						steve.attack();
					}
					break;
				}
		    }
		}
		return false;
	}
	
	//FUNÇÕES QUE CRIAM OBJETOS INDEPENDENTES
	public boolean canCreateCreeper = true;
	private void createPieceOfMap(int pieceNum, float pX, float pY) {
		MapPiece mapPiece = new MapPiece(pieceNum, pX, pY, gm.mapaPieceTexture[pieceNum], vbom);
		attachChild(mapPiece);
		mapPiece.setZIndex(1);
		sortChildren();
		mapPiecesToBeAdded.add(mapPiece);
		
		int probCreeper = (new Random()).nextInt(100);
		if(probCreeper < CREEPER_PROBABILITY && pieceNum == 4 && canCreateCreeper) {
			canCreateCreeper = false;
			createCreeper(mapPiece.getX() + 530);
		}
	}
	
	private void createCreeper(float pX) {
		Creeper creeper = new Creeper(pX, 70, vbom, physicsWorld);
		attachChild(creeper);
		creeper.setZIndex(10);
		sortChildren();
	}
	
	private void createEnemies(float pX, float pY) {
		ITiledTextureRegion enemyTiledTexture;
		int id;
		int health;
		int probEnemies = (new Random()).nextInt(100);
		if(probEnemies < ZOMBIE_PROBABILITY) { enemyTiledTexture = gm.zombieTiledTexture; id = 0; health = ZOMBIE_HEALTH;}
		else if(probEnemies < SPIDER_PROBABILITY) { enemyTiledTexture = gm.spiderTiledTexture; id = 1; health = SPIDER_HEALTH;}
		else { enemyTiledTexture = gm.skeletonTiledTexture; id = 2; health = SKELETON_HEALTH;}
		Enemies enemies = new Enemies(id, health, pX, pY, enemyTiledTexture, vbom);
		attachChild(enemies);
		enemies.setZIndex(10);
		sortChildren();
		enemiessToBeAdded.add(enemies);
	}
	
	private void createOre(float pX, float pY) {
		int oreIndex;
		int resistance;
		int probOres = (new Random()).nextInt(100);
		if(probOres < WOOD_PROBABILITY) { oreIndex = 0; resistance = WOOD_RESISTANCE;}
		else if(probOres < STONE_PROBABILITY) { oreIndex = 1; resistance = COBBLESTONE_RESISTANCE;}
		else if(probOres < IRON_PROBABILITY) { oreIndex = 2; resistance = IRON_RESISTANCE;}
		else if(probOres < GOLD_PROBABILITY) { oreIndex = 3; resistance = GOLD_RESISTANCE;}
		else {oreIndex = 4; resistance = DIAMOND_RESISTANCE;}
		Ore ore = new Ore(pX, pY, gm.oreTiledTexture, vbom, oreIndex, resistance);
		attachChild(ore);
		ore.setZIndex(10);
		sortChildren();
		oresToBeAdded.add(ore);
	}
	
	private void createCraftingTable(float pX, float pY) {
		canCreateCraftingTable = false;
		CraftingTable craftingTable = new CraftingTable(pX, pY, gm.craftingTableTexture, vbom);
		attachChild(craftingTable);
		registerTouchArea(craftingTable);
		craftingTable.setZIndex(10);
		sortChildren();
	}
	
	public void dropItem(float pX, float pY, int index) {
		DroppedItems droppedItems = new DroppedItems(pX, pY, gm.itemsTiledTexture, vbom, index);
		attachChild(droppedItems);
		droppedItems.setZIndex(10);
		sortChildren();
		droppedItems.dropItemFromEntity();
		droppedItemssToBeAdded.add(droppedItems);
	}
	
	public void dropOre(int index, ITiledTextureRegion pTiledTexture, float pX, float pY) {
		DroppedItems droppedItems = new DroppedItems(pX, pY, pTiledTexture, vbom, index);
		attachChild(droppedItems);
		droppedItems.setZIndex(10);
		sortChildren();
		droppedItems.dropItemFromEntity();
		droppedItemssToBeAdded.add(droppedItems);
	}
	
	//OUTRAS FUNÇÕES
	public void addItem(int itemIndex) {
		itemsQuantity[itemIndex]++;
		if(itemsQuantity[itemIndex] > 9999) itemsQuantity[itemIndex] = 9999;
		for(int i = 0; i < 5; i++) {
			itemsQuantityText[i].setText("x" + itemsQuantity[i]);
		}
		for(int i = 0; i < 3; i++) {
			materialsQuantityText[i].setText("x" + itemsQuantity[i + 2]);
		}
	}
	
	public void addHealth() {
		steve.steveHealth += 10;
		
		if(steve.steveHealth >= 10) gm.gameScene.healthTiledSprite[0].setCurrentTileIndex(1);
		if(steve.steveHealth >= 20) gm.gameScene.healthTiledSprite[0].setCurrentTileIndex(0);
		if(steve.steveHealth >= 30) gm.gameScene.healthTiledSprite[1].setCurrentTileIndex(1);
		if(steve.steveHealth >= 40) gm.gameScene.healthTiledSprite[1].setCurrentTileIndex(0);
		if(steve.steveHealth >= 50) gm.gameScene.healthTiledSprite[2].setCurrentTileIndex(1);
		if(steve.steveHealth >= 60) gm.gameScene.healthTiledSprite[2].setCurrentTileIndex(0);
		if(steve.steveHealth >= 70) gm.gameScene.healthTiledSprite[3].setCurrentTileIndex(1);
		if(steve.steveHealth >= 80) gm.gameScene.healthTiledSprite[3].setCurrentTileIndex(0);
		if(steve.steveHealth >= 90) gm.gameScene.healthTiledSprite[4].setCurrentTileIndex(1);
		if(steve.steveHealth >= 100) {
			steve.steveHealth = 100;
			gm.gameScene.healthTiledSprite[4].setCurrentTileIndex(0);
		}
		
	}
	
	public int currentSwordMax = 0;
	public int swordResistance = 0;
	public void updateSwordResistance(int damage) {
		swordResistance -= damage;
		if(swordResistance <= 0) {
			swordEntity.registerEntityModifier(new ScaleModifier(0.5f, 1, 0, EaseElasticIn.getInstance()));
			steve.steveSwordCode = 0;
			steve.updateArmorAndWeapon();
		}
		else {
			float scale = (float) swordResistance / (float) currentSwordMax;
			swordResistanceRect.setScaleX(scale);
		}
	}
	
	public int currentArmorMax = 0;
	public int armorResistance = 0;
	public void updateArmorResistance(int damage) {
		armorResistance -= damage;
		if(armorResistance <= 0) {
			armorEntity.registerEntityModifier(new ScaleModifier(0.5f, 1, 0, EaseElasticIn.getInstance()));
			steve.steveArmorCode = 0;
		}
		else {
			float scale = (float) armorResistance / (float) currentArmorMax;
			armorResistanceRect.setScaleX(scale);
		}
	}
	
	public void openCraftingTable() {
		touchEnabled = false;
		setIgnoreUpdate(true);
		touchY = 500;
		craftingTableEntity.registerEntityModifier(new ScaleModifier(1, 0, 1, EaseElasticOut.getInstance()));
	}
	
	public void craftingNewArmor(int index, boolean hasToPay) {		
		if(index == 0) steve.steveArmorCode = 2;
		else if(index == 1) steve.steveArmorCode = 4;
		else if(index == 2) steve.steveArmorCode = 6;
		else steve.steveArmorCode = 8;
		
		steve.updateArmorAndWeapon();
		armorTiledSprite.setCurrentTileIndex(index);
		armorResistance = ARMOR_RESISTANCE[index];
		currentArmorMax = ARMOR_RESISTANCE[index];
		armorResistanceRect.setScaleX(1.0f);
		armorEntity.registerEntityModifier(new ScaleModifier(0.5f, 0, 1, EaseElasticOut.getInstance()));
		
		if(hasToPay) {
			for(int i = 0; i < 3; i++) {
				itemsQuantity[i + 2] -= ARMOR_COST[index][i];
				itemsQuantityText[i + 2].setText("x" + itemsQuantity[i + 2]);
				materialsQuantityText[i].setText("x" + itemsQuantity[i + 2]);
			}
			craftingTableEntity.registerEntityModifier(new ScaleModifier(0.5f, 1, 0));
		}		
		
		touchEnabled = true;
		setIgnoreUpdate(false);
	}
	
	public void craftingNewSword(int index, boolean hasToPay) {
		if(index == 0) steve.steveSwordCode = 8;
		else if(index == 1) steve.steveSwordCode = 16;
		else if(index == 2) steve.steveSwordCode = 24;
		else steve.steveSwordCode = 32;
		
		steve.updateArmorAndWeapon();
		swordTiledSprite.setCurrentTileIndex(index);
		swordResistance = SWORD_RESISTANCE[index];
		currentSwordMax = SWORD_RESISTANCE[index];
		swordResistanceRect.setScaleX(1.0f);
		swordEntity.registerEntityModifier(new ScaleModifier(0.5f, 0, 1, EaseElasticOut.getInstance()));
		
		if(hasToPay) {
			for(int i = 0; i < 3; i++) {
				itemsQuantity[i + 2] -= SWORD_COST[index][i];
				itemsQuantityText[i + 2].setText("x" + itemsQuantity[i + 2]);
				materialsQuantityText[i].setText("x" + itemsQuantity[i + 2]);
			}
			craftingTableEntity.registerEntityModifier(new ScaleModifier(0.5f, 1, 0));
		}
		
		touchEnabled = true;
		setIgnoreUpdate(false);
	}
	
	private void checkDistanceAchievement() {
		boolean achieved = false;
		String achievementMessage = "";
		
		if(distance >= 1000 && !activity.getSharedPreferences(ACHIEVEMENTS_TAGS[3], Context.MODE_PRIVATE).getBoolean(ACHIEVEMENTS_TAGS[3], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[3], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[3], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_1000m_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ activity.getResources().getString(R.string.reward_sword_obsidian);
			
			steve.steveSwordCode = 32;			
			steve.updateArmorAndWeapon();
			swordTiledSprite.setCurrentTileIndex(3);
			swordResistance = SWORD_RESISTANCE[3];
			currentSwordMax = SWORD_RESISTANCE[3];
			swordResistanceRect.setScaleX(1.0f);
			swordEntity.registerEntityModifier(new ScaleModifier(0.5f, 0, 1, EaseElasticOut.getInstance()));
		}
		else if(distance >= 500 && !activity.getSharedPreferences(ACHIEVEMENTS_TAGS[2], Context.MODE_PRIVATE).getBoolean(ACHIEVEMENTS_TAGS[2], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[2], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[2], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_500m_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ activity.getResources().getString(R.string.reward_sword_diamond);
			
			steve.steveSwordCode = 24;			
			steve.updateArmorAndWeapon();
			swordTiledSprite.setCurrentTileIndex(2);
			swordResistance = SWORD_RESISTANCE[2];
			currentSwordMax = SWORD_RESISTANCE[2];
			swordResistanceRect.setScaleX(1.0f);
			swordEntity.registerEntityModifier(new ScaleModifier(0.5f, 0, 1, EaseElasticOut.getInstance()));
		}
		else if(distance >= 200 && !activity.getSharedPreferences(ACHIEVEMENTS_TAGS[1], Context.MODE_PRIVATE).getBoolean(ACHIEVEMENTS_TAGS[1], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[1], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[1], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_200m_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ activity.getResources().getString(R.string.reward_sword_gold);
			
			steve.steveSwordCode = 16;			
			steve.updateArmorAndWeapon();
			swordTiledSprite.setCurrentTileIndex(1);
			swordResistance = SWORD_RESISTANCE[1];
			currentSwordMax = SWORD_RESISTANCE[1];
			swordResistanceRect.setScaleX(1.0f);
			swordEntity.registerEntityModifier(new ScaleModifier(0.5f, 0, 1, EaseElasticOut.getInstance()));
		}
		else if(distance >= 100 && !activity.getSharedPreferences(ACHIEVEMENTS_TAGS[0], Context.MODE_PRIVATE).getBoolean(ACHIEVEMENTS_TAGS[0], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[0], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[0], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_100m_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ activity.getResources().getString(R.string.reward_sword_iron);
			
			steve.steveSwordCode = 8;			
			steve.updateArmorAndWeapon();
			swordTiledSprite.setCurrentTileIndex(0);
			swordResistance = SWORD_RESISTANCE[0];
			currentSwordMax = SWORD_RESISTANCE[0];
			swordResistanceRect.setScaleX(1.0f);
			swordEntity.registerEntityModifier(new ScaleModifier(0.5f, 0, 1, EaseElasticOut.getInstance()));
		}
		
		if(achieved) {
			gm.gameScene.setIgnoreUpdate(true);
	    	gm.gameDialogScene.setParameters(0, 0, 0, activity.getResources().getString(R.string.achievement_get_title), achievementMessage, activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 9);
			gm.gameDialogScene.show();
		}
	}
	
	public void checkCraftingAchievement() {
		boolean achieved = false;
		String achievementMessage = "";
		
		if(steve.steveArmorCode == 2 && steve.steveSwordCode == 8 && !activity.getSharedPreferences(MainScene.ACHIEVEMENTS_TAGS[4], Context.MODE_PRIVATE).getBoolean(MainScene.ACHIEVEMENTS_TAGS[4], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[4], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[4], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_iron_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ Integer.toString(5) + " "
					+ activity.getResources().getString(R.string.reward_diamond) + "!";
			itemsQuantity[4] += 5;
			itemsQuantityText[4].setText("x" + itemsQuantity[4]);
			materialsQuantityText[2].setText("x" + itemsQuantity[4]);
		}
		else if(steve.steveArmorCode == 4 && steve.steveSwordCode == 16 && !activity.getSharedPreferences(MainScene.ACHIEVEMENTS_TAGS[5], Context.MODE_PRIVATE).getBoolean(MainScene.ACHIEVEMENTS_TAGS[5], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[5], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[5], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_gold_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ Integer.toString(10) + " "
					+ activity.getResources().getString(R.string.reward_diamond) + "!";
			itemsQuantity[4] += 10;
			itemsQuantityText[4].setText("x" + itemsQuantity[4]);
			materialsQuantityText[2].setText("x" + itemsQuantity[4]);
		}
		else if(steve.steveArmorCode == 6 && steve.steveSwordCode == 24 && !activity.getSharedPreferences(MainScene.ACHIEVEMENTS_TAGS[6], Context.MODE_PRIVATE).getBoolean(MainScene.ACHIEVEMENTS_TAGS[6], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[6], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[6], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_diamond_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ Integer.toString(15) + " "
					+ activity.getResources().getString(R.string.reward_diamond) + "!";
			itemsQuantity[4] += 15;
			itemsQuantityText[4].setText("x" + itemsQuantity[4]);
			materialsQuantityText[2].setText("x" + itemsQuantity[4]);
		}
		else if(steve.steveArmorCode == 8 && steve.steveSwordCode == 32 && !activity.getSharedPreferences(MainScene.ACHIEVEMENTS_TAGS[7], Context.MODE_PRIVATE).getBoolean(MainScene.ACHIEVEMENTS_TAGS[7], false)) {
			activity.getSharedPreferences(ACHIEVEMENTS_TAGS[7], Context.MODE_PRIVATE).edit().putBoolean(ACHIEVEMENTS_TAGS[7], true).commit();
			achieved = true;
			achievementMessage = activity.getResources().getString(R.string.achievement_get_message) + " "
					+ activity.getResources().getString(R.string.achievements_obsidian_title) + ".\n"
					+ activity.getResources().getString(R.string.achievement_reward) + " "
					+ Integer.toString(20) + " "
					+ activity.getResources().getString(R.string.reward_diamond) + "!";
			itemsQuantity[4] += 20;
			itemsQuantityText[4].setText("x" + itemsQuantity[4]);
			materialsQuantityText[2].setText("x" + itemsQuantity[4]);
		}
		
		if(achieved) {
			gm.gameScene.setIgnoreUpdate(true);
	    	gm.gameDialogScene.setParameters(0, 0, 0, activity.getResources().getString(R.string.achievement_get_title), achievementMessage, activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 9);
			gm.gameDialogScene.show();
		}
	}
	
	private int tutoComplete = 0;
	public void showTutorialMessage(int pMessageId) {
		
		tutoComplete++;
		if(tutoComplete > 6) gm.activity.getSharedPreferences(TUTORIAL_TAGS, Context.MODE_PRIVATE).edit().putBoolean(TUTORIAL_TAGS, false).commit();
		
		gm.gameScene.setIgnoreUpdate(true);
		touchEnabled = false;
		
		if(pMessageId == 0) gm.gameDialogScene.setParameters(0, 0, 0, activity.getResources().getString(R.string.tutorial_welcome_title), activity.getResources().getString(R.string.tutorial_welcome_message), activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 10);
			
		else if(pMessageId == 1) gm.gameDialogScene.setParameters(0, 100, -100, activity.getResources().getString(R.string.tutorial_enemy_title), activity.getResources().getString(R.string.tutorial_enemy_message), activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 10);
			
		else if(pMessageId == 2) gm.gameDialogScene.setParameters(0, 100, -100, activity.getResources().getString(R.string.tutorial_mineral_title), activity.getResources().getString(R.string.tutorial_mineral_message), activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 10);
			
		else if(pMessageId == 3) gm.gameDialogScene.setParameters(0, 100, -100, activity.getResources().getString(R.string.tutorial_beef_title), activity.getResources().getString(R.string.tutorial_beef_message), activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 10);
			
		else if(pMessageId == 4) gm.gameDialogScene.setParameters(0, 100, -100, activity.getResources().getString(R.string.tutorial_resource_title), activity.getResources().getString(R.string.tutorial_resource_message), activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 10);
			
		else if(pMessageId == 5) gm.gameDialogScene.setParameters(0, 100, -100, activity.getResources().getString(R.string.tutorial_crafting_title), activity.getResources().getString(R.string.tutorial_crafting_message), activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 10);
			
		else if(pMessageId == 6) gm.gameDialogScene.setParameters(0, 100, -100, activity.getResources().getString(R.string.tutorial_creeper_title), activity.getResources().getString(R.string.tutorial_creeper_message), activity.getResources().getString(R.string.yes), activity.getResources().getString(R.string.no), 10);

		gm.gameDialogScene.show();
	}
	
	int totalDistance;
	int currentMaxDistance;
	int currentMaxMonsters;
	public void gameOver() {
		touchEnabled = false;
		unregisterUpdateHandler(runtimeHandler);
		Steve.playerSpeed = 0.0f;
		
		gameOverDistanceText.setText(activity.getResources().getString(R.string.distance) + " " + distance + "m");
		monstersOverText.setText(activity.getResources().getString(R.string.monsters) + " " + monsters);
		//verifica se a distância deste sprint é maior que a máxima histórica
		currentMaxDistance = activity.getSharedPreferences(MAX_DISTANCE, Context.MODE_PRIVATE).getInt(MAX_DISTANCE, 0);
		if(distance > currentMaxDistance) {
			activity.getSharedPreferences(MAX_DISTANCE, Context.MODE_PRIVATE).edit().putInt(MAX_DISTANCE, distance).commit();
		}
		
		//verifica se o número de monstros mortos deste sprint é maior que a máxima histórica
		currentMaxMonsters = activity.getSharedPreferences(MAX_MONSTERS, Context.MODE_PRIVATE).getInt(MAX_MONSTERS, 0);
		if(monsters > currentMaxMonsters) {
			activity.getSharedPreferences(MAX_MONSTERS, Context.MODE_PRIVATE).edit().putInt(MAX_MONSTERS, monsters).commit();
		}
		
		//soma a distância deste sprint à somatória das distâncias
		totalDistance = activity.getSharedPreferences(TOTAL_DISTANCE, Context.MODE_PRIVATE).getInt(TOTAL_DISTANCE, 0);
		totalDistance += distance;
		activity.getSharedPreferences(TOTAL_DISTANCE, Context.MODE_PRIVATE).edit().putInt(TOTAL_DISTANCE, totalDistance).commit();
		
		//guarda os novos valores dos recursos
		for(int i = 0; i < 5; i++) {
			activity.getSharedPreferences(ITEMS_TAGS[i], Context.MODE_PRIVATE).edit().putInt(ITEMS_TAGS[i], itemsQuantity[i]).commit();
			gameOverItemsQuantityText[i].setText("x" + itemsQuantity[i]);
		}
		
		//Atualiza os incrementais dos achievements do Google Play Services
		activity.getSharedPreferences(ACHIEVEMENT_DISTANCE_INCREMENT, Context.MODE_PRIVATE).edit().putInt(ACHIEVEMENT_DISTANCE_INCREMENT, distance).commit();
		activity.getSharedPreferences(ACHIEVEMENT_MONSTER_INCREMENT, Context.MODE_PRIVATE).edit().putInt(ACHIEVEMENT_MONSTER_INCREMENT, monsters).commit();
		activity.getSharedPreferences(ACHIEVEMENT_RESOURCES_INCREMENT, Context.MODE_PRIVATE).edit().putInt(ACHIEVEMENT_RESOURCES_INCREMENT, resources).commit();
		
		//mostra os recursos na entidade do game over
		gameOverEntity.registerEntityModifier(new ScaleModifier(1, 0, 1, EaseElasticOut.getInstance()) {
			@Override protected void onModifierFinished(IEntity pItem) {
				gameOverItemsEntity[0].registerEntityModifier(new ScaleModifier(0.2f, 0, 1, EaseElasticOut.getInstance()) {
					@Override protected void onModifierFinished(IEntity pItem) {
						gameOverItemsEntity[1].registerEntityModifier(new ScaleModifier(0.2f, 0, 1, EaseElasticOut.getInstance()) {
							@Override protected void onModifierFinished(IEntity pItem) {
								gameOverItemsEntity[2].registerEntityModifier(new ScaleModifier(0.2f, 0, 1, EaseElasticOut.getInstance()) {
									@Override protected void onModifierFinished(IEntity pItem) {
										gameOverItemsEntity[3].registerEntityModifier(new ScaleModifier(0.2f, 0, 1, EaseElasticOut.getInstance()) {
											@Override protected void onModifierFinished(IEntity pItem) {
												gameOverItemsEntity[4].registerEntityModifier(new ScaleModifier(0.2f, 0, 1, EaseElasticOut.getInstance()) {});
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
	}
	
	private void fadeToMenu() {
		Rectangle coverScreen = new Rectangle(400, 240, 800, 480, vbom);
    	coverScreen.setColor(0.1f, 0.1f, 0.1f, 0.0f);
    	gameHud.attachChild(coverScreen);
    	coverScreen.registerEntityModifier(new AlphaModifier(1, 0, 1) {
    		@Override
            protected void onModifierFinished(IEntity pItem) {
                    super.onModifierFinished(pItem);
                    Intent menuActivity = new Intent(activity, MainActivity.class);
                    menuActivity.putExtra("showLogo", false);
             	   	activity.startActivity(menuActivity);
             	   	activity.finish();
             	   	android.os.Process.killProcess(android.os.Process.myPid());
    		}
    	});
	}

}
