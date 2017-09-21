package sul.sul_protocol_1.Fragment3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import sul.sul_protocol_1.Fragment0.UserBean;
import sul.sul_protocol_1.R;

// search 해서 가져오지말고 , 전체를 일단 다 가져오는 방식으로, 그중에서 친구만 골라내기
public class GroupHomeMapsActivity extends Fragment implements OnMapReadyCallback {

    public final static String TAG = GroupHomeMapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    MarkerOptions marker = new MarkerOptions();
    MarkerOptions hmarker = new MarkerOptions();
    MarkerOptions mmarker = new MarkerOptions();

    MarkerOptions appointMarker = new MarkerOptions();
    Marker groupmember = null;
    Marker appoint = null;
    Marker homemarker = null;

    //send data
    private double latitude; // 나의 현재 위치 위도
    private double longitude; // 나의 현재 위치 경도
    private String u_x; // 위도
    private String u_y; // 경도
    private String u_id = UserBean.getId(); // 이 기기의 아이디

    // getdata
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADD = "address";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";
    private static final String TAG_HLAT = "hlat";
    private static final String TAG_HLON = "hlon";
    private static final String TAG_CONTEXT = "context";
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    ArrayList<Marker> groupmarkersList;
    ArrayList<LatLng> homeLatLng;
    LatLng myhome;
    ArrayList<Marker> homemarkersList;

    // SQLITE 에 있는 내 그룹 친구 목록을 가져옴
    GroupDBHelper myDb;
    Cursor res;
    Cursor res2;
    Cursor res3;
    //Cursor res4;
    ArrayList<String> task_gid;
    ArrayList<String> task_name;
    ArrayList<String> task_id;
    String x;
    String y;
    Double lox = 0.0;
    Double loy = 0.0;

    String placename;
    ArrayList<String> hlatarr;
    ArrayList<String> hlonarr;

    int friendcount = 0;
    String select_id = "";
    String glat = "";
    String glon = "";
    ArrayList<String> friend_id = null;
    ArrayList<Integer> friend_arrival = null;
    //ArrayList<LatLng> friend_home = null;
    int length = 0 ;

    // count 변수
    int counthome = 0;

    int counthomearrival = 0;

