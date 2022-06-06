package org.accounting.system.repositories.installation;

import org.accounting.system.entities.installation.Installation;
import org.accounting.system.repositories.modulators.AccessControlModulator;
import org.bson.types.ObjectId;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InstallationAccessControlRepository extends AccessControlModulator<Installation, ObjectId> {

}
