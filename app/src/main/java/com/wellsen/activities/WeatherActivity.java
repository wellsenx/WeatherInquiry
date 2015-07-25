package com.wellsen.activities;

import java.util.Locale;

import com.wellsen.R;
import com.wellsen.operations.WeatherOps;
import com.wellsen.retrofitWeather.WeatherData;
import com.wellsen.utils.GenericActivity;
import com.wellsen.utils.Utils;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The main Activity that prompts the user for a location and then
 * displays WeatherData about this location via either retrieving the
 * WeatherData from a ContentProvider-based cache or from the Weather
 * Service web service via the use of Retrofit.
 */
public class WeatherActivity extends GenericActivity<WeatherOps> {
    /**
     * Weather location entered by the user.
     */
    private EditText mEditText;
    
    /**
     * Views to hold the Weather Data from an Open Weather Map API
     * call.
     */
    private TextView mDateView;
    private TextView mFriendlyDateView;
    private TextView mLocationName;
    private TextView mDescriptionView;
    private TextView mCelsiusTempView;
    private TextView mFarhenheitTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mSunriseView;
    private TextView mSunsetView;
    private ImageView mIconView;

    /**
     * Keeps track of whether a call is already in progress and
     * ignores subsequent calls until the first call is done.
     */
    private boolean mCallInProgress;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., initializing
     * views.
     *
     * @param savedInstanceState
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the content view for this Activity.
        setContentView(R.layout.main_activity);

        // Initialize the view fields in the activity instance.
        initializeDisplayViewFields();

        // Call up to the special onCreate() method in
        // GenericActivity, passing in the ContactsListOps class to
        // instantiate and manage.
        super.onCreate(savedInstanceState,
                       WeatherOps.class);
    }
	
    /**
     * Initiate the weather lookup when the user presses the
     * "Get Current Weather" button.
     */
    public void getWeather(View v) {
        if (mCallInProgress)
	    // Don't allow multiple calls.
	    Utils.showToast(this,
                            "Call current in progress");
        else {
            // Hide the keyboard.
            Utils.hideKeyboard(this,
                               mEditText.getWindowToken());
        
            // Get the user's input and convert it to upper case so
            // it's consistent with what we get back from the Weather
            // Service web service.
            final String location =
                mEditText.getText().toString().toUpperCase(Locale.ENGLISH);

            if (location.isEmpty())
                // No location provided.
                Utils.showToast(this,
                                "Enter a location");
            else {
                mCallInProgress = true;

                // Get the current weather from either the cache or
                // the Weather Service web service.
                getOps().getCurrentWeather(location);
            }
        }
    }

    /**
     * Helper method that initializes the views used to display the
     * weather data.
     */
    public void initializeDisplayViewFields() {
        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText =
            (EditText) findViewById(R.id.editText);
        mIconView =
            (ImageView) findViewById(R.id.detail_icon);
        mDateView =
            (TextView) findViewById(R.id.detail_date_textview);
        mFriendlyDateView =
            (TextView) findViewById(R.id.detail_day_textview);
        mLocationName =
            (TextView) findViewById(R.id.detail_locationName);
        mDescriptionView =
            (TextView) findViewById(R.id.detail_forecast_textview);
        mCelsiusTempView =
            (TextView) findViewById(R.id.detail_high_textview);
        mFarhenheitTempView =
            (TextView) findViewById(R.id.detail_low_textview);
        mHumidityView =
            (TextView) findViewById(R.id.detail_humidity_textview);
        mWindView =
            (TextView) findViewById(R.id.detail_wind_textview);
        mSunriseView =
            (TextView) findViewById(R.id.detail_sunrise_textview);
        mSunsetView =
            (TextView) findViewById(R.id.detail_sunset_textview);
    }

    /**
     * Displays the weather data to the user
     *
     * @param wd
     *            List of WeatherData to be displayed, which should not be null.
     */
    public void displayResults(WeatherData wd,
                               String errorReason) {
        // Allow another call to proceed when this method returns.
        mCallInProgress = false;

        if (wd == null)
            Utils.showToast(this,
                            errorReason);
        else {
            // Get the city and country name.
            final String locationName = 
                wd.getName()
                + ", "
                + wd.getSys().getCountry();

            // Update view for location name.
            mLocationName.setText(locationName);

            // Use weather art image given by its weatherId.
            int weatherId = (int) wd.getWeathers().get(0).getId();
            mIconView.setImageResource
                (Utils.getArtResourceForWeatherCondition(weatherId));

            // Get user-friendly date text.
            final String dateText =
                Utils.formatCurrentDate();

            // Update views for day of week and date.
            mFriendlyDateView.setText("Today");
            mDateView.setText(dateText);

            // Read description and update the view.
            final String description =
                wd.getWeathers().get(0).getDescription();
            mDescriptionView.setText(description);

            // For accessibility, add a content description to the icon
            // field.
            mIconView.setContentDescription(description);

            // Read Sunrise time and update the view.
            final String sunriseText = "Sunrise:  "
                + Utils.formatTime(wd.getSys().getSunrise());
            mSunriseView.setText(sunriseText);

            // Read Sunset time and update the view.
            final String sunsetText = "Sunset:  "
                + Utils.formatTime(wd.getSys().getSunset());
            mSunsetView.setText(sunsetText);

            // Read Temperature in Celsius and Farhenheit
            final double temp = wd.getMain().getTemp();
            final String tempCelsius =
                Utils.formatTemperature(this,
                                        temp,
                                        false)
                + "C";
            final String tempFarhenheit =
                Utils.formatTemperature(this,
                                        temp,
                                        true)
                + "F";

            // Update the Views to display Celsius and Farhenheit
            // temperature.
            mCelsiusTempView.setText(tempCelsius);
            mFarhenheitTempView.setText(tempFarhenheit);

            // Read humidity and update the view.
            final float humidity = wd.getMain().getHumidity();
            mHumidityView.setText
                (getString(R.string.format_humidity,
                           humidity));

            // Read wind speed and direction and update the view.
            final double windSpeedStr =
                wd.getWind().getSpeed();
            final double windDirStr =
                wd.getWind().getDeg();
            mWindView.setText
                (Utils.getFormattedWind(this,
                                        windSpeedStr,
                                        windDirStr));
        }
    }
}
