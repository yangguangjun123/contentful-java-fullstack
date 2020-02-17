package myproject.contentful.productcatalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SuppressWarnings("unused")
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "contentful")
public class ContentfulProperties {

    private String spaceName;
    private String organisationName;
    private String deliveryAccessToken;
    private String managementAccessToken;
    private String spaceEnvironment;

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getDeliveryAccessToken() {
        return deliveryAccessToken;
    }

    public void setDeliveryAccessToken(String deliveryAccessToken) {
        this.deliveryAccessToken = deliveryAccessToken;
    }

    public String getManagementAccessToken() {
        return managementAccessToken;
    }

    public void setManagementAccessToken(String managementAccessToken) {
        this.managementAccessToken = managementAccessToken;
    }

    public String getSpaceEnvironment() {
        return spaceEnvironment;
    }

    public void setSpaceEnvironment(String spaceEnvironment) {
        this.spaceEnvironment = spaceEnvironment;
    }
}
