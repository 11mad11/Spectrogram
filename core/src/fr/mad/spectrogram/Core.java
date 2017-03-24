package fr.mad.spectrogram;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Core extends ApplicationAdapter {
	private SpectogramScreen ss;
	private Viewport ssvp;
	
	@Override
	public void create() {
		ss = new SpectogramScreen();
		ssvp = new FillViewport(1, 1);
		ss.create(ssvp);
		ss.show = true;
	}
	
	@Override
	public void render() {
		ss.render(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void resize(int width, int height) {
		ssvp.update(width, height, true);
		ss.resize(width, height);
	}
}
