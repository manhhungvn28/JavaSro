package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author HungNM
 */

public class HTTPUnitDriver {
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

    HTTPUnitDriver () throws Exception {
        System.out.println("-------------- Hello Hung Ba Thien Ha -----------");
        System.out.println("-------------- Start Scan ------------");
        System.out.println("-------------- Start Time: " + local);
        String str = "2021-06-24 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        File myObj = new File("config.txt");
        Scanner myReader = new Scanner(myObj);
        String[] strings = null;
        while (myReader.hasNextLine()) {
            strings = myReader.nextLine().split(",");
        }
        myReader.close();
        normalSound = new File(strings[5] + strings[3]);
        sosSound = new File(strings[5] + strings[4]);
        sosName = strings[6];
        timeSleep = Long.valueOf(strings[7]);
        System.out.println("--------------");
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
        for (int i = 0; i < 3; i++) {
            Document page = Jsoup.parse(resultConnect(strings[i]));
            Elements elements = page.getElementsByClass("title_bot_bg");
            String[] sarray = elements.toString().split("/tr");
            Map<String, Long> listNews = listPersonRealTime(sarray);
            setMapAll(listNews);
        }

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
                String text =  (mapAll.get(timeNews).get(x).longValue() > listNews.get(x).longValue()) ? " down" : " up";
                bel = true;
                if (x.equals(sosName)) {
                    sos = true;
                    System.out.print(ANSI_RED + "+ + +" + ANSI_RESET);
                }
                long t = listNews.get(x).longValue() - mapAll.get(timeNews).get(x).longValue();
                System.out.println(" -> " + x + text + ": " + t + " point at " + ((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
                mapAll.get(timeNews).put(x, listNews.get(x).longValue());
            }
        });
    }

    private Map<String, Long> listPersonRealTime(String[] sarray) {
        Map<String, Long> m = new HashMap<>();
        for (int i = 0; i < sarray.length - 1; i++) {
            String[] user = sarray[i].split("</td>");
            m.put(user[1].trim().substring(4, user[1].trim().length()), Long.valueOf(user[3].trim().substring(4, user[3].trim().length()).replace(".", "").toString()));
        }
        return m;
    }

    public String resultConnect (String link) throws InterruptedException {
        try {
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
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
        new HTTPUnitDriver();
    }
}
