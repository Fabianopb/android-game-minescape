package scenes;

import managers.MainManager;

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

import com.visivaemobile.minescape.MainActivity;

public class MainDialogScene extends MenuScene implements IOnMenuItemClickListener {
	
	protected static final int MENU_YES = 0;
	protected static final int MENU_NO = 1;
	protected static final int MENU_OK = 2;
	
	protected Engine engine;
    protected MainActivity activity;
    protected MainManager mm;
    protected VertexBufferObjectManager vbom;
    protected Camera camera;
	
	public MainDialogScene(Camera pCamera) {
		super(pCamera);
		this.mm = MainManager.getInstance();
        this.engine = mm.engine;
        this.activity = mm.activity;
        this.vbom = mm.vbom;
        this.camera = mm.camera;
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
    	
    	dialog = new Sprite(0, 0, mm.dialogTexture, vbom);
		attachChild(dialog);
		
		buttonA = new SpriteMenuItem(MENU_YES, mm.buttonTexture, vbom);
		buttonA.setPosition(-110, -65);
		buttonA.setScale(0.6f);
		addMenuItem(buttonA);

		buttonB = new SpriteMenuItem(MENU_NO, mm.buttonTexture, vbom);
		buttonB.setPosition(110, -65);
		buttonB.setScale(0.6f);
		addMenuItem(buttonB);
		
		buttonC = new SpriteMenuItem(MENU_OK, mm.buttonTexture, vbom);
		buttonC.setPosition(0, -65);
		buttonC.setScale(0.6f);
		addMenuItem(buttonC);
		
		title = new Text(-220, 80, mm.greenscrFont, mTitle, 30, vbom);
		title.setAnchorCenter(0, 0);
		title.setScale(0.5f);
		attachChild(title);
		
		TextOptions options = new TextOptions(AutoWrap.WORDS, 440);
		message = new Text(-240, 0, mm.bitlightFont, mMessage, 200, options, vbom);
		message.setAnchorCenter(0, 0);
		attachChild(message);
		
		yes = new Text(-110, -65, mm.greenscrFont, mYes, 10, vbom);
		yes.setScale(0.6f);
		attachChild(yes);
		
		no = new Text(110, -65, mm.greenscrFont, mNo, 10, vbom);
		no.setScale(0.6f);
		attachChild(no);
		
		ok = new Text(0, -65, mm.greenscrFont, mOk, 10, vbom);
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
    					activity.mainDialogCallback(mCallbackId, action);
    				}
    			});
    		}
    	});
    }
    
    public void setParameters(int pDialogType, String pTitle, String pMessage, String pYes, String pNo, int pCallbackId) {
    	mDialogType = pDialogType;
    	mCallbackId = pCallbackId;
    	title.setText(pTitle);
    	message.setText(pMessage);
    	yes.setText(pYes);
    	no.setText(pNo);
    	ok.setText("OK");
    	
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
    	
    	float messageY = message.getHeight();
		message.setPosition(-220, title.getY() - messageY - 20);
		
    }

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
		case MENU_YES:
			if(yes.isVisible() && isShown) {
				mm.buttonSound.play();
				dismiss(true);
			}
			return true;
		case MENU_NO:
			if(no.isVisible() && isShown) {
				mm.buttonSound.play();
				dismiss(false);
			}
			return true;
		case MENU_OK:
			if(ok.isVisible() && isShown) {
				mm.buttonSound.play();
				dismiss(false);
			}
			return true;
		default:
			return false;
		}
	}

}
