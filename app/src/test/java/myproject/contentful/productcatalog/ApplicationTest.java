package myproject.contentful.productcatalog;

import myproject.contentful.productcatalog.api.ContentfulNeo4jController;
import myproject.contentful.productcatalog.service.ContentfulService;
import myproject.contentful.productcatalog.service.Neo4jDatabaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ApplicationTest {

    @MockBean
    private Neo4jDatabaseService neo4jService;

    @MockBean
    private ContentfulService contentfulService;

    @InjectMocks
    private ContentfulNeo4jController contentfulNeo4jController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnDefaultPage() throws Exception {
        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("index")));
    }

}
