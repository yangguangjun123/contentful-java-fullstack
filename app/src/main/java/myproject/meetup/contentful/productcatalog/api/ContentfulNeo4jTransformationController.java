package myproject.meetup.contentful.productcatalog.api;

import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAEntry;
import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import myproject.meetup.contentful.productcatalog.service.ContentfulNeo4jService;
import myproject.meetup.contentful.productcatalog.service.ContentfulService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/contentful/neo4j/transform")
public class ContentfulNeo4jTransformationController {

    private ContentfulService contentfulSpaceService;
    private ContentfulProperties contentfulProperties;
    private ContentfulNeo4jService contentfulNeo4jService;
    private static final int SUCCESS = 204;

    @Autowired
    public ContentfulNeo4jTransformationController(ContentfulService contentfulSpaceService,
                                                   ContentfulProperties contentfulProperties,
                                                   ContentfulNeo4jService contentfulNeo4jService) {
        this.contentfulSpaceService = contentfulSpaceService;
        this.contentfulProperties = contentfulProperties;
        this.contentfulNeo4jService = contentfulNeo4jService;
    }

    @RequestMapping(path = "/{spaceName}/{accessToken}/{environment}", method= RequestMethod.POST)
    public String transform(@PathVariable String spaceName, @PathVariable String accessToken,
                                 @PathVariable String  environment) {
        contentfulNeo4jService.deleteAll();
        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(spaceName,
                accessToken, environment);
        JSONObject obj = new JSONObject(entries);
        contentfulNeo4jService.createEntryNode(obj.getJSONArray("items").toString());
        return "{result:success}";
    }

    @RequestMapping(path = "/default", method= RequestMethod.POST)
    public String transform() {
        contentfulNeo4jService.deleteAll();
        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(
                contentfulProperties.getWorkshopSpaceName(), contentfulProperties.getWorkshopManagementAccessToken(),
                contentfulProperties.getWorkshopSpaceEnvironment());
        JSONObject obj = new JSONObject(entries);
        contentfulNeo4jService.createEntryNode(obj.getJSONArray("items").toString());
        return "{result:success}";
    }

}
