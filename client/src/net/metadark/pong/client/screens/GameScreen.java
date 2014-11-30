package net.metadark.pong.client.screens;

import net.metadark.pong.client.ClientBall;
import net.metadark.pong.client.ClientPaddle;
import net.metadark.pong.client.PongClient;
import net.metadark.pong.shared.Paddle.Side;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameScreen extends PongScreen {
	
	private OrthographicCamera camera;
	private Music music;
	private ShapeRenderer shapeRenderer;

	private ClientBall ball;
	private ClientPaddle leftPaddle;
	private ClientPaddle rightPaddle;
	
	String leftName = "MetaDark";
	String rightName = "Kurt";
	
	int leftScore = 20;
	int rightScore = 0;
	
	private BitmapFont titleFont;
	private SpriteBatch spriteBatch;

	public GameScreen(Game game) {
		super(game);
	}
	
	@Override
	public void show() {

		// Setup the game camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// Load and play music
		music = Gdx.audio.newMusic(Gdx.files.internal("song.ogg"));
		music.setLooping(true);
		music.setVolume(0.3f);
		music.play();

		Sound bounce = Gdx.audio.newSound(Gdx.files.internal("bounce.ogg"));

		// Start the client
		PongClient client = new PongClient(this);
		
		// Setup the shape render and the objects
		shapeRenderer = new ShapeRenderer();
		leftPaddle = new ClientPaddle(client, camera, Side.LEFT);
		rightPaddle = new ClientPaddle(client, camera, Side.RIGHT);
		ball = new ClientBall(camera, leftPaddle, rightPaddle, bounce);

		// Handle player input
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.UP:
					leftPaddle.moveUp(true);
					break;
				case Keys.DOWN:
					leftPaddle.moveDown(true);
					break;
				}
				
				return true;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				switch (keycode) {
				case Keys.UP:
					leftPaddle.moveUp(false);
					break;
				case Keys.DOWN:
					leftPaddle.moveDown(false);
					break;
				}
				
				return true;
			}
		});
		
		renderFonts();
	    
	    // Create sprite batch
	    spriteBatch = new SpriteBatch();
	    
	}
	
	@Override
	public void render(float delta) {
		
		// Reset display
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		float height = camera.viewportHeight - 10;
		titleFont.draw(spriteBatch, leftName, 10, height);
		titleFont.draw(spriteBatch, Integer.toString(leftScore), camera.viewportWidth / 2 - 30, height);
		titleFont.draw(spriteBatch, rightName, camera.viewportWidth / 2 + 10, height);
		titleFont.draw(spriteBatch, Integer.toString(rightScore), camera.viewportWidth - 30, height);
		spriteBatch.end();

		// Start drawing shapes
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);

		// Draw separator
		shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
		for (int i = 0; i < 24; i++) {
			shapeRenderer.rect((camera.viewportWidth - 10) / 2 + 2.5f, i * 20, 5, 10);
		}

		// Draw ball and paddles
		ball.render(shapeRenderer);
		leftPaddle.render(shapeRenderer);
		rightPaddle.render(shapeRenderer);

		// Stop drawing shapes
		shapeRenderer.end();

	}
	
	
	private void renderFonts() {
		FileHandle fontFile = Gdx.files.internal("LogoCraft.ttf");
	    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
	    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
	    titleFont = generator.generateFont(parameter);
	    generator.dispose();
	}
	
	
	public ClientBall getBall() {
		return ball;
	}

	public ClientPaddle getLeftPaddle() {
		return leftPaddle;
	}

	public ClientPaddle getRightPaddle() {
		return rightPaddle;
	}

}