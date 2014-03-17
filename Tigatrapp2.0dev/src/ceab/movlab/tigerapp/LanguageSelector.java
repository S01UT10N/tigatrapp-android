/*
 * Tigatrapp
 * Copyright (C) 2012, 2013 John R.B. Palmer
 * Contact: jrpalmer@princeton.edu
 * 
 * This file is part of Space Mapper.
 * 
 * Space Mapper is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Space Mapper is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 */

package ceab.movlab.tigerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import ceab.movelab.tigerapp.R;

/**
 * Displays the IRB consent form and allows users to consent or decline.
 * 
 * @author John R.B. Palmer
 * 
 */
public class LanguageSelector extends Activity {

	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("LS", "ls1");

		if (!PropertyHolder.isInit())
			PropertyHolder.init(context);

		Log.d("LS", "ls2");

		context = this;

		Log.d("LS", "ls2");

		setContentView(R.layout.language_selector);

		Log.d("LS", "ls4");

		Util.overrideFonts(context, findViewById(android.R.id.content));

		Log.d("LS", "ls5");

		final RadioGroup languageRadioGroup = (RadioGroup) findViewById(R.id.languageRadioGroup);

		Log.d("LS", "ls6");

		Button positive = (Button) findViewById(R.id.languageOK);

		Log.d("LS", "ls7");

		positive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d("LS", "ls8");

				if (languageRadioGroup.getCheckedRadioButtonId() == R.id.caButton) {
					PropertyHolder.setLanguage("ca");
					startActivity(new Intent(LanguageSelector.this,
							Switchboard.class));
					finish();
					return;
				} else if (languageRadioGroup.getCheckedRadioButtonId() == R.id.esButton) {
					PropertyHolder.setLanguage("es");
					startActivity(new Intent(LanguageSelector.this,
							Switchboard.class));
					finish();
					return;
				} else if (languageRadioGroup.getCheckedRadioButtonId() == R.id.enButton) {
					PropertyHolder.setLanguage("en");
					startActivity(new Intent(LanguageSelector.this,
							Switchboard.class));
					finish();
					return;
				} else {
					// do nothing
				}
			}
		});

	}

}
