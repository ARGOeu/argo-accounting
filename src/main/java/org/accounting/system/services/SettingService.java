package org.accounting.system.services;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.UriInfo;
import org.accounting.system.dtos.SettingMetadata;
import org.accounting.system.dtos.pagination.PageResource;
import org.accounting.system.entities.Setting;
import org.accounting.system.entities.projections.MongoQuery;
import org.accounting.system.enums.APISetting;
import org.accounting.system.repositories.SettingRepository;
import org.accounting.system.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class SettingService {

    @Inject
    SettingRepository settingRepository;

    @Inject
    Utility utility;

    public PageResource<SettingMetadata> getMetadata(int page, int size, UriInfo uriInfo) {
        List<SettingMetadata> list = new ArrayList<>();

        for (APISetting key : APISetting.values()) {
            String currentValue = settingRepository.findByKey(key)
                    .map(Setting::getValue)
                    .orElse(key.getDefaultValue());

            list.add(new SettingMetadata(
                    key.name(),
                    currentValue,
                    key.getDefaultValue(),
                    key.getAllowedValues()
            ));
        }

        var partition = utility.partition(list, size);

        var settings = partition.get(page) == null ? Collections.EMPTY_LIST : partition.get(page);

        var pageable = new MongoQuery<SettingMetadata>();

        pageable.list = settings;
        pageable.index = page;
        pageable.size = size;
        pageable.count = list.size();
        pageable.page = Page.of(page, size);

        return new PageResource<>(pageable, pageable.list, uriInfo);
    }

    public void update(APISetting key, String value) {
        if (!key.isValidValue(value)) {
            throw new BadRequestException("Invalid value for setting " + key);
        }

        settingRepository.saveOrUpdate(key, value);
    }
}
