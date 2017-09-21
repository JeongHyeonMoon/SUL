package sul.sul_protocol_1.Fragment1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

import sul.sul_protocol_1.R;

/**
 * 친구 검색할 Fragment
 */
public class FindFriendActivity extends FragmentActivity {

    public static Activity FFindFriendActivity;
    ListView list;

    // 친구 검색을 위해 필요한 변수 선언
    private EditText editTextuid;
    String myPHP = "http://sul.duksung.ac.kr/test/srchxml.php";
    String myXML = "http://sul.duksung.ac.kr/srch.xml";
    Document doc = null;
    String TAG_RESULT = "result";
    String TAG_NAME = "name";
    String TAG_ID = "id";
    String TAG_ADRSS = "adrss";
    String TAG_X="x";
    String TAG_Y="y";
    String TAG_PIC="pic";
    String TAG_PHONE="phone";
    Friend friend = new Friend();
    //ArrayList<HashMap<String, String>> tasks;
    //HashMap<String, String> task;
    //ArrayAdapter adapter;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);
        Button btn = (Button) findViewById(R.id.srchBtn);
        FFindFriendActivity = FindFriendActivity.this;
        editTextuid = (EditText) findViewById(R.id.u_id);

        btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                eventFunc();
            } // end of if-else
        });
    }

    public void eventFunc() {
        editTextuid = (EditText) findViewById(R.id.u_id);

        String u_id = editTextuid.getText().toString();

        list = (ListView) findViewById(R.id.list);
        //editTextuid.setText(u_id, TextView.BufferType.EDITABLE);
        // url로 post 전송
        insertToDatabase(u_id);

    }

    /**
     * POST 방식 전달
     */
    private void insertToDatabase(String srch_id) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FindFriendActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();
                // Toast.makeText(getApplicationContext(), "친구가 검색되었습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String srch_id = (String) params[0];

                    String link = myPHP;
                    String data = URLEncoder.encode("u_id", "UTF-8") + "=" + URLEncoder.encode(srch_id, "UTF-8");

                    System.out.println("srch_id: " + srch_id);
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
        task.execute(srch_id);
    } // post 끝

    /**
     * XML 파싱
     */
    //private inner class extending AsyncTask
    private void xmlParsing() {
        class GetXMLTask extends AsyncTask<String, Void, Document> {

            @Override
            protected Document doInBackground(String... urls) {
                System.out.println("xml parsing test");
                URL url;
                try {
                    url = new URL(myXML);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                    doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                    doc.getDocumentElement().normalize();

                } catch (Exception e) {
                    Toast.makeText(FindFriendActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                }
                return doc;
            }

            @Override
            protected void onPostExecute(Document doc) {

                xmlParsing(doc);


                super.onPostExecute(doc);
            }

            public void xmlParsing(Document doc) {

                String sname = "";
                String sid = "";
                String sadrss = "";
                String sx = "";
                String sy = "";
                String spic = "";
                String sphone = "";

                //result태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
                NodeList rootNodeList = doc.getElementsByTagName(TAG_RESULT);
                try {
                    for (int i = 0; i < rootNodeList.getLength(); i++) {
                        //result 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환
                        Node rootNode = rootNodeList.item(i); // result 노드
                        Element rootElement = (Element) rootNode;

                        NodeList nameNodeList = rootElement.getElementsByTagName(TAG_NAME);
                        Element nameElement = (Element) nameNodeList.item(0);
                        NodeList nameList = nameElement.getChildNodes();


                        sname = nameList.item(0).getNodeValue();
                        sname = nameList.item(0).getNodeValue();
                        System.out.println("xmlTest name: " + sname);
                        friend.setName(sname);

                        NodeList idNodeList = rootElement.getElementsByTagName(TAG_ID);
                        Element idElement = (Element) idNodeList.item(0);
                        NodeList idList = idElement.getChildNodes();
                        sid = idList.item(0).getNodeValue();
                        System.out.println("xmlTest id: " + sid);
                        friend.setId(sid);

                        ////////
                        NodeList adrssNodeList = rootElement.getElementsByTagName(TAG_ADRSS);
                        Element adrssElement = (Element) adrssNodeList.item(0);
                        NodeList adrssList = adrssElement.getChildNodes();
                        sadrss = adrssList.item(0).getNodeValue();
                        System.out.println("xmlTest adrss: " + sadrss);
                        friend.setAdrss(sadrss);

                        NodeList xNodeList = rootElement.getElementsByTagName(TAG_X);
                        Element xElement = (Element) xNodeList.item(0);
                        NodeList xList = xElement.getChildNodes();
                        sx = xList.item(0).getNodeValue();
                        System.out.println("xmlTest x: " + sx);
                        friend.setX(sx);

                        NodeList yNodeList = rootElement.getElementsByTagName(TAG_Y);
                        Element yElement = (Element) yNodeList.item(0);
                        NodeList yList = yElement.getChildNodes();
                        sy = yList.item(0).getNodeValue();
                        System.out.println("xmlTest y: " + sy);
                        friend.setY(sy);

                        NodeList picNodeList = rootElement.getElementsByTagName(TAG_PIC);
                        Element picElement = (Element) picNodeList.item(0);
                        NodeList picList = picElement.getChildNodes();
                        spic = picList.item(0).getNodeValue();
                        System.out.println("xmlTest pic: " + spic);
                        friend.setPic(spic);

                        NodeList phoneNodeList = rootElement.getElementsByTagName(TAG_PHONE);
                        Element phoneElement = (Element) phoneNodeList.item(0);
                        NodeList phoneList = phoneElement.getChildNodes();
                        sphone = phoneList.item(0).getNodeValue();
                        System.out.println("xmlTest phone: " + sphone);
                        friend.setPhone(sphone);

                    } // end for

                    showList(sname, sid);
                    Toast.makeText(FindFriendActivity.this, "친구가 검색되었습니다.", Toast.LENGTH_LONG).show();
                } catch (NullPointerException e) {
                    // 검색 결과가 없다면
                    Toast.makeText(FindFriendActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }// end if


        }//end inner class - GetXMLTask

        GetXMLTask task = new GetXMLTask();
        task.execute();
    } // end xmlParsing()


    /**
     * 안드로이드에 리스트를 보여줄 함수
     *
     * @param sname
     * @param sid
     */

    protected void showList(String sname, String sid) {
        //tasks = new ArrayList<>();
        //task = new HashMap<String, String>();
        adapter = new ListViewAdapter();

        // task.put("이름", sname);
        // task.put("아이디", sid);
        adapter.addItem(sid, sname);


        //tasks.add(task);
        // adapter.notifyDataSetChanged();

        // 아이템 View를 선택(single choice)가능하도록 만듬.
        //adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, tasks);
        //adapter = new ListViewAdapter();

        list.setAdapter(adapter);
        //list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //  동적으로 생성된 list view가 클릭될 때
        list.setOnItemClickListener(listViewClickListener);
    } // end showList

    /**
     * 동적으로 생성된 list view가 클릭될 때 발생할 리스너
     */
    protected AdapterView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //  친구 추가하기
            Intent dbIntent = new Intent(getApplicationContext(), DBActivity_Friend.class);
            startActivity(dbIntent);
        }
    };
} // end class
