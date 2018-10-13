package objects;

import managers.GameManager;

import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.GameScene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Creeper extends AnimatedSprite {
	
	private AnimatedSprite mAnimatedSprite;
	private long[] animation = {200, 200, 200, 200};
	private Body creeperBody;
	private GameManager gm = GameManager.getInstance();
	private PhysicsWorld mCreeperPhysics;
	private PhysicsConnector mCreeperConnector;
	private static float creeperSpeed;
	private boolean goCreeper;
	
	public Creeper(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld) {
        super(pX, pY, GameManager.getInstance().creeperTiledTexture, vbo);
        mAnimatedSprite = this;
        mAnimatedSprite.animate(animation, 0, 3, true);
        mCreeperPhysics = physicsWorld;
        creeperSpeed = GameScene.currentSpeed;
        goCreeper = false;
        createPhysics();
	}
	
	private void createPhysics() {        
	    creeperBody = PhysicsFactory.createBoxBody(mCreeperPhysics, this.getX(), this.getY(), this.getHeight() / 2, this.getHeight() - 6, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.0f, 0.0f, 0.0f, false, (short)1, (short)1, (short)0));
	    creeperBody.setUserData("creeper");
	    creeperBody.setFixedRotation(true);
	    
	    mCreeperConnector = new PhysicsConnector(this, creeperBody, true, false) {
	    	@Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	            if(goCreeper) {
	            	if(mAnimatedSprite.collidesWith(gm.gameScene.steve)) explodeCreeper();
	            	creeperBody.setLinearVelocity(new Vector2(creeperSpeed, creeperBody.getLinearVelocity().y));
	            	if(gm.gameScene.creeperIsOnFloor == 0) jump();
		            if(mAnimatedSprite.getY() < -100) removeCreeper();
	            }
	            else if(gm.gameScene.steve.getX() - mAnimatedSprite.getX() > 200) {
	            	goCreeper = true;
	            	gm.fuseSound.play();
	            }
	            else if(gm.gameScene.tutorialActivated && gm.gameScene.firstCreeper && mAnimatedSprite.getX() - gm.gameScene.steve.getX() < 200) {
	            	gm.gameScene.firstCreeper = false;
	            	gm.gameScene.circle.setPosition(mAnimatedSprite.getX() - gm.gameScene.steve.getX() + 400, mAnimatedSprite.getY());
	    			gm.gameScene.circle.setVisible(true);
	            	gm.gameScene.showTutorialMessage(6);
				}
	        }
	    };
	    mCreeperPhysics.registerPhysicsConnector(mCreeperConnector);
	}
	
	private void jump() {
		gm.gameScene.creeperIsOnFloor++;
		creeperBody.setLinearVelocity(new Vector2(creeperBody.getLinearVelocity().x, 7.0f));
	}
	
	private void explodeCreeper() {
		Fire fire = new Fire(new PointParticleEmitter(this.getX(), this.getY()), 10, 20, 300, gm.fireTexture, gm.vbom);
		fire.setZIndex(10);
		gm.gameScene.attachChild(fire);
		gm.gameScene.sortChildren();
		gm.explosionSound.play();
		removeCreeper();
		gm.gameScene.steve.loseHealth(GameScene.CREEPER_ATTACK);
	}
	
	public void removeCreeper() {
		
		gm.gameScene.canCreateCreeper = true;
		
		this.setIgnoreUpdate(true);
		this.setVisible(false);
		
		gm.gameScene.physicsWorld.unregisterPhysicsConnector(mCreeperConnector);
		gm.gameScene.physicsWorld.destroyBody(creeperBody);
		
		gm.activity.runOnUpdateThread(new Runnable() {
		    @Override
		    public void run() {
		    	gm.gameScene.detachChild(mAnimatedSprite);
		    }
		});
		this.dispose();
		
	}

}
