package edu.uml.cs.mstowell.wrkr;

import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import edu.uml.cs.mstowell.wrkr.data.Globals;
import edu.uml.cs.mstowell.wrkr.ui.HelpFragment;
import edu.uml.cs.mstowell.wrkr.ui.HomeFragment;
import edu.uml.cs.mstowell.wrkr.ui.ProfileFragment;
import edu.uml.cs.mstowell.wrkr.ui.SettingsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, Globals {

    // global UI elements
    private TextView email;
    private NavigationView navigationView;
    private View root;

    // context
    public static Context mContext;
    public static MainActivity mActivity;
    int fragmentIndex = -1;

    // android wear comm
    private GoogleApiClient mApiClient;

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

                // wait 2 seconds before launching a browser
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                        startActivity(browserIntent);
                    }
                }, 2000);
            }
        });

        // set up the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
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
        } else {
            email.setText(strEmail);
        }

        // get root view (useful for making snackbars)
        root = (View) findViewById(R.id.main_root_view);

        // initialize GoogleApiClient to talk to wear
        initGoogleApiClient();
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

    /* BEGIN ANDROID WEAR COMMUNICATION */

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // initialize connection to wear
        sendMessage(MSG_INIT_FROM_DEVICE, "");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("wrkr", "ABCDE CONNECTION TO WEAR DEVICE SUSPENDED");
    }

    // TODO - make a Common.java that mobile and wear share that contains common code like this method
    public void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                Log.d("wrkr", "ABCDE there are " + nodes.getNodes().size() + " nodes found");
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();

                    Log.d("wrkr", "ABCDE RESULT = " + result.getStatus().toString());
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if ( mApiClient != null ) {
            //Wearable.MessageApi.removeListener( mApiClient, this ); //TODO enable once listening
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
    }
}
