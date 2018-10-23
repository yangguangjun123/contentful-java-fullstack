package myproject.meetup.contentful.productcatalog;

import static org.assertj.core.api.Assertions.assertThat;

import myproject.meetup.contentful.productcatalog.api.ContentfulAnalyticsController;
import myproject.meetup.contentful.productcatalog.api.ContentfulController;
import myproject.meetup.contentful.productcatalog.api.ContentfulNeo4jController;
import myproject.meetup.contentful.productcatalog.api.ContentfulNeo4jTransformationController;
import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import myproject.meetup.contentful.productcatalog.config.Neo4jProperties;
import myproject.meetup.contentful.productcatalog.service.ContentfulNeo4jService;
import myproject.meetup.contentful.productcatalog.web.ProductWebController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
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
    private ContentfulNeo4jService neo4jService;

    @InjectMocks
    private ContentfulNeo4jController contentfulNeo4jController;

    @Autowired
    private ContentfulAnalyticsController contentfulAnalyticsController;

    @Autowired
    private ContentfulNeo4jTransformationController contentfulNeo4jTransformationController;

    @Test
    public void shouldLoadContext() throws Exception {
        assertThat(contentfulController).isNotNull();
        assertThat(contentfulWebController).isNotNull();
        assertThat(contentfulProperties).isNotNull();
        assertThat(neo4jProperties).isNotNull();
        assertThat(contentfulNeo4jController).isNotNull();
        assertThat(contentfulAnalyticsController).isNotNull();
        assertThat(contentfulNeo4jTransformationController).isNotNull();
    }
}
