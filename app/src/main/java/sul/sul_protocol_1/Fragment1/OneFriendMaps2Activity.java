package sul.sul_protocol_1.Fragment1;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import sul.sul_protocol_1.Fragment2.FriendDBHelper;
import sul.sul_protocol_1.R;

// search 해서 가져오지말고 , 전체를 일단 다 가져오는 방식으로, 그중에서 친구만 골라내기
public class OneFriendMaps2Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    MarkerOptions marker = new MarkerOptions();
    Marker groupmember = null;

    //send data
    Double latitude; // 나의 현재 위치 위도
    Double longitude; // 나의 현재 위치 경도
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
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    ArrayList<Marker> groupmarkersList;


    // SQLITE 에 있는 내 친구 목록을 가져옴
    FriendDBHelper myDb;
    Cursor res;
    ArrayList<String> task_id;
    ArrayList<String> task_name;
    ArrayList<String> task_lat;
    ArrayList<String> task_lon;
    int friendcount = 0;
    String select_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_friend_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // 친구들 목록 가져옴
        personList = new ArrayList<>(); // 리스트 생성
        groupmarkersList = new ArrayList<Marker>();

        // sqlite 생성
        myDb = new FriendDBHelper(this);

        // sqlite 에 담겨 있는 친구들의 이름 담기
        task_id = new ArrayList<String>();
        task_name = new ArrayList<String>();
        task_lat = new ArrayList<String>();
        task_lon = new ArrayList<String>();

        // sqlite에서 친구들 가져옴
        res = myDb.getAllData();

        if (res.getCount() == 0) {
            //친구 명수가 0이면
            Toast.makeText(getApplicationContext(), "친구를 추가해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int i = 0;
        while (res.moveToNext()) {
            if (res.getString(1) != null) {

                task_id.add(res.getString(0));
                task_name.add(res.getString(1));
                task_lat.add(res.getString(3));
                task_lon.add(res.getString(4));
                System.out.println("test ID : " + task_id.get(i));
                System.out.println("test 이름 : " + task_name.get(i));
                System.out.println("test 위도 : " + task_lat.get(i));
                System.out.println("test 경도 : " + task_lon.get(i));
                System.out.println("test 주소 : " + res.getString(3));
                System.out.println("test 사진 : " + res.getString(5));
            }
            i++;
        }

        System.out.println("test 친구수 : " + res.getCount());
        friendcount = res.getCount();
        Bundle bundle = getIntent().getExtras();
        select_id = bundle.getString("select_id");

        // 내 위치 + 친구들 위치 보임
        startLocationService();
    }

    // 지도 기초 설정
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng curPoint = new LatLng(37.566535, 126.97796919999996);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 11));
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
            // 전역변수 이용 현재위치 저장
              latitude = location.getLatitude();
              longitude = location.getLongitude();

            // 동시에 서버에 자신의 현재 위치를 보냄
            u_x = String.valueOf(latitude);
            u_y = String.valueOf(longitude);

            // 자기 위치 서버로 보냄
            insertToDatabase(u_x, u_y, u_id);

            // 마커 찍기
            showAllGroupMembers();
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

        getData("http://sul.duksung.ac.kr/getdata.php");


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
        BitmapDescriptor aa= BitmapDescriptorFactory.fromResource(R.drawable.a);
        BitmapDescriptor bb= BitmapDescriptorFactory.fromResource(R.drawable.b);
        BitmapDescriptor cc= BitmapDescriptorFactory.fromResource(R.drawable.c);
        BitmapDescriptor dd= BitmapDescriptorFactory.fromResource(R.drawable.d);
        BitmapDescriptor ee= BitmapDescriptorFactory.fromResource(R.drawable.e);
        BitmapDescriptor ff= BitmapDescriptorFactory.fromResource(R.drawable.f);
        BitmapDescriptor gg= BitmapDescriptorFactory.fromResource(R.drawable.g);
        BitmapDescriptor hh= BitmapDescriptorFactory.fromResource(R.drawable.h);
        BitmapDescriptor jj= BitmapDescriptorFactory.fromResource(R.drawable.j);
        BitmapDescriptor kk= BitmapDescriptorFactory.fromResource(R.drawable.k);
        BitmapDescriptor tt= BitmapDescriptorFactory.fromResource(R.drawable.t);
        BitmapDescriptor ll= BitmapDescriptorFactory.fromResource(R.drawable.l);
        BitmapDescriptor mm= BitmapDescriptorFactory.fromResource(R.drawable.m);
        BitmapDescriptor ss= BitmapDescriptorFactory.fromResource(R.drawable.s);
        BitmapDescriptor pp= BitmapDescriptorFactory.fromResource(R.drawable.pp);

        BitmapDescriptor iconimage = null;

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

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

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_ID, id);
                persons.put(TAG_NAME, name);
                persons.put(TAG_ADD, address);
                persons.put(TAG_LAT, lat);
                persons.put(TAG_LON, lon);
                persons.put(TAG_HLAT, hlat);
                persons.put(TAG_HLON, hlon);
                personList.add(persons);

                //아이디값을 나눈다음 이미지를 넣어줄꺼
                String idString = persons.get("id");
                String oneChar = idString.charAt(0)+"";

                //알파벳에따라서
                if(oneChar.equals("a")){
                    iconimage = aa;
                }else if(oneChar.equals("b")){
                    iconimage = bb;
                }else if(oneChar.equals("c")){
                    iconimage = cc;
                }else if(oneChar.equals("d")){
                    iconimage = dd;
                }else if(oneChar.equals("e")){
                    iconimage = ee;
                }else if(oneChar.equals("f")){
                    iconimage = ff;
                }else if(oneChar.equals("g")){
                    iconimage = gg;
                }else if(oneChar.equals("h")){
                    iconimage = hh;
                }else if(oneChar.equals("j")){
                    iconimage = jj;
                }else if(oneChar.equals("k")){
                    iconimage = kk;
                }else if(oneChar.equals("t")){
                    iconimage = tt;
                }else if(oneChar.equals("s")){
                    iconimage = ss;
                } else if(oneChar.equals("l")){
                    iconimage = ll;
                }else if(oneChar.equals("m")){
                    iconimage = mm;
                }else {
                    iconimage = pp;
                }

                    if (id.equals(select_id)) {
                        marker.position(new LatLng(Double.valueOf(persons.get("lat")).doubleValue(), Double.valueOf(persons.get("lon")).doubleValue()));
                        marker.title(persons.get("name"));
                        marker.snippet(persons.get("id"));
                        marker.draggable(true);
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.qq));
                        groupmember = mMap.addMarker(marker);
                        groupmarkersList.add(groupmember);
                    } else if (id.equals(u_id)) {
                        //marker.position(new LatLng(Double.valueOf(persons.get("lat")).doubleValue(), Double.valueOf(persons.get("lon")).doubleValue()));
                        marker.position(new LatLng(Double.valueOf(persons.get("lat")).doubleValue(), Double.valueOf(persons.get("lon")).doubleValue()));
                        marker.title("나");
                        marker.snippet(persons.get("id"));
                        marker.draggable(true);
                        marker.icon(iconimage);
                        groupmember = mMap.addMarker(marker);
                        groupmarkersList.add(groupmember);
                    }

                }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
