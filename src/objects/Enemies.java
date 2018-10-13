package objects;

import java.util.Random;

import managers.GameManager;

import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.GameScene;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Enemies extends AnimatedSprite {
	
	private GameManager gm = GameManager.getInstance();
	private AnimatedSprite mAnimatedSprite;
	private Body mBody;
	private PhysicsConnector mPhysicsConnector;
	public boolean ignoreEnemiesCollision;
	private int mHealth;
	public boolean killThisEnemy;
	private int mId;
	private long[][] animationDefault = {{200, 200, 500, 200}, {800, 200, 500, 200}, {1000, 200, 200, 500}};
	private long[] animationDamage = {100, 100};
	
	public Enemies(int pId, int pHealth, float pX, float pY, ITiledTextureRegion mTexture, VertexBufferObjectManager vbom) {
		super(pX, pY, mTexture, vbom);
		mId = pId;
		mAnimatedSprite = this;		
		mAnimatedSprite.animate(animationDefault[mId], 0, 3, true);
		ignoreEnemiesCollision = false;
		mHealth = pHealth;
		killThisEnemy = false;
		createBody();
	}
	
	private void createBody() {
		mBody = PhysicsFactory.createBoxBody(gm.gameScene.physicsWorld, this.getX(), this.getY(), this.getHeight() - 6, this.getHeight() - 6, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.0f, 0.2f, 0.8f, false, (short)4, (short)1, (short)0));
		mPhysicsConnector = new PhysicsConnector(this, mBody, true, false);
		gm.gameScene.physicsWorld.registerPhysicsConnector(mPhysicsConnector);
	}
	
	public void jumpBehind() {
		mBody.setLinearVelocity(5, 5);
	}
	
	public void loseHealth() {
		
		if(mId == 0) gm.zombieSound.play();
		else if(mId == 1) gm.spiderSound.play();
		else if(mId == 2) gm.skeletonSound.play();
		
		mHealth -= gm.gameScene.steve.steveAttackValue;
		
		if(gm.gameScene.steve.steveSwordCode > 0) gm.gameScene.updateSwordResistance(1);
		
		if(mHealth < 1) {
			int probBeef = (new Random()).nextInt(100);
			if(probBeef < GameScene.DROP_BEEF_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 5);
			else if(probBeef < GameScene.DROP_IRON_SWORD_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 6);
			else if(probBeef < GameScene.DROP_GOLD_SWORD_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 7);
			else if(probBeef < GameScene.DROP_DIAMOND_SWORD_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 8);
			else if(probBeef < GameScene.DROP_OBSIDIAN_SWORD_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 9);
			else if(probBeef < GameScene.DROP_IRON_ARMOR_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 10);
			else if(probBeef < GameScene.DROP_GOLD_ARMOR_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 11);
			else if(probBeef < GameScene.DROP_DIAMOND_ARMOR_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 12);
			else if(probBeef < GameScene.DROP_OBSIDIAN_ARMOR_PROBABILITY) gm.gameScene.dropItem(mAnimatedSprite.getX() + 30, mAnimatedSprite.getY() + 50, 13);
			killEnemies();
		}
		else {
			mAnimatedSprite.animate(animationDamage, 4, 5, true, new IAnimationListener() {
				int nLoops = 5;
				@Override
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
					ignoreEnemiesCollision = true;
					mBody.setLinearVelocity(7, 7);
				}

				@Override
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,	int pOldFrameIndex, int pNewFrameIndex) {}

				@Override
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,	int pRemainingLoopCount, int pInitialLoopCount) {
					if(nLoops > 0) nLoops--;
					else this.onAnimationFinished(mAnimatedSprite);
				}

				@Override
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
					ignoreEnemiesCollision = false;
					mAnimatedSprite.animate(animationDefault[mId], 0, 3, true);
				}
				
			});
		}		
		
	}
	
	public void killEnemies() {
		gm.gameScene.monsters++;
		ignoreEnemiesCollision = true;
		this.stopAnimation(0);
		Die die = new Die(new PointParticleEmitter(this.getX(), this.getY()), 10, 20, 300, gm.dieTexture, gm.vbom);
		die.setZIndex(10);
		gm.gameScene.attachChild(die);
		gm.gameScene.sortChildren();
		killThisEnemy = true;
	}
	
	public void removeEnemies() {
		
		this.setIgnoreUpdate(true);
		this.setVisible(false);
		
		gm.gameScene.physicsWorld.unregisterPhysicsConnector(mPhysicsConnector);
		gm.gameScene.physicsWorld.destroyBody(mBody);
		
		gm.activity.runOnUpdateThread(new Runnable() {
		    @Override
		    public void run() {
		    	gm.gameScene.detachChild(mAnimatedSprite);
		    }
		});
		this.dispose();
		System.gc();
	}

}
