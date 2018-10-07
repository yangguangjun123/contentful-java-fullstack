package myproject.meetup.contentful.productcatalog.service;

import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAEntry;
import com.contentful.java.cma.model.CMALocale;
import com.contentful.java.cma.model.CMASpace;
import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class ContentfulService {

    private ContentfulProperties contentfulProperties;

    @Autowired
    public ContentfulService(ContentfulProperties contentfulProperties) {
        this.contentfulProperties = contentfulProperties;
    }

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
    }

    public CMASpace getContentfulSpace(String spaceId, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceId, accessToken, environment);
        return client.spaces().fetchOne(spaceId);
    }

    public CMAArray<CMAContentType> getAllContentfulTypes(String spaceId, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceId, accessToken, environment);
        return client.contentTypes().fetchAll();
    }

    private CMAClient getCMAClient(String spaceId, String accessToken, String environment) {
        return new CMAClient
                .Builder()
                .setAccessToken(accessToken)
                .setSpaceId(spaceId)
                .setEnvironmentId(environment)
                .build();
    }

    public CMAArray<CMAEntry> getAllContentfulEntries(String spaceId, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceId, accessToken, environment);
        return client.entries().fetchAll();
    }

    public CMAArray<CMALocale> getAllContentfulLocales(String spaceId, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceId, accessToken, environment);
        return client.locales().fetchAll();
    }

    public Integer deleteContentfulSpace(String spaceId, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceId, accessToken, environment);
        CMASpace space = client.spaces().fetchOne(spaceId);
        return client.spaces().delete(space);
    }

    public CMASpace createContentfulSpace(String spaceId, String accessToken, String environment, String name) {
        final CMAClient client = getCMAClient(spaceId, accessToken, environment);
        return client.spaces().create(name);

    }
}
