package sul.sul_protocol_1.Fragment3;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import sul.sul_protocol_1.R;

/**
 * Created by heeeeju on 2016-06-23.
 */
public class DrawerPage extends AppCompatActivity {

    private static final String TAG = DrawerPage.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView mDrawerList_f;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private ArrayList<String> mDrawerItmes = null;
    private ArrayList<String> mDrawerItmes_div = null;
    private ArrayList<String> friend_info_div = null;
    private ArrayList<String> friend_info = null;
    String glat = "";
    String glon = "";

    // 현재위치
    Double latitude ;
    Double longitude;
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_map);

        mTitle = mDrawerTitle = getTitle();

        mDrawerItmes_div = (ArrayList<String>) getIntent().getSerializableExtra("friend_id");

        //이것이 약속 정보
        friend_info = (ArrayList<String>) getIntent().getSerializableExtra("friend_info");

        // 약속 위도 경도
        glat = (String) getIntent().getSerializableExtra("glat");
        glon = (String) getIntent().getSerializableExtra("glon");

        // friend_info.add(0, mDrawerItmes_div.get(0));
        for (int j = friend_info.size() - 1; j > 2; j--) {
            friend_info.remove(j);
        }

        String a = friend_info.size() + "";

        friend_info.add("친구이름 (집)");
        for (int j = 0; j < mDrawerItmes_div.size(); j++) {
            friend_info.add(mDrawerItmes_div.get(j));
        }

        //mDrawerItmes.clear();
        mDrawerItmes = new ArrayList<>();
        mDrawerItmes = friend_info;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Add items to the ListView
        //하고싶으면은 커스터머로 바꿔야 한다.
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, friend_info));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                //  getActionBar().setTitle(mTitle);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }

            public void onDrawerOpened(View drawerView) {
                //  getActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set the default content area to item 0
        // when the app opens for the first time
        if (savedInstanceState == null) {
            navigateTo(0);
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // 퍼미션 체크
        checkDangerousPermissions();

        //현재위치 받아오기
        startLocationService();
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    // 위치 서비스 시작하면서 내 위치를 서버로 보냄
    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        GPSListener gpsListener = new GPSListener();
        long minTime = 10000; // 최소 10초 이후에는 표시하겠다.
        float minDistance = 0; // 최소 0 만큼 움직이면 반영하겠다.

        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /*
     * If you do not have any menus, you still need this function
     * in order to open or close the NavigationDrawer when the user
     * clicking the ActionBar app icon.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * When using the ActionBarDrawerToggle, you must call it during onPostCreate()
     * and onConfigurationChanged()
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        //Action viewAction = Action.newAction(
                //Action.TYPE_VIEW, // TODO: choose an action type.
                //"DrawerPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                //Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                //Uri.parse("android-app://sul.sul_protocol_1.Fragment3/http/host/path")
        //);
        //AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //Action viewAction = Action.newAction(
        //        Action.TYPE_VIEW, // TODO: choose an action type.
        //        "DrawerPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
        //        Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
        //        Uri.parse("android-app://sul.sul_protocol_1.Fragment3/http/host/path")
        //);
//        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            navigateTo(position);
        }
    }

    private void navigateTo(int position) {
        //여기가 내가 열 탭들이 있는것
        switch (position) {
            case 0:
                getSupportFragmentManager()
                        .beginTransaction()
                        //.replace(R.id.content_frame, GpsActivity2.newInstance(), GpsActivity2.TAG).commit();
                        .replace(R.id.content_frame, GroupFriendMaps2Activity2.newInstance(), GroupFriendMaps2Activity2.TAG).commit();
                // .replace(R.id.content_frame, GpsFregment.newInstance(), GpsFregment.TAG).commit();
                break;
            case 2:
                // 현재위치부터 도착지까지의 가는 방법 다음앱으로 연결
                String strlat = String.valueOf(latitude);
                String strlon = String.valueOf(longitude);
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp="+strlat+","+strlon+"&ep="+glat+","+glon+"&by=PUBLICTRANSIT"));
                startActivity(myIntent);
                break;
            /*case 3:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, GroupFriendMaps2Activity2.newInstance(), GroupHomeMapsActivity.TAG).commit();

                break;
                 */
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
        //   getActionBar().setTitle("");

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#330000ff")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));
    }

}