package net.threebit.jvr;

/*
 * $Id: DX_XPB.java,v 1.15 2004/12/27 21:58:09 kevino Exp $
 *
 * Copyright (c) 2003,2004 Kevin O'Donnell
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,  USA.
 */

/**
 * Java representation of the DX_XPB struct.
 */

import java.io.*;
import javax.sound.sampled.*;

public class DX_XPB extends JvrJni {
	static { jvr.foo(); } // importSymbols() hack until a better solution is available.

	public int wFileFormat;
	public long nSamplesPerSec;
	public long wBitsPerSample;
	public long wDataFormat;

	/** Native buffer offset. */
	private int offset = -1;

	public DX_XPB() throws JVRException {
		// when this runs (not guaranteed anyway).  Ergo,
		// all classes and fields are NULL!  arg.  Need sleep
		synchronized (DX_XPB.class) { allocate(); }
	}

	public DX_XPB (int wFileFormat, long nSamplesPerSec, long wBitsPerSample, long wDataFormat) throws JVRException {

		if ((wDataFormat == jvr.DATA_FORMAT_MULAW ||
			wDataFormat == jvr.DATA_FORMAT_ALAW ||
			wDataFormat == jvr.DATA_FORMAT_PCM) && wBitsPerSample != 8) {
			throw new IllegalArgumentException("bitsPerSample must be 8 for ALAW/ULAW/PCM formats");
		}


		this.wFileFormat = wFileFormat;
		this.wDataFormat = wDataFormat;
		this.nSamplesPerSec = nSamplesPerSec;
		this.wBitsPerSample = wBitsPerSample;
		synchronized (DX_XPB.class) { allocate(); }
	}

	/**
	 *
	 */
	public String toString() {
		return "DX_XPB{"+wFileFormat+";"+wDataFormat+";"+nSamplesPerSec+";"+wBitsPerSample+";offset="+offset+"}";
	}

	/**
	 * Returns an often required DX_XPB structure for wave playback and recording.
	 * Equivalent to the following:
	 * <pre class="code">
	 * return new DX_XPB(
	 *   dx.FILE_FORMAT_WAVE,
	 *   dx.DRT_11KHZ,
	 *   8,
	 *   dx.DATA_FORMAT_PCM
	 * );</pre>
	 */
	public static DX_XPB waveFormat() throws JVRException {
		// TODO: 8bit mono mu-law
		return new DX_XPB(dx.FILE_FORMAT_WAVE, dx.DRT_11KHZ, 8, dx.DATA_FORMAT_PCM);
	}

	/**
	 * Allocates a native-context DX_XPB structure.  This method is marked
	 * private since it is called automatically by the constructor.
	 */
	private native void allocate() throws JVRException;

	/**
	 * Releases the native-context DX_XPB structure.
	 */
	public native void release() throws JVRException;

	/**
	 * Ensures that the DX_XPB structure allocated to this instance is released.
	 */
	public void finalize () {
		try {
			synchronized (DX_XPB.class) {
				// logger.info("offset: " + offset);
				release();
				// logger.info("offset: " + offset);
			}
		}
		catch (JVRException e) {
			// The best we can do is log the exception
			logger.throwing(DX_XPB.class.getName(),"finalize",e);
		}
	}

	/**
	 * Returns a DX_XPB object suitable for use with the specified
	 * audio file.  If the file is not supported then a JVRException 
	 * is thrown.
	 */
	public static DX_XPB getWavFormat (File file) throws JVRException {
		try {
			int fileFormat;
			long dataFormat;
			long samplesPerSec;
			long bitsPerSample;

			// Determine the format/validity of the WAV file
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
			AudioFormat	audioFormat = audioInputStream.getFormat();
			if (audioFormat.getChannels() != 1) {
				throw new JVRException("File must be in 'mono'");
			}

			// This is static.
			fileFormat = jvr.FILE_FORMAT_WAVE;

			// Data (encoding) format
			AudioFormat.Encoding encoding = audioFormat.getEncoding();
			if (encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
				dataFormat = jvr.DATA_FORMAT_PCM;
			}
			else if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
				dataFormat = jvr.DATA_FORMAT_PCM;
			}
			else if (encoding.equals(AudioFormat.Encoding.ULAW)) {
				dataFormat = jvr.DATA_FORMAT_MULAW;
			}
			else if (encoding.equals(AudioFormat.Encoding.ALAW)) {
				dataFormat = jvr.DATA_FORMAT_ALAW;
			}
			else { throw new JVRException("Invalid encoding: '"+encoding+"'"); }

			// Frame Rate (samples per second)
			float frameRate = audioFormat.getFrameRate();
			final float KHZ_6 = 6000;
			final float KHZ_8 = 8000;
			final float KHZ_11 = 11025;
			if (frameRate == KHZ_6) { samplesPerSec = jvr.DRT_6KHZ; }
			else if (frameRate == KHZ_8) { samplesPerSec = jvr.DRT_8KHZ; }
			else if (frameRate == KHZ_11) { samplesPerSec = jvr.DRT_11KHZ; }
			else { throw new JVRException("Invalid frame rate: " + frameRate); }
			audioFormat.getSampleSizeInBits();

			// Bits per sample
			bitsPerSample = audioFormat.getSampleSizeInBits();

			return new DX_XPB (fileFormat, samplesPerSec, bitsPerSample, dataFormat);
		}
		catch (IOException ex) { throw new JVRException(ex); }
		catch (UnsupportedAudioFileException ex) { throw new JVRException(ex); }
	}

}
