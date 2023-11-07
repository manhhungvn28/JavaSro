package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * User: Hung Ba <hungnm@nextop.asia>
 */

public class PointChiaBang {
    Map<String, Double> list = new HashMap<>();
    List<String> listS = new ArrayList<>();
    List<String> listf = new ArrayList<>();
    public static void main(String[] args){
        new PointChiaBang();
    }
    PointChiaBang(){
        try {
            System.out.println("-------------- Hello Hung Ba Thien Ha -----------");
            System.out.println("-------------- Start Time: " + ((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
            String str = "2023-08-01 00:00:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            File myObj = new File("point.txt");
            Document page = Jsoup.parse(myObj, "UTF-8");
            Elements elements = page.getElementsByClass(" mr-2");
            for (Element element : elements) {
                toList (element);
            }
            process(listS, 0);
            //
//            matchOnlyInGroup();
            //
            twoGroupInOneTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    // tỷ lệ các trận của 1 bảng
    private void matchOnlyInGroup() {
        System.out.println("Bang A");
        for (int i = 0; i < listf.size(); i++) {
            int z = i %4;
            if (z == 0) {
                System.out.println(listf.get(i));
            }
        }
        System.out.println("Bang B");
        for (int i = 0; i < listf.size(); i++) {
            int z = i %4;
            if (z == 1) {
                System.out.println(listf.get(i));
            }
        }
        System.out.println("Bang C");
        for (int i = 0; i < listf.size(); i++) {
            int z = i %4;
            if (z == 2) {
                System.out.println(listf.get(i));
            }
        }
        System.out.println("Bang D");
        for (int i = 0; i < listf.size(); i++) {
            int z = i % 4;
            if (z == 3) {
                System.out.println(listf.get(i));
            }
        }
    }
    // 2 bảng tren 1 bàn
    private void twoGroupInOneTable() {
        System.out.println("Bang A va Bang B");
        for (int i = 0; i < listf.size(); i++) {
            int z = i %4;
            if (z == 0 || z == 1) {
                System.out.println(listf.get(i));
            }
        }

        System.out.println("Bang C va Bang D");
        for (int i = 0; i < listf.size(); i++) {
            int z = i %4;
            if (z == 2 || z == 3) {
                System.out.println(listf.get(i));
            }
        }
    }

    public  void toList (Element element) {
        String[] s =  element.toString().split("&amp;v=team\">");
        listS.add(s[1].split("</a> </p>")[0]);
    }
    public String process(List<String > list, int t) {
        if (t >=list.size()) {
            return "";
        };
        Double a = Double.valueOf(list.get(t).split(";")[1]) - Double.valueOf(list.get(t+1).split(";")[1]);
        double b = Math.round(Math.abs(a)*100.0)/100.0;
        listf.add(list.get(t).replace(";", ":") +" vs "+ list.get(t+1).replace(";", ":") + " : \t \t" + rate(b));
        t= t+2;
        return process(list, t);
    }
    public String rate (double a) {
        if ( a < 1) {
            return "0-0-0";
        }
        if ((1<= a) && (a <= 1.25)) {
            return "0-2-0";
        }
        if ((1.25< a) && (a <= 1.75)) {
            return "2-0-2";
        }
        if ((1.75< a) && (a <= 2.25)) {
            return "2-2-2";
        }
        if ((2.25  < a) && (a <= 2.5)) {
            return "2-3-2";
        }
        if ((2.5  < a) && (a <= 2.75)) {
            return "3-2-3";
        }
        if ((2.75  < a) && (a <= 3.25)) {
            return "3-3-3";
        }
        if ((3.25  < a) && (a <= 3.5)) {
            return "3-4-3";
        }
        if ((3.50  < a) && (a <= 3.75)) {
            return "4-3-4";
        }
        if ((3.75  < a) && (a <= 4.25)) {
            return "4-4-4";
        }
        if ((4.25  < a) && (a <= 4.50)) {
            return "4-5-4";
        }
        if ((4.50  < a) && (a <= 4.75)) {
            return "5-4-5";
        }
        if ((4.75  < a && a <= 5.25)) {
            return "5-5-5";
        }
        if ((5.25 < a && a <= 5.50)) {
            return "5-6-5";
        }
        if ((5.50 < a && a <= 5.75)) {
            return "6-5-6";
        }
        if ((5.75 < a && a <= 6.25)) {
            return "6-6-6";
        }
        if ((6.25 < a)) {
            return "6-6-7-6-6";
        }
        System.out.println(a);
        return "--";
    }
}
