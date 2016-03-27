package edu.uml.cs.mstowell.wrkr.ui;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import edu.uml.cs.mstowell.wrkr.R;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;

/**
 * Fragment to allow the user to change his/her Google profile
 */
public class ProfileFragment extends Fragment implements Globals {

    private TextView user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // get user's account
        SharedPreferences prefs = getActivity().getSharedPreferences(GLOBAL_PREFS, 0);
        String strEmail = prefs.getString(USER_EMAIL, "");

        user = (TextView) v.findViewById(R.id.profile_logged_in_as);
        user.setText(strEmail.split("@")[0]);

        Button change = (Button) v.findViewById(R.id.profile_change_prof);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGoogleAccount();
            }
        });

        return v;
    }

    public void getGoogleAccount() {
        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch (ActivityNotFoundException e) {
            // the user hasn't synced a Google account to their device yet - either
            // launcher an account adding intent or prompt for a manual email
            // address entry
            Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
            intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == Activity.RESULT_OK) {
            // got back the user's email address - save to preferences
            String strEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            SharedPreferences prefs = getActivity().getSharedPreferences(GLOBAL_PREFS, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(USER_EMAIL, strEmail).apply();
            user.setText(strEmail);
        }
    }
}
