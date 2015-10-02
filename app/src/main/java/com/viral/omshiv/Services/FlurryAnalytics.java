package com.viral.omshiv.Services;

import android.content.Context;

import com.flurry.android.FlurryAgent;

/**
 * Created by omshiv on 17/12/2014.
 */
public class FlurryAnalytics {

    static final String FLURRY_KEY = "W9N4BWPB5H7QZZCZBCP5";

    public static void startSession(Context context) {
        FlurryAgent.init(context, "W9N4BWPB5H7QZZCZBCP5");
        FlurryAgent.onStartSession(context, FLURRY_KEY);


        // here could be some other analytics calls (google analytics, etc)
    }

    public static void stopSession(Context context) {
        FlurryAgent.onEndSession(context);

        // other analytics camlls
    }


}
