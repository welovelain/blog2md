package dev.welovelain.wp2md.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    public long id;
    public String title;
    public LocalDateTime postDate;
    public LocalDateTime modifiedDate;
    public String htmlContent;
    public List<String> tags;
}
