/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus 
 * Contact: info@atrapaeltigre.com
 * 
 * This file is part of Tigatrapp.
 * 
 * Tigatrapp is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Tigatrapp is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file incorporates code from Space Mapper, which is subject 
 * to the following terms: 
 *
 * 		Space Mapper
 * 		Copyright (C) 2012, 2013 John R.B. Palmer
 * 		Contact: jrpalmer@princeton.edu
 *
 * 		Space Mapper is free software: you can redistribute it and/or modify 
 * 		it under the terms of the GNU General Public License as published by 
 * 		the Free Software Foundation, either version 3 of the License, or (at 
 * 		your option) any later version.
 * 
 * 		Space Mapper is distributed in the hope that it will be useful, but 
 * 		WITHOUT ANY WARRANTY; without even the implied warranty of 
 * 		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * 		See the GNU General Public License for more details.
 * 
 * 		You should have received a copy of the GNU General Public License along 
 * 		with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package ceab.movelab.tigabib;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * Uploads files to the server.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Sample extends Service {

	private String TAG = "Sample";
	Context context;
	private static final int ALARM_ID_START_FIX = 1;

	ContentResolver cr;
	Cursor c;

	@Override
	public void onStart(Intent intent, int startId) {

		Util.logInfo(context, TAG, "on start");

		if (!Util.privateMode(context)) {
			Thread uploadThread = new Thread(null, doSampling,
					"sampleBackground");
			uploadThread.start();
		}

	};

	private Runnable doSampling = new Runnable() {
		public void run() {
			setSamples();
		}
	};

	@Override
	public void onCreate() {

		context = getApplicationContext();
		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);

		Util.logInfo(context, TAG, "Sample onCreate.");

	}

	@Override
	public void onDestroy() {

	}

	private void setSamples() {

		Util.logInfo(context, TAG, "set samples");

		int samplesPerDay = PropertyHolder.getSamplesPerDay();
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		int alarmType = AlarmManager.RTC_WAKEUP;
		Calendar c;

		int thisRandomMinute;
		int thisRandomHour;
		int thisRandomMinuteIndex;
		long thisTriggerTime;
		Random mRandom = new Random();

		String[] currentSamplingTimes = new String[samplesPerDay];

		for (int i = 0; i < samplesPerDay; i++) {
			// draw random minute index from all minutes in 15 hour period
			thisRandomMinuteIndex = mRandom.nextInt(15 * 60);

			// figure out the hour of day this corresponds to when minute index
			// 0 is 00:00
			thisRandomHour = (int) Math.floor(thisRandomMinuteIndex
					/ ((double) 60));

			// figure out the minute of the hour
			if (thisRandomHour > 0)
				thisRandomMinute = thisRandomMinuteIndex % thisRandomHour;
			else
				thisRandomMinute = thisRandomMinuteIndex;

			// push the hour forward so it starts at 07:00 am
			thisRandomHour += 7;

			c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, thisRandomHour);
			c.set(Calendar.MINUTE, thisRandomMinute);

			// If it is already after 7 am in the user's local time, set this
			// sample for the next day
			if (c.getTimeInMillis() < System.currentTimeMillis())
				c.add(Calendar.DATE, 1);

			thisTriggerTime = c.getTimeInMillis();

			currentSamplingTimes[i] = Util.iso8601(thisTriggerTime);

			alarmManager.set(alarmType, thisTriggerTime, PendingIntent
					.getService(context, (ALARM_ID_START_FIX * i), new Intent(
							context, FixGet.class), 0));
		}
		Arrays.sort(currentSamplingTimes);
		PropertyHolder.setCurrentFixTimes(currentSamplingTimes);
		Intent i = new Intent(Messages.newSamplesReadyAction(context));
		sendBroadcast(i);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