    public GroupHomeMapsActivity(){

    }
    public static GroupHomeMapsActivity newInstance(){    return new GroupHomeMapsActivity(); }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.all_friend_context_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);

        if (Build.VERSION.SDK_INT < 21) {
            mapFragment = (SupportMapFragment) getActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map);
        } else {
            mapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.map);
        }

        mapFragment.getMapAsync(this);

        // 친구들 목록 가져옴
        personList = new ArrayList<>(); // 리스트 생성
        groupmarkersList = new ArrayList<Marker>();
        homeLatLng = new ArrayList<LatLng>();
        myhome = null;
        homemarkersList = new ArrayList<Marker>();

        // 집 위도 경도를 가져오는 변수
        hlatarr = new ArrayList<String>();
        hlonarr = new ArrayList<String>();

        // sqlite 생성
        myDb = new GroupDBHelper(getActivity());

        // sqlite 에 담겨 있는 친구들의 이름,ID 담기
        task_gid = new ArrayList<String>();
        task_id = new ArrayList<String>();
        task_name = new ArrayList<String>();

        // 그룹 명
        Bundle bundle = getActivity().getIntent().getExtras();
        select_id = bundle.getString("select_id");
        glat = bundle.getString("glat");
        glon = bundle.getString("glon");
        select_id = bundle.getString("select_id");
        friend_id = (ArrayList<String>) getActivity().getIntent().getSerializableExtra("friend_id");
        friend_arrival = (ArrayList<Integer>) getActivity().getIntent().getSerializableExtra("friend_arrival");
        //friend_home = (ArrayList<LatLng>) getActivity().getIntent().getSerializableExtra("friend_home");
        length = 0;

        // sqlite에서 친구들 가져옴
        res = myDb.getAllData();

        if (res.getCount() == 0) {
            //친구 명수가 0이면
            Toast.makeText(getActivity().getApplicationContext(), "친구를 추가해주세요.", Toast.LENGTH_SHORT).show();
            // return;
        }

        int i = 0;
        while (res.moveToNext()) {
            if (res.getString(1) != null) {
                task_gid.add(res.getString(0));
                if(select_id.equals(task_gid.get(i))) {
                    //task_name.add(res.getString(1));
                    //task_id.add(res.getString(2));
                    //task_lat.add(res.getString(3));
                    //task_lon.add(res.getString(4));
                    //System.out.println("test ID : " + task_id.get(i));
                    //System.out.println("test 이름 : " + task_name.get(i));
                    //System.out.println("test 위도 : " + task_lat.get(i));
                    //System.out.println("test 경도 : " + task_lon.get(i));
                    //System.out.println("test 주소 : " + res.getString(3));
                    //System.out.println("test 사진 : " + res.getString(5));
                    length ++;
                }
            }
            i++;
        }
        System.out.println("test 그룹원 수 : " + length);

        // 약속장소 정보 가져오기
        res2 = myDb.getXY(select_id);
        while (res2.moveToNext()){
            if (res2.getString(0) != null) {
                x = res2.getString(0);
                y = res2.getString(1);
            }
        }

        // 약속 위치 Double로 전환
        lox = Double.parseDouble(x);
        loy = Double.parseDouble(y);


        // 약속 장소 가져오기
        res3 = myDb.getplacename(select_id);
        while (res3.moveToNext()){
            if(res3.getString(0) != null){
                placename = res3.getString(0); // 약속 장소 이름이 담김
            }
        }

/*
        res4 = myDb.getfhome(select_id);
        while (res4.moveToNext()){
            if(res4.getString(0) != null){
                friend_home.add(new LatLng(Double.valueOf(res4.getString(0)),Double.valueOf(res4.getString(1))));
            }
        }

        res4 = myDb.getmyhome(select_id);
        while (res4.moveToNext()){
            if(res4.getString(0) != null){
                friend_home.add(new LatLng(Double.valueOf(res4.getString(0)),Double.valueOf(res4.getString(1))));
            }
        }
        */
        //task_gid.clear();
