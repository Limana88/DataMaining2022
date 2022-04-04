package ru.kpfu.itis.Makhsotova;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class YoutubeVideo {
    private String videoId;
    private String title;
    private String description;
    private String etag;
    private List<String> referralLinks;
    private String referralLink;
    private Integer idDB;
    private String linkVideo;
}
