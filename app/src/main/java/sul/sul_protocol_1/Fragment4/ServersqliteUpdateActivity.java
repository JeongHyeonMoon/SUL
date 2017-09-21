package sul.sul_protocol_1.Fragment4;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import sul.sul_protocol_1.Fragment1.DBHelper_User;
import sul.sul_protocol_1.R;

/**
 * Created by Jeong JuRi on 2016-06-05.
 */
public class ServersqliteUpdateActivity extends FragmentActivity {

    DBHelper_User myDb;
    //-------------------수정-----나머지 속성 추가
    EditText editPwd, editAdress, editPhone, editId, editName;
    Button btnUpdate;
    String id = "", name = "", password = "", address = "", phone = "";

    // geocoding
    private static String TAG = "GeoActivity";
    Geocoder gc;
    private double lat_double;
    private double log_double;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_myinfo);

        Log.i("SUQ onCreate", "yes");
        myDb = new DBHelper_User(this);

        getAllDate();

        editId = (EditText) findViewById(R.id.u_id);
        editId.setText(id);
        editId.setEnabled(false);

        editName = (EditText) findViewById(R.id.u_name);
        editName.setText(name);
        editName.setEnabled(false);

        editPwd = (EditText) findViewById(R.id.u_pwd);
        editAdress = (EditText) findViewById(R.id.u_adrss);
        editAdress.setText(address);

        editPhone = (EditText) findViewById(R.id.u_phone);
        editPhone.setText(phone);


        btnUpdate = (Button) findViewById(R.id.btnupdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(editPwd)) {
                    Log.i("isEmpty","yes");
                    Toast.makeText(getBaseContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    String u_pwd = editPwd.getText().toString();
                    String u_adrss = editAdress.getText().toString();
                    String u_phone = editPhone.getText().toString();

                    // 변환된 값
                    searchLocation(u_adrss);
                    String u_hx = String.valueOf(lat_double);
                    String u_hy = String.valueOf(log_double);

                    // sqlite에 저장하기 위해 자바빈을 이용하여 값 전달시킴


                    // sqlite 수정
                    updateData(u_hx, u_hy);

                    // 서버 수정
                    updateToDatabase(id, u_pwd, u_adrss, u_hx, u_hy, u_phone);
                    // updateToDatabase(u_id, u_name, u_pwd, u_adrss, u_pic, u_hx, u_hy, u_phone);

                    finish();
                }
            }
        });


        // 주소 -> 위도경도 바꿔줌
        gc = new Geocoder(this, Locale.KOREAN);

    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    public void getAllDate() {
        Cursor res = myDb.getAllData();


        while (res.moveToNext()) {
            // 0: 이름
            id = res.getString(1);
            name = res.getString(0);
            password = res.getString(2);
            address = res.getString(3);
            phone = res.getString(4);
        }
    }

    // sqlite 에 update하는 함수
    public void updateData(String x, String y) {

        boolean isupdated = myDb.updateData(id, name, password, address, x, y ,phone);

        if (isupdated == true)
            Toast.makeText(getApplicationContext(), "SQLite Update", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "SQLite Update 실패", Toast.LENGTH_SHORT).show();
    }

    // private void insertToDatabase(String u_id, String u_name,String u_pwd,String u_adrss,String u_pic, String u_hx, String u_hy,String u_phone){

    // 서버에 update하는 함수
    private void updateToDatabase(String u_id, String u_pwd, String u_adrss, String u_hx, String u_hy, String u_phone) {

        class UpdateData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ServersqliteUpdateActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                Toast.makeText(ServersqliteUpdateActivity.this, "서버 수정 성공", Toast.LENGTH_SHORT).show();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String u_id = (String) params[0];
                    //String u_name = (String)params[1];
                    String u_pwd = (String) params[1];
                    String u_adrss = (String) params[2];
                    // String u_pic = (String)params[4];
                    String u_hx = (String) params[3];
                    String u_hy = (String) params[4];
                    String u_phone = (String) params[5];

                    String link = "http://sul.duksung.ac.kr/test/serveruserupdate.php";
                    String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(u_id, "UTF-8");
                    data += "&" + URLEncoder.encode("u_pwd", "UTF-8") + "=" + URLEncoder.encode(u_pwd, "UTF-8");
                    data += "&" + URLEncoder.encode("u_adrss", "UTF-8") + "=" + URLEncoder.encode(u_adrss, "UTF-8");
                    data += "&" + URLEncoder.encode("u_hx", "UTF-8") + "=" + URLEncoder.encode(u_hx, "UTF-8");
                    data += "&" + URLEncoder.encode("u_hy", "UTF-8") + "=" + URLEncoder.encode(u_hy, "UTF-8");
                    data += "&" + URLEncoder.encode("u_phone", "UTF-8") + "=" + URLEncoder.encode(u_phone, "UTF-8");
                 /* String data  = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(u_id, "UTF-8");
                    data += "&" + URLEncoder.encode("u_name", "UTF-8") + "=" + URLEncoder.encode(u_name, "UTF-8");
                    data += "&" + URLEncoder.encode("u_pwd", "UTF-8") + "=" + URLEncoder.encode(u_pwd, "UTF-8");
                    data += "&" + URLEncoder.encode("u_adrss", "UTF-8") + "=" + URLEncoder.encode(u_adrss, "UTF-8");
                    data += "&" + URLEncoder.encode("u_pic", "UTF-8") + "=" + URLEncoder.encode(u_pic, "UTF-8");
                    data += "&" + URLEncoder.encode("u_hx", "UTF-8") + "=" + URLEncoder.encode(u_hx, "UTF-8");
                    data += "&" + URLEncoder.encode("u_hy", "UTF-8") + "=" + URLEncoder.encode(u_hy, "UTF-8");
                    data += "&" + URLEncoder.encode("u_phone", "UTF-8") + "=" + URLEncoder.encode(u_phone, "UTF-8");
                    */

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

        UpdateData task = new UpdateData();
        task.execute(u_id, u_pwd, u_adrss, u_hx, u_hy, u_phone);
    }

    // 주소 -> 위도 경도 바꾸는 함수
    private void searchLocation(String searchStr) {
        List<Address> addressList = null;
        try {
            addressList = gc.getFromLocationName(searchStr, 3);
            Address outAddr = addressList.get(0);
            lat_double = outAddr.getLatitude();
            log_double = outAddr.getLongitude();

        } catch (IOException ex) {
            android.util.Log.d(TAG, "위치 : " + ex.toString());
        }
    }

}