/*
        int u = 0; int uu = 0;
        while (res.moveToNext()) {
            if (res.getString(1) != null) {
                task_gid.add(res.getString(0));
                if (select_id.equals(task_gid.get(u)) && (uu <length)) {
                        task_name.add(res.getString(1));
                        task_id.add(res.getString(2));
                        System.out.println("test ID : " + task_id.get(u));
                        System.out.println("test 이름 : " + task_name.get(u));
                    uu++;
                }
            }
            u++;
        }
*/
        System.out.println("test 친구수 : " + res.getCount());
        friendcount = res.getCount();

        // 내 위치 + 친구들 위치 보임
        startLocationService();

        // 약속장소 마커 찍음
        //showmeetingpoint();

        // 집 마커 찍음
        //showhomemarker();


        //버튼 달기
        ImageButton btn = (ImageButton)rootView.findViewById(R.id.btn_send);
        final EditText input = new EditText(getActivity());

        btn.setOnClickListener(new ViewGroup.OnClickListener() {
            public void onClick(View v) {
                // 버튼을 누르면 다이얼로그창
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                alert.setTitle("메세지를 입력하세요"); //Set Alert dialog title here
                alert.setMessage("친구에게 한마디를 남겨보세요."); //Message here
                alert.setView(input);

                //ok
                alert.setPositiveButton("보내기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String u_id= UserBean.getId();
                        String u_context = input.getText().toString();


                        //-------------------수정
                        updateToDatabase(u_id, u_context);

                        //finish();
                        //String srt = input.getEditableText().toString();
                        Toast.makeText(getContext(), u_context, Toast.LENGTH_LONG).show();
                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); //End of alert.setPositiveButton


                //no
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                        getActivity().finish();
                    }
                });

                android.app.AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });

        return rootView;
    }


    // 대화 보내기
    private void updateToDatabase(String u_id, String u_context){

        class UpdateData extends AsyncTask<String,Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(), "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "메시지 수정 성공", Toast.LENGTH_SHORT).show();

            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String u_id = (String)params[0];
                    String u_context = (String)params[1];

                    String link="http://sul.duksung.ac.kr/test/messageupdate.php";
                    String data  = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(u_id, "UTF-8");
                    data += "&" + URLEncoder.encode("u_context", "UTF-8") + "=" + URLEncoder.encode(u_context, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        UpdateData task = new UpdateData();
        task.execute(u_id,u_context);
    }

    // 상태 바 알림
    public void NotificationSomethins1(String id) {


        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = getResources();


        Intent notificationIntent = new Intent(this.getActivity() , DrawerPage_Expand.class);
        //notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(this.getActivity() , 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext().getApplicationContext() )
                .setContentTitle("Sul")
                .setContentText(id + "님이 집에 도착했습니다.")
                // .setTicker("상태바 한줄 메시지")
                .setSmallIcon(R.drawable.sul)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.sul))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults( Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                .setNumber(13);

        Notification  n = builder.build();
        nm.notify(1234, n);
    }
    // 지도 기초 설정
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng curPoint = new LatLng(37.566535, 126.97796919999996);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 11));
    }

    // 약속장소 찍기
    private void showmeetingpoint() {

        String name = placename; // 지명이름
        appointMarker.position(new LatLng(lox, loy));
        appointMarker.title("약속 장소");
        appointMarker.snippet(name);
        appointMarker.draggable(true);
        appointMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.here2));

        // 마커가 한번만 찍히도록
        if(appoint != null){
            appoint.remove();
        }
        appoint = mMap.addMarker(appointMarker);
        mMap.addMarker(appointMarker);
    }

    // 집 찍기
    private void showhomemarker(){
        // 친구 집을 찍음

        for (int j = 0; j < homeLatLng.size(); j++) {
            hmarker.position(homeLatLng.get(j));
            hmarker.title(task_name.get(j)+"의 집");
            marker.snippet(task_id.get(j));
            hmarker.draggable(true);
            hmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.yourhome4));
            mMap.addMarker(hmarker);
        }
        // 내 집을 찍음
        mmarker.position(myhome);
        mmarker.title("우리집");
        mmarker.draggable(true);
        mmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.yourhome4));
        mMap.addMarker(mmarker);

