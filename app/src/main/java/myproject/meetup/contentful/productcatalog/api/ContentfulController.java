package myproject.meetup.contentful.productcatalog.api;

import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAEntry;
import com.contentful.java.cma.model.CMALocale;
import com.contentful.java.cma.model.CMASpace;
import myproject.meetup.contentful.productcatalog.service.ContentfulService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contentful")
public class ContentfulController {

    private ContentfulService contentfulSpaceService;
    private static final int SUCCESS = 204;

    @Autowired
    public ContentfulController(ContentfulService contentfulSpaceService) {
        this.contentfulSpaceService = contentfulSpaceService;
    }

    @RequestMapping(path = "/space/get/objectKey/{spaceId}/{accessToken}/{environment}", method= RequestMethod.GET)
    public String getSpaceById(@PathVariable String spaceId, @PathVariable String accessToken,
                               @PathVariable String  environment) {
        CMASpace space = contentfulSpaceService.getContentfulSpace(spaceId, accessToken, environment);
        JSONObject response = new JSONObject();
        response.put("id", space.getId());
        response.put("name", space.getName());
        return response.toString();
    }

    @RequestMapping(path = "/space/delete/objectKey/{spaceId}/{accessToken}/{environment}",
            method= RequestMethod.DELETE)
    public String deleteSpaceById(@PathVariable String spaceId, @PathVariable String accessToken,
                               @PathVariable String  environment) {
        Integer code = contentfulSpaceService.deleteContentfulSpace(spaceId, accessToken, environment);
        JSONObject response = new JSONObject();
        if(code == SUCCESS) {
            response.put("result", "success");
        } else {
            response.put("result", "fail");
            response.put("erorCode", code);
        }
        return response.toString();
    }

    @RequestMapping(path = "/space/create/objectKey/{spaceId}/{accessToken}/{environment}", method= RequestMethod.POST)
    public String createSpace(@PathVariable String spaceId, @PathVariable String accessToken,
                              @PathVariable String  environment, @RequestParam String name) {
        CMASpace space = contentfulSpaceService.createContentfulSpace(spaceId, accessToken, environment, name);
        JSONObject response = new JSONObject();
        response.put("id", space.getId());
        response.put("name", space.getName());
        return response.toString();
    }


    @RequestMapping(path = "/contenttype/get/objectKey/{spaceId}/{accessToken}/{environment}",
                            method= RequestMethod.GET)
    public String getAllContentTypes(@PathVariable String spaceId, @PathVariable String accessToken,
                               @PathVariable String  environment) {
        CMAArray<CMAContentType> types = contentfulSpaceService.getAllContentfulTypes(spaceId,
                accessToken, environment);
        JSONObject obj = new JSONObject(types);
        JSONObject response = new JSONObject();
        response.put("contentType", obj.getJSONArray("items"));
        return response.toString();
    }

    @RequestMapping(path = "/contententry/get/objectKey/{spaceId}/{accessToken}/{environment}",
            method= RequestMethod.GET)
    public String getAllContentEntries(@PathVariable String spaceId, @PathVariable String accessToken,
                                     @PathVariable String  environment) {
        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(spaceId,
                accessToken, environment);
        JSONObject obj = new JSONObject(entries);
        JSONObject response = new JSONObject();
        response.put("contentEntry", obj.getJSONArray("items"));
        return response.toString();
    }

    @RequestMapping(path = "/contentasset/get/objectKey/{spaceId}/{accessToken}/{environment}",
            method= RequestMethod.GET)
    public String getAllContentAssets(@PathVariable String spaceId, @PathVariable String accessToken,
                                       @PathVariable String  environment) {
        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(spaceId,
                accessToken, environment);
        JSONObject obj = new JSONObject(entries);
        JSONObject response = new JSONObject();
        response.put("contentAsset", obj.getJSONArray("items"));
        return response.toString();
    }

    @RequestMapping(path = "/contentlocale/get/objectKey/{spaceId}/{accessToken}/{environment}",
            method= RequestMethod.GET)
    public String getAllContentLocales(@PathVariable String spaceId, @PathVariable String accessToken,
                                      @PathVariable String  environment) {
        CMAArray<CMALocale> entries = contentfulSpaceService.getAllContentfulLocales(spaceId,
                accessToken, environment);
        JSONObject obj = new JSONObject(entries);
        JSONObject response = new JSONObject();
        response.put("contentLocale", obj.getJSONArray("items"));
        return response.toString();
    }
}
