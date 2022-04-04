package ru.kpfu.itis.Makhsotova.repository;

import ru.kpfu.itis.Makhsotova.YoutubeVideo;

import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    void save(YoutubeVideo video);
    Optional<YoutubeVideo> findLinkById(Integer id);
    List<YoutubeVideo> findAll();
}