// 친구들 집 정보 가져오기
        /*
        if(friend_home.size() != 0){
            friend_home.clear();
        }

        for (int j = 0; j < friendcount+1; j++) {
            hmarker.position(friend_home.get(j));
            /*
            if(friend_id.get(j).equals(UserBean.getId())) {
                hmarker.title("우리집");
            }else {
                hmarker.title((friend_id.get(j)));
            }
            */
        /*
            hmarker.draggable(true);
            hmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.yourhome4));
            homemarker = mMap.addMarker(hmarker);
            homemarkersList.add(homemarker);
        }
  */
    }

    // 사이즈 조절
    /*
    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
    */



    // 위치 서비스 시작하면서 내 위치를 서버로 보냄
    private void startLocationService() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        GPSListener gpsListener = new GPSListener();
        long minTime = 10000; // 최소 10초 이후에는 표시하겠다.
        float minDistance = 0; // 최소 0 만큼 움직이면 반영하겠다.

        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

            // 위치가 바뀌면 바뀐 값 전달
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();

                // 자신의 현재위치를 GroupBean에 저장
                GroupBean.setmylat(latitude);
                GroupBean.setmylon(longitude);
            }

            //showhomemarker();

        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            // 전역변수 이용 현재위치 저장
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            // 동시에 서버에 자신의 현재 위치를 보냄
            u_x = String.valueOf(latitude);
            u_y = String.valueOf(longitude);

            String u_x = String.valueOf(latitude);
            String u_y = String.valueOf(longitude);

            // 자기 위치 서버로 보냄
            insertToDatabase(u_x, u_y, u_id);

            // 마커 찍기
            showAllGroupMembers();

            // 약속 장소 찍기
            //showmeetingpoint();

            // 집 찍기
            if(counthome == 0) {
                showhomemarker();
                counthome ++;
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    // 나의 현재위치를 서버로 보냄
    private void insertToDatabase(String u_x, String u_y, String u_id) {
        class InsertData extends AsyncTask<String, Void, String> {
            //ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String u_x = (String) params[0];
                    String u_y = (String) params[1];
                    String u_id = (String) params[2];

                    String link = "http://sul.duksung.ac.kr/currentgpsinsert.php";
                    String data = URLEncoder.encode("u_x", "UTF-8") + "=" + URLEncoder.encode(u_x, "UTF-8");
                    data += "&" + URLEncoder.encode("u_y", "UTF-8") + "=" + URLEncoder.encode(u_y, "UTF-8");
                    data += "&" + URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(u_id, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(u_x, u_y, u_id);
    }

    // 친구들을 마커로 찍음
    private void showAllGroupMembers() {

        getData("http://sul.duksung.ac.kr/test/context/getdata.php");

        for (int i = 0; i < groupmarkersList.size(); i++) {
            if (groupmarkersList.get(i) != null) {
                groupmarkersList.get(i).remove();
            }
            groupmarkersList.get(i);
        }
    }



    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                makeList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    protected void makeList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);


            // homeLatLng에 넣음
            if(homeLatLng.size() != 0){
                homeLatLng.clear();
                task_id.clear();
                task_name.clear();
            }


            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                String address = c.getString(TAG_ADD);
                String lat = c.getString(TAG_LAT);
                String lon = c.getString(TAG_LON);

                // 집 정보
                String hlat = c.getString(TAG_HLAT);
                String hlon = c.getString(TAG_HLON);

                // 대화 정보
                String context = c.getString(TAG_CONTEXT);

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_ID, id);
                persons.put(TAG_NAME, name);
                persons.put(TAG_ADD, address);
                persons.put(TAG_LAT, lat);
                persons.put(TAG_LON, lon);
                persons.put(TAG_HLAT, hlat);
                persons.put(TAG_HLON, hlon);
                persons.put(TAG_CONTEXT,context);
                personList.add(persons);


                for (int j = 0; j < length; j++) {
                    if (id.equals(friend_id.get(j))) {
                        marker.position(new LatLng(Double.valueOf(persons.get("lat")).doubleValue(), Double.valueOf(persons.get("lon")).doubleValue()));
                        marker.title(persons.get("name")+"( "+persons.get("id")+")");
                        marker.snippet(persons.get("context"));
                        marker.draggable(true);
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pp));
                        groupmember = mMap.addMarker(marker);
                        groupmarkersList.add(groupmember);


                        homeLatLng.add(new LatLng(Double.valueOf(persons.get("hlat")).doubleValue(), Double.valueOf(persons.get("hlon")).doubleValue()));
                        task_id.add(id);
                        task_name.add(name);

                        // 마커로 찍을 때 위치 정보를 저장
                        //hlatarr.add(persons.get("hlat"));
                        //hlonarr.add(persons.get("hlon"));

                        System.out.print("test" + persons.get("hlat"));

                    } else if (id.equals(u_id)) {
                        marker.position(new LatLng(Double.valueOf(persons.get("lat")).doubleValue(), Double.valueOf(persons.get("lon")).doubleValue()));
                        marker.title("나");
                        marker.snippet(persons.get("context"));
                        marker.draggable(true);
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.uu));
                        groupmember = mMap.addMarker(marker);
                        groupmarkersList.add(groupmember);

                        // homeLatLng에 넣음
                        //homeLatLng.add(new LatLng(Double.valueOf(persons.get("hlat")).doubleValue(), Double.valueOf(persons.get("hlon")).doubleValue()));
                        myhome = new LatLng(Double.valueOf(persons.get("hlat")).doubleValue(), Double.valueOf(persons.get("hlon")).doubleValue());

                        // 마커로 찍을 때 위치 정보를 저장
                        //hlatarr.add(persons.get("hlat"));
                        //hlonarr.add(persons.get("hlon"));

                        //System.out.print("test" + persons.get("hlat"));

                    }

                }

                // Mission 1. 도착하면 알람 울리기 2. 알람 한번만 울리기 3. 핸드폰 꺼져있어도 알람뜨게 하기
