package the.trav.hookup;

import java.net.UnknownHostException;
import java.util.HashMap;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.extension.multiplayer.protocol.util.WifiUtils;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Toast;

public class HookUpActivity extends BaseGameActivity {
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;
	
	private static final int MENU_HOST = 1;
	private static final int MENU_JOIN = 2;
	private static final int MENU_SERVING = 3;
	private static final int MENU_CANCEL_SERVING = 4;
	
	private Font font = null;
	private Font font2 = null;
	private Camera camera = null; 
	
	private HashMap<String, Scene> scenes = new HashMap<String, Scene>();
	private MultiplayerServer server;
	
	@Override
	public Engine onLoadEngine() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera));
	}

	@Override
	public void onLoadResources() {
		font = loadFont();
		font2 = loadFont();
	}

	private Font loadFont() {
		BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		Font font = new Font(fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 96, true, Color.BLACK);
		this.mEngine.getTextureManager().loadTexture(fontTexture);
		this.mEngine.getFontManager().loadFont(font);
		return font;
	}

	@Override
	public Scene onLoadScene() {
		Scene scene = new Scene();		
		scene.setBackground(new ColorBackground(0.0f, 0.9f, 0.0f));

		scenes.put("main", scene);
		scenes.put("menu", this.createMenuScene(new int[]{MENU_HOST, MENU_JOIN}, new String[]{"HOST", "JOIN"}));
		scenes.put("hosting", createHostingScene()); 

		scene.setChildScene(scenes.get("menu"));
		return scene;
	}
	
	
	
	private Scene createMenuScene(int[] ids, String[] labels) {
		final Scene menuScene = new Scene();
		menuScene.setBackground(new ColorBackground(0.0f, 0.0f, 0.9f));
		for(int i=0; i< ids.length; i++) {
			addMenuItem(50, 10 + i*100, menuScene, ids[i], labels[i]);
		}

		return menuScene;
	}

	private void addMenuItem(float x, float y, final Scene menuScene, final int id, final String label) {
		final Text menuItem = new Text(x, y, font, label){
			@Override
			public boolean onAreaTouched(org.anddev.andengine.input.touch.TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				onMenuItemClicked(menuScene, id);
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			};
		};
		menuScene.attachChild(menuItem);
		menuScene.registerTouchArea(menuItem);
	}
	
	private Scene createHostingScene() {
		Scene hostingScene = new Scene();
		hostingScene.setBackground(new ColorBackground(0.9f, 0.0f, 0.0f));
		Text text = new Text(10, 310, font2, "Waiting...");
		hostingScene.attachChild(text);
		return hostingScene;
	}
	
	private void startHosting() {
		Scene scene = scenes.get("menu");
		Scene hostingScene = scenes.get("hosting");
		scene.setChildScene(hostingScene);
		try {
			String ipAddress = WifiUtils.getWifiIPv4Address(this);
			Debug.i(ipAddress);
			Text text = new Text(10, 400, font2, ipAddress);
			hostingScene.attachChild(text);
		} catch (UnknownHostException e) {
			Debug.e("error getting IP address "+e);
		}
		server = new MultiplayerServer();
		server.startHosting(this);
	}

	@Override
	public void onLoadComplete() {
		
	}

	public void onMenuItemClicked(Scene menuScene, int id) {
		switch(id) {
			case MENU_HOST:
				startHosting();
				break;
				
			case MENU_JOIN:
//				scene.clearChildScene();
//				menuScene.reset();
				break;
			default :
				break;
		}
	}
	
	public void toast(final String message) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(HookUpActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	protected void onPause() {
		server.close();
		super.onPause();
	}
}