package org.accounting.system.dtos.ams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceCatalogueMessage {
    private boolean active;
    private String auditState;
    private boolean draft;
    private String id;
    private Identifiers identifiers;
    private Object latestAuditInfo; // can be replaced with proper type if known
    private ActionInfo latestOnboardingInfo;
    private ActionInfo latestUpdateInfo;
    private List<ActionInfo> loggingInfo;
    private Metadata metadata;
    private Object migrationStatus; // can be replaced with proper type if known
    private ResourceExtras resourceExtras;
    private Service service;
    private String status;
    private boolean suspended;
    private Provider provider;

    @Getter
    @Setter
    public static class Identifiers {
        private String originalId;
        private String pid;
    }

    @Getter
    @Setter
    public static class ActionInfo {
        private String actionType;
        private String comment;
        private String date;
        private String type;
        private String userEmail;
        private String userFullName;
        private String userRole;
    }

    @Getter
    @Setter
    public static class Metadata {
        private String modifiedAt;
        private String modifiedBy;
        private boolean published;
        private String registeredAt;
        private String registeredBy;
        private List<String> terms;
    }

    @Getter
    @Setter
    public static class ResourceExtras {
        private String eoscIFGuidelines;
    }

    @Getter
    @Setter
    public static class Service {
        private String abbreviation;
        private List<String> accessModes;
        private String accessPolicy;
        private List<String> accessTypes;
        private Object alternativeIdentifiers;
        private String catalogueId;
        private List<Category> categories;
        private List<String> certifications;
        private List<String> changeLog;
        private String description;
        private List<String> fundingBody;
        private List<String> fundingPrograms;
        private List<String> geographicalAvailabilities;
        private List<String> grantProjectNames;
        private String helpdeskEmail;
        private String helpdeskPage;
        private boolean horizontalService;
        private String id;
        private List<String> languageAvailabilities;
        private String lastUpdate;
        private String lifeCycleStatus;
        private String logo;
        private Contact mainContact;
        private String maintenance;
        private List<String> marketplaceLocations;
        private Object multimedia;
        private String name;
        private String node;
        private List<String> openSourceTechnologies;
        private Object order;
        private String orderType;
        private String paymentModel;
        private String pricing;
        private String privacyPolicy;
        private List<Contact> publicContacts;
        private List<String> relatedPlatforms;
        private List<String> relatedResources;
        private List<String> requiredResources;
        private List<String> resourceGeographicLocations;
        private String resourceLevel;
        private String resourceOrganisation;
        private List<String> resourceProviders;
        private List<ScientificDomain> scientificDomains;
        private String securityContactEmail;
        private List<String> serviceCategories;
        private List<String> standards;
        private Object statusMonitoring;
        private String tagline;
        private List<String> tags;
        private List<String> targetUsers;
        private String termsOfUse;
        private Object trainingInformation;
        private String trl;
        private Object useCases;
        private String userManual;
        private String version;
        private String webpage;

        @Getter
        @Setter
        public static class Category {
            private String category;
            private String subcategory;
        }

        @Getter
        @Setter
        public static class Contact {
            private String email;
            private String firstName;
            private String lastName;
            private String organisation;
            private String phone;
            private String position;
        }

        @Getter
        @Setter
        public static class ScientificDomain {
            private String scientificDomain;
            private String scientificSubdomain;
        }
    }

    @Getter
    @Setter
    public class Provider {
        private String id;
        private String abbreviation;
        private String name;
        private String node;
        private String website;
        private boolean legalEntity;
        private String legalStatus;
        private String hostingLegalEntity;
        private Object alternativeIdentifiers;
        private String description;
        private String logo;
        private Object multimedia;
        private Object scientificDomains;
        private List<String> tags;
        private List<String> structureTypes;
        private Location location;
        private Contact mainContact;
        private List<Contact> publicContacts;
        private String lifeCycleStatus;
        private List<String> certifications;
        private List<String> participatingCountries;
        private List<String> affiliations;
        private List<String> networks;
        private String catalogueId;
        private List<String> esfriDomains;
        private String esfriType;
        private Object merilScientificDomains;
        private List<String> areasOfActivity;
        private List<String> societalGrandChallenges;
        private List<String> nationalRoadmaps;
        private List<User> users;
    }

    @Getter
    @Setter
    public static class Location {
        private String streetNameAndNumber;
        private String postalCode;
        private String city;
        private String region;
        private String country;
    }

    @Getter
    @Setter
    public static class Contact {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String position;
    }

    @Getter
    @Setter
    public static class User {
        private String id;
        private String email;
        private String name;
        private String surname;
    }
}