/*
                marker.position(new LatLng(Double.valueOf(persons.get("lat")).doubleValue(), Double.valueOf(persons.get("lon")).doubleValue()));
                marker.title(persons.get("name"));
                marker.snippet(persons.get("id"));
                marker.draggable(true);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bank2));
                groupmember = mMap.addMarker(marker);
                groupmarkersList.add(groupmember);
*/
                // homeLatLng에 넣음
                //homeLatLng.add(new LatLng(Double.valueOf(persons.get("hlat")).doubleValue(), Double.valueOf(persons.get("hlon")).doubleValue()));


                // 도착하면 알람 + sqlite를 이용해서 한번만 알람이 뜨도록해야 함.
/*
                for (int j = 0; j < length; j++) {
                    if (id.equals(friend_id.get(j))) {
                        if((friend_arrival.get(j)).equals(1)) { // farrival = 0이면
                            if (Math.abs(Double.valueOf(persons.get("lat")).doubleValue() - lox) + Math.abs(Double.valueOf(persons.get("lon")).doubleValue() - loy) < 0.01) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
                                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();  //닫기
                                    }
                                });
                                // farrival = 1 로 바꿈
                                updateArrivalata(id,0);
                                alert.setMessage("" + persons.get("name") + "님 약속 장소 도착");
                                alert.show();
                            }

                        }
                    }
                }
                */

                // 다 도착하면 오늘의 모임 시작 메세지 띄우기 + sqllite를 이용해야할 듯

                // 집에 도착하면 집에 도착했다고 알림
                for (int j = 0; j < length; j++) {
                    if (id.equals(friend_id.get(j))) {
                        if (Math.abs(Double.valueOf(persons.get("lat")).doubleValue() - Double.valueOf(persons.get("hlat")).doubleValue()) + Math.abs(Double.valueOf(persons.get("lon")).doubleValue() - Double.valueOf(persons.get("hlon")).doubleValue()) < 0.01) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            NotificationSomethins1(persons.get("name"));

                            alert.setMessage("" + persons.get("name") + "님 집 도착");
                            alert.show();

                            //int tmpcount = hcountList.get(i);
                            //hcountList.set(i,tmpcount++);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 정보 업데이트 함수
    public void updateArrivalata(String fid, int farrival) {
        // user table에 8개 insert 시킴
        boolean isupdated = myDb.updateArivalData(fid,farrival);

        if (isupdated == true)
            Toast.makeText(this.getContext(), "SQLite Update", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this.getContext(), "SQLite Update 실패", Toast.LENGTH_SHORT).show();
    }


    public void onStop() {
        super.onStop();
    }

    // 친구들 목록을 가져오고, 내 위치, 친구들 위치를 계속 반복해서 실행하는 스레드 필요
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateThread();
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        Thread myThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        handler.sendMessage(handler.obtainMessage());
                        Thread.sleep(100000);
                    } catch (Throwable t) {
                    }
                }
            }
        });
        myThread.start();
    }

    private void updateThread() {
        showAllGroupMembers();
    }

}
