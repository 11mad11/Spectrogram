package fr.mad.spectrogram;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.runtime.font.BitmapFontProvider;

public class SpectogramScreen implements MADScreen {
	public int sample = (int) Math.pow(2, 9);
	private SpriteBatch batch;
	private Texture img;
	private AudioRecorder ar;
	private AudioSampler as;
	private Viewport vp;
	private boolean error;
	private String errorText;
	private BitmapFont font;
	public boolean show;
	
	@Override
	public void create(Viewport vp) {
		this.vp = vp;
	}
	
	public SpectogramScreen() {
		try {
			batch = new SpriteBatch();
			//vp = new FitViewport(500, (float) Math.pow(2, 9));
			switch (Gdx.app.getType()) {
				case Android:
					break;
				case Applet:
					break;
				case Desktop:
					ar = new DesktopAudioRecorder(4410, true);
					break;
				case HeadlessDesktop:
					break;
				case WebGL:
					break;
				case iOS:
					break;
				default:
					break;
				
			}
			if (ar == null)
				ar = Gdx.audio.newAudioRecorder(44100, true);
			
			as = new AudioSampler(ar, img);
		} catch (Exception e) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream ps;
			e.printStackTrace(ps = new PrintStream(bos));
			ps.close();
			errorText = new String(bos.toByteArray());
			font = new BitmapFont();
			error = true;
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void resize(int width, int height) {
		img = new Texture(new Pixmap((int) (sample * (vp.getWorldWidth() / vp.getWorldHeight())), sample, Format.RGBA8888));
		as.resize(img);
		if(show&&!as.isAlive()){
			as.stop = false;
			as.start();
		}
			
	}
	
	@Override
	public void dispose() {
		as.stop = true;
		batch.dispose();
		ar.dispose();
		img.dispose();
	}
	
	@Override
	public void show() {
		as.stop = false;
		as.start();
		show = true;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (error) {
			batch.setProjectionMatrix(vp.getCamera().combined);
			batch.begin();
			font.draw(batch, errorText, 0, 0);
			batch.end();
			return;
		}
		as.repaint();
		batch.setProjectionMatrix(vp.getCamera().combined);
		batch.begin();
		batch.draw(img, 0, 0, vp.getWorldWidth(), vp.getWorldHeight());
		batch.end();
	}
	
	@Override
	public void pause() {
		as.stop = true;
	}
	
	@Override
	public void resume() {
		as.stop = false;
		as.start();
	}
	
	@Override
	public void hide() {
		as.stop = true;
		show = false;
	}
}
