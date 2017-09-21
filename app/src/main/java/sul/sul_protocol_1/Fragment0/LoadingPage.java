package sul.sul_protocol_1.Fragment0;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import java.io.File;

import sul.sul_protocol_1.BackPressCloseHandler;
import sul.sul_protocol_1.MainActivity;
import sul.sul_protocol_1.R;

public class LoadingPage extends FragmentActivity implements LocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingpage_layout);

        // 로딩페이지 전환

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                boolean main = intent.getBooleanExtra("main", true);

                if(main) {
                    Log.i("main","true");
                    checkGPS();
                } else{
                    Log.i("main","false");
                    checkGPS2();
                }
            }
        },1000);


        // writeArrayList();
        BackPressCloseHandler.loginactivity = LoadingPage.this;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // DB가 있나 체크하기
    public boolean isCheckDB(Context mContext){
        String filePath = "/data/data/" + this.getPackageName() + "/databases/sul.db";
        File file = new File(filePath);

        /*
        Cursor res = myDb.getMyId();
        while (res.moveToNext()) {
           id=res.getString(0);
        }*/

        if (file.exists()) {
            //file.delete();
            //UserBean.setId(id);
            return true;
        }

        return false;

    }

    public boolean checkGPS() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }catch (SecurityException e) {
            e.printStackTrace();
        }

        boolean isGPS = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        if(isGPS){
            //showToast("Gps On");
            if(isCheckDB(this)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

        }else{
            //---Gps 요청 다이어그램 창 열기
            AlertDialog dialog = request();
            dialog.show();
            //
        }
        return isGPS;
    }

    public boolean checkGPS2() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }catch (SecurityException e) {
            e.printStackTrace();
        }

        boolean isGPS2 = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        if(isGPS2){
            //showToast("Gps On");
            if(!isCheckDB(this)) {
                Intent intent = new Intent(getApplicationContext(), ServersqliteinsertActivity.class);
                startActivity(intent);
                finish();
            }

        }else{
            //---Gps 요청 다이어그램 창 열기
            AlertDialog dialog = request();
            dialog.show();
            //
        }
        return isGPS2;
    }

    //---GPS 대화상자
    private AlertDialog request() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("GPS 요청");
        builder.setMessage("GPS를 설정하시겠습니까?");

        //'예'버튼 클릭시 GPS 설정창 띄우기
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });

        //'아니오'버튼 클릭시 toast창띄우기
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoadingPage.this, "GPS를 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkGPS();
        checkGPS2();
    }

    private void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
}

