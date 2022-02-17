package br.com.girardon.kasino;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Kasino extends ApplicationAdapter {
	private Texture kasinoAvatarImage;
	private Texture gilavatarImage;
	private Sound kasinoSound;
	private Sound kasinaoSound;
	private Sound sabadacoSound;
	private Music bgMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle gilAvatar;
	private Array<Rectangle> kasinoDrops;
	private long lastDropTime;
	private int score;
	private String scoreText;
	private BitmapFont scoreFont;

	@Override
	public void create () {
		//start score
		score = 0;

		scoreFont = new BitmapFont();

		// load the images for the kasino and the gil
		kasinoAvatarImage = new Texture(Gdx.files.internal("kasino.png"));
		gilavatarImage = new Texture(Gdx.files.internal("gil.png"));

		// load the sounds effect and the background "music"
		kasinoSound = Gdx.audio.newSound(Gdx.files.internal("kasino_sound.mp3"));
		kasinaoSound = Gdx.audio.newSound(Gdx.files.internal("kasinao_sound.mp3"));
		sabadacoSound = Gdx.audio.newSound(Gdx.files.internal("sabadaco_sound.mp3"));
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("bg_music.mp3"));

		// start the playback of the background music immediately
		bgMusic.setLooping(true);
		bgMusic.play();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the gilAvatar
		gilAvatar = new Rectangle();
		gilAvatar.x = 800 / 2 - 64 / 2; // center the gilAvatar horizontally
		gilAvatar.y = 20; // bottom left corner of the gilAvatar is 20 pixels above the bottom screen edge
		gilAvatar.width = 64;
		gilAvatar.height = 64;

		// create the kasinodrops array and spawn the first kasino
		kasinoDrops = new Array<Rectangle>();
		spawnkasinoDrop();
	}

	private void spawnkasinoDrop() {
		Rectangle kasinoDrop = new Rectangle();
		kasinoDrop.x = MathUtils.random(0, 800-64);
		kasinoDrop.y = 480;
		kasinoDrop.width = 64;
		kasinoDrop.height = 64;
		kasinoDrops.add(kasinoDrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		// clear the screen with a dark blue color. The
		// arguments to clear are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		ScreenUtils.clear(0, 0, 0.2f, 1);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the gilAvatar and
		// all drops
		batch.begin();
		scoreText = "Kasinos Score: " + score;

		scoreFont.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		scoreFont.draw(batch, scoreText, 25, 100);

		batch.draw(gilavatarImage, gilAvatar.x, gilAvatar.y);
		for(Rectangle kasinoDrop: kasinoDrops) {
			batch.draw(kasinoAvatarImage, kasinoDrop.x, kasinoDrop.y);
		}
		batch.end();

		// process user input
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			gilAvatar.x = touchPos.x - 64 / 2;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)) gilAvatar.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) gilAvatar.x += 200 * Gdx.graphics.getDeltaTime();

		// make sure the gilAvatar stays within the screen bounds
		if(gilAvatar.x < 0) gilAvatar.x = 0;
		if(gilAvatar.x > 800 - 64) gilAvatar.x = 800 - 64;

		// check if we need to create a new kasinoDrop
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnkasinoDrop();

		// move the kasinoDrops, remove any that are beneath the bottom edge of
		// the screen or that hit the gilAvatar. In the latter case we play back
		// a sound effect as well.
		for (Iterator<Rectangle> iter = kasinoDrops.iterator(); iter.hasNext(); ) {
			Rectangle kasinoDrop = iter.next();
			kasinoDrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(kasinoDrop.y + 64 < 0) iter.remove();
			if(kasinoDrop.overlaps(gilAvatar)) {
				switch (ThreadLocalRandom.current().nextInt(1, 3 + 1)) {
					case 1: kasinoSound.play(); break;
					case 2: sabadacoSound.play(); break;
					case 3: kasinaoSound.play(); break;
				}
				score=score+1;
				iter.remove();
			}
		}

	}

	@Override
	public void dispose () {
		kasinoAvatarImage.dispose();
		gilavatarImage.dispose();
		kasinoSound.dispose();
		bgMusic.dispose();
		batch.dispose();
	}
}
