package dev.welovelain.wp2md.domain.pipe;

import dev.welovelain.wp2md.domain.MdFile;
import dev.welovelain.wp2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class DiskWritingMdFilePipe extends AbstractMdFilePipe {

    private final String blogDirectory;

    @Override
    protected MdFile processHere(MdFile file, Post post) {
        String fileName = blogDirectory + "/" + file.fileName;
        try (FileWriter fileWriter = new FileWriter(fileName); PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(file.content);
            return file;
        } catch (IOException e) {
            log.error("Failed to write to file. FileName: {}, error: {}", fileName, e.getMessage());
            throw new RuntimeException("Failed to write the file: " + fileName);
        }
    }
}
