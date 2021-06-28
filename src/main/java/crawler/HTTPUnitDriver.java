package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

/**
 * @author HungNM
 */

public class HTTPUnitDriver {
    public Map<Long, Map<String, Long>> mapTrader =  new HashMap<>();
    public Map<Long, Map<String, Long>> mapHunter =  new HashMap<>();
    public Map<Long, Map<String, Long>> mapThief =  new HashMap<>();

    private static LocalDateTime local = LocalDateTime.now();
    private static long timeTrader =  local.getNano();
    private static long timeHunter =  local.getNano();
    private static long timeThief =  local.getNano();
    private static long timeNews =  timeTrader;
    private int ttime = 0;
    private boolean sos = false;
    private String trader = "";
    private String hunter = "";
    private String thief = "";
    private static File file = new File("E:\\sss.mp3");
    private static File fileSOS = new File("E:\\baochaychuan.mp3");
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    HTTPUnitDriver () throws Exception {
        System.out.println("-------------- Hello Hung Ba Thien Ha -----------");
        System.out.println("-------------- Start Scan ------------");
        System.out.println("-------------- Start Time: " + local);
        String str = "2021-06-24 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        while (local.compareTo(dateTime.now().plusDays(30)) < 0) {
            trader = resultConnect("http://aressro.com/index2.php?mod=ttrade");
            hunter = resultConnect("http://aressro.com/index2.php?mod=thunter");
            thief = resultConnect("http://aressro.com/index2.php?mod=tthief");
            ttime = ttime + 1;
            System.out.println("--- Scan number: "+ ttime  +" ---");
            getNewest(trader, 0);
            getNewest(hunter, 1);
            getNewest(thief, 2);

            if (LocalDateTime.now().compareTo(local.plusMinutes(2)) > 0) {
                local =  local.plusMinutes(2);
                System.out.println("---- Reset list ---- " + local.getHour() + ":" + local.getMinute());
                sos = false;
                timeNews = timeNews + local.getNano();
                getNewest(trader, 0);
                getNewest(hunter, 1);
                getNewest(thief, 2);
            }

            Thread.sleep(30000);
        }
    }

    private void getNewest(String s, int t) {
        Document page = Jsoup.parse(s);
        Elements elements = page.getElementsByClass("title_bot_bg");
        String[] sarray = elements.toString().split("/tr");
        if (t == 0) {
            scanAll(sarray, t);
        } else {
            scanAll(sarray, t);
        }
    }

    private void scanAll(String[] sarray, int t) {
        if (t==0) {
            if (timeNews != timeTrader) {
                mapTrader.remove(timeTrader);
                timeTrader =  timeNews;
                mapTrader.put(timeNews, listPersonRealTime(sarray));
            }
            if (mapTrader.isEmpty()) {
                System.out.println("---------- Creat new list trader ---------");
                mapTrader.put(timeNews, listPersonRealTime(sarray));
            }
        } else if (t == 1) {
            if (timeNews != timeHunter) {
                mapHunter.remove(timeHunter);
                timeHunter =  timeNews;
                mapHunter.put(timeNews, listPersonRealTime(sarray));
            }
            if (mapHunter.isEmpty()) {
                System.out.println("---------- Creat new list hunter ---------");
                mapHunter.put(timeNews, listPersonRealTime(sarray));
            }
        } else {
            if (timeNews != timeThief) {
                mapThief.remove(timeHunter);
                timeThief =  timeNews;
                mapThief.put(timeNews, listPersonRealTime(sarray));
            }
            if (mapThief.isEmpty()) {
                System.out.println("---------- Creat new list thief ---------");
                mapThief.put(timeNews, listPersonRealTime(sarray));
            }
        }

        Map<String, Long> people;
        if ( t== 0) {
            people = mapTrader.get(timeNews);
        } else if (t == 1) {
            people = mapHunter.get(timeNews);
        } else {
            people = mapThief.get(timeNews);
        }
        if (people.isEmpty()) {
            return;
        }
        Map<String, Long> listNews = listPersonRealTime(sarray);
        Collection<String> a = listNews.keySet();
        a.stream().forEach(x -> {
            if (ttime == 0) {
                System.out.println(" - " + x + " + "+people.get(x).longValue());
            }
            if (people.get(x).longValue() != listNews.get(x).longValue()) {
                String text =  (people.get(x).longValue() > listNews.get(x).longValue()) ? " down" : " up";
                if (x.equals("LaiLotHang")) System.out.print(ANSI_RED + "+ + + + +" + ANSI_RESET);
                System.out.println(" ---> " + x + text + " point time: " +LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
                try {
                    if (x.equals("LaiLotHang")) {
                        sos = true;
                    }
                    if (sos) {
                        Desktop.getDesktop().open(fileSOS);
                    } else {
                        Desktop.getDesktop().open(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Map<String, Long> listPersonRealTime(String[] sarray) {
        Map<String, Long> m = new HashMap<>();
        for (int i = 0; i < sarray.length - 1; i++) {
            String[] user = sarray[i].split("</td>");
            //System.out.println(" user: " + user[1].trim().substring(4, user[1].trim().length()) + " -- point: " + Long.valueOf(user[3].trim().substring(4, user[3].trim().length()).replace(".", "").toString()));
            m.put(user[1].trim().substring(4, user[1].trim().length()), Long.valueOf(user[3].trim().substring(4, user[3].trim().length()).replace(".", "").toString()));
        }
        return m;
    }

    public String resultConnect (String link) throws IOException {
        URL url = new URL(link);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    public static void main(String[] args) throws Exception {
        new HTTPUnitDriver();
    }


}
