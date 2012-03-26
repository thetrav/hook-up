package the.trav.hookup;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;

public class HookUpActivity extends BaseGameActivity implements IOnMenuItemClickListener {
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;
	
	private static final int MENU_HOST = 1;
	private static final int MENU_JOIN = 2;
	private static final int MENU_SERVING = 3;
	private static final int MENU_CANCEL_SERVING = 4;
	
	private Font font = null;
	private Camera camera = null; 
	
	private Scene scene = null;
	
	@Override
	public Engine onLoadEngine() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera));
	}

	@Override
	public void onLoadResources() {
		loadFont();
	}

	private void loadFont() {
		BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.font = new Font(fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 96, true, Color.BLACK);
		this.mEngine.getTextureManager().loadTexture(fontTexture);
		this.mEngine.getFontManager().loadFont(font);
	}

	@Override
	public Scene onLoadScene() {
		scene = new Scene();

		scene.setBackground(new ColorBackground(0.9f, 0.9f, 0.9f));
		
		Scene menuScene = this.createMenuScene(new int[]{MENU_HOST, MENU_JOIN}, new String[]{"HOST", "JOIN"});
		scene.setChildScene(menuScene);
		
		return scene;
	}
	
	
	
	private MenuScene createMenuScene(int[] ids, String[] labels) {
		final MenuScene menuScene = new MenuScene(this.camera);
		for(int i=0; i< ids.length; i++) {
			addMenuItem(menuScene, ids[i], labels[i]);
		}

		menuScene.buildAnimations();

		menuScene.setBackgroundEnabled(false);

		menuScene.setOnMenuItemClickListener(this);
		return menuScene;
	}

	private void addMenuItem(final MenuScene menuScene, int id, String label) {
		final IMenuItem menuItem = new ColorMenuItemDecorator(new TextMenuItem(id, this.font, label), 1.0f,0.0f,0.0f, 0.0f,0.0f,0.0f);
		menuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(menuItem);
	}
	
	private Scene startHosting() {
		Scene hostingScene = new Scene();
		hostingScene.setBackground(new ColorBackground(0.9f, 0.9f, 0.9f));
		Text text = new Text(10,10, font, "Waiting for client");
		hostingScene.attachChild(text);
		return hostingScene;
	}

	@Override
	public void onLoadComplete() {
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene menuScene, IMenuItem menuItem, float x, float y) {
		switch(menuItem.getID()) {
			case MENU_HOST:
				scene.reset();
				
				scene.clearChildScene();
				menuScene.back();
				
				scene.setChildScene(startHosting());
				scene.reset();
				return true;
				
			case MENU_JOIN:
//				scene.clearChildScene();
//				menuScene.reset();
				return true;
			default :
				return false;
		}
	}    
}