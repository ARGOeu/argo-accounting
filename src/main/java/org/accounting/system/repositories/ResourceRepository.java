package org.accounting.system.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import org.accounting.system.entities.Resource;
import org.accounting.system.repositories.modulators.AccessibleModulator;

@ApplicationScoped
public class ResourceRepository extends AccessibleModulator<Resource, String> {
}
