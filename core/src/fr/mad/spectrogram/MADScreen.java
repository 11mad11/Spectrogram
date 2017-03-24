package fr.mad.spectrogram;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface MADScreen extends Screen, Disposable {
	/**
	 * Should be fast. Called on main loop
	 * @param vp
	 */
	public void create(Viewport vp);
	/**
	 * New size of the viewport passed in {@link #create}
	 */
	@Override
	public void resize(int width, int height);
}
