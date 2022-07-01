package org.accounting.system.enums;

import java.util.Set;

/**
 * The available Accounting System Collections
 */
public enum Collection {

    MetricDefinition{
        @Override
        public Set<Operation> availableOperations() {
            return Set.of(Operation.CREATE, Operation.DELETE, Operation.UPDATE, Operation.READ);
        }
    },
    Metric{
        @Override
        public Set<Operation> availableOperations() {
            return Set.of(Operation.CREATE, Operation.DELETE, Operation.UPDATE, Operation.READ);
        }
    },
    Role{
        @Override
        public Set<Operation> availableOperations() {
            return Set.of(Operation.CREATE, Operation.DELETE, Operation.UPDATE, Operation.READ);
        }
    },
    Project{
        @Override
        public Set<Operation> availableOperations() {
            return Set.of(Operation.ASSOCIATE, Operation.DISSOCIATE, Operation.REGISTER, Operation.READ, Operation.ACL);
        }
    },
    Provider{
        @Override
        public Set<Operation> availableOperations() {
            return Set.of(Operation.CREATE, Operation.DELETE, Operation.UPDATE, Operation.READ, Operation.ACL);
        }
    },
    Installation{
        @Override
        public Set<Operation> availableOperations() {
            return Set.of(Operation.CREATE, Operation.DELETE, Operation.UPDATE, Operation.READ, Operation.ACL);
        }
    },
    Client{
        @Override
        public Set<Operation> availableOperations() {
            return Set.of(Operation.READ, Operation.ASSIGN_ROLE, Operation.DETACH_ROLE);
        }
    };

    public abstract Set<Operation> availableOperations();
}
