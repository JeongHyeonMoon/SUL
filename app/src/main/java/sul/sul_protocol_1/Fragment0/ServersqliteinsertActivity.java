package sul.sul_protocol_1.Fragment0;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sul.sul_protocol_1.MainActivity;
import sul.sul_protocol_1.R;

/**
 * Created by Jeong JuRi on 2016-06-02.
 */
public class ServersqliteinsertActivity extends FragmentActivity {

    private EditText editTextuid;
    private EditText editTextuname;
    private EditText editTextupwd;
    private EditText editTextuadrss;
    //private EditText editTextupic;
    private EditText editTextuphone;
    private EditText editTextupwdch;

    ArrayList<EditText> editArray;


    // sqlite 에 저장
    UserDBHelper myDb;
    //JoinDBHelper myDb;

    // geocoding
    private static String TAG = "GeoActivity";
    Geocoder gc;
    private double lat_double;
    private double log_double;

    boolean idch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serversqliteinsert);
        //BackPressCloseHandler.joinactivity = ServersqliteinsertActivity.this;
        //BackPressCloseHandler.joinbool = true;


        // 회원가입 버튼
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                editTextuid = (EditText) findViewById(R.id.u_id);
                editTextuname = (EditText) findViewById(R.id.u_name);
                editTextuadrss = (EditText) findViewById(R.id.u_adrss);
                editTextupwd = (EditText) findViewById(R.id.u_pwd);
                editTextupwdch = (EditText) findViewById(R.id.u_pwdch);
                //editTextupic = (EditText) findViewById(R.id.u_adrss);
                editTextuphone = (EditText) findViewById(R.id.u_phone);

                //editArray
                editArray = new ArrayList<EditText>();
                editArray.add(editTextuid);
                editArray.add(editTextuname);
                editArray.add(editTextuadrss);
                editArray.add(editTextupwd);
                editArray.add(editTextupwdch);
                editArray.add(editTextuphone);

                // 입력창이 비었는지 확인
                if (!isEmptyAny(editArray)) {
                    // 사용자에게 받은 값
                    String u_id = editTextuid.getText().toString();
                    String u_name = editTextuname.getText().toString();
                    String u_adrss = editTextuadrss.getText().toString();
                    String u_pwd = editTextupwd.getText().toString();
                    String u_pwdch = editTextupwdch.getText().toString();
                    // String u_pic = editTextupic.getText().toString();
                    String u_phone = editTextuphone.getText().toString();

                    if (u_pwd.equals(u_pwdch)) {
                        // 비밀번호 일치

                        if (idch) {
                            // 중복검사 확인
                            myDb = new UserDBHelper(getBaseContext());

                            Log.i("db", "created");
                            // 변환된 값
                            searchLocation(u_adrss);
                            String u_hx = String.valueOf(lat_double);
                            String u_hy = String.valueOf(log_double);

                            // sqlite에 저장하기 위해 자바빈을 이용하여 값 전달시킴
                            UserBean.setId(u_id);
                            UserBean.setName(u_name);
                            UserBean.setPwd(u_pwd);
                            UserBean.setAdrss(u_adrss);
                            UserBean.setPic(u_id);
                            UserBean.setPhone(u_phone);
                            UserBean.setX(u_hx);
                            UserBean.setY(u_hy);
                            UserBean.setArrival(0); // 처음은  0 : 도착 안함

                            InsertData();
                            insertToDatabase(u_id, u_name, u_pwd, u_adrss, u_id, u_hx, u_hy, u_phone);
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(ServersqliteinsertActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                                    dialog.dismiss(); //닫기
                                }
                            });
                            alert.setMessage("아이디 중복검사를 해주세요.");
                            alert.show();
                        }

                    } else {
                        // 비밀번호 불일치
                        AlertDialog.Builder alert = new AlertDialog.Builder(ServersqliteinsertActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                                dialog.dismiss(); //닫기
                            }
                        });
                        alert.setMessage("비밀번호가 일치하지 않습니다.");
                        alert.show();
                    }
                }


            }


        });

        // 중복검사 버튼
        Button idchbtn = (Button) findViewById(R.id.idchbtn);
        idchbtn.setOnClickListener(new Button.OnClickListener() {
           public void onClick(View v) {
               Log.i("click", "yes");

               // 임의
               idch=true;

               AlertDialog.Builder alert = new AlertDialog.Builder(ServersqliteinsertActivity.this);
               alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                       Log.i("중복검사", "true");
                       idch = true;
                       //editTextuid.setEnabled(false);
                       dialog.dismiss(); //닫기
                   }
               });
               alert.setMessage("사용가능한 아이디입니다..");
               alert.show();

           }
       });

        // 주소 -> 위도경도 바꿔줌
        gc = new

                Geocoder(this, Locale.KOREAN);

    }


    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                Log.i("getData", "doInBackground");
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
                Log.i("getData", "onPostExecute");
                makeList(result);
                Log.i("result ", result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    protected void makeList(String response) {
        Log.i("makeList", "yes");
        try {
            JSONArray list = new JSONArray(response);
            Log.i("JSONArray", list.toString());
            JSONObject obj = (JSONObject) list.get(0);
            Log.i("JSONObject", obj.toString());
            String res = obj.getString("count");

            Log.i("result", res);
            if (res.equals("0")) {
                // 사용가능한 아이디
                // 아이디 중복
                AlertDialog.Builder alert = new AlertDialog.Builder(ServersqliteinsertActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                        Log.i("중복검사", "true");
                        idch = true;
                        editTextuid.setEnabled(false);
                        dialog.dismiss(); //닫기
                    }
                });
                alert.setMessage("사용가능한 이이디입니다..");
                alert.show();
            } else {
                // 아이디 중복
                AlertDialog.Builder alert = new AlertDialog.Builder(ServersqliteinsertActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // 확인 누르면
                        Log.i("중복검사", "false");
                        idch = false;
                        editTextuid.setText("");
                        dialog.dismiss(); //닫기
                    }
                });
                alert.setMessage("이미 사용중인 아이디입니다.");
                alert.show();
            }


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }


    /**
     * 회원가입 할 때 빈 텍스트인지 검사
     */

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    /**
     * 회원가입 할 때 빈 텍스트인지 검사 2
     */
    private boolean isEmptyAny(ArrayList<EditText> array) {
        for (int i = 0; i < array.size(); i++) {
            if (isEmpty(array.get(i))) {
                // null 값이 입력되면 Toast로 경고해주기
                Toast.makeText(getBaseContext(), "모든 정보를 빠짐없이 입력해주세요.", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public void intent() {
        startActivity(new Intent(this, MainActivity.class));
    }

    // 서버에 저장하는 함수
    private void insertToDatabase(String u_id, String u_name, String u_pwd, String u_adrss, String u_pic, String u_hx, String u_hy, String u_phone) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ServersqliteinsertActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                Toast.makeText(ServersqliteinsertActivity.this, "서버 등록 성공", Toast.LENGTH_SHORT).show();
                intent();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String u_id = (String) params[0];
                    String u_name = (String) params[1];
                    String u_pwd = (String) params[2];
                    String u_adrss = (String) params[3];
                    String u_pic = (String) params[4];
                    String u_hx = (String) params[5];
                    String u_hy = (String) params[6];
                    String u_phone = (String) params[7];

                    String link = "http://sul.duksung.ac.kr/serveruserinsert.php";
                    String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(u_id, "UTF-8");
                    data += "&" + URLEncoder.encode("u_name", "UTF-8") + "=" + URLEncoder.encode(u_name, "UTF-8");
                    data += "&" + URLEncoder.encode("u_pwd", "UTF-8") + "=" + URLEncoder.encode(u_pwd, "UTF-8");
                    data += "&" + URLEncoder.encode("u_adrss", "UTF-8") + "=" + URLEncoder.encode(u_adrss, "UTF-8");
                    data += "&" + URLEncoder.encode("u_pic", "UTF-8") + "=" + URLEncoder.encode(u_pic, "UTF-8");
                    data += "&" + URLEncoder.encode("u_hx", "UTF-8") + "=" + URLEncoder.encode(u_hx, "UTF-8");
                    data += "&" + URLEncoder.encode("u_hy", "UTF-8") + "=" + URLEncoder.encode(u_hy, "UTF-8");
                    data += "&" + URLEncoder.encode("u_phone", "UTF-8") + "=" + URLEncoder.encode(u_phone, "UTF-8");

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
        task.execute(u_id, u_name, u_pwd, u_adrss, u_pic, u_hx, u_hy, u_phone);
    }

    // sqlite 에 저장하는 함수
    public void InsertData() {
        Intent udbIntent = new Intent(getApplicationContext(), UserDBActivity.class);
        startActivity(udbIntent);
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


    public void roadsearchbtnclicked(View v) {
        Toast.makeText(getBaseContext(), "주소가 변환되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
