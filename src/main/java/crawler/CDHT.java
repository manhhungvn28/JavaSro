package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author HungNM
 */

public class CDHT {
    public Map<Long, Map<String, Long>> mapAll =  new HashMap<>();

    private static LocalDateTime local = LocalDateTime.now();
    private static long timeReset =  local.getNano();
    private static long timeNews = timeReset;
    private boolean sos = false;
    private boolean bel = false;
    private long timeSleep = 0;
    private static File normalSound = null;
    private static File sosSound = null;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static  String sosName = "";
    public static  int traderOrHunter = 2;

    CDHT() throws Exception {
        System.out.println("-------------- Hello Hung Ba Thien Ha -----------");
        System.out.println("-------------- Start Time: " + ((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
        String str = "2023-08-01 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        File myObj = new File("configCDHT.txt");
        Scanner myReader = new Scanner(myObj);
        String[] strings = null;
        while (myReader.hasNextLine()) {
            strings = myReader.nextLine().split(",");
        }
        myReader.close();
        //string [1] path, string[3] name file mp3, strings[6] choice thief or trader or hunter
        normalSound = new File(strings[3] + strings[1]);
        sosSound = new File(strings[3] + strings[2]);
        sosName = strings[4];
        traderOrHunter = Integer.valueOf(strings[6]);
        timeSleep = Long.valueOf(strings[5]);
        System.out.println("--------------" + strings[0]);
        while (local.compareTo(dateTime.now().plusDays(30)) < 0) {
            getNewest(strings);
            try {
                if (sos && bel) {
                    System.out.println("--------------");
                    Desktop.getDesktop().open(sosSound);
                    sos = false;
                    bel = false;
                } else if (bel) {
                    System.out.println("--------------");
                    bel = false;
                    Desktop.getDesktop().open(normalSound);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread.sleep(timeSleep);
        }
    }

    private void getNewest(String[] strings) throws InterruptedException {
            Document page = Jsoup.parse(resultConnect(strings[0]));
            Elements elements = page.getElementsByClass("item-show");
            elements.remove(0);
            elements.remove(traderOrHunter);
            String[] sarray = elements.toString().split("/tr");

            Map<String, Long> listNews = listPersonRealTime(sarray);
            setMapAll(listNews);
    }

    private void setMapAll (Map<String, Long> listNews) {
        if (mapAll.isEmpty()) {
            mapAll.put(timeNews, listNews);
            return;
        }
        scanAll(listNews);
    }

    private void scanAll(Map<String, Long> listNews) {
        Collection<String> a = listNews.keySet();
        a.stream().forEach(x -> {
            if (mapAll.get(timeNews).isEmpty()) {
                return;
            }
            if (mapAll.get(timeNews).get(x) == null) {
                mapAll.get(timeNews).put(x, listNews.get(x).longValue());
            }
            if (mapAll.get(timeNews).get(x).longValue() != listNews.get(x).longValue()) {
                bel = true;
                if (x.equals(sosName)) {
                    sos = true;
                    System.out.print(ANSI_RED + "+ + +" + ANSI_RESET);
                }
                long t = listNews.get(x).longValue() - mapAll.get(timeNews).get(x).longValue();
                System.out.println(" -> " + x + ": " + t + " point at " + ((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
                mapAll.get(timeNews).put(x, listNews.get(x).longValue());
            }
        });
    }

    private Map<String, Long> listPersonRealTime(String[] sarray) {
        Map<String, Long> m = new HashMap<>();
        for (int i = 1; i < sarray.length - 1; i++) {
            String[] user = sarray[i].split("</td>");
            if (user.length < 4 || user[2].split(">").length < 3 || user[2].split(">")[user[2].split(">").length-2].length() <4) {
//                System.out.println("- break");
                continue;
            }
            String[] t = user[2].split(">");
            if (t[t.length-2].trim().substring(0, t[t.length-2].length()-4).trim().isEmpty()) {
                String s = t[t.length-3].trim().split("href=\"https://cdht.zoneplay.vn/player/")[1];
                m.put(s.substring(0, s.length()-1) + " I " , Long.valueOf(user[6].trim().replaceAll("[<td>]", "").toString()));
            } else {
                m.put(t[t.length-2].trim().substring(0, t[t.length-2].length()-4).trim(), Long.valueOf(user[6].trim().replaceAll("[<td>]", "").toString()));
            }
        }
        return m;
    }

    public String resultConnect (String link) throws InterruptedException {
        try {
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(25000);
            con.setReadTimeout(25000);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } catch (IOException e) {
            System.out.println("Retry connect to server");
            Thread.sleep(5000);
            return this.resultConnect(link);
        }
    }

    public static void main(String[] args) throws Exception {
        new CDHT();
    }
}
