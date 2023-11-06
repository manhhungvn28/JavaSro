package crawler;

import org.jsoup.Connection;
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

public class VietAutoOffAdds {
    VietAutoOffAdds () throws FileNotFoundException {
        String url = "";
        long click = 0;
        long order = 0;
        int acos = 0;
        System.out.println("-------------- Hello VietPro -----------");
        System.out.println("-------------- Start Time: " + ((LocalDateTime.now().getHour() < 10) ? "0"+LocalDateTime.now().getHour() : LocalDateTime.now().getHour() ) + "h" + ((LocalDateTime.now().getMinute() < 10) ? "0"+LocalDateTime.now().getMinute() : LocalDateTime.now().getMinute() ));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        File myObj = new File("vietconfig.txt");
        Scanner myReader = new Scanner(myObj);
        String[] strings = null;
        while (myReader.hasNextLine()) {
            strings = myReader.nextLine().split(",");
        }
        myReader.close();
        //string [1] path, string[3] name file mp3, strings[6] choice thief or trader or hunter
        url = strings[0];
        click = Long.valueOf(strings[1]);
        order = Long.valueOf(strings[2]);
        acos = Integer.valueOf(strings[2]);
        try {
            this.procees();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("--------------" + strings[0]);
    }
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
    final String LOGIN_FORM_URL = "https://www.amazon.com/ap/signin?openid.pape.max_auth_age=28800&openid.return_to=https%3A%2F%2Fadvertising.amazon.com%2Fcm%2Fcampaigns%3FentityId%3DENTITY3L5425YZT33Q3&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=amzn_bt_desktop_us&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&ssoResponse=eyJ6aXAiOiJERUYiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiQTI1NktXIn0.V6Uko4sfHit8l0uPfPa0NbRAPzSuEIOnSgbouhlOn0BVfGmyyO6Tlg._7mVikkacxKmjFgd.kGPTj58z64iPOWHFoXGzJpVrkLCw1ZlWNgymR0ZntlIYjYbEp65S7WWnNVpmy9rrCfqdoA8EqSauJU_65WUHRs-EgtkkAfw0kyCbeB4OMpLGpknG9zwhVIzRE9VnM76sZ23L7Liha3ZuWFygquAAN89ojaLIi9OqAgaj7SMxXPLq_aOo1f0Kuv-XXjHnJ25fhjI2EktZsr4A-kw1M1fyFCf7W6teOAzvEDJRUuBoYBCXsme1R5pz._hQzfP8sv4eJYPjz2Svqvw";
    final String LOGIN_ACTION_URL = "https://advertising.amazon.com/cm/campaigns?entityId=ENTITY3L5425YZT33Q3";
    final String EMAIL = "caasischachterle@hotmail.com";
    final String PASSWORD = "bkM5AgPHa";
    public void procees () throws IOException {
        Connection.Response response = Jsoup.connect(LOGIN_FORM_URL)
                .data("email", EMAIL)
                .data("password", PASSWORD)
                .method(Connection.Method.POST)
                .execute();

        Document homePage = Jsoup.connect(LOGIN_ACTION_URL)
                .cookies(response.cookies())
                .method(Connection.Method.GET)
                .get();
        System.out.println(homePage +"00");
    }
    public static void main(String[] args) throws FileNotFoundException {
        VietAutoOffAdds vietAutoOffAdds = new VietAutoOffAdds();
    }
}
