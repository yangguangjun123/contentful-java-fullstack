package myproject.meetup.contentful.productcatalog;

import static org.assertj.core.api.Assertions.assertThat;

import myproject.meetup.contentful.productcatalog.api.ContentfulController;
import myproject.meetup.contentful.productcatalog.web.ContentfulWebController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmokeTest {

    @Autowired
    private ContentfulController contentfulController;

    @Autowired
    private ContentfulWebController contentfulWebController;

    @Test
    public void shouldLoadContext() throws Exception {
        assertThat(contentfulController).isNotNull();
        assertThat(contentfulWebController).isNotNull();
    }
}
