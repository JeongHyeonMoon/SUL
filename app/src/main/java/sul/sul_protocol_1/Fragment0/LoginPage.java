package sul.sul_protocol_1.Fragment0;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import sul.sul_protocol_1.BackPressCloseHandler;
import sul.sul_protocol_1.R;

public class LoginPage extends FragmentActivity {
    // ------변수 선언-------------------------------------------------------//
    // 사용자가 입력한 id창
    EditText editTextId;
    EditText editTextPwd;

    //------------------자동로그인 check,SharedPreferences
    CheckBox chk_auto;
    SharedPreferences setting;
    SharedPreferences.Editor editor;


    // ListView list;

    String LoginPhp = "http://sul.duksung.ac.kr/login.php";
    String LoginXml = "http://sul.duksung.ac.kr/login.xml";

    String TAG_RESULT = "result";
    String TAG_LOGIN = "login";
    Document doc = null;

    String sname = "";
    String myJSON;
    JSONArray jsons = null;

    UserDBHelper db;
   // JoinDBHelper db;
//--------------------------------------------------------------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage_layout);

        // 로딩페이지 전환
        /*
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LoginPage.this , MainActivity.class);
                //UserBean.setId("mmmm");
                startActivity(i);
                finish();
            }
        },1000);
        */

        // writeArrayList();
        BackPressCloseHandler.loginactivity = LoginPage.this;
        db = new UserDBHelper(this);

        //---자동로그인
        editTextId = (EditText) findViewById(R.id.login_id);
        editTextPwd = (EditText) findViewById(R.id.password);
        chk_auto = (CheckBox) findViewById(R.id.chk_auto);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        if (setting.getBoolean("chk_auto", false)) {

            editTextId.setText(setting.getString("login_id", ""));
            editTextPwd.setText(setting.getString("login_pwd", ""));

            chk_auto.setChecked(true);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //---로그인 버튼 클릭시
    public void onButtonLoginClicked(View v) {

        // 자동 로그인 체크
        if (chk_auto.isChecked()) {
            String login_id = editTextId.getText().toString();
            String login_pwd = editTextPwd.getText().toString();
            editor.putString("login_id", login_id);
            editor.putString("login_pwd", login_pwd);
            editor.putBoolean("chk_auto", true);
            editor.commit();
        } else {
            editor.clear();
            editor.commit();
        }

        //로그인 되었을떄
        // ---------------------변수 선언--------------------------//
        editTextId = (EditText) findViewById(R.id.login_id);
        String login_id = editTextId.getText().toString();
        UserBean.setId(editTextId.getText().toString());

        editTextPwd = (EditText) findViewById(R.id.password);
        String login_pwd = editTextPwd.getText().toString();
        UserBean.setPwd(editTextPwd.getText().toString());

        System.out.println("test login_id = " + login_id);
        System.out.println("test login_pwd = " + login_pwd);
        //list = (ListView) findViewById(R.id.list);

        // -----------------------------------------------------//
        // -----------------------------------------------------//
        //   if(true) {
        // url로 post 전송

        // 내가 회원가입해놓은 아이디와 입력한 아이디가 같다면
        if(UserBean.getId().equals(login_id)) {
            insertToDatabase(login_id, login_pwd);//데이터 검사
        }
        //   }
        // ----------------------------------------------------//


    }

    //---회원가입 버튼 클릭시
    public void onButtonPluseClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), ServersqliteinsertActivity.class);
        startActivity(intent);
    }
/*
    protected void jsonfun(){
        Log.i("jsonfun","test");
        try {
            JSONObject jsonObj = new JSONObject("result");
            jsons = jsonObj.getJSONArray("result");

            String id = null;
            String name = null;
            String address = null;
            String pwd = null;
            String hx = null;
            String hy = null;
            String phone = null;

            for(int i=0;i<jsons.length();i++){
                JSONObject c = jsons.getJSONObject(i);
                id = c.getString("id");
                name = c.getString("name");
                address = c.getString("address");
                pwd = c.getString("pwd");
                hx = c.getString("hx");
                hy = c.getString("hy");
                phone = c.getString("phone");

            }

            db.insertData(id, name, pwd, address, phone, hx, hy);
            Log.i("sql insert", "test");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
*/
    /**
     * POST 방식 전달
     */
    private void insertToDatabase(String login_id, String login_pwd) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginPage.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                //super.onPostExecute(s);
                //loading.dismiss();
                myJSON = s;
                //jsonfun();
            }

            @Override
            protected String doInBackground(String... params) {

                String json;
                try {
                    String login_id = (String) params[0];
                    String login_pwd = (String) params[1];

                    String link = LoginPhp;
                    String data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(login_id, "UTF-8") + "&";
                    //data += "&";
                    data += URLEncoder.encode("login_pwd", "UTF-8") + "=" + URLEncoder.encode(login_pwd, "UTF-8");

                    System.out.println("test data for login: " + data);

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

                    // xml 파싱 함수 호출
                    xmlParsing();

                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertData task = new InsertData();
        task.execute(login_id, login_pwd);

    } // post 끝

    /**
     * XML 파싱
     */
    //private inner class extending AsyncTask
    private void xmlParsing() {
        class GetXMLTask extends AsyncTask<String, Void, Document> {

            @Override
            protected Document doInBackground(String... urls) {
                URL url;
                try {
                    url = new URL(LoginXml);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                    doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                    doc.getDocumentElement().normalize();

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                }
                return doc;
            }

            @Override
            protected void onPostExecute(Document doc) {

                xmlParsing(doc);
                //jsonfun();

                super.onPostExecute(doc);
            }

            public void xmlParsing(Document doc) {

                sname = "";
                String sid = "";
                //result태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
                NodeList rootNodeList = doc.getElementsByTagName(TAG_RESULT);

                for (int i = 0; i < rootNodeList.getLength(); i++) {
                    //result 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환
                    Node rootNode = rootNodeList.item(i); // result 노드
                    Element rootElement = (Element) rootNode;

                    NodeList nameNodeList = rootElement.getElementsByTagName(TAG_LOGIN);
                    Element nameElement = (Element) nameNodeList.item(0);
                    NodeList nameList = nameElement.getChildNodes();
                    sname = nameList.item(0).getNodeValue();
                    System.out.println("xmlTest login: " + sname);
                    Toast.makeText(getApplicationContext(), sname, Toast.LENGTH_SHORT).show();

                    if (sname.equals("로그인 되었습니다.")) {
                        Intent intent = new Intent(getApplicationContext(), GPSconsent.class);
                        startActivity(intent);
                    }

                }

                // showList(sname, sid);
            }

        }//end inner class - GetXMLTask

        GetXMLTask task = new GetXMLTask();
        task.execute();
    } // end xmlParsing()



}

