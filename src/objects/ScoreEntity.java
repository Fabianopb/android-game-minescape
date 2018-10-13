package objects;

import managers.GameManager;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScoreEntity extends Text {
	
	private GameManager gm = GameManager.getInstance();
	private Text mText;
	private float mDuration;

	public ScoreEntity(int pScore, float pX, float pY, IFont pFont, CharSequence pText,	VertexBufferObjectManager vbom) {
		super(pX, pY, pFont, pText, vbom);
		mText = this;
		mDuration = 6.0f;
		//TODO: ganha pontos
		//gm.gameScene.addScore(pScore);
		scoreModifier(pX, pY);		
		gm.engine.registerUpdateHandler(new TimerHandler(mDuration, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                gm.engine.unregisterUpdateHandler(pTimerHandler);
                eraseText();
            }
		}));
	}
	
	private void scoreModifier(float pX, float pY) {
		mText.registerEntityModifier(new MoveModifier(mDuration, pX, pY, pX, pY + 300));
	}
	
	private void eraseText() {
		mText.setVisible(false);
		mText.setIgnoreUpdate(true);
		mText.clearEntityModifiers();
		mText.clearUpdateHandlers();
		gm.activity.runOnUpdateThread(new Runnable() {
		    @Override
		    public void run() {
		    	gm.gameScene.detachChild(mText);
		    }
		});
		mText.dispose();
		
	}

}
