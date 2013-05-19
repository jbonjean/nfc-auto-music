/* Copyright (C) 2013 Julien Bonjean <julien@bonjean.info>
 * 
 * This file is part of NFC AutoMusic.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package info.bonjean.nfcautomusic;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 
 * @author Julien Bonjean <julien@bonjean.info>
 * 
 */
public class TagReceiveTask extends AsyncTask<Void, Void, Void> {

	private static String TAG = TagReceiveTask.class.getName();
	private Activity activity;
	private int volume;
	private AudioManager audioManager;

	public TagReceiveTask(Activity activity) {
		this.activity = activity;
		audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Toast.makeText(activity.getApplicationContext(), "Tag detected", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		Toast.makeText(activity.getApplicationContext(), "Tag lost", Toast.LENGTH_SHORT).show();
	}

	private void togglePlayPause() {
		long eventtime = SystemClock.uptimeMillis();

		Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
		KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
		downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
		activity.sendOrderedBroadcast(downIntent, null);

		Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
		KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
		upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
		activity.sendOrderedBroadcast(upIntent, null);
	}

	private void setMaxVolume() {
		volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioManager
				.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
	}

	private void restoreVolume() {
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
	}

	@Override
	protected Void doInBackground(Void... params) {
		Tag tag = activity.getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(tag);
		try {
			ndef.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Log.d(TAG, "connected");
		if (!audioManager.isMusicActive())
			togglePlayPause();
		setMaxVolume();

		while (ndef.isConnected()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Log.d(TAG, "disconnected");
		restoreVolume();
		if (audioManager.isMusicActive())
			togglePlayPause();
		return null;
	}
}
