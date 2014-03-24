package com.rikardlegge.gymnasticsapplication;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	static CustomViewPager mViewPager;
	static int pages = 4;
	static int currentPage = 0;
	static int inputcount = 0;
	public static MainActivity This;
	public static ArrayList<Float> ringTimes = new ArrayList<Float>();
	public static int challangeLevel = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startscreen);

		// Create the adapter that will return a fragment for each of the four
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (CustomViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		OnPageChangeListener list = new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				if (arg0 == 3) {
					
				} else if (arg0 == 2) {

				} else if (arg0 == 1) {
					if (ringTimes.isEmpty() != false)
						mViewPager.setPagingEnabled(false);
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		};

		mViewPager.setOnPageChangeListener(list);
		This = MainActivity.this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
		case KeyEvent.KEYCODE_BACK:
			if (mViewPager.getCurrentItem() > 0) {
				mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
				mViewPager.setPagingEnabled(true);
				return true;
			}
		}
		return super.onKeyDown(keycode, e);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return pages;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */

		private ArrayList<String> DayDefenitionList1 = new ArrayList<String>();
		private ArrayList<String> DayDefenitionList2 = new ArrayList<String>();

		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		public EditText[] editTextCompactor(View view) {
			EditText[] returnable = new EditText[2];
			returnable[0] = ((EditText) view.findViewById(R.id.InputText_01));
			returnable[1] = ((EditText) view.findViewById(R.id.InputText_02));
			return returnable;
		}

		public void refillView(View view, int id) {
			if (DayDefenitionList1.size() > 0 && DayDefenitionList2.size() > 0) {
				((EditText) view.findViewById(R.id.InputText_01)).setText(DayDefenitionList1.get(id));
				((EditText) view.findViewById(R.id.InputText_02)).setText(DayDefenitionList2.get(id));
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			int position = getArguments().getInt(ARG_SECTION_NUMBER);
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			View subrootView = null;
			currentPage = position;
			switch (position) {
			case 1:
				subrootView = inflater.inflate(R.layout.startpage, container, false);
				break;
			case 2:
				subrootView = inflater.inflate(R.layout.daydefenition, container, false);
				LinearLayout subRootLayout = ((LinearLayout) subrootView.findViewById(R.id.ItemList));
				final ArrayList<EditText[]> dataSources = new ArrayList<EditText[]>();
				final TextView feedbackText = (TextView) subrootView.findViewById(R.id.YourDayNextText);

				if (ringTimes.isEmpty() != true)
					feedbackText.setText(R.string.pull_left);

				final OnEditorActionListener listener = new OnEditorActionListener() {

					public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

						ringTimes.clear();

						DecimalFormat decim = new DecimalFormat("0.00");

						String start = ((EditText) ((EditText[]) dataSources.get(0))[0]).getText().toString();
						String end = ((EditText) ((EditText[]) dataSources.get(0))[1]).getText().toString();

						if (start.length() > 0 && end.length() > 0) {

							if (Float.parseFloat(end) < Float.parseFloat(start)) {
								((EditText) ((EditText[]) dataSources.get(0))[1]).setText("" + decim.format(Float.parseFloat(end) + 12));
								end = "" + (Float.parseFloat(end) + 12);
							}

							float fstart = Float.parseFloat(start);
							float fend = Float.parseFloat(end);

							for (float i = fstart + .5f; i < fend; i += 0.5f) {
								boolean ring = true;
								boolean isStartEnd = true;
								for (EditText[] etView : dataSources) {
									if (((EditText) etView[0]).getText().length() > 0 && ((EditText) etView[1]).getText().length() > 0) {
										float src1 = Float.parseFloat(((EditText) etView[0]).getText().toString());
										float src2 = Float.parseFloat(((EditText) etView[1]).getText().toString());

										if (src1 < fstart)
											src1 = fstart;
										else if (src1 > fend)
											src1 = fend;

										if (src2 < fstart)
											src2 = fstart;
										else if (src2 > fend)
											src2 = fend;

										((EditText) etView[0]).setText("" + decim.format(src1));
										((EditText) etView[1]).setText("" + decim.format(src2));

										if (src1 != fstart)
											if (src1 < fstart && src1 < 12) {
												((EditText) etView[0]).setText("" + decim.format(src1 + 12));
												src1 += 12;
											}
										if (src2 < fstart)
											if (src2 < fstart && src2 < 12) {
												((EditText) etView[1]).setText("" + decim.format(src2 + 12));
												src2 += 12;
											}

										if (src2 > src1 && isStartEnd == false) {
											if (i >= timeToDecimal(src1) && i <= timeToDecimal(src2)) {
												ring = false;
											}
										}
										isStartEnd = false;

									}
								}
								if (ring) {
									System.out.println("Ring:" + i);
									ringTimes.add(i);
								}

							}
							System.out.println("-----------------");

							feedbackText.setText(R.string.pull_left);
							mViewPager.setPagingEnabled(true);
						} else {
							feedbackText.setText(R.string.fill_all_fields);
							mViewPager.setPagingEnabled(false);
						}

						DayDefenitionList1.clear();
						DayDefenitionList2.clear();
						int j = 0;
						for (EditText[] etView : dataSources) {
							DayDefenitionList1.add(j, etView[0].getText().toString());
							DayDefenitionList2.add(j, etView[1].getText().toString());
							j += 1;
						}

						return false;
					}
				};
				OnEditorActionListener SubListener = new OnEditorActionListener() {

					public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
						return false;
					}
				};

				OnFocusChangeListener fCListener = new OnFocusChangeListener() {

					public void onFocusChange(View v, boolean hasFocus) {
						System.out.println("View Changed");
						listener.onEditorAction(null, 0, null);
					}
				};

				// Work hours //
				if (true) {
					View subRootPart1 = inflater.inflate(R.layout.daydefenition_dummy_full, container, false);
					((TextView) subRootPart1.findViewById(R.id.Title)).setText(R.string.work_hours);

					dataSources.add(editTextCompactor(subRootPart1));

					subRootLayout.addView(subRootPart1);
				}

				// Lunch //
				if (true) {
					View subRootPart1 = inflater.inflate(R.layout.daydefenition_dummy_full, container, false);
					((TextView) subRootPart1.findViewById(R.id.Title)).setText(R.string.lunch);

					dataSources.add(editTextCompactor(subRootPart1));

					subRootLayout.addView(subRootPart1);

				}

				// Break //
				if (true) {
					View subRootPart1 = inflater.inflate(R.layout.daydefenition_dummy_full, container, false);
					View subLayout1 = inflater.inflate(R.layout.daydefenition_dummy_part, container, false);
					((TextView) subRootPart1.findViewById(R.id.Title)).setText(R.string.abreak);

					dataSources.add(editTextCompactor(subRootPart1));
					dataSources.add(editTextCompactor(subLayout1));

					((TextView) subLayout1.findViewById(R.id.Title)).setText("");
					((LinearLayout) subRootPart1.findViewById(R.id.subroot)).addView(subLayout1,
							((LinearLayout) subRootPart1.findViewById(R.id.subroot)).getChildCount() - 1);

					subRootLayout.addView(subRootPart1);
				}

				// Meeting //
				if (true) {
					View subRootPart1 = inflater.inflate(R.layout.daydefenition_dummy_full, container, false);
					View subLayout1 = inflater.inflate(R.layout.daydefenition_dummy_part, container, false);
					((TextView) subRootPart1.findViewById(R.id.Title)).setText(R.string.meeting);

					dataSources.add(editTextCompactor(subRootPart1));
					dataSources.add(editTextCompactor(subLayout1));

					((TextView) subLayout1.findViewById(R.id.Title)).setText("");
					((LinearLayout) subRootPart1.findViewById(R.id.subroot)).addView(subLayout1,
							((LinearLayout) subRootPart1.findViewById(R.id.subroot)).getChildCount() - 1);

					subRootLayout.addView(subRootPart1);
				}
				for (EditText[] etView : dataSources) {
					etView[1].setOnEditorActionListener(SubListener);
					etView[0].clearFocus();
					etView[1].clearFocus();
					etView[0].setOnFocusChangeListener(fCListener);
					etView[1].setOnFocusChangeListener(fCListener);
				}

				break;

			case 3:
				subrootView = inflater.inflate(R.layout.chooselevel, container, false);
				((RadioGroup) subrootView.findViewById(R.id.chooseLevelRatio)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(RadioGroup group, int checkedId) {
						challangeLevel = Integer.parseInt(((String) ((group.findViewById(checkedId)).getTag())));
						System.out.println(challangeLevel);
					}
				});
				;
				break;

			case 4:
				subrootView = inflater.inflate(R.layout.finished, container, false);
				break;
			}

			((RelativeLayout) rootView.findViewById(R.id.editableLayout)).removeAllViews();
			if (subrootView != null)
				((RelativeLayout) rootView.findViewById(R.id.editableLayout)).addView(subrootView);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText("");// Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

			return rootView;
		}
	}

	public ArrayList<String> linkGenerator(int level, int amt) {
		ArrayList<String> response = new ArrayList<String>();
		for (int i = 0; i < amt; i++) {
			response.add(i, "KHRWB4UgCdQ");
		}
		return response;
	}

	public String linkGenerator(int level) {
		String response = "SwuejHpx3r4";
		return response;
	}

	/*
	 * // Old nofication code
	 * 
	 * @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public void
	 * showNotification(String title, String subtitle, String link, int delay) {
	 * Intent intent = new Intent(MainActivity.this, NotificationService.class);
	 * intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	 * intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
	 * intent.putExtra("timers", ringTimes); intent.putExtra("links",
	 * linkGenerator(challangeLevel, ringTimes.size()));
	 * MainActivity.this.startService(intent); }
	 * 
	 * public void createSceduledNotifications() { int i = 0; Calendar calendar
	 * = Calendar.getInstance(); while (i < ringTimes.size()) { float curTime =
	 * timeToDecimal(calendar.get(Calendar.HOUR_OF_DAY) +
	 * calendar.get(Calendar.MINUTE) / 100); for (int j = i; j <
	 * ringTimes.size(); j++) { if (ringTimes.get(j) > curTime) { i = j; break;
	 * } } float diff = ringTimes.get(i) - curTime; System.out.println(diff);
	 * createScheduledNotification((int) (diff * 60), 827391928 +
	 * (int)(ringTimes.get(i)*100)); i++; } } //
	 */

	public static float timeToDecimal(float time) {
		float deciTmp = Math.round(time - .5);
		return (float) (deciTmp + (time - deciTmp) * 5 / 3);
	}

	@SuppressWarnings("static-access")
	public void createScheduledNotification(int MinutesFromNow, int id) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MINUTE, MinutesFromNow);

		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(getBaseContext().ALARM_SERVICE);
		// Every scheduled intent needs a different ID, else it is just executed
		// once
		// int id = (int) System.currentTimeMillis();

		// Prepare the intent which should be launched at the date
		Intent intent = new Intent(this, TimeAlarm.class);
		intent.putExtra("link", linkGenerator(challangeLevel));

		// Prepare the pending intent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Register the alert in the system. You have the option to define if
		// the device has to wake up on the alert or not
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	public void cancelNotification(int notificationId) {

		if (Context.NOTIFICATION_SERVICE != null) {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
			nMgr.cancel(notificationId);
		}
	}

}
