package myproject.contentful.productcatalog.service;

import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAAsset;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAEntry;
import com.contentful.java.cma.model.CMALocale;
import com.contentful.java.cma.model.CMAOrganization;
import com.contentful.java.cma.model.CMASpace;
import myproject.contentful.productcatalog.config.ContentfulProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ContentfulService {

    private final ConcurrentMap<String, String> cachedSpaceId;
    private final ContentfulProperties contentfulProperties;

    public static final int SUCCESS = 204;
    public static final int SPACE_NOT_EXISTS = -1;

    @Autowired
    public ContentfulService(ContentfulProperties contentfulProperties) {
        this.contentfulProperties = contentfulProperties;
        cachedSpaceId = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        CMAClient client = new CMAClient
                .Builder()
                .setAccessToken(contentfulProperties.getManagementAccessToken())
                .setEnvironmentId(contentfulProperties.getSpaceEnvironment())
                .build();
        client.spaces().fetchAll().getItems()
                .forEach(s -> cachedSpaceId.put(s.getName(), s.getId()));
    }

    @SuppressWarnings("unused")
    @PreDestroy
    public void destroy() {
    }

    public Optional<CMASpace> getContentfulSpace(String name, String accessToken, String environment) {
        final CMAClient client = getCMAClient("", accessToken, environment);
        return client.spaces().fetchAll().getItems()
                .stream()
                .filter(s -> s.getName().equals(name))
                .findFirst();
    }

    public CMAArray<CMAContentType> getAllContentfulTypes(String spaceName, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceName, accessToken, environment);
        return client.contentTypes().fetchAll();
    }

    private CMAClient getCMAClient(String spaceName, String accessToken, String environment) {
        if(!cachedSpaceId.containsKey(spaceName)) {
            init();
        }
        if(cachedSpaceId.containsKey(spaceName)) {
            return new CMAClient
                    .Builder()
                    .setAccessToken(accessToken)
                    .setSpaceId(cachedSpaceId.get(spaceName))
                    .setEnvironmentId(environment)
                    .build();
        } else {
            return new CMAClient
                    .Builder()
                    .setAccessToken(accessToken)
                    .setEnvironmentId(environment)
                    .build();
        }
    }

    private Optional<String> getContentfulSpaceIdByName(String name) {
//        Optional<CMASpace> spaceOptional = client.spaces().fetchAll().getItems()
//                .stream()
//                .filter(s -> s.getName().equals(name))
//                .findFirst();
//        return spaceOptional.map(CMASpace::getSpaceId);
        return Optional.of(cachedSpaceId.get(name));
    }

    public CMAArray<CMAEntry> getAllContentfulEntries(String spaceName, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceName, accessToken, environment);
        return client.entries().fetchAll();
    }

    public CMAArray<CMALocale> getAllContentfulLocales(String spaceName, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceName, accessToken, environment);
        return client.locales().fetchAll();
    }

    public Integer deleteContentfulSpace(String name, String accessToken, String environment) {
        final CMAClient client = getCMAClient("", accessToken, environment);
        Optional<String> spaceOptional = this.getContentfulSpaceIdByName(name);
        if(spaceOptional.isPresent()) {
            Integer result = client.spaces().delete(spaceOptional.get());
            cachedSpaceId.remove(name);
            return result;
        } else {
            return SPACE_NOT_EXISTS;
        }
    }

    public Optional<CMASpace> createContentfulSpace(String accessToken, String environment, String name,
                                          String organisation) {
        final CMAClient client = getCMAClient("", accessToken, environment);
        final CMAArray<CMAOrganization> organizations = client
                .organizations()
                .fetchAll();
        Optional<CMAOrganization> cmaOrganizationOptional =
                organizations.getItems().stream()
                                        .filter(o -> o.getName().equals(organisation))
                                        .findFirst();
        if(cmaOrganizationOptional.isPresent()) {
            CMASpace space = client.spaces().create(name, cmaOrganizationOptional.get().getId());
            cachedSpaceId.put(space.getName(), space.getId());
            return Optional.of(space);
        } else {
            return Optional.empty();
        }
    }

    public CMAArray<CMAAsset> getAllContentfulAssets(String spaceName, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceName, accessToken, environment);
        return client.assets().fetchAll();
    }
}
