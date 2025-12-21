package com.company.andy.archunit;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.company.andy", importOptions = DoNotIncludeTests.class)
class PackageDependencyArchTest {

    @ArchTest
    static final ArchRule commonClassesNotDependOnBusinessPackages = noClasses()
            .that()
            .resideInAnyPackage("..com.company.andy.common..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "..com.company.andy.business..")
            .because("The common package is shard by all businesses, it should not depend on any specific business packages.");

    @ArchTest
    static final ArchRule businessClassesShouldAllUnderSpecificPackages = classes()
            .that()
            .resideInAnyPackage("..com.company.andy.business..")
            .should()
            .resideInAnyPackage(
                    "..com.company.andy.business..command..",
                    "..com.company.andy.business..controller..",
                    "..com.company.andy.business..domain..",
                    "..com.company.andy.business..eventhandler..",
                    "..com.company.andy.business..infrastructure..",
                    "..com.company.andy.business..job..",
                    "..com.company.andy.business..query.."
            )
            .because("We use the following packages to house all business classes: command, controller, domain, eventhandler, infrastructure, job, query.");

    @ArchTest
    static final ArchRule domainClassesShouldNotDependOnOuterPackages = noClasses()
            .that()
            .resideInAnyPackage("..com.company.andy.business..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "..com.company.andy.business..command..",
                    "..com.company.andy.business..eventhandler..",
                    "..com.company.andy.business..infrastructure..",
                    "..com.company.andy.business..job..",
                    "..com.company.andy.business..query..")
            .because("Domain package is most important part of the application and reside in the kernel of the architecture, it should only contain business logic and  should not depend on other outer packages.");

}
