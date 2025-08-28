package org.accounting.system.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ServerErrorException;
import org.accounting.system.dtos.ams.AmsMessage;
import org.accounting.system.dtos.ams.ServiceCatalogueMessage;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
public class AmsService {
    private static final Logger LOG = Logger.getLogger(AmsService.class);

    public void consumeAmsMessage(AmsMessage message) {

        var mapper = new ObjectMapper();

        LOG.info("Consuming AMS message...");

        LOG.info("Message id : "+message.getMessage().getMessageId());

        LOG.info("AMS Subscription : "+message.getSubscription());

        LOG.info("Message data : "+message.getMessage().getData());

        var decodedData = new String(Base64.getDecoder().decode(message.getMessage().getData()), StandardCharsets.UTF_8);

        LOG.info("Decoded data : "+decodedData);

        try {
            var serviceCatalogueMessage = mapper.readValue(decodedData, ServiceCatalogueMessage.class);

            LOG.info("Service catalogue message id : "+serviceCatalogueMessage.getId());
            LOG.info("Latest on boarding info : "+serviceCatalogueMessage.getLatestOnboardingInfo().getActionType());
            LOG.info("Latest update info : "+serviceCatalogueMessage.getLatestUpdateInfo().getActionType());
        } catch (JsonProcessingException e) {
            throw new ServerErrorException(e.getMessage(), 500);
        }
    }
}
