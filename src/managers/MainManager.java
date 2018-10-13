package managers;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface.OnCreateResourcesCallback;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.util.debug.Debug;

import resources.ForestResources;
import resources.MenuResources;
import resources.VillageResources;
import scenes.MainDialogScene;
import scenes.MainScene;
import android.content.Context;

import com.visivaemobile.minescape.MainActivity;
import com.visivaemobile.minescape.R;

public class MainManager {
	
private static final MainManager INSTANCE = new MainManager();
    
    public Engine engine;
    public MainActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;
    
    public static void prepareManager(Engine engine, MainActivity activity, Camera camera, VertexBufferObjectManager vbom) {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    public static MainManager getInstance() {
        return INSTANCE;
    }
    
    
    //****************************//
    /* SPLASH RESOURCES AND SCENE */
    //****************************//
    
    private Scene splashScene;
	private ITexture splashFontTexture;
	private Font splashFont;
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	private Sprite splashSprite;
	
	public void createSplashResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		if(activity.getSharedPreferences("firstLaunch", Context.MODE_PRIVATE).getBoolean("firstLaunch", true)) {
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
	    	this.splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 500, 200, TextureOptions.BILINEAR);
	    	this.splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash-landscape.png", 0, 0);
	    	this.splashTextureAtlas.load();
		}
		else {
			FontFactory.setAssetBasePath("fonts/");    	
	    	splashFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			splashFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), splashFontTexture, activity.getAssets(), "greenscr.ttf", 60.0f, true, android.graphics.Color.rgb(54, 90, 51), 2, android.graphics.Color.WHITE);
			splashFont.load();
		}
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {		
		splashScene = new Scene();
		
		
		if(activity.getSharedPreferences("firstLaunch", Context.MODE_PRIVATE).getBoolean("firstLaunch", true)) {
			splashScene.setBackground(new Background(0.1f, 0.1f, 0.1f));
			splashSprite = new Sprite(activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2, splash_region, vbom);
			splashScene.attachChild(splashSprite);
		}
		else {
			splashScene.setBackground(new Background(0.1f, 0.1f, 0.1f));
			Text splashText = new Text(activity.CAMERA_WIDTH / 2, activity.CAMERA_HEIGHT / 2, splashFont, activity.getResources().getString(R.string.loading), engine.getVertexBufferObjectManager());
			splashScene.attachChild(splashText);
		}
		
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	//**************************//
	/* MAIN RESOURCES AND SCENE */
	//**************************//
	
	private TexturePackTextureRegionLibrary texturePackLibrary;
	private TexturePack texturePack;
	
	public ITextureRegion background01Texture;
	public ITextureRegion background02Texture;
	public ITextureRegion cloudsTexture;
	public ITextureRegion foregroundTexture;
	public ITextureRegion buttonTexture;
	public ITextureRegion inactiveButtonTexture;
	public ITextureRegion logoTexture;
	public ITextureRegion visivaeMiniTexture;
	public ITextureRegion dialogTexture;
	public ITextureRegion fbLikeTexture;
	public ITextureRegion shareTexture;
	public ITextureRegion rateTexture;
	public ITiledTextureRegion googlePlayTiledTexture;
	public ITiledTextureRegion achievementsTiledTexture;
	public ITextureRegion gPlayAchievementsTexture;
	public ITextureRegion gPlayLeaderboardsTexture;
	
	public ITexture greenscrTexture;
	public Font greenscrFont;
	public ITexture bitlightTexture;
	public Font bitlightFont;
	
	//public ITiledTextureRegion batTiledTexture;
	
	public void loadMainResources() {
		
		//TODO: carregar recursos de diferentes cenários
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
		cloudsTexture = texturePackLibrary.get(ForestResources.CLOUDS_ID);
		foregroundTexture = texturePackLibrary.get(ForestResources.FOREST_2_ID);
		
		//Carrega texturas do menu
		try {
			texturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/menu/").loadFromAsset(activity.getAssets(), "menu_resources.xml");
    		texturePack.loadTexture();
    		texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
	    } 
	    catch (final TexturePackParseException e) {
	        Debug.e(e);
	    }
		
		buttonTexture = texturePackLibrary.get(MenuResources.BUTTON_ID);
		inactiveButtonTexture = texturePackLibrary.get(MenuResources.BUTTON_INACTIVE_ID);
		logoTexture = texturePackLibrary.get(MenuResources.LOGO_ID);
		visivaeMiniTexture = texturePackLibrary.get(MenuResources.VISIVAE_MINI_ID);
		dialogTexture = texturePackLibrary.get(MenuResources.DIALOG_BACK_ID);
		gPlayAchievementsTexture = texturePackLibrary.get(MenuResources.ACHIEVEMENTS_BUTTON_ID);
		gPlayLeaderboardsTexture = texturePackLibrary.get(MenuResources.LEADERBOARD_BUTTON_ID);
		fbLikeTexture = texturePackLibrary.get(MenuResources.FBLIKETHUMB_ID);
		shareTexture = texturePackLibrary.get(MenuResources.SHARE_ID);
		rateTexture = texturePackLibrary.get(MenuResources.RATE_ID);
		googlePlayTiledTexture = texturePackLibrary.getTiled(MenuResources.GOOGLEPLAYBUTTON_ID, 2, 1);
		achievementsTiledTexture = texturePackLibrary.getTiled(MenuResources.ACHIEVEMENTS_TILED_ID, 4, 4);
		
		FontFactory.setAssetBasePath("fonts/");
		
		greenscrTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		greenscrFont = FontFactory.createFromAsset(activity.getFontManager(), greenscrTexture, activity.getAssets(), "greenscr.ttf", 50.0f, true, android.graphics.Color.WHITE);
		greenscrFont.load();
		
		bitlightTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		bitlightFont = FontFactory.createFromAsset(activity.getFontManager(), bitlightTexture, activity.getAssets(), "bitlight.ttf", 20.0f, true, android.graphics.Color.WHITE);
		bitlightFont.load();
	}
	
	//**************************//
	/*     VILLAGE RESOURCES    */
	//**************************//
	
	public ITextureRegion villageSmokeRegion;
	public ITextureRegion villageTextureRegion;
	public ITextureRegion villageForestRegion;
	public ITextureRegion villageLevel001Region;
	public ITextureRegion villageLevel002Region;
	public ITextureRegion villageLevel003Region;
	public ITextureRegion villageLevel004Region;
	public ITextureRegion villageLevel005Region;
	public ITextureRegion villageLevel006Region;
	public ITextureRegion villageLevel007Region;
	public ITextureRegion villageLevel008Region;
	public ITextureRegion villageLevel009Region;
	public ITextureRegion villageLevel010Region;
	public ITextureRegion villageLevel011Region;
	public ITextureRegion villageLevel012Region;
	public ITextureRegion villageLevel013aRegion;
	public ITextureRegion villageLevel013bRegion;
	public ITextureRegion villageLevel014Region;
	public ITextureRegion villageLevel015aRegion;
	public ITextureRegion villageLevel015bRegion;
	public ITextureRegion villageLevel016aRegion;
	public ITextureRegion villageLevel016bRegion;
	public ITextureRegion villageLevel017Region;
	public ITextureRegion villageLevel018Region;
	public ITextureRegion villageLevel019aRegion;
	public ITextureRegion villageLevel019bRegion;
	public ITextureRegion villageLevel020Region;
	
	public ITiledTextureRegion itemsTiledTexture;
	
	public void loadVillageResources() {
		try {
			texturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/village/").loadFromAsset(activity.getAssets(), "village_resources.xml");
    		texturePack.loadTexture();
    		texturePackLibrary = texturePack.getTexturePackTextureRegionLibrary();
	    } 
	    catch (final TexturePackParseException e) {
	        Debug.e(e);
	    }
		
		villageSmokeRegion = texturePackLibrary.get(VillageResources.PARTICLE_DEBRIS_ID);
		villageTextureRegion = texturePackLibrary.get(VillageResources.VILLAGE_ID);
		villageForestRegion = texturePackLibrary.get(VillageResources.VILLAGE_FOREST_ID);
		itemsTiledTexture = texturePackLibrary.getTiled(VillageResources.ITEMS_TILED_ID, 7, 2);
		villageLevel001Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL001_ID);
		villageLevel002Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL002_ID);
		villageLevel003Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL003_ID);
		villageLevel004Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL004_ID);
		villageLevel005Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL005_ID);
		villageLevel006Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL006_ID);
		villageLevel007Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL007_ID);
		villageLevel008Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL008_ID);
		villageLevel009Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL009_ID);
		villageLevel010Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL010_ID);
		villageLevel011Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL011_ID);
		villageLevel012Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL012_ID);
		villageLevel013aRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL013A_ID);
		villageLevel013bRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL013B_ID);
		villageLevel014Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL014_ID);
		villageLevel015aRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL015A_ID);
		villageLevel015bRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL015B_ID);
		villageLevel016aRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL016A_ID);
		villageLevel016bRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL016B_ID);
		villageLevel017Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL017_ID);
		villageLevel018Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL018_ID);
		villageLevel019aRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL019A_ID);
		villageLevel019bRegion = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL019B_ID);
		villageLevel020Region = texturePackLibrary.get(VillageResources.VILLAGE_LEVEL020_ID);
	}
	
	public Music introMusic;	
	public void createMusic() {
		MusicFactory.setAssetBasePath("mfx/");
		try {
			introMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "impact.ogg");
			introMusic.setLooping(false);
		} catch (final IOException e) {
			Debug.e(e);
		}		
	}
	
	public Sound buttonSound;
	public void createSoundEffects() {
		SoundFactory.setAssetBasePath("sfx/");
		try {
			buttonSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "click3.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	public MainScene mainScene;
	
	public void createMainScene() {		
		mainScene = new MainScene();
	}
	
	public MainDialogScene mainDialogScene;
	
	public void createMainDialogScene() {		
		mainDialogScene = new MainDialogScene(camera);
	}
	
}


