package dev.welovelain.wp2md.domain;

import lombok.Value;
import lombok.With;

@Value
public class MdFile {
    @With
    public String fileName;
    @With
    public String content;
}
