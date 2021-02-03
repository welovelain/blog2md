package dev.welovelain.wp2md.infrastructure;

import dev.welovelain.wp2md.domain.Post;
import dev.welovelain.wp2md.domain.PostSupplier;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DbPostSupplier implements PostSupplier {

    private static final String SKIP_TAG = "Uncategorized";

    private static final String GET_POSTS_SQL = """
            SELECT ID, post_date, post_title, post_content FROM wp_posts WHERE post_status = 'publish'
            """;

    private static final String GET_TAGS = """
            SELECT object_id, name
            FROM wp_terms t JOIN wp_term_relationships r
            ON t.term_id = r.term_taxonomy_id;
            """;
    private final Connection connection;

    @Override
    public List<Post> get() {
        Map<Long, Post> postMap = new HashMap<>();

        try (Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery(GET_POSTS_SQL);

            while (rs.next()) {
                long id = rs.getLong("ID");
                LocalDateTime postDate = rs.getTimestamp("post_date").toLocalDateTime();
                String title = rs.getString("post_title");
                String postContent = rs.getString("post_content");

                Post post = new Post(id, title, postDate, postContent, new ArrayList<>());
                postMap.put(id, post);
            }

            fillTags(postMap);
            return new ArrayList<>(postMap.values());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get posts: " + e.getMessage());
        }
    }

    private void fillTags(Map<Long, Post> postMap) {
        List<PostIdTagName> tags = getTags();
        tags.forEach(tag -> {
            var post = postMap.get(tag.postId);
            if (post != null) {
                post.getTags().add(tag.tagName);
            }
        });
    }

    private List<PostIdTagName> getTags() {
        List<PostIdTagName> list = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery(GET_TAGS);

            while (rs.next()) {
                long id = rs.getLong("object_id");
                String name = rs.getString("name");
                if (SKIP_TAG.equals(name)) {
                    continue;
                }
                var postIdTagName = new PostIdTagName(id, name);
                list.add(postIdTagName);
            }

            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get tags: " + e.getMessage());
        }
    }

    @Value
    private static class PostIdTagName {
        long postId;
        String tagName;
    }
}
