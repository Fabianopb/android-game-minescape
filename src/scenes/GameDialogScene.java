package scenes;

import managers.GameManager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.visivaemobile.minescape.GameActivity;

public class GameDialogScene extends MenuScene implements IOnMenuItemClickListener {
	
	protected static final int MENU_YES = 0;
	protected static final int MENU_NO = 1;
	protected static final int MENU_OK = 2;
	
	protected Engine engine;
    protected GameActivity activity;
    protected GameManager gm;
    protected VertexBufferObjectManager vbom;
    protected Camera camera;
	
	public GameDialogScene(Camera pCamera) {
		super(pCamera);
		this.gm = GameManager.getInstance();
        this.engine = gm.engine;
        this.activity = gm.activity;
        this.vbom = gm.vbom;
        this.camera = gm.camera;
        createScene();
        backRect.registerEntityModifier(new AlphaModifier(0, 1, 0));
        registerEntityModifier(new ScaleModifier(0, 1, 0));
	}
    
	private Rectangle backRect;
	private Sprite dialog;
	
	private Text title;
	private Text message;
	private Text yes;
	private Text no;
	private Text ok;
	private SpriteMenuItem buttonA;
    private SpriteMenuItem buttonB;
    private SpriteMenuItem buttonC;
	
    private String mTitle = "";
    private String mMessage = "";
    private String mYes = "";
    private String mNo = "";
    private String mOk = "";
    
    private float mX;
    private float mY;
    
    private int mDialogType;
    private int mCallbackId;
    public boolean isShown;
    
    public void createScene() {
    	
    	setBackgroundEnabled(false);
    	setOnMenuItemClickListener(this);
    	setPosition(400, 240);
    	
    	backRect = new Rectangle(0, 0, 800, 480, vbom);
    	backRect.setColor(0.0f, 0.0f, 0.0f, 0.5f);
    	attachChild(backRect);
    	
    	dialog = new Sprite(0, 0, gm.dialogTexture, vbom);
		attachChild(dialog);
		
		buttonA = new SpriteMenuItem(MENU_YES, gm.buttonTexture, vbom);
		buttonA.setPosition(-110, -65);
		buttonA.setScale(0.6f);
		addMenuItem(buttonA);

		buttonB = new SpriteMenuItem(MENU_NO, gm.buttonTexture, vbom);
		buttonB.setPosition(110, -65);
		buttonB.setScale(0.6f);
		addMenuItem(buttonB);
		
		buttonC = new SpriteMenuItem(MENU_OK, gm.buttonTexture, vbom);
		buttonC.setPosition(0, -65);
		buttonC.setScale(0.6f);
		addMenuItem(buttonC);
		
		title = new Text(-220, 80, gm.greenscrFont, mTitle, 40, vbom);
		title.setAnchorCenter(0, 0);
		title.setScale(0.5f);
		attachChild(title);
		
		TextOptions options = new TextOptions(AutoWrap.WORDS, 440);
		message = new Text(-240, 0, gm.bitlightFont, mMessage, 300, options, vbom);
		message.setAnchorCenter(0, 0);
		attachChild(message);
		
		yes = new Text(-110, -65, gm.greenscrFont, mYes, 10, vbom);
		yes.setScale(0.6f);
		attachChild(yes);
		
		no = new Text(110, -65, gm.greenscrFont, mNo, 10, vbom);
		no.setScale(0.6f);
		attachChild(no);
		
		ok = new Text(0, -65, gm.greenscrFont, mOk, 10, vbom);
		ok.setScale(0.6f);
		attachChild(ok);
		
		isShown = false;
    }
    
    public void show() {
    	isShown = true;
		registerEntityModifier(new ScaleModifier(0.1f, 0, 1) {
			@Override protected void onModifierFinished(IEntity pItem) {
				backRect.registerEntityModifier(new AlphaModifier(0.1f, 0, 0.5f));
			}
		});
	}
    
    public void dismiss(final boolean action) {
    	isShown = false;
    	backRect.registerEntityModifier(new AlphaModifier(0.1f, 0.5f, 0) {
    		@Override protected void onModifierFinished(IEntity pItem) {
    			registerEntityModifier(new ScaleModifier(0.1f, 1, 0) {
    				@Override protected void onModifierFinished(IEntity pItem) {
    					activity.gameDialogCallback(mCallbackId, action);
    				}
    			});
    		}
    	});
    }
    
    public void setParameters(int pDialogType, float pX, float pY, String pTitle, String pMessage, String pYes, String pNo, int pCallbackId) {
    	mDialogType = pDialogType;
    	mCallbackId = pCallbackId;
    	title.setText(pTitle);
    	message.setText(pMessage);
    	yes.setText(pYes);
    	no.setText(pNo);
    	ok.setText("OK");
    	mX = 400 - pX;
    	mY = 240 - pY;
    	
    	setPosition(mX, mY);
    	backRect.setVisible(true);
    	
    	if(mDialogType == 0) {
    		buttonA.setVisible(false);
    		yes.setVisible(false);
    		buttonB.setVisible(false);
    		no.setVisible(false);
    		buttonC.setVisible(true);
    		ok.setVisible(true);
    	}
    	else {
    		buttonA.setVisible(true);
    		yes.setVisible(true);
    		buttonB.setVisible(true);
    		no.setVisible(true);
    		buttonC.setVisible(false);
    		ok.setVisible(false);
    	}
    	
    	//*************************//
    	//   CASO PARA TUTORIAL    //
    	if(mX != 400 || mY != 240) backRect.setVisible(false);
    	
    	float messageY = message.getHeight();
		message.setPosition(-220, title.getY() - messageY - 20);
    }

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
		case MENU_YES:
			if(yes.isVisible() && isShown) {
				gm.buttonSound.play();
				dismiss(true);
			}
			return true;
		case MENU_NO:
			if(no.isVisible() && isShown) {
				gm.buttonSound.play();
				dismiss(false);
			}
			return true;
		case MENU_OK:
			if(ok.isVisible() && isShown) {
				gm.buttonSound.play();
				dismiss(false);
			}
			return true;
		default:
			return false;
		}
	}

}
