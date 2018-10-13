package managers;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.util.debug.Debug;

import resources.ForestResources;
import resources.GameResources;
import resources.PhysicsEditorShapeLibrary;
import scenes.GameDialogScene;
import scenes.GameScene;

import com.badlogic.gdx.physics.box2d.Body;
import com.visivaemobile.minescape.GameActivity;
import com.visivaemobile.minescape.R;

public class GameManager {
	
	private static final GameManager INSTANCE = new GameManager();
	
	public Engine engine;
    public GameActivity activity;
    public BoundCamera camera;
    public VertexBufferObjectManager vbom;
    
    public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom) {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    public static GameManager getInstance() {
        return INSTANCE;
    }
    
    //****************************//
    /* SPLASH RESOURCES AND SCENE */
    //****************************//
    
    private Scene splashScene;
	private ITexture splashFontTexture;
	private Font splashFont;
	
	public void createSplashResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		FontFactory.setAssetBasePath("fonts/");    	
    	splashFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		splashFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), splashFontTexture, activity.getAssets(), "greenscr.ttf", 60.0f, true, android.graphics.Color.rgb(54, 90, 51), 2, android.graphics.Color.WHITE);
		splashFont.load();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {		
		splashScene = new Scene();
		
		splashScene.setBackground(new Background(0.1f, 0.1f, 0.1f));
		Text splashText = new Text(activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2, splashFont, activity.getResources().getString(R.string.loading), engine.getVertexBufferObjectManager());
		splashScene.attachChild(splashText);
		
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	
	
	//****************************//
    /* GAME RESOURCES AND SCENE */
    //****************************//
	
	private TexturePackTextureRegionLibrary texturePackLibrary;
	private TexturePack texturePack;
	public PhysicsEditorShapeLibrary physicsEditorShapeLibrary;
	
	public ITextureRegion background01Texture;
	public ITextureRegion background02Texture;
	public ITextureRegion backgroundClouds;
	public static final int MAX = 10;
	public ITextureRegion[] mapaPieceTexture = new ITextureRegion[MAX];	
	public Sprite[] mapaSprite = new Sprite[MAX];
	public Body[] mapaBody = new Body[MAX];
	public PhysicsConnector[] mapaConnector = new PhysicsConnector[MAX];
	public String[] shapeName = { "forest-0", "forest-1", "forest-2", "forest-3", "forest-4", "forest-5" };
	
	public void loadScenarioResources() {
		try {
			texturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/scenario/forest/").loadFromAsset(activity.getAssets(), "forest_resources.xml");
    		texturePack.loadTexture();
    		texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
	    } 
	    catch (final TexturePackParseException e) {
	        Debug.e(e);
	    }
		
		background01Texture = texturePackLibrary.get(ForestResources.BACKGROUND1_ID);
		background02Texture = texturePackLibrary.get(ForestResources.BACKGROUND2_ID);
		backgroundClouds = texturePackLibrary.get(ForestResources.CLOUDS_ID);
		mapaPieceTexture[0] = texturePackLibrary.get(ForestResources.FOREST_0_ID);
		mapaPieceTexture[1] = texturePackLibrary.get(ForestResources.FOREST_1_ID);
		mapaPieceTexture[2] = texturePackLibrary.get(ForestResources.FOREST_2_ID);
		mapaPieceTexture[3] = texturePackLibrary.get(ForestResources.FOREST_3_ID);
		mapaPieceTexture[4] = texturePackLibrary.get(ForestResources.FOREST_4_ID);
		mapaPieceTexture[5] = texturePackLibrary.get(ForestResources.FOREST_5_ID);
		
		physicsEditorShapeLibrary = new PhysicsEditorShapeLibrary();
        physicsEditorShapeLibrary.open(activity, "gfx/scenario/forest/forest_shapes.xml");
	}
	
	
	public ITiledTextureRegion steveTiledTexture;
	public ITiledTextureRegion zombieTiledTexture;
	public ITiledTextureRegion spiderTiledTexture;
	public ITiledTextureRegion skeletonTiledTexture;
	public ITiledTextureRegion creeperTiledTexture;
	public ITiledTextureRegion healthTiledTexture;
	public ITiledTextureRegion helmentTiledTexture;
	public ITiledTextureRegion swordTiledTexture;
	public ITiledTextureRegion oreTiledTexture;
	public ITiledTextureRegion itemsTiledTexture;
	
	public ITextureRegion craftingTableTexture;
	public ITextureRegion buttonTexture;
	public ITextureRegion dialogTexture;
	public ITextureRegion fireTexture;
	public ITextureRegion debrisTexture;
	public ITextureRegion dieTexture;
	public ITextureRegion circleTexture;
	
	public ITexture greenscrTexture;
	public Font greenscrFont;
	public ITexture bitlightTexture;
	public Font bitlightFont;
	
	public void loadGameResources() {		
		try {
			texturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/game/").loadFromAsset(activity.getAssets(), "game_resources.xml");
    		texturePack.loadTexture();
    		texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
	    } 
	    catch (final TexturePackParseException e) {
	        Debug.e(e);
	    }
		
		steveTiledTexture = texturePackLibrary.getTiled(GameResources.STEVE_TILED_ID, 20, 10);
		zombieTiledTexture = texturePackLibrary.getTiled(GameResources.ZOMBIE_TILED_ID, 6, 1);
		spiderTiledTexture = texturePackLibrary.getTiled(GameResources.SPIDER_TILED_ID, 6, 1);
		skeletonTiledTexture = texturePackLibrary.getTiled(GameResources.SKELETON_TILED_ID, 6, 1);
		creeperTiledTexture = texturePackLibrary.getTiled(GameResources.CREEPER_TILED_ID, 4, 1);
		healthTiledTexture = texturePackLibrary.getTiled(GameResources.HEALTH_TILED_ID, 3, 1);
		helmentTiledTexture = texturePackLibrary.getTiled(GameResources.HELMET_TILED_ID, 2, 2);
		swordTiledTexture = texturePackLibrary.getTiled(GameResources.SWORDS_TILED_ID, 2, 2);
		oreTiledTexture = texturePackLibrary.getTiled(GameResources.ORE_TILED_ID, 1, 5);
		itemsTiledTexture = texturePackLibrary.getTiled(GameResources.ITEMS_TILED_ID, 7, 2);
		
		craftingTableTexture = texturePackLibrary.get(GameResources.CRAFTING_TABLE_ID);
		buttonTexture = texturePackLibrary.get(GameResources.BUTTON_ID);
		dialogTexture = texturePackLibrary.get(GameResources.DIALOG_BACK_ID);
		fireTexture = texturePackLibrary.get(GameResources.PARTICLE_FIRE_ID);
		debrisTexture = texturePackLibrary.get(GameResources.PARTICLE_DEBRIS_ID);
		dieTexture = texturePackLibrary.get(GameResources.PARTICLE_DIE_ID);
		circleTexture = texturePackLibrary.get(GameResources.CIRCLE_ID);
		
		FontFactory.setAssetBasePath("fonts/");
		
		greenscrTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		greenscrFont = FontFactory.createFromAsset(activity.getFontManager(), greenscrTexture, activity.getAssets(), "greenscr.ttf", 50.0f, true, android.graphics.Color.WHITE);
		greenscrFont.load();
		
		bitlightTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		bitlightFont = FontFactory.createFromAsset(activity.getFontManager(), bitlightTexture, activity.getAssets(), "bitlight.ttf", 20.0f, true, android.graphics.Color.LTGRAY);
		bitlightFont.load();
	}
	
	public Sound buttonSound;
	public Sound attackSound;
	public Sound ohhhSound;
	public Sound stepSound;
	public Sound popSound;
	public Sound explosionSound;
	public Sound zombieSound;
	public Sound debrisSound;
	public Sound spiderSound;
	public Sound skeletonSound;
	public Sound fuseSound;
	public void createSoundEffects() {
		SoundFactory.setAssetBasePath("sfx/");
		try {
			buttonSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "click3.ogg");
			attackSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "attack.ogg");
			ohhhSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "ohhh.ogg");
			stepSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "steps.ogg");
			popSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "pop.ogg");
			explosionSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "explosion.ogg");
			zombieSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "zombie.ogg");
			debrisSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "debris.ogg");
			spiderSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "spider.ogg");
			skeletonSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "skeleton.ogg");
			fuseSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "fuse.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	public GameScene gameScene;
	
	public void createGameScene() {		
		gameScene = new GameScene();
	}
	
	public GameDialogScene gameDialogScene;
	
	public void createGameDialogScene() {		
		gameDialogScene = new GameDialogScene(camera);
	}

}
