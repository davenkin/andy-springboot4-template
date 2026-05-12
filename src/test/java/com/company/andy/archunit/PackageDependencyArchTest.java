package com.company.andy.archunit;

import com.fasterxml.jackson.annotation.*;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.company.andy", importOptions = DoNotIncludeTests.class)
class PackageDependencyArchTest {

    @ArchTest
    static final ArchRule business_classes_should_all_under_specific_packages = classes()
            .that()
            .resideInAnyPackage("..com.company.andy.feature..")
            .should()
            .resideInAnyPackage(
                    "..com.company.andy.feature..command..",
                    "..com.company.andy.feature..controller..",
                    "..com.company.andy.feature..domain..",
                    "..com.company.andy.feature..eventhandler..",
                    "..com.company.andy.feature..infrastructure..",
                    "..com.company.andy.feature..job..",
                    "..com.company.andy.feature..query.."
            )
            .because("We use the following packages to house all business classes: command, controller, domain, eventhandler, infrastructure, job, query.");

    @ArchTest
    static final ArchRule domain_classes_should_not_depend_on_outer_packages = noClasses()
            .that()
            .resideInAnyPackage("..com.company.andy.feature..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "..com.company.andy.feature..command..",
                    "..com.company.andy.feature..eventhandler..",
                    "..com.company.andy.feature..infrastructure..",
                    "..com.company.andy.feature..job..",
                    "..com.company.andy.feature..query..")
            .because("Domain package is most important part of the application and reside in the kernel of the architecture, it should only contain business logic and should not depend on other outer packages.");

    @ArchTest
    static final ArchRule map_struct_should_not_be_used = noClasses()
            .should()
            .dependOnClassesThat().resideInAPackage("org.mapstruct..")
            .because("We don't use MapStruct as we think manual mapper implementations are more straightforward and easier to debug, and also manual mappers do not take much effort.");

    @ArchTest
    static final ArchRule spring_data_repositories_should_not_be_used = noClasses()
            .should()
            .dependOnClassesThat().belongToAnyOf(MongoRepository.class, ListCrudRepository.class, ListPagingAndSortingRepository.class)
            .because("We don't use Spring Data's repository interfaces directly as it's too rigid for method names, instead we use AbstractMongoRepository as the base repository class and define our own repository interfaces for better flexibility and readability.");

    @ArchTest
    static final ArchRule usage_of_jackson_annotations_should_be_minimized = noClasses()
            .should()
            .dependOnClassesThat()
            .belongToAnyOf(
                    JsonProperty.class,
                    JsonAlias.class,
                    JsonIgnore.class,
                    JsonIgnoreProperties.class,
                    JsonAnyGetter.class,
                    JsonAnySetter.class,
                    JsonSerialize.class,
                    JsonDeserialize.class,
                    JsonFormat.class,
                    JsonView.class
            )
            .because("Jackson annotations make the code deeply coupled with the Jackson library, and also it might be bad for code navigability.");
}
