package edu.uml.cs.mstowell.wrkr;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import edu.uml.cs.mstowell.wrkr.ui.HelpFragment;
import edu.uml.cs.mstowell.wrkr.ui.HomeFragment;
import edu.uml.cs.mstowell.wrkr.ui.ProfileFragment;
import edu.uml.cs.mstowell.wrkr.ui.SettingsFragment;
import edu.uml.cs.mstowell.wrkrlib.common.APIClientCommon;
import edu.uml.cs.mstowell.wrkrlib.common.Globals;
import edu.uml.cs.mstowell.wrkrlib.common.User;

/**
 * MainActivity for the entire UI, including all fragments
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, Globals {

    // global UI elements
    private TextView email;

    // context
    public static Context mContext;
    public static MainActivity mActivity;
    int fragmentIndex = -1;

    // android wear comm
    private APIClientCommon mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Launching website", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // wait 1.5 seconds before launching a browser
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(WEBSITE_URL));
                        startActivity(browserIntent);
                    }
                }, 1500);
            }
        });

        // set up the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        email = (TextView) headerLayout.findViewById(R.id.nav_header_email);

        // set to the main fragment
        setTitle(getResources().getString(R.string.menu_home));
        transitionFragment(new HomeFragment(), FRAGMENT_INDEX_HOME);

        // get the user's google profile if none saved yet
        SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, 0);
        String strEmail = prefs.getString(USER_EMAIL, "");
        if (strEmail.isEmpty()) {
            getGoogleAccount();
        } else {
            email.setText(strEmail);
        }

        // initialize GoogleApiClient to talk to wear
        mApiClient = new APIClientCommon(this);

        // make sure the SetWristAlarmReceiver is fired at least once
        Intent intent = new Intent();
        intent.setAction(SET_WRIST_ALARM_BROADCAST_ACTION);
        mContext.sendBroadcast(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {

            // got back the user's email address - save to preferences
            String strEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(USER_EMAIL, strEmail).apply();
            email.setText(strEmail);

            // set the ProfileFragment's account text
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if(fragment instanceof ProfileFragment) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                        ((ProfileFragment) fragment).setAccountText(strEmail.split("@")[0]);
                    }
                }
            }

            // then publish this user to the DB - save the uid to preferences
            new PublishUserToDBTask().execute(strEmail);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // close the nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // handle navigation view item clicks
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                setTitle(getResources().getString(R.string.menu_home));
                if (fragmentIndex != FRAGMENT_INDEX_HOME)
                    transitionFragment(new HomeFragment(), FRAGMENT_INDEX_HOME);
                break;
            case R.id.nav_profile:
                setTitle(getResources().getString(R.string.menu_profile));
                if (fragmentIndex != FRAGMENT_INDEX_PROFILE)
                    transitionFragment(new ProfileFragment(), FRAGMENT_INDEX_PROFILE);
                break;
            case R.id.nav_settings:
                setTitle(getResources().getString(R.string.menu_settings));
                if (fragmentIndex != FRAGMENT_INDEX_SETTINGS)
                    transitionFragment(new SettingsFragment(), FRAGMENT_INDEX_SETTINGS);
                break;
            case R.id.nav_help:
                setTitle(getResources().getString(R.string.menu_help));
                if (fragmentIndex != FRAGMENT_INDEX_HELP)
                    transitionFragment(new HelpFragment(), FRAGMENT_INDEX_HELP);
                break;
        }

        return true;
    }

    public void transitionFragment(Fragment fragment, int newIndex) {

        fragmentIndex = newIndex;
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private class PublishUserToDBTask extends AsyncTask<String, Void, Void> {

        ProgressDialog load;
        User u;

        @Override
        protected void onPreExecute() {
            load = new ProgressDialog(mContext);
            load.setMessage("Saving user ...");
            load.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            u = RestAPI.postUser(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (load != null) load.dismiss();
            SharedPreferences prefs = getSharedPreferences(GLOBAL_PREFS, 0);
            SharedPreferences.Editor edit = prefs.edit();

            if (u == null) {
                // TODO - handle case
            } else {
                edit.putInt(USER_ID, u.id).apply();
            }
            super.onPostExecute(aVoid);
        }
    }

    /* BEGIN ANDROID WEAR COMMUNICATION */

    public void sendMessage(final String path, final String text) {
        mApiClient.sendMessage(path, text);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // initialize connection to wear
        mApiClient.sendMessage(MSG_INIT_FROM_DEVICE, "");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("wrkr", "ABCDE CONNECTION TO WEAR DEVICE SUSPENDED");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting()))
            mApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mApiClient != null) {
            if (mApiClient.isConnected()) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mApiClient != null)
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
    }
}
