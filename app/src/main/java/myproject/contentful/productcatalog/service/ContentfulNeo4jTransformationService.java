package myproject.contentful.productcatalog.service;

import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAAsset;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAEntry;
import com.contentful.java.cma.model.CMALocale;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentfulNeo4jTransformationService {

    private final ContentfulService contentfulSpaceService;
    private final Neo4jDatabaseService neo4JDatabaseService;

    private static final Logger logger = LoggerFactory.getLogger(ContentfulNeo4jTransformationService.class);

    @Autowired
    public ContentfulNeo4jTransformationService(ContentfulService contentfulSpaceService,
                                                   Neo4jDatabaseService neo4JDatabaseService) {
        this.contentfulSpaceService = contentfulSpaceService;
        this.neo4JDatabaseService = neo4JDatabaseService;
    }

    public String transform(String spaceName, String accessToken, String  environment) {
        JSONObject response = new JSONObject();

        JSONObject contentfulResponse = new JSONObject();
        neo4JDatabaseService.deleteAll();

        CMAArray<CMAContentType> entryTypes = contentfulSpaceService.getAllContentfulTypes(spaceName,
                accessToken, environment);
        JSONObject entryTypeJson = new JSONObject(entryTypes);
        neo4JDatabaseService.createEntryNode(entryTypeJson.getJSONArray("items").toString());
        contentfulResponse.put("contentEntryType", entryTypeJson.getJSONArray("items"));

        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(spaceName,
                accessToken, environment);
        JSONObject entryJson = new JSONObject(entries);
        neo4JDatabaseService.createEntryNode(entryJson.getJSONArray("items").toString());
        contentfulResponse.put("contentEntry", entryJson.getJSONArray("items"));

        CMAArray<CMAAsset> assets = contentfulSpaceService.getAllContentfulAssets(spaceName, accessToken, environment);
        JSONObject assetJson = new JSONObject(assets);
        neo4JDatabaseService.createAssetNode(assetJson.getJSONArray("items").toString());
        contentfulResponse.put("contentAsset", assetJson.getJSONArray("items"));

        CMAArray<CMALocale> locales = contentfulSpaceService.getAllContentfulLocales(spaceName, accessToken, environment);
        JSONObject localJson = new JSONObject(locales);
        neo4JDatabaseService.createLocaleNode(localJson.getJSONArray("items").toString());
        contentfulResponse.put("contentLocale", localJson.getJSONArray("items"));
        response.put("content", contentfulResponse);

        neo4JDatabaseService.deleteAllUserRelationship();

        response.put("neo4j", neo4JDatabaseService.retrieveAll());
        logger.info("response: " + response.toString());

        return response.toString();
    }
}
