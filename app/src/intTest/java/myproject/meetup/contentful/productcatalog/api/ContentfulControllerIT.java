package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import org.json.JSONArray;
import org.json.JSONException;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentfulControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ContentfulProperties contefulProperties;

    private HttpHeaders headers = new HttpHeaders();
    private String productCatalogueSpaceId;

    private static final Logger logger = LoggerFactory.getLogger(ContentfulControllerIT.class);

    @Before
    public void setUp() {
        if(Objects.isNull(productCatalogueSpaceId)) {
            HttpEntity<String> entity = new HttpEntity(null, headers);
            StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/get/objectKey/");
            restUrlBuilder.append(contefulProperties.getWorkshopSpaceName());
            restUrlBuilder.append("/");
            restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
            restUrlBuilder.append("/");
            restUrlBuilder.append("master");
            ResponseEntity<String> response = restTemplate.exchange(
                    createURLWithPort(restUrlBuilder.toString()),
                    HttpMethod.GET, entity, String.class);
            JSONObject jsonObject = new JSONObject(response.getBody());
            productCatalogueSpaceId = jsonObject.getString("id");
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnContentfulSpaceByName() throws JSONException {
        // given
        logger.info("shouldReturnContentfulSpaceByName");
        HttpEntity<String> entity = new HttpEntity(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject jsonObject = new JSONObject(response.getBody());
        logger.info("json response: " + jsonObject);
        assertEquals(contefulProperties.getWorkshopSpaceName(), jsonObject.getString("name"));
        assertTrue(jsonObject.getString("id").length() > 0);
    }

    @Test
    public void shouldDeleteContentfulSpaceByName() {
        logger.info("shouldDeleteContentfulSpaceByName");
        createContentfulSpace("Copy of Product Catalogue");
        HttpEntity<String> entity = new HttpEntity(null, headers);
        String expected = "{result:success}";
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/delete/objectKey/");
        restUrlBuilder.append("Copy of Product Catalogue");
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.DELETE, entity, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void shouldCreateContentfulSpaceForGivenNameAndOrganisation() {
        logger.info("shouldCreateContentfulSpaceForGivenNameAndOrganisation");
        deleteContentfulSpace("Copy of Product Catalogue");
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        map.add("organisation", contefulProperties.getWorkshopOrganisationName());
        HttpEntity<Object> entity = new HttpEntity(map, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/create/objectKey/");
        restUrlBuilder.append("Copy of Product Catalogue");
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.POST, entity, String.class);

        // verify
        JSONObject jsonObject = new JSONObject(response.getBody());
        logger.info("json response: " + jsonObject);
        assertEquals(contefulProperties.getWorkshopSpaceName(), jsonObject.getString("name"));
        assertTrue(jsonObject.getString("id").length() > 0);
        deleteContentfulSpace("Copy of Product Catalogue");
    }

    @Test
    public void shouldRetrieveAllContentTypesBySpaceName() {
        // given
        HttpEntity<String> entity = new HttpEntity(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contenttype/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentType");
        assertTrue(arr.length() >= 0);
        arr.toList().stream()
                    .map(s -> (Map<String,?>) s)
                    .forEach( o -> {
                        assertEquals(productCatalogueSpaceId, o.get("spaceId"));
                        assertEquals("master", o.get("environmentId"));
                        assertTrue(((String) o.get("id")).length() > 0);
                        assertTrue(((String) o.get("name")).length() > 0);
                    });
    }

    @Test
    public void shouldRetrieveAllContentEntriesBySpaceName() {
        // given
        HttpEntity<String> entity = new HttpEntity(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contententry/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentEntry");
        assertTrue(arr.length() >= 0);
        arr.toList().stream()
                .map(s -> (Map<String,?>) s)
                .forEach( o -> {
                    assertEquals(productCatalogueSpaceId, o.get("spaceId"));
                    assertEquals("master", o.get("environmentId"));
                    assertTrue(((String) o.get("id")).length() > 0);
                });
    }

    @Test
    public void shouldRetrieveAllContentAssets() {
        // given
        HttpEntity<String> entity = new HttpEntity(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contentasset/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentAsset");
        assertTrue(arr.length() >= 0);
        arr.toList().stream()
                .map(s -> (Map<String,?>) s)
                .forEach( o -> {
                    assertEquals(productCatalogueSpaceId, o.get("spaceId"));
                    assertEquals("master", o.get("environmentId"));
                    assertTrue(((String) o.get("id")).length() > 0);
                });
    }

    @Test
    public void shouldRetrieveAllContentLocalesBySpaceName() {
        // given
        HttpEntity<String> entity = new HttpEntity(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contentlocale/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentLocale");
        assertTrue(arr.length() > 0);
        arr.toList().stream()
                .map(s -> (Map<String,?>) s)
                .forEach( o -> {
                    assertEquals(productCatalogueSpaceId, o.get("spaceId"));
                    assertEquals("master", o.get("environmentId"));
                    assertTrue(((String) o.get("id")).length() > 0);
                    assertTrue(((String) o.get("name")).length() > 0);
                });
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private void deleteContentfulSpace(String spaceName) {
        logger.info("deleteContentfulData");
        HttpEntity<String> entity = new HttpEntity(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/delete/objectKey/");
        restUrlBuilder.append(spaceName);
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.DELETE, entity, String.class);
        logger.info("deleteContentfulData(json response): " + response);
    }

    private void createContentfulSpace(String spaceName) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        map.add("organisation", contefulProperties.getWorkshopOrganisationName());
        HttpEntity<Object> entity = new HttpEntity(map, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/create/objectKey/");
        restUrlBuilder.append(spaceName);
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.POST, entity, String.class);
        logger.info("createContentfulSpace(json response): " + response);
    }

}