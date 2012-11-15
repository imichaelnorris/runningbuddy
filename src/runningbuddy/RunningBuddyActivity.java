package runningbuddy;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Allows the user to collects GPS data and takes a sum of total distance 
 * traveled. Has a rudimentary UI: does not reset distance without full
 * restart!
 * 
 * <Add a new View for a menu, then have a Running Activity and another
 * Activity where data can be analyzed (look at data from an individual
 * run and look at data between runs -- rate of change of speed, distance
 * traveled, etc)>
 * 
 * @author michael
 *
 */
public class RunningBuddyActivity extends Activity {
	//private TextView latitude;
	//private TextView longitude;
	/**
	 * For displaying distanceTraveledValue.
	 */
	private TextView distanceTraveled;
	
	/**
	 * Positive value of total distanceTraveled. 0.0f does not explicitly
	 * mean that there is no total distanceTraveled (it may also mean that
	 * the application is still unbundling this value).
	 */
	private double distanceTraveledValue;
	
	/**
	 * Deals with Android's GPS.
	 */
	private LocationManager mlocManager = null;
	
	/**
	 * Will hold the overwritten LocationListener which will have
	 * the logic for dealing with updated Location data.
	 */
	private LocationListener mlocListener = null;
	
	/**
	 * Every time we start the application (including when it has only been
	 * rotated) we are recreating all of these values.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		
		setContentView(R.layout.main);

		//(time between updates in milliseconds, distance in meters to update) -- 2nd and 3rd args
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5*1000, 1,
				mlocListener);
		
		//latitude = (TextView) findViewById(R.id.latitude_value);
		//longitude = (TextView) findViewById(R.id.longitude_value);
		distanceTraveled = (TextView) findViewById(R.id.distanceTraveledTextView);
		
		/** 
		 * If savedInstanceState != null and there is a distanceTraveledValue
		 * which is not equal to 0.0f, then this is just a placeholder until
		 * that value is updated.
		 */
		distanceTraveled.setText("0.0 meters");
		
		/**
		 * Initial value or placeholder until the LocationListener updates its
		 * value.
		 */
		distanceTraveledValue = 0.0;
		
		if (savedInstanceState != null)
		{
			if (savedInstanceState.containsKey("distanceTraveled"))
					distanceTraveledValue = savedInstanceState.getDouble("distanceTraveled");
		}
    }
    
    @Override
	public void onSaveInstanceState(Bundle output)
	{
		output.putDouble("distanceTraveled", distanceTraveledValue);
		mlocManager = null;
		mlocListener = null;
	}

	public class MyLocationListener implements LocationListener{
		/**
		 * Used for calculating distance between current and previous 
		 * locations.
		 */
		private Location previousLocation;
		
		@Override
		public void onLocationChanged(Location loc) {
			//loc.getLatitude();
			//loc.getLongitude();
			
			if (previousLocation != null)
			{
				if (previousLocation == loc)
				{
					//there's nothing to do if we have not moved.
					return;
				}

				else// (loc.distanceTo(previousLocation) > 1.0d)
				{
					//distance defined using the WGS84 ellipsoid
					// - error < 2cm
					distanceTraveledValue += Math.abs(loc.distanceTo(previousLocation));
					previousLocation = new Location(loc);
					distanceTraveled.setText(Double.toString(((int)distanceTraveledValue)) + " meters");
				}			
			}
			else // when PreviousLocation = null, we need to assign our first previousLocation;
			{
				previousLocation = new Location(loc);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
