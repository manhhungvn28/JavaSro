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
    private boolean bel = false;
    private String trader = "";
    private String hunter = "";
    private String thief = "";
    private long timeSleep = 0;
    private int distancePerRequest = 10000;
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
        while (local.compareTo(dateTime.now().plusDays(30)) < 0) {
            File myObj = new File("E:\\toolsro\\config.txt");
            Scanner myReader = new Scanner(myObj);
            String[] strings = null;
            while (myReader.hasNextLine()) {
                strings = myReader.nextLine().split(",");

            }
            myReader.close();
            hunter = strings[0];
            thief = strings[1];
            trader = strings[2];
            normalSound = new File(strings[5] + strings[3]);
            sosSound = new File(strings[5] + strings[4]);
            sosName = strings[6];
            timeSleep = Long.valueOf(strings[7]).longValue();
            distancePerRequest = Long.valueOf(strings[8]).intValue();
            ttime = ttime + 1;
            System.out.println("-> Times: "+ ttime);
            getNewest(trader, 0);
            getNewest(hunter, 1);
            getNewest(thief, 2);
            try {
                if (sos && bel) {
                    Desktop.getDesktop().open(sosSound);
                } else if (bel) {
                    Desktop.getDesktop().open(normalSound);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (LocalDateTime.now().compareTo(local.plusMinutes(distancePerRequest)) > 0) {
                local =  local.plusMinutes(distancePerRequest);
                System.out.println("-> Reset: " + ((local.getHour() < 10) ? "0"+local.getHour() : local.getHour()) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
                sos = false;
                bel = false;
                timeNews = timeNews + local.getNano();
                getNewest(trader, 0);
                getNewest(hunter, 1);
                getNewest(thief, 2);
            }

            Thread.sleep(timeSleep);
        }
    }

    private void getNewest(String s, int t) {
        Document page = Jsoup.parse(s);
        Elements elements = page.getElementsByClass("title_bot_bg");
        String[] sarray = elements.toString().split("/tr");
        scanAll(sarray, t);
    }

    private void scanAll(String[] sarray, int t) {
        if (t==0) {
            if (timeNews != timeTrader) {
                mapTrader.remove(timeTrader);
                timeTrader =  timeNews;
                mapTrader.put(timeNews, listPersonRealTime(sarray));
            }
            if (mapTrader.isEmpty()) {
                System.out.println("---------- Start trader ---------");
                mapTrader.put(timeNews, listPersonRealTime(sarray));
            }
        } else if (t == 1) {
            if (timeNews != timeHunter) {
                mapHunter.remove(timeHunter);
                timeHunter =  timeNews;
                mapHunter.put(timeNews, listPersonRealTime(sarray));
            }
            if (mapHunter.isEmpty()) {
                System.out.println("---------- Start hunter ---------");
                mapHunter.put(timeNews, listPersonRealTime(sarray));
            }
        } else {
            if (timeNews != timeThief) {
                mapThief.remove(timeHunter);
                timeThief =  timeNews;
                mapThief.put(timeNews, listPersonRealTime(sarray));
            }
            if (mapThief.isEmpty()) {
                System.out.println("---------- Start thief ---------");
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
                if (x.equals(sosName)) System.out.print(ANSI_RED + "+ + +" + ANSI_RESET);
                System.out.println(" -> " + x + text + " point at: " +((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
                    bel = true;
                    if (x.equals(sosName)) {
                        sos = true;
                    }
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
//    class CSV {
//        private String urlHunter;
//        private String urlThief;
//        private String urlTrader;
//        private String normalSound;
//        private String sosSound;
//        private String pathSound;
//        private String nameSos;
//        private long timeSleep;
//        private int distancePerRequest;
//        CSV() {
//
//        }
//
//        public String getUrlHunter() {
//            return urlHunter;
//        }
//
//        public void setUrlHunter(String urlHunter) {
//            this.urlHunter = urlHunter;
//        }
//
//        public String getUrlThief() {
//            return urlThief;
//        }
//
//        public void setUrlThief(String urlThief) {
//            this.urlThief = urlThief;
//        }
//
//        public String getUrlTrader() {
//            return urlTrader;
//        }
//
//        public void setUrlTrader(String urlTrader) {
//            this.urlTrader = urlTrader;
//        }
//
//        public String getNormalSound() {
//            return normalSound;
//        }
//
//        public void setNormalSound(String normalSound) {
//            this.normalSound = normalSound;
//        }
//
//        public String getSosSound() {
//            return sosSound;
//        }
//
//        public void setSosSound(String sosSound) {
//            this.sosSound = sosSound;
//        }
//
//        public String getPathSound() {
//            return pathSound;
//        }
//
//        public void setPathSound(String pathSound) {
//            this.pathSound = pathSound;
//        }
//
//        public String getNameSos() {
//            return nameSos;
//        }
//
//        public void setNameSos(String nameSos) {
//            this.nameSos = nameSos;
//        }
//
//        public long getTimeSleep() {
//            return timeSleep;
//        }
//
//        public void setTimeSleep(long timeSleep) {
//            this.timeSleep = timeSleep;
//        }
//
//        public int getDistancePerRequest() {
//            return distancePerRequest;
//        }
//
//        public void setDistancePerRequest(int distancePerRequest) {
//            this.distancePerRequest = distancePerRequest;
//        }
//    }
}
