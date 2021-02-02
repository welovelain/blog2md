package dev.welovelain.wp2md.domain;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Post {
    private long id;
    private String title;
    private LocalDate postDate;
    private String htmlContent;
    private List<String> tags;
}
