package org.accounting.system.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ServerErrorException;
import org.accounting.system.dtos.ams.AmsMessage;
import org.accounting.system.dtos.ams.ServiceCatalogueMessage;
import org.accounting.system.dtos.installation.InstallationRequestDto;
import org.accounting.system.dtos.provider.ProviderRequestDto;
import org.accounting.system.enums.AMSMessageStatus;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.accounting.system.services.installation.InstallationService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@ApplicationScoped
public class AmsService {

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ProjectService projectService;

    @Inject
    ProviderService providerService;

    @Inject
    InstallationService installationService;


    @ConfigProperty(name = "api.accounting.ams.project")
    String project;

    private static final Logger LOG = Logger.getLogger(AmsService.class);

    public void consumeAmsMessage(AmsMessage message) {

        var toBeUsed = projectService.findByIdOptional(project);

        if(toBeUsed.isEmpty()){

            LOG.error("There is no Project with id : "+project+" to which the AMS messages can be associated.");
            throw new ServerErrorException("There is no Project with id : "+project+" to which the AMS messages can be associated.", 500);
        }

        var mapper = new ObjectMapper();

        LOG.info("Consuming AMS message...");

        LOG.info("Message id : "+message.getMessage().getMessageId());

        LOG.info("AMS Subscription : "+message.getSubscription());

        var decodedData = new String(Base64.getDecoder().decode(message.getMessage().getData()), StandardCharsets.UTF_8);

        LOG.info("Decoded data : "+decodedData);

        try {
            var serviceCatalogueMessage = mapper.readValue(decodedData, ServiceCatalogueMessage.class);

            if(Objects.isNull(serviceCatalogueMessage.getStatus())){

                LOG.error("Service catalogue message status is empty.");
                throw new BadRequestException("Service catalogue message status is empty.");
            } else if(serviceCatalogueMessage.getStatus().equals(AMSMessageStatus.CREATE_PROVIDER.getValue())){

                LOG.info("Creating a new Provider with id : "+serviceCatalogueMessage.getProvider().getId());
                createProvider(serviceCatalogueMessage.getProvider());
            } else if(serviceCatalogueMessage.getStatus().equals(AMSMessageStatus.CREATE_INSTALLATION.getValue())){

                LOG.info("Creating a new Installation with id : "+serviceCatalogueMessage.getService().getId());
                createInstallation(serviceCatalogueMessage.getService());
            } else {

                LOG.error("Unknown service catalogue status : "+serviceCatalogueMessage.getStatus());
                throw new BadRequestException("Unknown service catalogue status : "+serviceCatalogueMessage.getStatus());
            }

            LOG.info("Service catalogue message id : "+serviceCatalogueMessage.getId());
        } catch (JsonProcessingException e) {
            throw new ServerErrorException(e.getMessage(), 500);
        }
    }

    private void createProvider(ServiceCatalogueMessage.Provider provider){

        var toBeSaved = new ProviderRequestDto();
        toBeSaved.name = provider.getName();
        toBeSaved.abbreviation = provider.getAbbreviation();
        toBeSaved.logo = provider.getLogo();
        toBeSaved.website = provider.getWebsite();
        toBeSaved.externalId = provider.getId();

        var response = providerService.save(toBeSaved, "from-ams");

        projectService.associateProjectWithProvider(project, response.id);
    }

    private void createInstallation(ServiceCatalogueMessage.Service service){

        var provider = providerRepository.findByExternalId(service.getResourceOrganisation());

        var toBeSaved = new InstallationRequestDto();
        toBeSaved.installation = service.getId();
        toBeSaved.infrastructure = service.getName();
        toBeSaved.externalId = service.getId();
        toBeSaved.organisation = provider.get().getId();
        toBeSaved.project = project;

        installationService.save(toBeSaved, "from-ams");
    }
}
