package org.accounting.system.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ServerErrorException;
import org.accounting.system.dtos.ams.AmsMessage;
import org.accounting.system.dtos.ams.ServiceCatalogueMessage;
import org.accounting.system.entities.installation.Installation;
import org.accounting.system.entities.provider.Provider;
import org.accounting.system.enums.AMSMessageStatus;
import org.accounting.system.repositories.installation.InstallationRepository;
import org.accounting.system.repositories.provider.ProviderRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;

@ApplicationScoped
public class AmsService {

    @Inject
    ProviderRepository providerRepository;

    @Inject
    ProjectService projectService;

    @Inject
    InstallationRepository installationRepository;

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

        var providerToBeSaved = new Provider();

        providerToBeSaved.setId(Base64.getUrlEncoder().withoutPadding().encodeToString(provider.getId().getBytes()));
        providerToBeSaved.setName(provider.getName());
        providerToBeSaved.setAbbreviation(provider.getAbbreviation());
        providerToBeSaved.setLogo(provider.getLogo());
        providerToBeSaved.setWebsite(provider.getWebsite());

        providerToBeSaved.setRegisteredOn(LocalDateTime.now());

        providerToBeSaved.setCreatorId("from-ams");

        providerRepository.persist(providerToBeSaved);

        projectService.associateProjectWithProviders(project, Set.of(providerToBeSaved.getId()));
    }

    private void createInstallation(ServiceCatalogueMessage.Service service){

        var installation = new Installation();
        installation.setProject(project);
        installation.setOrganisation(Base64.getUrlEncoder().withoutPadding().encodeToString(service.getResourceOrganisation().getBytes()));
        installation.setExternalId(service.getId());
        installation.setInfrastructure(service.getName());
        installation.setInstallation(service.getId());
        installation.setCreatorId("from-ams");

        var optional = installationRepository.exist(installation.getProject(), installation.getOrganisation(), installation.getInstallation());

        if(optional.isPresent()){

            return;
        }

        if (installationRepository.fetchInstallationByExternalId(installation.getProject(),  installation.getOrganisation(), installation.getExternalId()).isPresent()) {
            return;
        }

        installationRepository.saveForAms(installation);
    }
}
