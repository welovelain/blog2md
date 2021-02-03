package dev.welovelain.wp2md.domain;

import lombok.Value;

@Value
public class MdFile {
    public String fileName;
    public String content;
}
