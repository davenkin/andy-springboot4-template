package com.company.andy.archunit;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.company.andy", importOptions = DoNotIncludeTests.class)
class PackageDependencyArchTest {

    @ArchTest
    static final ArchRule businessClassesShouldAllUnderSpecificPackages = classes()
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
    static final ArchRule domainClassesShouldNotDependOnOuterPackages = noClasses()
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
    static final ArchRule mapStructShouldNotBeUsed = noClasses()
            .should()
            .dependOnClassesThat().resideInAPackage("org.mapstruct..")
            .because("We don't MapStruct as we think manual mapper implementations are more straightforward and easier to debug, and also manual mappers do not take much effort.");

    @ArchTest
    static final ArchRule springDataRepositoriesShouldNotBeUsed = noClasses()
            .should()
            .dependOnClassesThat().belongToAnyOf(MongoRepository.class, ListCrudRepository.class, ListPagingAndSortingRepository.class)
            .because("We don't use Spring Data's repository interfaces directly as it's too rigid for method names, instead we use AbstractMongoRepository as the base repository class and define our own repository interfaces for better flexibility and readability.");
}
