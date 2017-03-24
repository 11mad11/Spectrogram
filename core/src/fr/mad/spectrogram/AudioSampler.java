package fr.mad.spectrogram;

import java.util.Arrays;

import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.Timer;

public class AudioSampler extends Thread {
	
	private static final boolean logModeEnabled = false;
	private AudioRecorder ar;
	private Gdx2DPixmap g2d;
	private Pixmap pm;
	private Texture img;
	private short[] samples;
	private Complex[][] results;
	private int times;
	private int start;
	private boolean startB;
	public boolean stop = true;
	
	public AudioSampler(AudioRecorder ar, Texture img) {
		this.ar = ar;
		if (img != null)
			resize(img);
		times = 0;
		start = 0;
		startB = false;
	}
	
	public void run() {
		while (!stop && img != null) {
			ar.read(samples, 0, samples.length);
			
			/////////////////////////////////////////////
			int cur = times;
			times++;
			Complex[] complex = new Complex[samples.length];
			for (int i = 0; i < samples.length; i++) {
				complex[i] = new Complex(samples[i], 0);
			}
			complex = fft(complex);
			results[cur] = complex;
			
			if (startB)
				start++;
			if (start >= results.length)
				start = 0;
			if (times >= results.length) {
				startB = true;
				times = 0;
			}
			/////////////////////////////////////////////
			
			for (int i = 0; i < results.length; i++) {
				int offi = i + start;
				if (offi >= results.length)
					offi -= results.length;
				int freq = 1;
				int s = 2;
				if (results[offi] != null)
					for (int line = 1; line < results[offi].length / s; line++) {
						// To get the magnitude of the sound at a given frequency slice
						// get the abs() from the complex number.
						// In this case I use Math.log to get a more managable number (used for color)
						double magnitude = Math.log(results[offi][freq].abs() + 1);
						
						// The more blue in the color the more intensity for a given frequency point:
						//g2d.setColor(new Color(0,(int)magnitude*10,(int)magnitude*20));
						if (magnitude > 40f)
							System.out.println(magnitude);
						g2d.fillRect(i, (results[offi].length - line * s), 1, s, Color.rgba8888(0f, (float) magnitude / 40f, 0f, 1f));
						//g2d.fillRect(i*blockSizeX, (size-line)*blockSizeY,blockSizeX,blockSizeY);
						
						// I used a improviced logarithmic scale and normal scale:
						if (logModeEnabled && (Math.log10(line) * Math.log10(line)) > 1) {
							freq += (int) (Math.log10(line) * Math.log10(line));
						} else {
							freq++;
						}
					}
			}
		}
		
	}
	
	public static Complex[] fft(Complex[] x) {
		int n = x.length;
		
		// base case
		if (n == 1)
			return new Complex[] { x[0] };
		
		// fft of even terms
		Complex[] even = new Complex[n / 2];
		for (int k = 0; k < n / 2; k++) {
			even[k] = x[2 * k];
		}
		Complex[] q = fft(even);
		
		// fft of odd terms
		Complex[] odd = even; // reuse the array
		for (int k = 0; k < n / 2; k++) {
			odd[k] = x[2 * k + 1];
		}
		Complex[] r = fft(odd);
		
		// combine
		Complex[] y = new Complex[n];
		for (int k = 0; k < n / 2; k++) {
			double kth = -2 * k * Math.PI / n;
			Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
			y[k] = q[k].plus(wk.times(r[k]));
			y[k + n / 2] = q[k].minus(wk.times(r[k]));
		}
		return y;
	}
	
	public void repaint() {
		img.draw(pm, 0, 0);
	}
	
	public void resize(Texture img) {
		if((img.getHeight() & (img.getHeight() - 1)) != 0)
			return;
		this.g2d = new Gdx2DPixmap(img.getWidth(), img.getHeight(), Format.toGdx2DPixmapFormat(img.getTextureData().getFormat()));
		this.pm = new Pixmap(this.g2d);
		this.img = img;
		this.samples = new short[img.getHeight()];
		if (results == null)
			results = new Complex[img.getWidth()][];
		else {
			Arrays.copyOf(results, img.getWidth());
		}
		startB = false;
	}
	
}
