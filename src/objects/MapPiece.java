package objects;

import managers.GameManager;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;

public class MapPiece extends Sprite {
	
	private GameManager gm = GameManager.getInstance();
	private Sprite mSprite;
	private Body mBody;
	private PhysicsConnector mPhysicsConnector;

	public MapPiece(int pSegment, float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager vbom) {
		super(pX, pY, pTextureRegion, vbom);
		mSprite = this;
		mSprite.setAnchorCenter(0, 0);
		createFloorPiece(pSegment);
	}
	
	private void createFloorPiece(int mSegment) {
		mBody = gm.physicsEditorShapeLibrary.createBody(gm.shapeName[mSegment], mSprite, gm.gameScene.physicsWorld);
    	mBody.setUserData("floor");
    	mPhysicsConnector = new PhysicsConnector(mSprite, mBody, false, false);
		gm.gameScene.physicsWorld.registerPhysicsConnector(mPhysicsConnector);
	}
	
	public void removeMapPiece() {
		mSprite.setIgnoreUpdate(true);
		mSprite.setVisible(false);
		
		gm.gameScene.physicsWorld.unregisterPhysicsConnector(mPhysicsConnector);
		gm.gameScene.physicsWorld.destroyBody(mBody);
		
		gm.activity.runOnUpdateThread(new Runnable() {
		    @Override
		    public void run() {
		    	gm.gameScene.detachChild(mSprite);
		    }
		});
		mSprite.dispose();
		
	}
}
