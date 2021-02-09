package dev.welovelain.blog2md.domain.postsupplier;

import dev.welovelain.blog2md.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class BloggerXmlSupplier implements PostSupplier {

    private static final String POST_SCHEME = "http://schemas.google.com/g/2005#kind";
    private static final String POST_TERM = "http://schemas.google.com/blogger/2008/kind#post";
    private static final String BLOG_SCHEME = "http://www.blogger.com/atom/ns#";
    private final String xmlLocation;

    @Override
    public List<Post> get() {
        try {
            File fXmlFile = new File(xmlLocation);
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(fXmlFile);
            doc.getDocumentElement().normalize();

            List<Post> posts = new ArrayList<>();

            NodeList nList = doc.getElementsByTagName("entry");
            for (int i = 0; i < nList.getLength(); ++i) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    if (isPost(el)) {
                        var post = new Post(
                                getId(el),
                                getTitle(el),
                                getDateTime(el, "published"),
                                getDateTime(el, "updated"),
                                getHtmlContent(el),
                                getTags(el));
                        posts.add(post);
                    }
                }
            }
            return posts;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean isPost(Element element) {
        NodeList cats = element.getElementsByTagName("category");
        for (int i = 0; i < cats.getLength(); ++i) {
            var attr = cats.item(i).getAttributes();
            if (attr.getNamedItem("scheme").getTextContent().contains(POST_SCHEME)) {
                return attr.getNamedItem("term").getTextContent().equals(POST_TERM);
            }
        }
        return false;
    }
    private String getId(Element element) {
        return element.getElementsByTagName("id").item(0).getTextContent();
    }

    private String getTitle(Element element) {
        return element.getElementsByTagName("title").item(0).getTextContent();
    }

    private LocalDateTime getDateTime(Element element, String updated) {
        return ZonedDateTime.parse(element.getElementsByTagName("updated").item(0).getTextContent())
                .toLocalDateTime();
    }

    private List<String> getTags(Element element) {
        NodeList cats = element.getElementsByTagName("category");
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < cats.getLength(); ++i) {
            var attr = cats.item(i).getAttributes();
            if (attr.getNamedItem("scheme").getTextContent().contains(BLOG_SCHEME)) {
                tags.add(attr.getNamedItem("term").getTextContent());
            }
        }
        return tags;
    }

    private String getHtmlContent(Element element) {
        return element.getElementsByTagName("content").item(0).getTextContent();
    }
}
