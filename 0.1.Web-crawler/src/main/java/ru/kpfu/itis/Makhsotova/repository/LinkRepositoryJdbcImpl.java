package ru.kpfu.itis.Makhsotova.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.kpfu.itis.Makhsotova.YoutubeVideo;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class LinkRepositoryJdbcImpl  implements LinkRepository{

    private JdbcTemplate jdbcTemplate;

    //language=SQL
    private String SQL_INSERT = "insert into link (link, source, link_on_video) values (?, ?, ?)";

    //language=SQL
    private String SQL_FIND_BY_ID = "select * from link where id = ?";

    //language=SQL
    private String SQL_FIND_ALL = "select * from link";

    public LinkRepositoryJdbcImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final RowMapper<YoutubeVideo> linkRawMapper = (row, rowNumber) -> {
        return YoutubeVideo.builder()
                .idDB(row.getInt("id"))
                .referralLink(row.getString("link"))
                .title(row.getString("source"))
                .build();
    };

    @Override
    public void save(YoutubeVideo video) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update( connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT, new String[]{"id"});
            statement.setString(1, video.getReferralLink());
            statement.setString(2, video.getTitle());
            statement.setString(3, video.getLinkVideo());
            return statement;
        }, keyHolder);

        video.setIdDB (keyHolder.getKey().intValue());
    }

    @Override
    public Optional<YoutubeVideo> findLinkById(Integer id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL_FIND_BY_ID, linkRawMapper, id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<YoutubeVideo> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, linkRawMapper);
    }


}
