package objects;

import managers.GameManager;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class DroppedItems extends TiledSprite {
	
	private GameManager gm = GameManager.getInstance();
	private TiledSprite mTiledSprite;
	private Body mBody;
	private PhysicsConnector mPhysicsConnector;
	private int mIndex;
	
	public DroppedItems(float pX, float pY, ITiledTextureRegion pTiledTexture, VertexBufferObjectManager vbom, int itemIndex) {
		super(pX, pY, pTiledTexture, vbom);
		mTiledSprite = this;
		mIndex = itemIndex;
		mTiledSprite.setCurrentTileIndex(mIndex);
		createBody();
		animateDroppedItems();
	}
	
	private void createBody() {		
		mBody = PhysicsFactory.createBoxBody(gm.gameScene.physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.0f, 0.0f, 1.0f, false, (short)4, (short)1, (short)0));
		mPhysicsConnector = new PhysicsConnector(this, mBody, true, false);
		gm.gameScene.physicsWorld.registerPhysicsConnector(mPhysicsConnector);
	}
	
	private void animateDroppedItems() {
		mTiledSprite.registerEntityModifier(
				new LoopEntityModifier(
						new SequenceEntityModifier(
								new ScaleModifier(0.5f, 0.8f, 1.2f), 
								new ScaleModifier(0.5f, 1.2f, 0.8f)
						)
				)
		);
	}
	
	public void dropItemFromEntity() {
		mBody.setLinearVelocity(7, 7);
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	public void removeDroppedItems(boolean gotcha) {
		
		if(gotcha) {
			if(mIndex < 5) {
				gm.gameScene.resources++;
				gm.gameScene.addItem(mIndex);
			}
			else if(mIndex == 5){
				gm.gameScene.addHealth();
			}
			else if(mIndex > 5 && mIndex < 10) {
				gm.gameScene.craftingNewSword(mIndex - 6, false);
			}
			else if(mIndex >= 10) {
				gm.gameScene.craftingNewArmor(mIndex - 10, false);
			}
		}
		
		this.setIgnoreUpdate(true);
		this.setVisible(false);
		
		gm.gameScene.physicsWorld.unregisterPhysicsConnector(mPhysicsConnector);
		gm.gameScene.physicsWorld.destroyBody(mBody);
		
		gm.activity.runOnUpdateThread(new Runnable() {
		    @Override
		    public void run() {
		    	gm.gameScene.detachChild(mTiledSprite);
		    }
		});
		this.dispose();
	}

}
