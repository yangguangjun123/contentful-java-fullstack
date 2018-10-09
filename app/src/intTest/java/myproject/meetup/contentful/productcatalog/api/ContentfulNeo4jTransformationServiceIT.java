package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.service.ContentfulClientService;
import myproject.meetup.contentful.productcatalog.service.ContentfulNeo4jTransformationService;
import myproject.meetup.contentful.productcatalog.service.Neo4jService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentfulNeo4jTransformationServiceIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ContentfulClientService contentfulClientService;

    @Autowired
    ContentfulNeo4jTransformationService contentfulNeo4jTransformationService;

    @Autowired
    private Neo4jService neo4jService;

    private HttpHeaders headers = new HttpHeaders();

    private static final Logger logger = LoggerFactory.getLogger(ContentfulNeo4jTransformationServiceIT.class);

    @Ignore
    @Test
    public void shouldTransformContentfulAssetSuccessfully() {
        String assetJson = contentfulClientService.getContentfulAssets();
        List<String> cypherStatements = contentfulNeo4jTransformationService.transformAssets(assetJson);
        neo4jService.execute(cypherStatements);
    }

}
