package tech.mangosoft.autolinkedin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./config/global.properties")
public class GlobalProperties {

    @Value("${email}")
    private String email;

    @Value("${linkedinSalesLink}")
    private String linkedinSalesLink;

    @Value("${linkedinLink}")
    private String linkedinLink;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedinSalesLink() {
        return linkedinSalesLink;
    }

    public void setLinkedinSalesLink(String linkedinSalesLink) {
        this.linkedinSalesLink = linkedinSalesLink;
    }

    public String getLinkedinLink() {
        return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
        this.linkedinLink = linkedinLink;
    }
}
