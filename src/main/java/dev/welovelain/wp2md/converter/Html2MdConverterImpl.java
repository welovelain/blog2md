package dev.welovelain.wp2md.converter;

import io.github.furstenheim.CopyDown;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Html2MdConverterImpl implements Html2mdConverter {

    private final CopyDown converter;

    @Override
    public String convert(String html) {
        return converter.convert(html);
    }
}
