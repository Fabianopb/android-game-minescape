package objects;

import managers.MainManager;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.visivaemobile.minescape.R;

public class Credits extends Entity {
	
	protected MainManager mm = MainManager.getInstance();
	protected VertexBufferObjectManager vbom = mm.vbom;
	public Rectangle closeDialogRect;
	public boolean touchableCredits = true;
	
	public Credits(float pX, float pY) {
		super(pX, pY);
		createChildren();
		registerEntityModifier(new ScaleModifier(0, 1, 0));
	}
	
	private void createChildren() {
		
		Rectangle creditsBackground = new Rectangle(0, 0, 800, 480, vbom);
    	creditsBackground.setColor(0.1f, 0.1f, 0.1f);
    	attachChild(creditsBackground);
    	
    	Text creditsTitle = new Text(0, 210, mm.greenscrFont, mm.activity.getResources().getString(R.string.credits), vbom);
    	attachChild(creditsTitle);
    	
    	Text creditsVisivae = new Text(-380, 90, mm.bitlightFont, mm.activity.getResources().getString(R.string.credits_visivae), vbom);
    	creditsVisivae.setAnchorCenterX(0);
    	attachChild(creditsVisivae);
    	
    	Text creditsMusic = new Text(-380, 30, mm.bitlightFont, mm.activity.getResources().getString(R.string.credits_music), vbom);
    	creditsMusic.setAnchorCenterX(0);
    	attachChild(creditsMusic);
    	
    	Text creditsSoundEffect = new Text(-380, -100, mm.bitlightFont, mm.activity.getResources().getString(R.string.credits_sfx), vbom);
    	creditsSoundEffect.setAnchorCenterX(0);
    	attachChild(creditsSoundEffect);
    	
    	Sprite creditsLogo = new Sprite(0, 150, mm.logoTexture, vbom);
    	creditsLogo.setScale(0.4f);
    	attachChild(creditsLogo);
    	
    	Sprite visivaeLogo = new Sprite(300, -200, mm.visivaeMiniTexture, vbom);
    	attachChild(visivaeLogo);
    	
    	closeDialogRect = new Rectangle(-375, 215, 46, 46, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				switch(pTouchEvent.getAction()) {			
				case TouchEvent.ACTION_UP:
					if(touchableCredits) {
						mm.buttonSound.play();
						hideCreditsEntity();
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
		
	}
	
	public void showCreditsEntity() {
    	registerEntityModifier(new ScaleModifier(0.2f, 0, 1));
    	mm.activity.creditsEntityIsOnTop = true;
    	touchableCredits = true;
    }
    
    public void hideCreditsEntity() {
    	touchableCredits = false;
    	mm.activity.creditsEntityIsOnTop = false;
    	registerEntityModifier(new ScaleModifier(0.2f, 1, 0) {
    		@Override protected void onModifierFinished(IEntity pItem) {
    			mm.mainScene.showMenu();
    		}
    	});    	
    }

}
