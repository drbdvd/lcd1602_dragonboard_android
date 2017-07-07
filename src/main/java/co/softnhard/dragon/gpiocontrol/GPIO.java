package co.softnhard.dragon.gpiocontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by Victor Sinaniev on 25/06/2017.
 */

/*
Additional
echo 0 > /sys/kernel/refreshrate/enable (56 hz)


 */
public class GPIO {

    private String port;
    private int pin;

    //Constructor
    public GPIO(int pin) {
        this.port = "gpio" + pin;
        this.pin = pin;
    }

    //get direction of gpio
    private String getInOut() {
        String command = String.format("cat /sys/class/gpio/%s/direction", this.port);
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            return text.toString();
        } catch (IOException e) {
            return "";
        }
    }

    // get state of GPIO for input and output
    // test if GPIO is configured
    public int getState() {
        String command = String.format("cat /sys/class/gpio/%s/value", this.port);
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            try {
                String state = text.toString();
                if (state.equals("")) {
                    return -1;
                } else {
                    return Integer.parseInt(state.substring(0, 1));
                }
            } catch (NumberFormatException nfe) {
                return -1;
            }
        } catch (IOException e) {
            return -1;
        }
    }

    //set value of the output
    public boolean setValue(int value) {
        String command = String.format(Locale.ENGLISH, "echo %d > /sys/class/gpio/%s/value", value, this.port);
        try {
            String[] test = new String[]{"su", "-c", command};
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(test);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //set value of the output (bool)
    public boolean setValue(boolean value) {
        int val = value ? 1 : 0;
        String command = String.format(Locale.ENGLISH, "echo %d > /sys/class/gpio/%s/value", val, this.port);
        try {
            String[] test = new String[]{"su", "-c", command};
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(test);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // set direction
    private boolean setInOut(String direction) {
        String command = String.format("echo %s > /sys/class/gpio/%s/direction", direction, this.port);
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // export GPIO
    public boolean activationPin() {
        String command = String.format(Locale.ENGLISH, "echo %d > /sys/class/gpio/export", this.pin);
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // unexport GPIO
    public boolean deactivationPin() {
        String command = String.format(Locale.ENGLISH, "echo %d > /sys/class/gpio/unexport", this.pin);
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //init the pin
    public int initPin(String direction) {
        int state;
        boolean ret;

        // see if gpio is already set
        state = getState();
        if (state == -1) {
            // unexport the gpio
            ret = deactivationPin();
            if (!ret) {
                state = -1;
            }

            //export the gpio
            ret = activationPin();
            if (!ret) {
                state = -2;
            }
        }

        // get If gpio direction is define
        String ret2 = getInOut();
        if (!ret2.contains(direction)) {
            // set the direction (in or out)
            ret = setInOut(direction);
            if (!ret) {
                state = -3;
            }
        }

        return state;
    }


}
