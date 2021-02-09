package dev.welovelain.blog2md.domain.postsupplier;

import dev.welovelain.blog2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
@Slf4j
public class WordpressDbPostSupplier implements PostSupplier {

    private static final String SKIP_TAG = "Uncategorized";

    private static final String GET_POSTS_SQL = """
            SELECT ID, post_date, post_title, post_modified, post_content FROM wp_posts WHERE post_status = 'publish'
            """;

    private static final String GET_TAGS = """
            SELECT object_id, name
            FROM wp_terms t JOIN wp_term_relationships r
            ON t.term_id = r.term_taxonomy_id
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
                LocalDateTime modifiedDate = rs.getTimestamp("post_modified").toLocalDateTime();
                String title = rs.getString("post_title");
                String postContent = rs.getString("post_content");

                Post post = new Post(String.valueOf(id), title, postDate, modifiedDate, postContent, new ArrayList<>());
                postMap.put(id, post);
            }

            fillTags(postMap);
            return new ArrayList<>(postMap.values());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to get posts: " + e.getMessage());
        }
    }

    private void fillTags(Map<Long, Post> postMap) {
        Map<Long, List<String>> tags = getTags()
                .stream()
                .collect(groupingBy(PostIdTagName::getPostId, mapping(PostIdTagName::getTagName, toList())));
        postMap.forEach((id, post) -> post.setTags(tags.getOrDefault(id, Collections.emptyList())));
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
            log.error(e.getMessage(), e);
            throw new RuntimeException("Failed to get tags: " + e.getMessage());
        }
    }

    @Value
    private static class PostIdTagName {
        long postId;
        String tagName;
    }
}
