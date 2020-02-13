package myproject.contentful.productcatalog;

import myproject.contentful.productcatalog.api.ContentfulAnalyticsController;
import myproject.contentful.productcatalog.api.ContentfulController;
import myproject.contentful.productcatalog.api.ContentfulNeo4jController;
import myproject.contentful.productcatalog.api.ContentfulNeo4jTransformationController;
import myproject.contentful.productcatalog.config.ContentfulProperties;
import myproject.contentful.productcatalog.config.Neo4jProperties;
import myproject.contentful.productcatalog.service.Neo4jDatabaseService;
import myproject.contentful.productcatalog.web.ProductWebController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SmokeTest {

    @Autowired
    private ContentfulController contentfulController;

    @Autowired
    private ProductWebController contentfulWebController;

    @Autowired
    private ContentfulProperties contentfulProperties;

    @Autowired
    private Neo4jProperties neo4jProperties;

    @MockBean
    private Neo4jDatabaseService neo4jService;

    @InjectMocks
    private ContentfulNeo4jController contentfulNeo4jController;

    @Autowired
    private ContentfulAnalyticsController contentfulAnalyticsController;

    @Autowired
    private ContentfulNeo4jTransformationController contentfulNeo4jTransformationController;

    @Test
    public void shouldLoadContext() throws Exception {
        assertNotNull(contentfulController);
        assertNotNull(contentfulWebController);
        assertNotNull(contentfulProperties);
        assertNotNull(neo4jProperties);
        assertNotNull(contentfulNeo4jController);
        assertNotNull(contentfulAnalyticsController);
        assertNotNull(contentfulNeo4jTransformationController);
    }
}
