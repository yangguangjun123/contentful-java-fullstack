package myproject.contentful.productcatalog.api;

import myproject.contentful.productcatalog.config.ContentfulProperties;
import myproject.contentful.productcatalog.service.ContentfulNeo4jTransformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping(value = "/contentful/neo4j/transform", produces={"application/json","application/xml"})
public class ContentfulNeo4jTransformationController {

    private final ContentfulNeo4jTransformationService contentfulNeo4jTransformationService;
    private final ContentfulProperties contentfulProperties;

    @Autowired
    public ContentfulNeo4jTransformationController(
            ContentfulNeo4jTransformationService contentfulNeo4jTransformationService,
            ContentfulProperties contentfulProperties) {
        this.contentfulNeo4jTransformationService = contentfulNeo4jTransformationService;
        this.contentfulProperties = contentfulProperties;
    }

    @RequestMapping(path = "/{spaceName}/{accessToken}/{environment}", method= RequestMethod.POST)
    public String transform(@PathVariable String spaceName, @PathVariable String accessToken,
                                 @PathVariable String  environment) {
        return contentfulNeo4jTransformationService.transform(spaceName, accessToken, environment);
    }

    @RequestMapping(path = "/default", method= RequestMethod.POST)
    public String transform() {
        return contentfulNeo4jTransformationService.transform(contentfulProperties.getSpaceName(),
                contentfulProperties.getManagementAccessToken(),
                contentfulProperties.getSpaceEnvironment());
    }

}
