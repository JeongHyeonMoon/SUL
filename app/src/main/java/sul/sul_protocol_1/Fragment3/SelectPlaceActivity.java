package sul.sul_protocol_1.Fragment3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import sul.sul_protocol_1.Fragment3.logger.Log;
import sul.sul_protocol_1.R;
//import sul.sul_protocol_1.R;

public class SelectPlaceActivity extends SampleActivityBase  implements GoogleApiClient.OnConnectionFailedListener{

    // 화면 닫기
    SelectFriendActivity selectFriendActivity = (SelectFriendActivity) SelectFriendActivity.SSelectFriendActivity;
    GetGroupIdActivity getGroupIdActivity = (GetGroupIdActivity) GetGroupIdActivity.GGetGroupIdActivity;
    Fragment3 fragment3 = (Fragment3) Fragment3.FFragment3;

    GroupDBHelper myDb;

    String GName;
    String GTIME;
    int ffdsize;

    private static String TAG = "GeoActivity";

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    //private TextView search_address;

    private double lat_double = 0.0; // 약속장소
    private double log_double = 0.0;
    private  static String search_address_text;

    Geocoder gc;

    //send data
    private  String u_id = "kim"; // 이 기기의 아이디


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_selectplace);

        openAutocompleteActivity();
        myDb = new GroupDBHelper(this);
        // 친구 수 가져오기
        ffdsize= GroupBean.getlengh();
        // 그룹 이름 가져오기
        GName =GroupBean.getgid();
        // 시간 포멧 확인 필요
        GTIME = String.valueOf(GroupBean.getYear()) +"/"+ String.valueOf(GroupBean.getMonth()) +"/"+ String.valueOf(GroupBean.getDay()) +"/"+ String.valueOf(GroupBean.getCurrentHour()) +"/"+ String.valueOf(GroupBean.getCurrentMinute());
        // 주소 txt
        //search_address = (TextView) findViewById(R.id.search_address);
        // 주소 -> 위도경도 바꿔줌
        gc = new Geocoder(this, Locale.KOREAN);

    }

    // 검색을 위한 메소드
    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            //searchLocation(search_address_text);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());
                //search_address.setText(formatPlaceDetails_address(getResources(), place.getName(),place.getId(), place.getAddress(), place.getPhoneNumber(),place.getWebsiteUri()));
                formatPlaceDetails_address(getResources(), place.getName(),place.getId(), place.getAddress(), place.getPhoneNumber(),place.getWebsiteUri());
                searchLocation(search_address_text);

                // 장소 선택하면, 알람 창 뜸
                // 그룹 추가하기전에 한번 물어봄
                AlertDialog.Builder alert = new AlertDialog.Builder(SelectPlaceActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                        AddData(); // 친구 추가
                        //viewAll(); // 친구 추가 완료 확인창
                        dialog.dismiss(); //닫기
                        finish();
                        getGroupIdActivity.finish();
                        selectFriendActivity.finish();
                        //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit(); // 하나 안하나 같음.
                    }
                });
                alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); //닫기
                        finish();
                        getGroupIdActivity.finish();
                        selectFriendActivity.finish();
                    }
                });
                alert.setMessage("" + GName + "그룹을 만드시겠습니까?");
                alert.show();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    public void AddData() {
        for(int i=0; i<ffdsize; i++) {
            //boolean isInserted = myDb.insertData(GroupBean.getgid(),GroupBean.getname(i),GroupBean.getid(i));
            boolean isInserted = myDb.insertData(GroupBean.getgid(),GroupBean.getname(i),GroupBean.getid(i),GTIME,search_address_text,String.valueOf(lat_double),String.valueOf(log_double));
            System.out.println("getid test: " + GroupBean.getid(i));
            if (isInserted == true)
                Toast.makeText(SelectPlaceActivity.this, GName+" 그룹이 생성되었습니다.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(SelectPlaceActivity.this, "Data not Inserted", Toast.LENGTH_SHORT).show();
        } // end fir
    }

    public void viewAll() {
        //DatabaseHelper인 myDb객체를 가져와 mainactivity의 getAlldata 함수를 호출
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            //show message
            showMessage("Error", "Nothing found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("GID :" + res.getString(0) + "\n");
            buffer.append("FNAME :" + res.getString(1) + "\n");
            buffer.append("FID :" + res.getString(2) + "\n");
            //System.out.println("res.getString GID test: " + res.getString(2));
        }
        //show all data
        //showMessage("sul_Group Data", buffer.toString());
        showMessage("그룹 생성 완료", GName + "그룹이 생성되었습니다.");

    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    // 검색 결과 가져오고 저장
    private static Spanned formatPlaceDetails_address(Resources res, CharSequence name, String id,CharSequence address, CharSequence phoneNumber, Uri websiteUri ) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,websiteUri));
        search_address_text = String.valueOf(Html.fromHtml(res.getString(R.string.search_address, name, id, address)));
        return Html.fromHtml(res.getString(R.string.search_address, name, id, address));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


    private void searchLocation(String searchStr) {
        List<Address> addressList = null;
        try {
            addressList = gc.getFromLocationName(searchStr, 3);
            Address outAddr = addressList.get(0);
            lat_double = outAddr.getLatitude();
            log_double = outAddr.getLongitude();

        } catch(IOException ex) {
            android.util.Log.d(TAG, "위치 : " + ex.toString());
        }
    }
}
