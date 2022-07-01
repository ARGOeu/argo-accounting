package org.accounting.system.repositories.modulators;

import org.accounting.system.entities.Entity;
import org.accounting.system.entities.acl.AccessControl;

/**
 * This {@link AccessControlModulator} provides access only to entities
 * explicitly declared in the {@link AccessControl} collection.
 *
 * @param <E> Generic class that represents a mongo collection.
 */
public abstract class AccessControlModulator<E extends Entity, I, A extends AccessControl> extends AccessModulator<E, I, A> {

//    @Override
//    public E fetchEntityById(I id) {
//
//        var optional = getAccessControl(id, AccessControlPermission.READ);
//
//        if(optional.isPresent()){
//            return findById(id);
//        } else {
//            return super.fetchEntityById(id);
//        }
//    }
//
//    @Override
//    public boolean deleteEntityById(I id) {
//        var optional = getAccessControl(id, AccessControlPermission.DELETE);
//
//        if(optional.isPresent()){
//            return deleteById(id);
//        } else {
//            return super.deleteEntityById(id);
//        }
//    }
//
//    @Override
//    public E updateEntity(E entity, I id) {
//        var optional = getAccessControl(id, AccessControlPermission.UPDATE);
//        if(optional.isPresent()){
//            update(entity);
//        } else {
//            return super.updateEntity(entity, id);
//        }
//        return entity;
//    }
//
//    @Override
//    public List<E> getAllEntities() {
//
//        List<String> entities = getAccessControlEntities();
//
//        return list("_id in ?1", Utility.transformIdsToSpecificClassType(getIdentity(), entities));
//    }
//
//    @Override
//    public <T> T lookUpEntityById(String from, String localField, String foreignField, String as, Class<T> projection, I id) {
//
//        var optional = getAccessControl(id, AccessControlPermission.READ);
//
//        if(optional.isPresent()){
//            Bson bson = Aggregates.lookup(from, localField, foreignField, as);
//            List<T> projections = getMongoCollection().aggregate(List.of(bson, Aggregates.match(Filters.eq("_id", id))), projection).into(new ArrayList<>());
//            return projections.get(0);
//
//        } else {
//            return super.lookUpEntityById(from, localField, foreignField, as, projection, id);
//        }
//    }

//    @Override
//    public void grantPermission(A accessControl) {
//
//        var optional = getAccessControl(accessControl.getEntity(), AccessControlPermission.ACL);
//
//        if(optional.isPresent()){
//            getAccessControlRepository().persist(accessControl);
//        } else {
//            super.modifyPermission(accessControl);
//        }
//
//        super.grantPermission(accessControl);
//    }
//
//    @Override
//    public void modifyPermission(A accessControl) {
//
//        var optional = getAccessControl(accessControl.getEntity(), AccessControlPermission.ACL);
//
//        if(optional.isPresent()){
//            getAccessControlRepository().update(accessControl);
//        } else {
//            super.modifyPermission(accessControl);
//        }
//    }
//
//    @Override
//    public void deletePermission(A accessControl) {
//
//        var optional = getAccessControl(accessControl.getEntity(), AccessControlPermission.ACL);
//
//        if(optional.isPresent()){
//            getAccessControlRepository().delete(accessControl);
//        } else {
//            super.deletePermission(accessControl);
//        }
//    }
//
//    @Override
//    public A getPermission(String entity, String who) {
//
//        var optional = getAccessControl(entity, AccessControlPermission.ACL);
//
//        if(optional.isPresent()){
//            return getAccessControlRepository().findByWhoAndCollectionAndEntity(who, collection(), entity);
//        } else {
//            return super.getPermission(entity, who);
//        }
//    }
//
//    public <ID> Optional<A> getAccessControl(ID id, AccessControlPermission permission){
//
//        return getAccessControlRepository().findByWhoAndCollectionAndEntityAndPermission(getRequestInformation().getSubjectOfToken(), collection(), id.toString(), permission);
//    }
//
//    /**
//     * This method returns all entity ids from current Collection to which the current User has access.
//     */
//    public List<String> getAccessControlEntities(){
//
//        List<A> accessControlList = getAccessControlRepository().findAllByWhoAndCollection(getRequestInformation().getSubjectOfToken(), collection(), AccessControlPermission.READ);
//
//        return accessControlList
//                .stream()
//                .map(AccessControl::getEntity)
//                .collect(Collectors.toList());
//    }
}