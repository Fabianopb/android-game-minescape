package resources;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.visivaemobile.minescape.R;

public class AppRater {
	
	private static String APP_TITLE;
    private final static String APP_PNAME = "com.visivaemobile.minescape";
    
    private final static int DAYS_UNTIL_PROMPT = 2;
    private final static int LAUNCHES_UNTIL_PROMPT = 6;
    
    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }
        
        SharedPreferences.Editor editor = prefs.edit();
        
        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }
        
        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + 
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
                editor.putLong("launch_count", 0);
            }
        }
        
        editor.commit();
    }
    
    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        
        APP_TITLE = mContext.getResources().getString(R.string.app_name);        
        
        final AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
        
        ad.setTitle(mContext.getResources().getString(R.string.rater_rate) + APP_TITLE);
        ad.setMessage(mContext.getResources().getString(R.string.rater_title1) + APP_TITLE + mContext.getResources().getString(R.string.rater_title2));
        ad.setPositiveButton(mContext.getResources().getString(R.string.rater_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
			}
		});
        ad.setNeutralButton(mContext.getResources().getString(R.string.rater_later), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});        
        ad.setNegativeButton(mContext.getResources().getString(R.string.rater_rate), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
			}
		});
        ad.create();
        ad.show();
    }
}
