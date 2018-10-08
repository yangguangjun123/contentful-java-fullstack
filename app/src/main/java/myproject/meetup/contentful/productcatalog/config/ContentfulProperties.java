package myproject.meetup.contentful.productcatalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:contentful.properties")
@ConfigurationProperties
public class ContentfulProperties {

    @Value("${contentful.workshop.space.name}")
    private String workshopSpaceName;

    @Value("${contentful.workshop.organisation.name}")
    private String workshopOrganisationName;

    @Value("${contentful.workshop.delivery.access.token}")
    private String workshopDeliveryAccessToken;

    @Value("${contentful.workshop.management.access.token}")
    private String workshopManagementAccessToken;

    public String getWorkshopDeliveryAccessToken() {
        return workshopDeliveryAccessToken;
    }

    public void setWorkshopDeliveryAccessToken(String workshopDeliveryAccessToken) {
        this.workshopDeliveryAccessToken = workshopDeliveryAccessToken;
    }

    public String getWorkshopManagementAccessToken() {
        return workshopManagementAccessToken;
    }

    public void setWorkshopManagementAccessToken(String workshopManagementAccessToken) {
        this.workshopManagementAccessToken = workshopManagementAccessToken;
    }

    public String getWorkshopSpaceName() {
        return workshopSpaceName;
    }

    public void setWorkshopSpaceName(String workshopSpaceName) {
        this.workshopSpaceName = workshopSpaceName;
    }

    public String getWorkshopOrganisationName() {
        return workshopOrganisationName;
    }

    public void setWorkshopOrganisationName(String workshopOrganisationName) {
        this.workshopOrganisationName = workshopOrganisationName;
    }
}
