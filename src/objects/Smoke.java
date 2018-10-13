package objects;

import org.andengine.entity.Entity;
import org.andengine.entity.particle.BatchedPseudoSpriteParticleSystem;
import org.andengine.entity.particle.emitter.IParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.opengl.GLES20;

public class Smoke extends BatchedPseudoSpriteParticleSystem {
	
	public Smoke(IParticleEmitter pParticleEmitter, float pRateMinimum, float pRateMaximum, int pParticlesMaximum,	ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pParticleEmitter, pRateMinimum, pRateMaximum, pParticlesMaximum, pTextureRegion, pVertexBufferObjectManager);
		
		setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		
		addParticleInitializer(new ColorParticleInitializer<Entity>(0.2f, 0.2f, 0.2f));
		addParticleInitializer(new AlphaParticleInitializer<Entity>(0));
		addParticleInitializer(new VelocityParticleInitializer<Entity>(-3, 6, 5, 10));
		addParticleInitializer(new ExpireParticleInitializer<Entity>(3));

		addParticleModifier(new ScaleParticleModifier<Entity>(0, 2, 0.5f, 1.0f));
		addParticleModifier(new ColorParticleModifier<Entity>(0, 3, 0.2f, 1, 0.2f, 1, 0.2f, 1));
		addParticleModifier(new AlphaParticleModifier<Entity>(0, 0.1f, 0, 1));
		addParticleModifier(new AlphaParticleModifier<Entity>(2.5f, 3, 1, 0));
		
	}
	
}
