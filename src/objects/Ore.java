package objects;

import managers.GameManager;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ore extends TiledSprite {
	
	private GameManager gm = GameManager.getInstance();
	private TiledSprite mTiledSprite;
	private Body mBody;
	private PhysicsConnector mPhysicsConnector;
	private int mIndex;
	private int mResistance;
	public boolean ignoreOreCollision;
	public boolean crackThisOre;
	
	public Ore(float pX, float pY, ITiledTextureRegion pTiledTexture, VertexBufferObjectManager vbom, int itemIndex, int resistance) {
		super(pX, pY, pTiledTexture, vbom);
		mTiledSprite = this;
		mIndex = itemIndex;
		mTiledSprite.setCurrentTileIndex(mIndex);
		mResistance = resistance;
		ignoreOreCollision = false;
		crackThisOre = false;
		createBody();
	}
	
	private void createBody() {		
		mBody = PhysicsFactory.createBoxBody(gm.gameScene.physicsWorld, this.getX(), this.getY(), this.getWidth() - 2, this.getHeight() - 2, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1000.0f, 0.0f, 1.0f));
		mBody.setUserData("floor");
		mPhysicsConnector = new PhysicsConnector(this, mBody, true, false);
		gm.gameScene.physicsWorld.registerPhysicsConnector(mPhysicsConnector);
	}
	
	public void loseResistance() {
		
		ignoreOreCollision = true;
		
		mTiledSprite.registerEntityModifier(new MoveXModifier(0.05f, this.getX(), this.getX() + 10));
		
		mResistance -= gm.gameScene.steve.steveAttackValue;
		
		if(gm.gameScene.steve.steveSwordCode > 0) gm.gameScene.updateSwordResistance(1);
		
		if(mResistance < 1) {
			gm.gameScene.dropOre(mIndex, gm.itemsTiledTexture, mTiledSprite.getX() + 30, mTiledSprite.getY() + 50);
			crackOre();
		}
		gm.engine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                gm.engine.unregisterUpdateHandler(pTimerHandler);
                ignoreOreCollision = false;
            }
		}));
		
		
	}
	
	public void crackOre() {
		crackThisOre = true;
		gm.debrisSound.play();
		Debris debris = new Debris(new PointParticleEmitter(this.getX(), this.getY()), 40, 80, 250, gm.debrisTexture, gm.vbom);
		debris.setZIndex(10);
		gm.gameScene.attachChild(debris);
		gm.gameScene.sortChildren();
	}
	
	public void removeOre() {

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
