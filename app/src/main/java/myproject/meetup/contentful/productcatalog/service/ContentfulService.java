package myproject.meetup.contentful.productcatalog.service;

import com.contentful.java.cma.CMAClient;
import com.contentful.java.cma.model.CMAArray;
import com.contentful.java.cma.model.CMAContentType;
import com.contentful.java.cma.model.CMAEntry;
import com.contentful.java.cma.model.CMALocale;
import com.contentful.java.cma.model.CMAOrganization;
import com.contentful.java.cma.model.CMASpace;
import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;

@Service
public class ContentfulService {

    private ContentfulProperties contentfulProperties;
    public static final int SPACE_NOT_EXISTS = -1;

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

    public Optional<CMASpace> getContentfulSpace(String name, String accessToken, String environment) {
        final CMAClient client = getCMAClient("", accessToken, environment);
        Optional<CMASpace> spaceOptional = client.spaces().fetchAll().getItems()
                .stream()
                .filter(s -> s.getName().equals(name))
                .findFirst();
        return spaceOptional;
    }

    public CMAArray<CMAContentType> getAllContentfulTypes(String spaceName, String accessToken, String environment) {
        final CMAClient client = getCMAClient(spaceName, accessToken, environment);
        return client.contentTypes().fetchAll();
    }

    private CMAClient getCMAClient(String spaceName, String accessToken, String environment) {
        CMAClient client = new CMAClient
                .Builder()
                .setAccessToken(accessToken)
                .setEnvironmentId(environment)
                .build();
        Optional<CMASpace> spaceOptional =
                client.spaces().fetchAll().getItems()
                                  .stream()
                                  .filter(s -> s.getName().equals(spaceName))
                                  .findFirst();
        if(spaceOptional.isPresent()) {
            return new CMAClient
                    .Builder()
                    .setAccessToken(accessToken)
                    .setSpaceId(spaceOptional.get().getId())
                    .setEnvironmentId(environment)
                    .build();
        } else {
            return client;
        }
    }

    private Optional<String> getContentfulSpaceIdByName(String name, CMAClient client) {
        Optional<CMASpace> spaceOptional = client.spaces().fetchAll().getItems()
                .stream()
                .filter(s -> s.getName().equals(name))
                .findFirst();
        return spaceOptional.map(CMASpace::getSpaceId);
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
        Optional<String> spaceOptional = this.getContentfulSpaceIdByName(name, client);
        if(spaceOptional.isPresent()) {
            return client.spaces().delete(spaceOptional.get());
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
            return Optional.of(client.spaces().create(name, cmaOrganizationOptional.get().getId()));
        } else {
            return Optional.empty();
        }
    }
}
