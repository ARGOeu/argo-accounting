package org.accounting.system.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.Setting;
import org.accounting.system.enums.APISetting;
import org.accounting.system.repositories.modulators.AbstractAccessModulator;
import org.bson.types.ObjectId;

import java.util.Optional;

@ApplicationScoped
public class SettingRepository extends AbstractAccessModulator<Setting, String> {

    public Optional<Setting> findByKey(APISetting key) {
        return find("key", key).firstResultOptional();
    }

    public void saveOrUpdate(APISetting key, String value) {

        var setting = findByKey(key).orElseGet(() -> {
            var s = new Setting();
            s.setKey(key);
            s.setId(new ObjectId().toString());
            return s;
        });
        setting.setValue(value);
        persistOrUpdate(setting);
    }
}
