package fr.mad.spectrogram;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class DesktopAudioRecorder implements AudioRecorder {
	private TargetDataLine line;
	private byte[] buffer = new byte[1024 * 4];
	
	public DesktopAudioRecorder(int samplingRate, boolean isMono) {
		try {
			AudioFormat format = getFormat(samplingRate, isMono);
			line = AudioSystem.getTargetDataLine(format);
			line.open(format, buffer.length);
			line.start();
		} catch (Exception ex) {
			throw new GdxRuntimeException("Error creating JavaSoundAudioRecorder.", ex);
		}
	}
	
	private AudioFormat getFormat(int samplingRate, boolean isMono) {
		int sampleSizeInBits = 16;
		int channels = isMono ? 1 : 2; //mono
		boolean bigEndian = false;
		return new AudioFormat(Encoding.PCM_SIGNED,samplingRate, sampleSizeInBits, channels, 2, samplingRate, bigEndian);
	}
	
	public void read(short[] samples, int offset, int numSamples) {
		if (buffer.length < numSamples * 2)
			buffer = new byte[numSamples * 2];
		
		int toRead = numSamples * 2;
		int read = 0;
		while (read != toRead)
			read += line.read(buffer, read, toRead - read);
		
		for (int i = 0, j = 0; i < numSamples * 2; i += 2, j++)
			samples[offset + j] = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xff));
	}
	
	public void dispose() {
		line.close();
	}
}
