package sul.sul_protocol_1.Fragment3;

import java.util.ArrayList;

/**
 * 그룹 테이블 생성할 때 사용할 자바 빈
 */
public class GroupBean {
    // property help us to keep data
    public static String gid;
    public static ArrayList<String> name = new ArrayList<String>();
    public static ArrayList<String> id = new ArrayList<String>();
    public static int gyear;
    public static int gmonth;
    public static int gday;
    public static int gCurrentHour;
    public static int gCurrentMinute;

    public static Double mylat;
    public static Double mylon;




    //public static String name;
    //public static String id;

    //public static HashMap<String, String> ffdhash = new HashMap<String, String>();

    /**
     * setter
     * @param
     */
    public static void setgid(String g){
        gid=g;
    }
    public static void setname(String n){
        name.add(n);
    }
    public static void setid(String i){
        id.add(i);
    }
    public static void setYear(int g){
        gyear=g;
    }
    public static void setMonth(int g){
        gmonth=g;
    }
    public static void setDay(int g){
        gday=g;
    }
    public static void setCurrentHour(int g){
        gCurrentHour=g;
    }
    public static void setCurrentMinute(int g){
        gCurrentMinute=g;
    }
    public static void setmylat(Double g){
        mylat=g;
    }
    public static void setmylon(Double g){
        mylon=g;
    }

    /**
     * getter
     * @return
     */
    public static String getgid(){
        return gid;
    }
    public static String getname(int i){
        return name.get(i);
    }
    public static String getid(int i){
        return id.get(i);
    }
    public static int getlengh(){
        return name.size();
    }
    public static int getYear(){
        return gyear;
    }
    public static int getMonth(){
        return gmonth;
    }
    public static int getDay(){
        return gday;
    }
    public static int getCurrentHour(){
        return gCurrentHour;
    }
    public static int getCurrentMinute(){
        return gCurrentMinute;
    }
    public static Double getMylat(){
        return mylat;
    }
    public static Double getmylon(){
        return mylon;
    }

    /**
     * clear
     */
    public static void clearname(){name.clear();}
    public static void clearid(){id.clear();}
}
