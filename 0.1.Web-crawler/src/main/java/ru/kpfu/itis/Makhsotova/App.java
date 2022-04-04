package ru.kpfu.itis.Makhsotova;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opencsv.CSVWriter;
import ru.kpfu.itis.Makhsotova.config.Config;
import ru.kpfu.itis.Makhsotova.repository.LinkRepository;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    private static RequestHandler requestHandler = new RequestHandler();
    private static LinkRepository linkRepository ;

    public static  String LINK_FOR_VIDEOS;
    public static String API_KEY;
    public static String CHANNEL_ID;
    public static String LINK_VIDEO;
    public static String NEXT_PAGE_TOKEN;
    public static List<YoutubeVideo> list = new ArrayList<>();
    public static String []stopList = new String[]{"instagram", "vk", "youtube", "t.me", "vdudvdud", "youtu.be", "twitter", "facebook", "ok.ru", "t-do.ru"};
    public static void main(String [] args)  {

        init();
        parseVideo("noPrevToken");
        findLinks();
        writeCSV();
    }

    private static String exec(String url) {
        System.out.println(url);
        URLConnection conn;
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            conn = new URL(url).openConnection();
            System.out.println("open conn");

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            System.out.println("Read");

            String line;
            Pattern pattern = Pattern.compile("<title>.+<\\/title>");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    builder.append(line.substring(matcher.start()+7, matcher.end()-8));
                }
            }
            reader.close();
            System.out.println("close");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
         return builder.toString();
    }

    private static void writeCSV() {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("data.csv"));
            String[] column = "Название видео,Имя компании,Реферальная ссылка".split(",");

            writer.writeNext(column);

            List<YoutubeVideo> videos = linkRepository.findAll();
            String[] data = new String[3];
            for (YoutubeVideo video : videos) {
                data[0] = video.getTitle();
                data[1] = exec(video.getReferralLink());
                data[2] = video.getReferralLink();
                writer.writeNext(data);
            }
            writer.close();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static void init() {
        Properties properties = new Properties();

        try {
            properties.load(ClassLoader.getSystemResourceAsStream("application.properties"));
            API_KEY = properties.getProperty("api_key2");
            CHANNEL_ID = properties.getProperty("channel_id");
            LINK_FOR_VIDEOS = properties.getProperty("link_videos");
            LINK_VIDEO = properties.getProperty("link_video");
        } catch (IOException ex) {
            System.out.println("mistake with load resources");
        }

        Config config = new Config();
        linkRepository = config.getLinkRepository();
    }

    public static InputStream makeRequest(String url) {
        requestHandler.setURL(url);
        requestHandler.openConnection();
        return  requestHandler.getInputStream();
    }

    public static void parseVideo(String pageToken) {
        String linkToken;
        if (pageToken.equals("noPrevToken")) {
            linkToken = LINK_FOR_VIDEOS;
        } else {
            linkToken = LINK_FOR_VIDEOS+"&pageToken="+pageToken;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(makeRequest(linkToken + "&channelId=" + CHANNEL_ID + "&key=" + API_KEY), StandardCharsets.UTF_8));
        Gson gson = new Gson();
        JsonObject object = gson.fromJson(br, JsonObject.class);
        JsonArray array = object.getAsJsonArray("items");
        JsonObject pageInfo = object.getAsJsonObject("pageInfo");
        int result = pageInfo.get("resultsPerPage").getAsInt();

        for (int i = 0; i < result; i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            JsonObject id = obj.getAsJsonObject("id");
            JsonObject snippet = obj.getAsJsonObject("snippet");
            if (id.get("videoId") == null) {
                break;
            }
            String videoId = id.get("videoId").getAsString();
            String title = snippet.get("title").getAsString();

            YoutubeVideo video = YoutubeVideo.builder()
                    .videoId(videoId)
                    .etag(obj.get("etag").getAsString())
                    .title(title)
                    .build();

            list.add(video);

            BufferedReader reader = new BufferedReader(new InputStreamReader(makeRequest(LINK_VIDEO + "%2CcontentDetails%2Cstatistics&id=" + video.getVideoId() + "&key=" + API_KEY), StandardCharsets.UTF_8));

            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray array1 = jsonObject.getAsJsonArray("items");
            JsonObject object1 = array1.get(0).getAsJsonObject();
            JsonObject snippet1 = object1.getAsJsonObject("snippet");
            String description = snippet1.get("description").getAsString();

            video.setDescription(description);
        }

        if (object.get("nextPageToken") != null) {
            NEXT_PAGE_TOKEN = object.get("nextPageToken").getAsString();
            parseVideo(NEXT_PAGE_TOKEN);
        }
    }

    public static void findLinks() {

        Pattern pattern = Pattern.compile("(https?:\\/\\/)+([\\w-]{1,32}\\.[\\w-]{1,32})[^\\s@]*");
        for (YoutubeVideo youtubeVideo : list) {
            youtubeVideo.setReferralLinks(new ArrayList<>());
            String description = youtubeVideo.getDescription();
            Matcher matcher = pattern.matcher(description);
            while (matcher.find()) {
                String link = description.substring(matcher.start(), matcher.end());
                youtubeVideo.getReferralLinks().add(link);
                boolean flag = false;
                for (String example : stopList) {
                    if (link.contains(example)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    youtubeVideo.setReferralLink(link);
                    linkRepository.save(youtubeVideo);
                }
            }
        }
    }
}
