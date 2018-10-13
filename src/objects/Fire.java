package objects;

import managers.GameManager;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.particle.BatchedPseudoSpriteParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.opengl.GLES20;

public class Fire extends BatchedPseudoSpriteParticleSystem {
	
	private GameManager gm = GameManager.getInstance();
	private BatchedPseudoSpriteParticleSystem mParticleSystem;

	public Fire(IParticleEmitter pParticleEmitter, float pRateMinimum, float pRateMaximum, int pParticlesMaximum, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pParticleEmitter, pRateMinimum, pRateMaximum, pParticlesMaximum, pTextureRegion, pVertexBufferObjectManager);
		
		mParticleSystem = this;
		
		setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

		addParticleInitializer(new ColorParticleInitializer<Entity>(1, 0, 0));
		addParticleInitializer(new AlphaParticleInitializer<Entity>(0));
		addParticleInitializer(new VelocityParticleInitializer<Entity>(-20, 20, -20, 20));
		addParticleInitializer(new RotationParticleInitializer<Entity>(0.0f, 360.0f));
		addParticleInitializer(new ExpireParticleInitializer<Entity>(3));

		addParticleModifier(new ScaleParticleModifier<Entity>(0, 2, 1.0f, 2.0f));
		addParticleModifier(new ColorParticleModifier<Entity>(0, 0.5f, 1, 1, 0, 0.5f, 0, 0));
		addParticleModifier(new ColorParticleModifier<Entity>(0.6f, 3, 1, 1, 0.5f, 1, 0, 1));
		addParticleModifier(new AlphaParticleModifier<Entity>(0, 0.1f, 0, 1));
		addParticleModifier(new AlphaParticleModifier<Entity>(2.5f, 3, 1, 0));
		
		gm.engine.registerUpdateHandler(new TimerHandler(3, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                gm.engine.unregisterUpdateHandler(pTimerHandler);
                extinguishFire();
            }
		}));
		
	}
	
	private void extinguishFire() {
		
		mParticleSystem.setParticlesSpawnEnabled(false);
		
		gm.engine.registerUpdateHandler(new TimerHandler(3, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                gm.engine.unregisterUpdateHandler(pTimerHandler);
                mParticleSystem.setVisible(false);
            	mParticleSystem.setIgnoreUpdate(true);
            	mParticleSystem.clearEntityModifiers();
            	mParticleSystem.clearUpdateHandlers();
        		gm.activity.runOnUpdateThread(new Runnable() {
        		    @Override
        		    public void run() {
        		    	gm.gameScene.detachChild(mParticleSystem);
        		    }
        		});
        		mParticleSystem.dispose();
            }
		})); 
	}

}
