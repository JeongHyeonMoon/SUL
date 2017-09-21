package sul.sul_protocol_1.Fragment0;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


public class GPSconsent extends FragmentActivity implements LocationListener {

    //public final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);;
    public static LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //일단은 로그인버튼 누르면 main레이아웃보여줌
        //setContentView(R.layout.activity_main);
        checkGPS();
    }

    public boolean checkGPS() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }catch (SecurityException e) {
            e.printStackTrace();
        }

        boolean isGPS = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        if(isGPS){
            //showToast("Gps On");
            finish();
            Intent intent = new Intent(getApplicationContext(), sul.sul_protocol_1.MainActivity.class);
            startActivity(intent);
        }else{
            //---Gps 요청 다이어그램 창 열기
            AlertDialog dialog = request();
            dialog.show();
            //
        }
        return isGPS;
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
                Toast.makeText(GPSconsent.this, "GPS를 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkGPS();
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
