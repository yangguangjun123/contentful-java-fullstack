package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import org.json.JSONArray;
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
    private ContentfulProperties contentfulProperties;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers = new HttpHeaders();

    private static final Logger logger = LoggerFactory.getLogger(Neo4jControllerIT.class);

    @Test
    public void shouldDeleteAllEntities() {
        // given
        logger.info("shouldDeleteAllEntities");
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        String expected = String.format("{result: success}");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/neo4j/delete/all"),
                HttpMethod.DELETE, entity, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void shouldCreateNeo4jNodeFromContentfulEntry() {
        // given
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contententry/get/objectKey/");
        restUrlBuilder.append(contentfulProperties.getWorkshopSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getWorkshopSpaceEnvironment());
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentEntry");

        // when
        String expected = "{result:success}";
        HttpEntity<String> entityForNeo4j = new HttpEntity<String>(JSONObject.valueToString(arr), headers);
        response = restTemplate.exchange(createURLWithPort("/neo4j/create/node/label/Entry"),
                HttpMethod.POST, entityForNeo4j, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
