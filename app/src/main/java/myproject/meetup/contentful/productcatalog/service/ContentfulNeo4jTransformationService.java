package myproject.meetup.contentful.productcatalog.service;

import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAAsset;
import com.contentful.java.cma.model.CMAEntry;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentfulNeo4jTransformationService {

    private ContentfulService contentfulSpaceService;
    private ContentfulNeo4jService contentfulNeo4jService;

    @Autowired
    public ContentfulNeo4jTransformationService(ContentfulService contentfulSpaceService,
                                                   ContentfulNeo4jService contentfulNeo4jService) {
        this.contentfulSpaceService = contentfulSpaceService;
        this.contentfulNeo4jService = contentfulNeo4jService;
    }

    public String transform(String spaceName, String accessToken, String  environment) {
        contentfulNeo4jService.deleteAll();
        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(spaceName,
                accessToken, environment);
        JSONObject entryJson = new JSONObject(entries);
        contentfulNeo4jService.createEntryNode(entryJson.getJSONArray("items").toString());

        CMAArray<CMAAsset> assets = contentfulSpaceService.getAllContentfulAssets(spaceName, accessToken, environment);
        JSONObject assetJson = new JSONObject(assets);
        contentfulNeo4jService.createAssetNode(assetJson.getJSONArray("items").toString());

        return "{result:success}";
    }
}
