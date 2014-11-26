package pl.polsl.dotnet.itacademicday.layouts;

import pl.polsl.dotnet.itacademicday.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #makeActionBar()}.
	 */
	private static int mColor;

	private static ConnectivityManager mConManager;
	private static Typeface regular, light, semilight;
	private TextView mTitleView;

	//	/**
	//	 * Used to store data about lectures
	//	 */
	//	private static ArrayList<LecturesEntity> mLectures;
	//
	//	/**
	//	 * Used to store data about sponsors
	//	 */
	//	private static ArrayList<SponsorsEntity> mSponsors;

	public static int getCurrentColor(){
		return mColor;
	}

	//
	//	public static ArrayList<LecturesEntity> getLectures(){
	//		return mLectures;
	//	}
	//
	//	public static ArrayList<SponsorsEntity> getSponsors(){
	//		return mSponsors;
	//	}
	//
	//	public static void setLectures(ArrayList<LecturesEntity> lectures){
	//		mLectures = lectures;
	//	}
	//
	//	public static void setSponsors(ArrayList<SponsorsEntity> sponsors){
	//		mSponsors = sponsors;
	//	}

	public static boolean hasAccessToNetwork(){
		final NetworkInfo activeNetwork = mConManager.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnected());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//		Thread thread = new Thread(new Runnable() {
		//
		//			@SuppressLint("NewApi")
		//			@Override
		//			public void run(){
		//				try {
		//					if (android.os.Build.VERSION.SDK_INT > 9) {
		//						StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//						StrictMode.setThreadPolicy(policy);
		//					}
		//					setLectures(DataFactory.getLecturesData());
		//					setSponsors(DataFactory.getSponsorsData());
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		});
		//		thread.start();

		mConManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		regular = Typeface.createFromAsset(getAssets(), "Segoe.ttf");
		light = Typeface.createFromAsset(getAssets(), "SegoeLight.ttf");
		semilight = Typeface.createFromAsset(getAssets(), "SegoeSemilight.ttf");
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(
				R.id.navigation_drawer);
		mTitleView = new TextView(this);
		mTitleView.setText(getString(R.string.app_name));
		mTitleView.setTypeface(semilight);
		mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.app_title_font));
		mTitleView.setTextColor(Color.WHITE);
		mTitleView.setPadding((int) getResources().getDimension(R.dimen.title_padding), 0, 0, 0);
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

	}

	@Override
	protected void onResume(){
		super.onResume();
		if (mConManager == null)
			mConManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position){
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		position++;
		Fragment f = null;
		switch (position) {
			case 1:
				f = AboutFragment.newInstance(position);
				mColor = getResources().getColor(R.color.section1);
				break;
			case 2:
				f = AgendaFragment.newInstance(position);
				mColor = getResources().getColor(R.color.section2);
				break;
			case 3:
				f = WallFragment.newInstance(position);
				mColor = getResources().getColor(R.color.section3);
				break;
			case 4:
				f = SponsorsFragment.newInstance(position);
				mColor = getResources().getColor(R.color.section4);
				break;
			case 5:
				break;
		}
		makeActionBar();
		fragmentManager.beginTransaction().replace(R.id.container, f).commit();
	}

	public void makeActionBar(){
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(mTitleView);
		actionBar.setBackgroundDrawable(new ColorDrawable(mColor));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			makeActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	public enum FontStyle {
		REGULAR, LIGHT, SEMILIGHT
	};

	public static void setFont(View v, FontStyle style){
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			int numChildren = vg.getChildCount();
			for (int i = 0; i < numChildren; i++) {
				setFont(vg.getChildAt(i), style);
			}
		} else if (v instanceof TextView) {
			TextView tv = (TextView) v;
			switch (style) {
				case LIGHT:
					tv.setTypeface(light);
					break;
				case SEMILIGHT:
					tv.setTypeface(semilight);
					break;
				case REGULAR:
				default:
					tv.setTypeface(regular);
					break;
			}

		}
	}
}
