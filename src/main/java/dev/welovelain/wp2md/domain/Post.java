package dev.welovelain.wp2md.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private long id;
    private String title;
    private LocalDateTime postDate;
    private LocalDateTime modifiedDate;
    private String htmlContent;
    private List<String> tags;
}
