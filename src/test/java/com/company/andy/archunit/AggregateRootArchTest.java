package com.company.andy.archunit;

import com.company.andy.common.model.AggregateRoot;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.annotation.TypeAlias;

import static com.company.andy.archunit.util.ArchUnitUtils.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.company.andy.business", importOptions = DoNotIncludeTests.class)
class AggregateRootArchTest {

    @ArchTest
    static final ArchRule aggregateRootShouldResideInDomainPackage = classes()
            .that()
            .areAssignableTo(AggregateRoot.class)
            .should()
            .resideInAnyPackage("com.company.andy.business..domain")
            .because("Aggregate root should located directly under domain package.");

    @ArchTest
    static final ArchRule concreteAggregateRootShouldBeAnnotatedWithTypeAlias = classes()
            .that()
            .areAssignableTo(AggregateRoot.class)
            .and(areConcreteClasses())
            .should()
            .beAnnotatedWith(TypeAlias.class)
            .because("Concrete aggregate roots should be annotated with @TypeAlias as otherwise the class FQCN will be used as type information and stored in database, which does not survive repackaging.");

    @ArchTest
    static final ArchRule concreteAggregateRootShouldHavePrivateNoArgConstructor = classes()
            .that()
            .areAssignableTo(AggregateRoot.class)
            .and(areConcreteClasses())
            .should(havePrivateNoArgConstructor())
            .because("Private no-arg constructors of aggregate roots are only used for deserialization, it should not be used for manual aggregate root creation because otherwise we might end up with invalid aggregate roots. You may use @NoArgsConstructor(access = PRIVATE) for private constructors.");

    @ArchTest
    static final ArchRule aggregateRootShouldHaveNonPublicNoArgConstructor = classes()
            .that()
            .areAssignableTo(AggregateRoot.class)
            .should(haveNonPublicNoArgConstructor())
            .because("Non-public no-arg constructors of aggregate roots are only used for deserialization, it should not be used for manual aggregate root creation because otherwise we might end up with invalid aggregate roots. You may use @NoArgsConstructor(access = PRIVATE) or @NoArgsConstructor(access = PROTECTED) for constructors.");

    @ArchTest
    static final ArchRule aggregateRootShouldNotHaveBuilder = classes()
            .that()
            .areAssignableTo(AggregateRoot.class)
            .should(notHaveBuilderMethod())
            .because("Aggregate roots should be created using explict constructors but not builders, otherwise we might end up with invalid objects.");

}
