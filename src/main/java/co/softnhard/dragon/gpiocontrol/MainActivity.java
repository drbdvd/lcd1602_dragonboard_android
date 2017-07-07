package co.softnhard.dragon.gpiocontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private Lcd1602 lcd;
    private boolean state = false;
    private boolean turnOff = false;
    private int test = 0;
    private final GPIO GPIO_TEST = new GPIO(936); // APQ GPIO_69

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLcd();

        GPIO_TEST.initPin("out");

        final EditText outputText = (EditText) findViewById(R.id.outputText);
        final Button toggleMode = (Button) findViewById(R.id.btnMode);
        toggleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = !state;
                outputText.append("Mode: " + state + "\n");

                if (state)
                    startPrint();
                else {
                    try {
                        lcd.clear();
                        outputText.append("cleared: " + "\n");
                    } catch (Exception ex) {
                        outputText.append("Error: " + ex.getMessage() + "\n");
                    }
                }
            }
        });
        final Button toggleDisplay = (Button) findViewById(R.id.btnTurnOff);
        toggleDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOff = !turnOff;
                outputText.append("Display OFF: " + turnOff + "\n");
                try {
                    if (turnOff)
                        lcd.noDisplay();
                    else {
                        lcd.display();
                    }
                } catch (Exception ex) {
                    outputText.append("Error: " + ex.getMessage() + "\n");
                }

            }
        });

        final Button toggleTest = (Button) findViewById(R.id.btnTest);
        toggleTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputText.append("Test: " + test + "\n");

                for (int i = 0; i < 100; i++) {
                    test = (test + 1) % 2;
                    GPIO_TEST.setValue(test);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        outputText.append("Test Error: " + ex.getMessage() + "\n");
                        break;
                    }
                }


            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final EditText outputText = (EditText) findViewById(R.id.outputText);
        if (lcd != null) {
            try {
                lcd.close();
            } catch (Exception e) {
                outputText.append("Error: " + e.getMessage() + "\n");
            } finally {
                lcd = null;
            }
        }
    }

    private void initLcd() {
        final EditText outputText = (EditText) findViewById(R.id.outputText);

        try {
            lcd = new Lcd1602();
            outputText.append("Lcd ready.\n");
        } catch (Exception ex) {
            outputText.append("Error: " + ex.getMessage() + "\n");
        }
    }

    private void startPrint() {
        final EditText outputText = (EditText) findViewById(R.id.outputText);
        Date d = new Date();

        try {

            outputText.append("writing...\n");
            lcd.begin(16, 2);
            outputText.append("began 16,2\n");
            lcd.clear();
            outputText.append("Lcd clear.\n");
            outputText.append("Lcd: " + d.toString().substring(0, 16) + "\n");
            lcd.print(d.toString().substring(0, 16));
            lcd.setCursor(0, 1);

            //get gps position and print to lcd

            outputText.append("Message written.\n");

        } catch (Exception ex) {
            outputText.append("Error: " + ex.getMessage() + "\n");
        }
    }

    /*
    private class CustomLocationListener implements LocationListener {

        public String currentCity = "UNKNOWN";

        @Override
        public void onLocationChanged(Location loc) {

            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

        //------- To get city name from coordinates --------
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentCity = cityName;
            return;
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        private Location getLastBestLocation() throws SecurityException {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if (0 < GPSLocationTime - NetLocationTime) {
                return locationGPS;
            } else {
                return locationNet;
            }
        }
    }
    */
}
