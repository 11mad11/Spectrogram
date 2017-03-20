package fr.mad.spectrogram;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Core extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	private AudioRecorder ar;
	private AudioSampler as;
	private FitViewport vp;
	
	@Override
	public void create () {
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
		if(ar==null)
			ar = Gdx.audio.newAudioRecorder(44100, true);
		batch = new SpriteBatch();
		vp = new FitViewport(500, (float) Math.pow(2, 8));
		img = new Texture(new Pixmap((int)vp.getWorldWidth(), (int)vp.getWorldHeight(), Format.RGBA8888));
		
		as = new AudioSampler(ar,img);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		as.start();
		as.repaint();
		batch.setProjectionMatrix(vp.getCamera().combined);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		vp.update(width, height,true);
		System.out.println(Gdx.graphics.getFramesPerSecond());
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		ar.dispose();
		img.dispose();
	}
}
