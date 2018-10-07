package myproject.meetup.contentful.productcatalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:contentful.properties")
@ConfigurationProperties
public class ContentfulProperties {

    @Value("${contentful.productcatalogue.space.id}")
    private String productCatalogueSpaceId;

    @Value("${contentful.productcatalogue.space.name}")
    private String productCatalogueSpaceName;

    @Value("${contentful.productcatalogue.delivery.access.token}")
    private String productCatalogueDeliveryAccessToken;

    @Value("${contentful.workshop.space.id}")
    private String workshopSpaceId;

    @Value("${contentful.workshop.space.name}")
    private String workshopSpaceName;

    @Value("${contentful.workshop.delivery.access.token}")
    private String workshopDeliveryAccessToken;

    @Value("${contentful.workshop.management.access.token}")
    private String workshopManagementAccessToken;

    public String getProductCatalogueSpaceId() {
        return productCatalogueSpaceId;
    }

    public void setProductCatalogueSpaceId(String sourceSpaceId) {
        this.productCatalogueSpaceId = sourceSpaceId;
    }

    public String getProductCatalogueDeliveryAccessToken() {
        return productCatalogueDeliveryAccessToken;
    }

    public void setProductCatalogueDeliveryAccessToken(String sourceDeliveryAccessToken) {
        this.productCatalogueDeliveryAccessToken = sourceDeliveryAccessToken;
    }

    public String getWorkshopSpaceId() {
        return workshopSpaceId;
    }

    public void setWorkshopSpaceId(String workshopSpaceId) {
        this.workshopSpaceId = workshopSpaceId;
    }

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

    public String getProductCatalogueSpaceName() {
        return productCatalogueSpaceName;
    }

    public void setProductCatalogueSpaceName(String productCatalogueSpaceName) {
        this.productCatalogueSpaceName = productCatalogueSpaceName;
    }

    public String getWorkshopSpaceName() {
        return workshopSpaceName;
    }

    public void setWorkshopSpaceName(String workshopSpaceName) {
        this.workshopSpaceName = workshopSpaceName;
    }
}
