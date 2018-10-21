package myproject.meetup.contentful.productcatalog;

import static org.assertj.core.api.Assertions.assertThat;

import myproject.meetup.contentful.productcatalog.api.ContentfulController;
import myproject.meetup.contentful.productcatalog.api.Neo4jController;
import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import myproject.meetup.contentful.productcatalog.service.ContentfulNeo4jService;
import myproject.meetup.contentful.productcatalog.web.ProductWebController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmokeTest {

    @Autowired
    private ContentfulController contentfulController;

    @Autowired
    private ProductWebController contentfulWebController;

    @Autowired
    private ContentfulProperties contentfulProperties;

    @MockBean
    private ContentfulNeo4jService neo4jService;

    @InjectMocks
    private Neo4jController neo4jController;

    @Test
    public void shouldLoadContext() throws Exception {
        assertThat(contentfulController).isNotNull();
        assertThat(contentfulWebController).isNotNull();
        assertThat(contentfulProperties).isNotNull();
        assertThat(neo4jController).isNotNull();
    }
}
