package objects;

import managers.GameManager;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class CraftingTable extends Sprite {
	
	private GameManager gm = GameManager.getInstance();
	private Sprite mSprite;
	private Body mBody;
	private PhysicsConnector mPhysicsConnector;
	
	public CraftingTable(float pX, float pY, ITextureRegion pTexture, VertexBufferObjectManager vbom) {
		super(pX, pY, pTexture, vbom);
		mSprite = this;
		createBody();
	}
	
	private void createBody() {		
		mBody = PhysicsFactory.createBoxBody(gm.gameScene.physicsWorld, this.getX(), this.getY(), this.getWidth() - 2, this.getHeight() - 2, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1000.0f, 0.0f, 1.0f));
		mBody.setUserData("floor");
		mPhysicsConnector = new PhysicsConnector(this, mBody, true, false);
		gm.gameScene.physicsWorld.registerPhysicsConnector(mPhysicsConnector);
	}
	
	@Override
    protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if(gm.gameScene.tutorialActivated && gm.gameScene.firstCraftingTable && mSprite.getX() - gm.gameScene.steve.getX() < 200) {
			gm.gameScene.firstCraftingTable = false;
			gm.gameScene.circle.setPosition(mSprite.getX() - gm.gameScene.steve.getX() + 400, mSprite.getY());
			gm.gameScene.circle.setVisible(true);
			gm.gameScene.showTutorialMessage(5);
		}
		if(gm.gameScene.steve.getX() - mSprite.getX() > 500) {
			removeCraftingTable();
		}
    }
	
	@Override
	public boolean onAreaTouched(TouchEvent pTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		switch(pTouchEvent.getAction()) {		
		case TouchEvent.ACTION_UP:
			if(!gm.gameDialogScene.isShown && gm.gameScene.steve.collidesWith(mSprite) && gm.gameScene.touchEnabled) {
				gm.buttonSound.play();
				gm.gameScene.openCraftingTable();
			}
			break;
	    }
		return false;
    }
	
	public void removeCraftingTable() {
		
		gm.gameScene.canCreateCraftingTable = true;

		this.setIgnoreUpdate(true);
		this.setVisible(false);
		
		gm.gameScene.physicsWorld.unregisterPhysicsConnector(mPhysicsConnector);
		gm.gameScene.physicsWorld.destroyBody(mBody);
		
		gm.activity.runOnUpdateThread(new Runnable() {
		    @Override
		    public void run() {
		    	gm.gameScene.detachChild(mSprite);
		    }
		});
		this.dispose();
	}

}
