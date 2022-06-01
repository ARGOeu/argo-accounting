//package org.accounting.system;
//
//import io.quarkus.test.junit.QuarkusTest;
//import org.accounting.system.entities.HierarchicalRelation;
//import org.accounting.system.entities.Metric;
//import org.accounting.system.repositories.HierarchicalRelationRepository;
//import org.apache.commons.lang3.StringUtils;
//import org.junit.jupiter.api.Test;
//
//import javax.inject.Inject;
//import java.util.Arrays;
//
//@QuarkusTest
//public class HierarchicalRelationTest {
//
//    @Inject
//    HierarchicalRelationRepository hierarchicalRelationRepository;
//
//    @Test
//    public void createHierarchicalRelation() {
//
//        firstRelationship();
//
//        secondRelationship();
//
//        thirdRelationship();
//
//        fourthRelationship();
//
//        fifthRelationship();
//
//        var projectRelation = hierarchicalRelationRepository.findByExternalId("project");
//
//        var installationRelation = hierarchicalRelationRepository.findByExternalId("installation1");
//
//        var relation = hierarchicalRelationRepository.findByPath("project.installation1");
//
//        print(projectRelation.toArray(new HierarchicalRelation[0]));
//
//        print(installationRelation.toArray(new HierarchicalRelation[0]));
//
//        print(relation);
//    }
//
//    private void firstRelationship(){
//
//        //first relationship project -> installation -> provider
//
//        // adding metrics to provider
//
//        Metric metric = new Metric();
//        metric.setValue(15);
//
//        Metric metric1 = new Metric();
//        metric1.setValue(18);
//
//        HierarchicalRelation project = new HierarchicalRelation("project");
//
//        HierarchicalRelation installation = new HierarchicalRelation("installation", project);
//
//        HierarchicalRelation provider = new HierarchicalRelation("provider", installation);
//
//        hierarchicalRelationRepository.save(project, null);
//        hierarchicalRelationRepository.save(installation, null);
//        hierarchicalRelationRepository.save(provider, metric);
//        hierarchicalRelationRepository.save(provider, metric1);
//    }
//
//    private void secondRelationship(){
//
//        // second relationship project -> installation -> provider1
//
//        HierarchicalRelation project = new HierarchicalRelation("project");
//
//        HierarchicalRelation installation = new HierarchicalRelation("installation", project);
//
//        HierarchicalRelation provider = new HierarchicalRelation("provider1", installation);
//
//        hierarchicalRelationRepository.save(project, null);
//        hierarchicalRelationRepository.save(installation, null);
//        hierarchicalRelationRepository.save(provider, null);
//    }
//
//    private void thirdRelationship(){
//
//        // third relationship project -> installation1
//
//        // adding metrics to installation1
//
//        Metric metric = new Metric();
//        metric.setValue(50);
//
//        HierarchicalRelation project = new HierarchicalRelation("project");
//
//        HierarchicalRelation installation = new HierarchicalRelation("installation1", project);
//
//        hierarchicalRelationRepository.save(project, null);
//        hierarchicalRelationRepository.save(installation, metric);
//    }
//
//    private void fourthRelationship(){
//
//        // fourth relationship project -> installation1 -> provider2
//
//        // adding metrics to provider2
//
//        Metric metric = new Metric();
//        metric.setValue(50);
//
//        HierarchicalRelation project = new HierarchicalRelation("project");
//
//        HierarchicalRelation installation = new HierarchicalRelation("installation1", project);
//
//        HierarchicalRelation provider = new HierarchicalRelation("provider2", installation);
//
//        hierarchicalRelationRepository.save(project, null);
//        hierarchicalRelationRepository.save(installation, null);
//        hierarchicalRelationRepository.save(provider, metric);
//    }
//
//    private void fifthRelationship(){
//
//        // fourth relationship project1 -> installation1 -> provider3
//
//        HierarchicalRelation project = new HierarchicalRelation("project1");
//
//        HierarchicalRelation installation = new HierarchicalRelation("installation1", project);
//
//        HierarchicalRelation provider = new HierarchicalRelation("provider3", installation);
//
//        hierarchicalRelationRepository.save(project, null);
//        hierarchicalRelationRepository.save(installation, null);
//        hierarchicalRelationRepository.save(provider, null);
//    }
//
//    private void print(HierarchicalRelation... relations){
//        System.out.println();
//        System.out.println("-------------- Printing Hierarchical Relationship ----------------");
//        System.out.println();
//
//        Arrays.stream(relations).forEach(rl->recursionPrint(rl, rl.externalId, StringUtils.SPACE));
//    }
//
//    private void recursionPrint(HierarchicalRelation relation, String id, String... space){
//
//        System.out.print(id);
//        if (!relation.metrics.isEmpty()) {
//            System.out.print(StringUtils.SPACE + "-> " + relation.metrics.toString());
//        }
//        System.out.println();
//
//        for (HierarchicalRelation relation1 : relation.hierarchicalRelations) {
//            recursionPrint(relation1, String.join("", space) + "|-- " + relation1.externalId, String.join("", space), StringUtils.SPACE);
//        }
//
//        if (relation.hierarchicalRelations.isEmpty()) {
//            return;
//        }
//    }
//}
