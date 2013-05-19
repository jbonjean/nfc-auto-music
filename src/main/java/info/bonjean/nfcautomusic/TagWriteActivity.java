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

import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Julien Bonjean <julien@bonjean.info>
 * 
 */
public class TagWriteActivity extends Activity {

	private static String TAG = TagWriteActivity.class.getName();
	private TextView logs;
	private Button startButton;
	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter tagDetected;

	private void appendLog(String text) {
		logs.setText(logs.getText() + "\n" + text);
	}

	private void clearLogs() {
		logs.setText("");
	}

	public void initNFC() {
		Log.d(TAG, "initNFC");
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Intent nfcIntent = new Intent(this, TagWriteActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
	}

	public void enableNFC() {
		Log.d(TAG, "enableNFC");
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[] { tagDetected }, null);
		appendLog("NFC initialized, waiting for tag");
	}

	private void disableNFC() {
		Log.d(TAG, "disableNFC");
		nfcAdapter.disableForegroundDispatch(this);
	}

	public void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");
		appendLog("Tag detected");

		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "application/info.bonjean.nfcautomusic".getBytes(Charset
				.forName("US-ASCII")), null, null);
		NdefMessage message = new NdefMessage(relayRecord);

		appendLog("Starting write");

		Ndef ndef = Ndef.get(tag);
		try {
			ndef.connect();
			ndef.writeNdefMessage(message);
			appendLog("Write successfully");
		} catch (Exception e) {
			appendLog("Write failed");
		}
		startButton.setEnabled(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_tag);
		logs = (TextView) findViewById(R.id.logs);
		initNFC();
		startButton = (Button) findViewById(R.id.start);
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startButton.setEnabled(false);
				clearLogs();
				enableNFC();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!startButton.isEnabled())
			disableNFC();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!startButton.isEnabled())
			enableNFC();
	}
}
