/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.survey.android.logic;

import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Used to return device properties to JavaRosa
 *
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */

public class PropertyManager {

    private String t = "PropertyManager";

    private Context mContext;

    private TelephonyManager mTelephonyManager;
    private HashMap<String, String> mProperties;

    private final static String DEVICE_ID_PROPERTY = "deviceid"; // imei
    private final static String SUBSCRIBER_ID_PROPERTY = "subscriberid"; // imsi
    private final static String SIM_SERIAL_PROPERTY = "simserial";
    private final static String PHONE_NUMBER_PROPERTY = "phonenumber";


    public String getName() {
        return "Property Manager";
    }


    public PropertyManager(Context context) {
        Log.i(t, "calling constructor");

        mContext = context;

        mProperties = new HashMap<String, String>();
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceId = mTelephonyManager.getDeviceId();
        if (deviceId != null && (deviceId.contains("*") || deviceId.contains("000000000000000"))) {
            deviceId =
                Settings.Secure
                        .getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        if ( deviceId == null ) {
        	// no SIM -- WiFi only
        	// Retrieve WiFiManager
        	WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

    		// Get WiFi status
    		WifiInfo info = wifi.getConnectionInfo();
    		if ( info != null ) {
    			deviceId = info.getMacAddress();
    		}
        }

        // if it is still null, use ANDROID_ID
        if ( deviceId == null ) {
            deviceId =
                    Settings.Secure
                            .getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        mProperties.put(DEVICE_ID_PROPERTY, deviceId);
        mProperties.put(SUBSCRIBER_ID_PROPERTY, mTelephonyManager.getSubscriberId());
        mProperties.put(SIM_SERIAL_PROPERTY, mTelephonyManager.getSimSerialNumber());
        mProperties.put(PHONE_NUMBER_PROPERTY, mTelephonyManager.getLine1Number());
    }



    public String getSingularProperty(String propertyName) {
        return mProperties.get(propertyName.toLowerCase(Locale.ENGLISH));
    }


    public void setProperty(String propertyName, String propertyValue) {
    	mProperties.put(propertyName.toLowerCase(Locale.ENGLISH), propertyValue);
    }

}