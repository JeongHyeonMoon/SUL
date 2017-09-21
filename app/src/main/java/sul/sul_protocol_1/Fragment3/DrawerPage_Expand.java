package sul.sul_protocol_1.Fragment3;

/**
 * Created by heeeeju on 2016-06-27.
 */
/*
 *
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sul.sul_protocol_1.R;
import sul.sul_protocol_1.sercive.MyService;

@SuppressWarnings("deprecation")
public class DrawerPage_Expand extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    //frgment -> 전환시킬 탭
    private android.support.v4.app.Fragment fragment = null;
    private ExpandableListView expListView;
    private HashMap<String, List<String>> listDataChild;
    //List확장을 위한 소스
    private ExpandableListAdapter listAdapter;
    //큰 제목들을 가지고 있음
    private List<String> listDataHeader;
    boolean check =true;
    //리스트를 만들어 주기 위해
    private ArrayList<String> mDrawerItmes = null;
    private ArrayList<String> mDrawerItmes_div = null;
    private ArrayList<String> friend_info = null;

    static int[] icon = { R.drawable.what, R.drawable.p_name,
            R.drawable.place, R.drawable.a,
            R.drawable.b, R.drawable.c, R.drawable.d,
            R.drawable.e, R.drawable.f,
            R.drawable.g, R.drawable.h };
    View view_Group;

    private static final int FILTER_ID = 0;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    private ActionBarDrawerToggle mDrawerToggle;

    // 약속장소
    String glat = "";
    String glon = "";

    // 현재위치
    Double latitude ;
    Double longitude;

    // SQLITE 에 있는 내 그룹 친구 목록을 가져옴
    GroupDBHelper myDb;
    Cursor res;
    Cursor res2;
    Cursor res3;
    ArrayList<String> task_gid;
    ArrayList<String> task_name;
    ArrayList<String> task_id;
    ArrayList<String> friend_phone;

    String select_id = "";
    ArrayList<String> friend_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_f3);

        // enabling action bar app icon and behaving it as toggle button   "#0082C3"  ffebee
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#ffcdd2"))); //액션바 색깔ffcdd2
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTitle = mDrawerTitle = getTitle();


        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.clear();
        listDataChild.clear();
        //d친구차가 실행
        setUpDrawer();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                // accessibility
                R.string.app_name // nav drawer close - description for
                // accessibility
        ) {
            @Override
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        makeActionOverflowMenuShown();
        if (savedInstanceState == null) {
            navigateTo(0,0);
        }

        // 약속 위도 경도
        glat = (String) getIntent().getSerializableExtra("glat");
        glon = (String) getIntent().getSerializableExtra("glon");

        // 퍼미션 체크
        checkDangerousPermissions();

        //현재위치 받아오기
        startLocationService();

        // sqlite 생성
        myDb = new GroupDBHelper(this);

        // sqlite 에 담겨 있는 친구들의 이름,ID 담기
        task_gid = new ArrayList<String>();
        task_id = new ArrayList<String>();
        task_name = new ArrayList<String>();
        friend_phone = new ArrayList<String>();

        // 그룹 명
        Bundle bundle = this.getIntent().getExtras();
        select_id = bundle.getString("select_id");
        friend_id = (ArrayList<String>) this.getIntent().getSerializableExtra("friend_id");
        friend_phone = (ArrayList<String>) this.getIntent().getSerializableExtra("friend_phone");

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



    // actionbar over flow icon
    private void makeActionOverflowMenuShown() {
        // devices with hardware menu button (e.g. Samsung ) don't show action
        // overflow menu
        try {
            final ViewConfiguration config = ViewConfiguration.get(this);
            final Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (final Exception e) {
            Log.e("", e.getLocalizedMessage());
        }
    }

    /**
     *
     * Get the names and icons references to build the drawer menu...
     */
    private void setUpDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		/*
		 * mDrawerLayout.setScrimColor(getResources().getColor(
		 * android.R.color.transparent));
		 */
        mDrawerLayout.setDrawerListener(mDrawerListener);
        expListView = (ExpandableListView) findViewById(R.id.list_slidermenu);



        prepareListData();


        listAdapter = new ExpandableListAdapter(this, listDataHeader,
                listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        // expandable list view click listener

        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // setbackground color for list that is selected in child group
                v.setSelected(true);
                if (view_Group != null) {
                    //색지정
                    view_Group.setBackgroundColor(Color.parseColor("#ffcdd2"));
                }
                view_Group = v;
                view_Group.setBackgroundColor(Color.parseColor("#ffcdd2"));


                navigateTo(groupPosition, childPosition);
                return false;
            }

        });
    }
    private void navigateTo(int groupPosition, int childPosition){
        switch (groupPosition) {

				/*
				 * Here add your fragment class name for each case menu (eg.
				 * Layout1, layout2 in screen) you can add n number of classes
				 * to the swithch case Also when you add the class name here,
				 * also add the corresponding name to the array list
				 */
            // dash board
            case 0: // 처음 보여주고 싶은 페이지
                //getSupportFragmentManager()
                        //.beginTransaction()
                      //  .replace(R.id.frame_container, GroupFriendMaps2Activity2.newInstance(), GroupFriendMaps2Activity2.TAG).commit();
                fragment = new GroupFriendMaps2Activity2();
                break;

            // before you file
            case 1:
                switch (childPosition) {
                    case 0:
                      //  fragment = new One();
                        break;
                    case 1:
                      //  fragment = new One();
                        break;

                    default:
                        break;
                }
                break;

            // 약속장소 부분
            case 2:
                switch (childPosition) {
                    case 0:
                        // Daum Map 으로 이동 (Daum Map 있어야 가능)
                        String strlat = String.valueOf(latitude);
                        String strlon = String.valueOf(longitude);
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?sp="+strlat+","+strlon+"&ep="+glat+","+glon+"&by=PUBLICTRANSIT"));
                        startActivity(myIntent);
                        break;
                    case 1:
                        // 약속 장소 보기
                       /* getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, GroupFriendMaps2Activity2.newInstance(), GroupFriendMaps2Activity2.TAG).commit();*/
                        fragment = new GroupFriendMaps2Activity2();
                        break;
                    case 2:
                        //  집 맵 보기
                       /* getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, GroupHomeMapsActivity.newInstance(), GroupHomeMapsActivity.TAG).commit();*/
                        fragment = new GroupHomeMapsActivity();
                        break;

                    default:
                        break;
                }
                break;

            // income slip

            ///////////////////친구는 3부터 시작///////////////////
            case 3: // 첫번째 친구
                switch (childPosition) {

                    case 0: // 친구 위치 파악
                        // 누구가 선택되었는지 정보 넘겨주기
                        Bundle bundle = new Bundle();
                        bundle.putString("select_friend",friend_id.get(0));
                        /*getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, GroupFriendMaps2Activity2.newInstance(), GroupFriendMaps2Activity2.TAG).commit();*/
                        fragment = new GroupFriendMaps2Activity2();
                        break;
                    case 1: // 전화걸기
                        //Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+task_phone.get(0)));
                        //startActivity(new Intent("android.intent.action.CALL",Uri.parse("tel:010-0000-0000")));
                        //uri= Uri.parse("tel:01012345678"); //전화와 관련된 Data는 'Tel:'으로 시작. 이후는 전화번호
                        //i= new Intent(Intent.ACTION_DIAL,Uri.parse("tel:01012345678")); //시스템 액티비티인 Dial Activity의 action값
                        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+friend_phone.get(0))));// 다이얼만 눌러줌 액티비티 실행
                        //startActivity(new Intent("android.intent.action.CALL",Uri.parse("tel:010-0000-0000"))); // 실제로 전화걸기

                        break;
                    case 2: //
                        // fragment = new One();
                        break;
                    default:
                        break;
                }
                break;

            // federal deduction
            case 4: // 두번째 친구
                switch (childPosition) {
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putString("select_friend",friend_id.get(1));
                        break;
                    case 1: // 전화걸기
                        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+friend_phone.get(1))));// 다이얼만 눌러줌 액티비티 실행
                        //startActivity(new Intent("android.intent.action.CALL",Uri.parse("tel:010-0000-0000"))); // 실제로 전화걸기
                        break;
                    case 2:
                        // fragment = new One();
                        break;

                    default:
                        break;
                }
                break;

            // provincial activity
            case 5: // 세번째 친구
                switch (childPosition) {
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putString("select_friend",friend_id.get(2));
                        break;
                    case 1:
                        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+friend_phone.get(2))));// 다이얼만 눌러줌 액티비티 실행
                        //startActivity(new Intent("android.intent.action.CALL",Uri.parse("tel:010-0000-0000"))); // 실제로 전화걸기
                        break;

                    default:
                        break;
                }
                break;

            // expenses
            case 6: // 네번째 친구
                switch (childPosition) {
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putString("select_friend",friend_id.get(3));
                    case 1:
                        startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+friend_phone.get(3))));// 다이얼만 눌러줌 액티비티 실행
                        //startActivity(new Intent("android.intent.action.CALL",Uri.parse("tel:010-0000-0000"))); // 실제로 전화걸기
                        break;
                    case 2:
                        //fragment = new One();
                        break;

                    default:
                        break;
                }
                break;

            case 7: // 다섯번째 친구
                // fragment = new One();
                switch (childPosition) {
                    case 0:
                        if(friend_phone.size()>=5) {
                            Bundle bundle = new Bundle();
                            bundle.putString("select_friend", friend_id.get(4));
                        }
                        break;
                    case 1:// 전화걸기
                        if(friend_phone.size()>=5) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + friend_phone.get(4))));// 다이얼만 눌러줌 액티비티 실행
                            //startActivity(new Intent("android.intent.action.CALL",Uri.parse("tel:010-0000-0000"))); // 실제로 전화걸기
                        }
                        break;
                    case 2:
                        //fragment = new One();
                        break;

                    default:
                        break;
                }
                break;
            case 8:
                // fragment = new One();
                switch (childPosition) {
                    case 0:
                        if(friend_phone.size()>=6) {
                            Bundle bundle = new Bundle();
                            bundle.putString("select_friend", friend_id.get(5));
                        }
                        break;
                    case 1:
                        if(friend_phone.size()>=6) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + friend_phone.get(5))));// 다이얼만 눌러줌 액티비티 실행
                            //startActivity(new Intent("android.intent.action.CALL",Uri.parse("tel:010-0000-0000"))); // 실제로 전화걸기
                        }
                        break;
                    case 2:
                        //fragment = new One();
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;
        }
        //fragment = new GroupFriendMaps2Activity2();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, fragment).commit();
        expListView.setItemChecked(childPosition, true);
        mDrawerLayout.closeDrawer(expListView);
        //  return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState); // Sync the toggle state after
        // onRestoreInstanceState has
        // occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig); // Pass any configuration
        // change to the drawer
        // toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem toggle = menu.findItem(R.id.action_toggle);
        toggle.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(toggle.isChecked()){

                    Log.i("stopToggle","clicked");
                    toggle.setIcon(R.drawable.toggle_off);
                    toggle.setChecked(false);


                    // 서비스 종료
                    Intent service = new Intent(DrawerPage_Expand.this, MyService.class);
                    service.putExtra("gid", (String) getIntent().getSerializableExtra("select_id"));
                    stopService(service);
                }else{
                    toggle.setIcon(R.drawable.toggle_on);
                    toggle.setChecked(true);

                    Intent service1 = new Intent(DrawerPage_Expand.this, MyService.class);
                    stopService(service1);
                    // 서비스 등록
                    Intent service2 = new Intent(DrawerPage_Expand.this, MyService.class);
                    //Intent service = new Intent(GroupFriendMaps2Activity2.newInstance().getContext(), MyService.class);
                    startService(service2);
                    Log.i("service","start");
                }
                return true;
            }
        });

        final MenuItem refresh = menu.findItem(R.id.action_settings);
        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("refresh","clicked");


                fragment.onResume();
                /*
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, GroupFriendMaps2Activity2.newInstance(), GroupFriendMaps2Activity2.TAG).commit();
*/
                return true;
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Catch the events related to the drawer to arrange views according to this
    // action if necessary...
    private DrawerListener mDrawerListener = new DrawerListener() {

        @Override
        public void onDrawerStateChanged(int status) {

        }

        @Override
        public void onDrawerSlide(View view, float slideArg) {

        }

        @Override
        public void onDrawerOpened(View view) {
            getActionBar().setTitle(mDrawerTitle);
            // calling onPrepareOptionsMenu() to hide action bar icons
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerClosed(View view) {
            getActionBar().setTitle(mTitle);
            // calling onPrepareOptionsMenu() to show action bar icons
            invalidateOptionsMenu();
        }
    };

    private void prepareListData() {



        listDataHeader.clear();
        //약속, 이름, 등등을 가져오는것
        mDrawerItmes_div = (ArrayList<String>) getIntent().getSerializableExtra("friend_id");
        //이것이 약속 정보
        friend_info = (ArrayList<String>) getIntent().getSerializableExtra("friend_info");
        //중복된값 잘라내기

        for (int j = friend_info.size() - 1; j > 2; j--) {
            friend_info.remove(j);
        }
        // friend_info.add("친구이름");
        for (int j = 0; j < mDrawerItmes_div.size(); j++) {
            //  friend_info.add(mDrawerItmes_div.get(j));
            friend_info.add(mDrawerItmes_div.get(j));
        }
        //String[] array = getResources()
        //   .getStringArray(R.array.nav_drawer_items);
        // String[] array = (ArrayList<String>) getIntent().getSerializableExtra("friend_id");
        // listDataHeader = Arrays.asList(array);
        listDataHeader = friend_info;

        // Adding child data
        // dash board
        List<String> dashboard = new ArrayList<String>();
        String[] dash = getResources().getStringArray(R.array.dash_board);
        dashboard = Arrays.asList(dash);


        //약속이름
        // before you file
        List<String> l1 = new ArrayList<String>();
        String[] before = getResources()
                .getStringArray(R.array.before_you_file);
        l1 = Arrays.asList(before);

        // profile
        //약속일시
        List<String> l2 = new ArrayList<String>();
        //  String[] myproe = getResources().getStringArray(R.array.my_profile);
        String[] myproe = getResources().getStringArray(R.array.before_you_file);
        l2 = Arrays.asList(myproe);

        // income slip
        //약속 장소
        List<String> l3 = new ArrayList<String>();
        //String[] inco = getResources().getStringArray(R.array.income_slip);
        String[] inco = {"친구 위치파악", "전화걸기"};
        l3 = Arrays.asList(inco);

        // federal deduction
        List<String> l4 = new ArrayList<String>();
        String[] fed = {"친구 위치파악", "전화걸기"};
        //   String[] fed = getResources().getStringArray(R.array.federal_deduction);
        l4 = Arrays.asList(fed);

        // provincial credits
      //  List<String> l5 = new ArrayList<String>();
      //  String[] provi = getResources().getStringArray(
       //         R.array.provincial_credit);
      //  l5 = Arrays.asList(provi);
/*
        // expenses
        List<String> l6 = new ArrayList<String>();
        String[] expen = getResources().getStringArray(R.array.expenses);
        l6 = Arrays.asList(expen);

        // review
        List<String> l7 = new ArrayList<String>();
        String[] revieww = getResources().getStringArray(R.array.review);
        l7 = Arrays.asList(revieww);
*/
        // cra profile
        //    List<String> l8 = new ArrayList<String>();

        // submit
        //   List<String> l9 = new ArrayList<String>();

        // cloud drive
        //    List<String> l10 = new ArrayList<String>();

        // assigning values to menu and submenu

        listDataChild.clear();
        //  for (int j = 0; j < listDataChild.size(); j++) {
        //   listDataChild.remove(j);
        //  }
        listDataChild.put(listDataHeader.get(0), l2); // Header, Child
        // data
        //리스트의 정보를 넣어준다.
        listDataChild.put(listDataHeader.get(1), l1);
        listDataChild.put(listDataHeader.get(2), dashboard);

        //   if(check ==true) {
        for (int j = 0; j < mDrawerItmes_div.size(); j++) {
            //friend_info.add(mDrawerItmes_div.get(j));
            listDataChild.put(listDataHeader.get(j + 3), l3);
        }
        //     check =false;
        // }
        // listDataChild.put(listDataHeader.get(3), l3);
        // listDataChild.put(listDataHeader.get(4), l3);
        // listDataChild.put(listDataHeader.get(5), l3);
        //listDataChild.put(listDataHeader.get(6), l6);
        //     listDataChild.put(listDataHeader.get(7), l7);
        //   listDataChild.put(listDataHeader.get(8), l8);
        //  listDataChild.put(listDataHeader.get(9), l9);
        //  listDataChild.put(listDataHeader.get(10), l10);

    }

    //어댑터가 사용하는것 -> 레이아웃 다룸?
    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;

        public ExpandableListAdapter(Context context,
                                     List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(
                    this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition,
                    childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(
                    this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }
            // changing the +/- on expanded list view
            TextView txt_plusminus = (TextView) convertView
                    .findViewById(R.id.plus_txt);
            if (isExpanded) {
                if(groupPosition==0||groupPosition==1){
                    txt_plusminus.setText("");
                } else {
                    txt_plusminus.setText("v");
                }
            } else {
                if(groupPosition==0||groupPosition==1){
                    txt_plusminus.setText("");
                } else {
                    txt_plusminus.setText("^");
                }
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            // lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            // nav drawer icons from resources
            // navMenuIcons =
            // getResources().obtainTypedArray(R.array.nav_drawer_icons);
            // imgListGroup.setImageDrawable(navMenuIcons.getDrawable(groupPosition));

            // adding icon to expandable list view
            ImageView imgListGroup = (ImageView) convertView
                    .findViewById(R.id.ic_txt);
            imgListGroup.setImageResource(DrawerPage_Expand.icon[groupPosition]);
            return convertView;
        }

        /*
        public View getGroupView2(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }
            // changing the +/- on expanded list view
            TextView txt_plusminus = (TextView) convertView
                    .findViewById(R.id.plus_txt);
            if (isExpanded) {
                txt_plusminus.setText("");
            } else {
                txt_plusminus.setText("");
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            // lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            // nav drawer icons from resources
            // navMenuIcons =
            // getResources().obtainTypedArray(R.array.nav_drawer_icons);
            // imgListGroup.setImageDrawable(navMenuIcons.getDrawable(groupPosition));

            // adding icon to expandable list view
            ImageView imgListGroup = (ImageView) convertView
                    .findViewById(R.id.ic_txt);
            imgListGroup.setImageResource(MainActivity.icon[groupPosition]);
            return convertView;
        }
*/
        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
