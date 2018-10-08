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

import java.util.Optional;

@RestController
@RequestMapping("/contentful")
public class ContentfulController {

    private ContentfulService contentfulSpaceService;
    private static final int SUCCESS = 204;

    @Autowired
    public ContentfulController(ContentfulService contentfulSpaceService) {
        this.contentfulSpaceService = contentfulSpaceService;
    }

    @RequestMapping(path = "/space/get/objectKey/{spaceName}/{accessToken}/{environment}", method= RequestMethod.GET)
    public String getSpaceByName(@PathVariable String spaceName, @PathVariable String accessToken,
                               @PathVariable String  environment) {
        Optional<CMASpace> spaceOptional = contentfulSpaceService.getContentfulSpace(spaceName, accessToken, environment);
        JSONObject response = new JSONObject();
        if(spaceOptional.isPresent()) {
            response.put("id", spaceOptional.get().getId());
            response.put("name", spaceOptional.get().getName());
        } else {
            response.put("id", "");
            response.put("name", "");
        }
        return response.toString();
    }

    @RequestMapping(path = "/space/delete/objectKey/{spaceName}/{accessToken}/{environment}",
            method= RequestMethod.DELETE)
    public String deleteSpaceByName(@PathVariable String spaceName, @PathVariable String accessToken,
                               @PathVariable String  environment) {
        Integer code = contentfulSpaceService.deleteContentfulSpace(spaceName, accessToken, environment);
        JSONObject response = new JSONObject();
        if(code == SUCCESS || code == ContentfulService.SPACE_NOT_EXISTS) {
            response.put("result", "success");
        } else {
            response.put("result", "fail");
            response.put("erorCode", code);
        }
        return response.toString();
    }

    @RequestMapping(path = "/space/create/objectKey/{spaceName}/{accessToken}/{environment}",
                            method= RequestMethod.POST)
    public String createSpace(@PathVariable String spaceName, @PathVariable String accessToken,
                              @PathVariable String  environment, @RequestParam String organisation) {
        Optional<CMASpace> spaceOptional = contentfulSpaceService.createContentfulSpace(accessToken, environment,
                spaceName,  organisation);
        JSONObject response = new JSONObject();
        if(spaceOptional.isPresent()) {
            response.put("id", spaceOptional.get().getId());
            response.put("name", spaceOptional.get().getName());
        } else {
            response.put("id", "");
            response.put("name", "");
        }
        return response.toString();
    }


    @RequestMapping(path = "/contenttype/get/objectKey/{spaceName}/{accessToken}/{environment}",
                            method= RequestMethod.GET)
    public String getAllContentTypes(@PathVariable String spaceName, @PathVariable String accessToken,
                               @PathVariable String  environment) {
        CMAArray<CMAContentType> types = contentfulSpaceService.getAllContentfulTypes(spaceName,
                accessToken, environment);
        JSONObject obj = new JSONObject(types);
        JSONObject response = new JSONObject();
        response.put("contentType", obj.getJSONArray("items"));
        return response.toString();
    }

    @RequestMapping(path = "/contententry/get/objectKey/{spaceName}/{accessToken}/{environment}",
            method= RequestMethod.GET)
    public String getAllContentEntries(@PathVariable String spaceName, @PathVariable String accessToken,
                                     @PathVariable String  environment) {
        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(spaceName,
                accessToken, environment);
        JSONObject obj = new JSONObject(entries);
        JSONObject response = new JSONObject();
        response.put("contentEntry", obj.getJSONArray("items"));
        return response.toString();
    }

    @RequestMapping(path = "/contentasset/get/objectKey/{spaceName}/{accessToken}/{environment}",
            method= RequestMethod.GET)
    public String getAllContentAssets(@PathVariable String spaceName, @PathVariable String accessToken,
                                       @PathVariable String  environment) {
        CMAArray<CMAEntry> entries = contentfulSpaceService.getAllContentfulEntries(spaceName,
                accessToken, environment);
        JSONObject obj = new JSONObject(entries);
        JSONObject response = new JSONObject();
        response.put("contentAsset", obj.getJSONArray("items"));
        return response.toString();
    }

    @RequestMapping(path = "/contentlocale/get/objectKey/{spaceName}/{accessToken}/{environment}",
            method= RequestMethod.GET)
    public String getAllContentLocales(@PathVariable String spaceName, @PathVariable String accessToken,
                                      @PathVariable String  environment) {
        CMAArray<CMALocale> entries = contentfulSpaceService.getAllContentfulLocales(spaceName,
                accessToken, environment);
        JSONObject obj = new JSONObject(entries);
        JSONObject response = new JSONObject();
        response.put("contentLocale", obj.getJSONArray("items"));
        return response.toString();
    }
}
