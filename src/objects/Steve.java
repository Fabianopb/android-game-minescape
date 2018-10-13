package objects;

import managers.GameManager;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scenes.GameScene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Steve extends AnimatedSprite {
	
	public AnimatedSprite mAnimatedSprite;
	public Body steveBody;
	private GameManager gm = GameManager.getInstance();
	public PhysicsWorld mStevePhysics;
	public PhysicsConnector mSteveConnector;
	private Camera mCamera;
	public static float playerSpeed;
	public boolean isAttacking;
	public boolean isBeingDamaged;
	public int steveHealth;
	public int steveAttackValue;
	public float steveArmorValue;
	private long[] walkingLong = {150, 200, 150, 200};
	private long[] attackingLong = {130, 130};
	private long[] damagedLong = {150, 150};
	
	/* armor code 0 = no armor */
	/* armor code 2 = iron armor */
	/* armor code 4 = gold armor */
	/* armor code 6 = diamond armor */
	/* armor code 8 = obsidian armor */
	public int steveArmorCode;
	/* sword code 0 = no sword */
	/* sword code 8 = iron sword */
	/* sword code 16 = gold sword */
	/* sword code 24 = diamond sword */
	/* sword code 32 = obsidian sword */
	public int steveSwordCode;
	
	public Steve(float pX, float pY, VertexBufferObjectManager vbo, Camera pCamera, PhysicsWorld physicsWorld) {
        super(pX, pY, GameManager.getInstance().steveTiledTexture, vbo);
        mAnimatedSprite = this;
        steveArmorCode = 0;
        steveSwordCode = 0;
        updateArmorAndWeapon();
        mAnimatedSprite.animate(walkingLong, FirstWalkingTileIndex, LastWalkingTileIndex, true);
        mStevePhysics = physicsWorld;
        mCamera = pCamera;
        playerSpeed = GameScene.currentSpeed;
        isAttacking = false;
        isBeingDamaged = false;
        steveHealth = 100;
        createPhysics(mCamera);
        mCamera.setChaseEntity(this);
	}
	
	private void createPhysics(final Camera camera) {        
	    steveBody = PhysicsFactory.createBoxBody(mStevePhysics, this.getX(), this.getY(), this.getHeight() / 2 - 10, this.getHeight() - 6, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.0f, 0.0f, 0.0f, false, (short)1, (short)1, (short)0));
	    steveBody.setUserData("steve");
	    steveBody.setFixedRotation(true);
	    
	    mSteveConnector = new PhysicsConnector(this, steveBody, true, false) {
	    	@Override
	        public void onUpdate(float pSecondsElapsed) {
	            super.onUpdate(pSecondsElapsed);
	            //coloca o jogador em movimento
	            steveBody.setLinearVelocity(new Vector2(playerSpeed, steveBody.getLinearVelocity().y));
	        }
	    };
	    mStevePhysics.registerPhysicsConnector(mSteveConnector);
	}
	
	public void jump() {
		//pula de acordo com a velocidade em Y
		steveBody.setLinearVelocity(new Vector2(steveBody.getLinearVelocity().x, 7.0f));
		gm.gameScene.steveIsOnFloor++;
	}
	
	public void attack() {
		mAnimatedSprite.animate(attackingLong, FirstAttackingTileIndex, LastAttackingTileIndex, true, new IAnimationListener() {
			int nLoops = 2;
			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
				isAttacking = true;
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
				isAttacking = false;
				mAnimatedSprite.animate(walkingLong, FirstWalkingTileIndex, LastWalkingTileIndex, true);
			}
			
		});
		
	}
	
	public void loseHealth(final int health) {
		isAttacking = false;
		mAnimatedSprite.animate(damagedLong, FirstBeingDamagedTileIndex, LastBeingDamagedTileIndex, true, new IAnimationListener() {
			int nLoops = 4;
			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
				
				gm.ohhhSound.play();
				
				steveHealth -= health * (1 - steveArmorValue);
				
				if(steveArmorCode > 0) gm.gameScene.updateArmorResistance(1);
				
				if(steveHealth <= 90) gm.gameScene.healthTiledSprite[4].setCurrentTileIndex(1);
				if(steveHealth <= 80) gm.gameScene.healthTiledSprite[4].setCurrentTileIndex(2);
				if(steveHealth <= 70) gm.gameScene.healthTiledSprite[3].setCurrentTileIndex(1);
				if(steveHealth <= 60) gm.gameScene.healthTiledSprite[3].setCurrentTileIndex(2);
				if(steveHealth <= 50) gm.gameScene.healthTiledSprite[2].setCurrentTileIndex(1);
				if(steveHealth <= 40) gm.gameScene.healthTiledSprite[2].setCurrentTileIndex(2);
				if(steveHealth <= 30) gm.gameScene.healthTiledSprite[1].setCurrentTileIndex(1);
				if(steveHealth <= 20) gm.gameScene.healthTiledSprite[1].setCurrentTileIndex(2);
				if(steveHealth <= 10) gm.gameScene.healthTiledSprite[0].setCurrentTileIndex(1);
				if(steveHealth <= 0) gm.gameScene.healthTiledSprite[0].setCurrentTileIndex(2);
				
				isBeingDamaged = true;
				playerSpeed = -4.0f;
				steveBody.setLinearVelocity(new Vector2(steveBody.getLinearVelocity().x, 8.0f));
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
				
				if(gm.gameScene.armorResistance <= 0) updateArmorAndWeapon();
				
				if(steveHealth <=0) {
					mAnimatedSprite.stopAnimation(FirstBeingDamagedTileIndex);
					gm.gameScene.gameOver();
				}
				else {
					mAnimatedSprite.animate(walkingLong, FirstWalkingTileIndex, LastWalkingTileIndex, true);
					playerSpeed = GameScene.currentSpeed;
					isBeingDamaged = false;
				}
			}
			
		});
	}
	
	private int FirstWalkingTileIndex;
	private int LastWalkingTileIndex;
	private int FirstAttackingTileIndex;
	private int LastAttackingTileIndex;
	private int FirstBeingDamagedTileIndex;
	private int LastBeingDamagedTileIndex;
	public void updateArmorAndWeapon() {
		
		stopAnimation();
		playerSpeed = GameScene.currentSpeed;
		isBeingDamaged = false;
		isAttacking = false;
		
		//define a armadura do Steve
		if(steveArmorCode == 8) steveArmorValue = GameScene.OBSIDIAN_ARMOR_IMPACT;
		else if(steveArmorCode == 6) steveArmorValue = GameScene.DIAMOND_ARMOR_IMPACT;
		else if(steveArmorCode == 4) steveArmorValue = GameScene.GOLD_ARMOR_IMPACT;
		else if(steveArmorCode == 2) steveArmorValue = GameScene.IRON_ARMOR_IMPACT;
		else steveArmorValue = GameScene.NO_ARMOR_IMPACT;
		
		//define a força de ataque do Steve
		if(steveSwordCode == 32) steveAttackValue = GameScene.OBSIDIAN_SWORD_STRENGHT;
		else if(steveSwordCode == 24) steveAttackValue = GameScene.DIAMOND_SWORD_STRENGHT;
		else if(steveSwordCode == 16) steveAttackValue = GameScene.GOLD_SWORD_STRENGHT;
		else if(steveSwordCode == 8) steveAttackValue = GameScene.IRON_SWORD_STRENGHT;
		else steveAttackValue = GameScene.HAND_STRENGTH;
		
		//define a imagem do Steve
		FirstWalkingTileIndex = 20 * steveArmorCode + steveSwordCode;
		LastWalkingTileIndex = FirstWalkingTileIndex + 3;
		FirstAttackingTileIndex = LastWalkingTileIndex + 1;
		LastAttackingTileIndex = FirstAttackingTileIndex + 1;
		FirstBeingDamagedTileIndex = LastAttackingTileIndex + 1;
		LastBeingDamagedTileIndex = FirstBeingDamagedTileIndex + 1;
		
		mAnimatedSprite.animate(walkingLong, FirstWalkingTileIndex, LastWalkingTileIndex, true);
	}

}
