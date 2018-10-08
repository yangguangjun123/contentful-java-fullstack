package myproject.meetup.contentful.productcatalog.api;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Neo4jControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    private static final Logger logger = LoggerFactory.getLogger(Neo4jControllerIT.class);

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    @Test
    public void shouldDeleteAllEntities() {
        // given
        logger.info("shouldDeleteAllEntities");
        HttpEntity<String> entity = new HttpEntity(null, headers);
        String expected = String.format("{result: success}");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/neo4j/deleteAll"),
                HttpMethod.DELETE, entity, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
