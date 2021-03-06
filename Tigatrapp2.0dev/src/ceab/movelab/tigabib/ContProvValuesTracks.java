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
 **/

package ceab.movelab.tigabib;

import java.util.Date;

import android.content.ContentValues;
import ceab.movelab.tigabib.ContProvContractTracks.Fixes;

/**
 * Creates content values from location fix data.
 * 
 * @author John R.B. Palmer
 * 
 */

public class ContProvValuesTracks {

	public static ContentValues createFix(double latitude, double longitude,
			long time, float power_proportion, boolean task_fix) {
		Date usertime = new Date(time);
		ContentValues initialValues = new ContentValues();
		initialValues.put(Fixes.KEY_LATITUDE, (double) latitude);
		initialValues.put(Fixes.KEY_LONGITUDE, (double) longitude);
		initialValues.put(Fixes.KEY_TIME, (long) time);
		initialValues.put(Fixes.KEY_POWER_LEVEL, power_proportion);
		initialValues.put(Fixes.KEY_UPLOADED, 0);
		initialValues.put(Fixes.KEY_TASK_FIX, task_fix?1:0);
		

		return initialValues;
	}

}
