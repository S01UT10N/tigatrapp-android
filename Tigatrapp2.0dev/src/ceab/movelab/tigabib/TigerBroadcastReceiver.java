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
 * This file also incorporates code written by Chang Y. Chung and Necati E. Ozgencil 
 * for the Human Mobility Project, which is subject to the following terms: 
 * 
 * 		Copyright (C) 2010, 2011 Human Mobility Project
 *
 *		Permission is hereby granted, free of charge, to any person obtaining 
 *		a copy of this software and associated documentation files (the
 *		"Software"), to deal in the Software without restriction, including
 *		without limitation the rights to use, copy, modify, merge, publish, 
 *		distribute, sublicense, and/or sell copies of the Software, and to
 *		permit persons to whom the Software is furnished to do so, subject to
 *		the following conditions:
 *
 *		The above copyright notice and this permission notice shall be included
 *		in all copies or substantial portions of the Software.
 *
 *		THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *		EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *		MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *		IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *		CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *		TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *		SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * 
 */

package ceab.movelab.tigabib;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.ContProvContractMissions.Tasks;

/**
 * Triggers and stops location fixes at set intervals; also sends notification
 * to notification bar.
 * 
 * @author John R.B. Palmer
 */
public class TigerBroadcastReceiver extends BroadcastReceiver {

	private static String TAG = "TigerBroadCastReceiver";

	private static final int ALARM_ID_SAMPLING = 0;
	private static final int ALARM_ID_SYNC = 1;
	private static final int ALARM_ID_FIX = 2;

	private static final int NOTIFICATION_ID_MISSION = 0;

	private static final long DAILY_INTERVAL = 1000 * 60 * 60 * 24;

	@Override
	public void onReceive(Context context, Intent intent) {

		Util.logInfo(context, TAG, "on receive");

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		if (PropertyHolder.hasConsented() && !Util.privateMode(context)) {

			String action = intent.getAction();
			String extra = "";
			if (intent.hasExtra(Messages.INTERNAL_MESSAGE_EXTRA)) {
				extra = intent.getStringExtra(Messages.INTERNAL_MESSAGE_EXTRA);
				Util.logInfo(context, TAG, "extra: " + extra);
			}
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			Intent i2sample = new Intent(context, Sample.class);
			PendingIntent pi2sample = PendingIntent.getBroadcast(context,
					ALARM_ID_SAMPLING, i2sample, 0);

			Intent i2sync = new Intent(context, SyncData.class);
			PendingIntent pi2sync = PendingIntent.getService(context,
					ALARM_ID_SYNC, i2sync, 0);

			Intent i2stopfix = new Intent(context, FixGet.class);
			i2stopfix.setAction(Messages.stopFixAction(context));
			PendingIntent pi2stopfix = PendingIntent.getService(context,
					ALARM_ID_FIX, i2stopfix, 0);

			if (extra.contains(Messages.START_DAILY_SAMPLING)) {
				// first sample now
				context.startService(new Intent(context, Sample.class));
				int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
				alarmManager.setRepeating(alarmType,
						SystemClock.elapsedRealtime(), DAILY_INTERVAL,
						pi2sample);
				PropertyHolder.setServiceOn(true);
				Util.logInfo(context, TAG, "started daily sampling");
				PropertyHolder
						.lastSampleSchedleMade(System.currentTimeMillis());
			} else if (extra.contains(Messages.STOP_DAILY_SAMPLING)) {
				alarmManager.cancel(pi2sample);
				PropertyHolder.setServiceOn(false);
				context.stopService(i2stopfix);
				Util.logInfo(context, TAG, "stopped sampling");
			} else if (extra.contains(Messages.START_DAILY_SYNC)) {
				// first sync now
				context.startService(new Intent(context, SyncData.class));
				int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
				alarmManager.setRepeating(alarmType,
						SystemClock.elapsedRealtime(), DAILY_INTERVAL, pi2sync);
				Util.logInfo(context, TAG, "started daily sync");
			} else if (extra.contains(Messages.STOP_DAILY_SYNC)) {
				alarmManager.cancel(pi2sync);
				Util.logInfo(context, TAG, "stop sync");
			} else if (extra.contains(Messages.START_TASK_FIX)) {
				Intent tfi = new Intent(context, FixGet.class);
				tfi.setAction(Messages.taskFixAction(context));
				context.startService(tfi);
				// long baseTime = SystemClock.elapsedRealtime();
				// alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				// baseTime
				// + Util.TASK_FIX_WINDOW, pi2stopfix);
				Util.logInfo(context, TAG, "start task fix");
			} else if (extra.contains(Messages.SHOW_TASK_NOTIFICATION)) {
				final String taskTitle = intent.getStringExtra(Tasks.KEY_TITLE);
				createNotification(context, taskTitle);
				Util.logInfo(context, TAG, "show task notification");
			} else if (extra.contains(Messages.REMOVE_TASK_NOTIFICATION)) {
				cancelNotification(context);
				Util.logInfo(context, TAG, "remove task notification");
			} else if (action.contains(Intent.ACTION_BOOT_COMPLETED)) {
				if (PropertyHolder.isServiceOn()) {
					Util.internalBroadcast(context,
							Messages.START_DAILY_SAMPLING);
				}
				Util.internalBroadcast(context, Messages.START_DAILY_SYNC);
				Util.logInfo(context, TAG, "boot completed");
			} else if (action.contains(Intent.ACTION_POWER_CONNECTED)) {
				context.startService(new Intent(context, SyncData.class));
				Util.logInfo(context, TAG, "power connected");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void createNotification(Context context, String taskTitle) {

		Util.logInfo(context, TAG, "create notification");

		Resources res = context.getResources();
		Util.setDisplayLanguage(res);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				R.drawable.ic_stat_mission,
				res.getString(R.string.new_mission), System.currentTimeMillis());
		// notification.flags |= Notification.FLAG_NO_CLEAR;
		// notification.flags |= Notification.FLAG_ONGOING_EVENT;

		Intent intent = new Intent(context, MissionListActivity.class);

		// not sure if we still need this
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(Switchboard.class);
		stackBuilder.addNextIntent(intent);
		PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_CANCEL_CURRENT);

		// PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		// intent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context,
				res.getString(R.string.new_mission), taskTitle, pendingIntent);
		notificationManager.notify(NOTIFICATION_ID_MISSION, notification);

	}

	public void cancelNotification(Context context) {
		Util.logInfo(context, TAG, "cancel notification");
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID_MISSION);
	}

}
