package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * User: Hung Ba <hungnm@nextop.asia>
 */

public class PointALL {
    Map<String, Double> list = new HashMap<>();
    List<String> listS = new ArrayList<>();
    List<String> listf = new ArrayList<>();
    public static void main(String[] args){
        new PointALL();
    }
    PointALL(){
        try {
            System.out.println("-------------- Hello Hung Ba Thien Ha -----------");
            System.out.println("-------------- Start Time: " + ((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
            String str = "2023-08-01 00:00:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            File myObj = new File("pointAll.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
               listS.add(data);
            }
            myReader.close();
            process(listS, 0);
            //
            matchOnlyInGroup();
            //
//            twoGroupInOneTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    // tỷ lệ các trận
    private void matchOnlyInGroup() {
        for (int i = 0; i < listf.size(); i++) {
//            int z = i %4;
//            if (z == 0) {
                System.out.println(listf.get(i));
//            }
        }

    }

    public String process(List<String > list, int t) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i+1; j < list.size(); j++) {
                Double a = Double.valueOf(list.get(i).split(";")[1].replace(",", ".")) - Double.valueOf(list.get(j).split(";")[1].replace(",", "."));
                double b = Math.round(Math.abs(a)*100.0)/100.0;
                listf.add(list.get(i).replace(";", ":") +" vs "+ list.get(j).replace(";", ":") + " : \t \t" + rate(b));
            }
        }
        return "";
    }
    public String rate (double a) {
        if ( a < 1) {
            return "0--0--0";
        }
        if ((1<= a) && (a <= 1.25)) {
            return "0--2--0";
        }
        if ((1.25< a) && (a <= 1.75)) {
            return "2--0--2";
        }
        if ((1.75< a) && (a <= 2.25)) {
            return "2--2--2";
        }
        if ((2.25  < a) && (a <= 2.5)) {
            return "2--3--2";
        }
        if ((2.5  < a) && (a <= 2.75)) {
            return "3--2--3";
        }
        if ((2.75  < a) && (a <= 3.25)) {
            return "3--3--3";
        }
        if ((3.25  < a) && (a <= 3.5)) {
            return "3--4--3";
        }
        if ((3.50  < a) && (a <= 3.75)) {
            return "4--3--4";
        }
        if ((3.75  < a) && (a <= 4.25)) {
            return "4--4--4";
        }
        if ((4.25  < a) && (a <= 4.50)) {
            return "4--5--4";
        }
        if ((4.50  < a) && (a <= 4.75)) {
            return "5--4--5";
        }
        if ((4.75  < a && a <= 5.25)) {
            return "5--5--5";
        }
        if ((5.25 < a && a <= 5.50)) {
            return "5--6--5";
        }
        if ((5.50 < a && a <= 5.75)) {
            return "6--5--6";
        }
        if ((5.75 < a && a <= 6.25)) {
            return "6--6--6";
        }
        if ((6.25 < a)) {
            return "6--6--7--6--6";
        }
        System.out.println(a);
        return "----";
    }
}
