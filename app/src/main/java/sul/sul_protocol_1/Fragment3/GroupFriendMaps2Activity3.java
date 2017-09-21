package sul.sul_protocol_1.Fragment3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
public class GroupFriendMaps2Activity3 extends Fragment implements OnMapReadyCallback {

    public final static String TAG = GroupFriendMaps2Activity2.class.getSimpleName();

    private GoogleMap mMap;
    MarkerOptions marker = new MarkerOptions();
    MarkerOptions appointMarker = new MarkerOptions();
    Marker groupmember = null;
    Marker appoint = null;

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
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    ArrayList<Marker> groupmarkersList;


    // SQLITE 에 있는 내 그룹 친구 목록을 가져옴
    GroupDBHelper myDb;
    Cursor res;
    Cursor res2;
    Cursor res3;
    ArrayList<String> task_gid;
    ArrayList<String> task_name;
    ArrayList<String> task_id;
    String x;
    String y;

    String placename;

    int friendcount = 0;
    String select_id = "";

    // 장소 위도 경도 String
    String glat = "";
    String glon = "";

    // 장소 위도 경도 Double
    Double lox = 0.0;
    Double loy = 0.0;

    ArrayList<String> friend_id = null;

    // 친구 도착
    ArrayList<Integer> friend_arrival = null;

    int length = 0 ;

    int countarrival = 0;

    public GroupFriendMaps2Activity3(){

    }
    public static GroupFriendMaps2Activity2 newInstance(){    return new GroupFriendMaps2Activity2(); }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        countarrival=0;

        // 친구들 목록 가져옴
        personList = new ArrayList<>(); // 리스트 생성
        groupmarkersList = new ArrayList<Marker>();

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

                    length ++;
                }
            }
            i++;
        }


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

        System.out.println("test 친구수 : " + res.getCount());
        friendcount = res.getCount();

        // 내 위치 + 친구들 위치 보임
        startLocationService();


    }


    // 위치 서비스 시작하면서 내 위치를 서버로 보냄
    public void startLocationService() {
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
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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


    public void getDataBackground(String url) {
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
                makeListBackground();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void makeListBackground(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                final String id = c.getString(TAG_ID);
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


                for (int j = 0; j < length; j++) {
                    if (id.equals(friend_id.get(j))) {
                        if((friend_arrival.get(j)).equals(1)) { // farrival = 0이면
                            if (Math.abs(Double.valueOf(persons.get("lat")).doubleValue() - lox) + Math.abs(Double.valueOf(persons.get("lon")).doubleValue() - loy) < 0.01) {

                                NotificationSomethins1(persons.get("name"));

                                updateArrivalata(id, 0);
                                friend_arrival.set(j,0);
                                /*if (countarrival == 0) {


                                    AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
                                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                            dialog.dismiss();  //닫기


                                        }
                                    });

                                    updateArrivalata(id, 1);
                                    //friend_arrival.set(j,1);


                                    alert.setMessage("" + persons.get("name") + "님 약속 장소 도착");
                                    alert.show();
                                    countarrival ++;*/
                            }
                        }
                    }
                }
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                .setContentText(id + "님이 도착했습니다.")
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
                        //handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                        Thread.sleep(7000);
                    } catch (Throwable t) {
                    }
                }
            }
        });
        myThread.start();
    }

    private void updateThread() {
        getDataBackground("http://sul.duksung.ac.kr/getdata.php");
    }

}
