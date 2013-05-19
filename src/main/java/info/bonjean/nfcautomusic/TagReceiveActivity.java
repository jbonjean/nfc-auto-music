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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Julien Bonjean <julien@bonjean.info>
 * 
 */
public class TagReceiveActivity extends Activity {

	private static String TAG = TagReceiveActivity.class.getName();
	private static TagReceiveTask task = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		if (task == null || !task.getStatus().equals(AsyncTask.Status.RUNNING)) {
			task = new TagReceiveTask(this);
			task.execute();
		} else
			Log.e(TAG, "Problem: background task already running");
	}
}
