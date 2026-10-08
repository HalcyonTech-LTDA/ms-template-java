package com.example.templatejava.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

@AnalyzeClasses(
        packages = "com.example.templatejava",
        importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule
            domain_and_application_should_not_depend_on_infrastructure_or_spring =
                    noClasses()
                            .that()
                            .resideInAnyPackage("..domain..", "..application..")
                            .should()
                            .dependOnClassesThat()
                            .resideInAnyPackage("..infrastructure..", "org.springframework..");

    @ArchTest
    public static final ArchRule interfaces_in_domain_should_be_pure =
            classes()
                    .that()
                    .resideInAPackage("..domain..")
                    .should(
                            new ArchCondition<JavaClass>(
                                    "not be annotated with Spring annotations") {
                                @Override
                                public void check(JavaClass item, ConditionEvents events) {
                                    boolean hasSpringAnnotation =
                                            item.getAnnotations().stream()
                                                    .anyMatch(
                                                            ann ->
                                                                    ann.getRawType()
                                                                            .getPackageName()
                                                                            .startsWith(
                                                                                    "org.springframework"));
                                    if (hasSpringAnnotation) {
                                        events.add(
                                                SimpleConditionEvent.violated(
                                                        item,
                                                        String.format(
                                                                "Class or interface %s is"
                                                                        + " annotated with Spring"
                                                                        + " annotation",
                                                                item.getName())));
                                    }
                                }
                            });

    @ArchTest
    public static final ArchRule infrastructure_should_depend_on_domain_and_application =
            classes()
                    .that()
                    .resideInAPackage("..infrastructure..")
                    .should()
                    .onlyAccessClassesThat()
                    .resideInAnyPackage(
                            "..infrastructure..",
                            "..application..",
                            "..domain..",
                            "..common..",
                            "java..",
                            "jakarta..",
                            "org.springframework..",
                            "org.slf4j..",
                            "com.mongodb..",
                            "net.javacrumbs.shedlock..",
                            "io.swagger..",
                            "feign..",
                            "tools.jackson..",
                            "org.apache.commons..");

    @ArchTest
    public static final ArchRule use_cases_should_only_reside_in_application_usecase =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("UseCase")
                    .and()
                    .areInterfaces()
                    .should()
                    .resideInAPackage("..application.usecase..");

    @ArchTest
    public static final ArchRule
            use_case_implementations_should_only_reside_in_application_usecase_impl =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("UseCaseImpl")
                            .should()
                            .resideInAPackage("..application.usecase.impl..");

    @ArchTest
    public static final ArchRule gateways_should_only_reside_in_application_gateway =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Gateway")
                    .and()
                    .areInterfaces()
                    .should()
                    .resideInAPackage("..application.gateway..");

    @ArchTest
    public static final ArchRule domain_repositories_should_only_reside_in_domain_repository =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Repository")
                    .and()
                    .areInterfaces()
                    .and()
                    .resideOutsideOfPackage("..infrastructure.database.repository..")
                    .should()
                    .resideInAPackage("..domain.repository..");

    @ArchTest
    public static final ArchRule facades_should_only_reside_in_application_api =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Facade")
                    .and()
                    .areInterfaces()
                    .should()
                    .resideInAPackage("..application.api..");

    @ArchTest
    public static final ArchRule controllers_should_only_reside_in_infrastructure_web =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Controller")
                    .should()
                    .resideInAPackage("..infrastructure.web..");

    @ArchTest
    public static final ArchRule jobs_should_only_reside_in_infrastructure_job =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Job")
                    .should()
                    .resideInAPackage("..infrastructure.job..");

    @ArchTest
    public static final ArchRule
            database_adapters_should_only_reside_in_infrastructure_database_adapter =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("DatabaseAdapter")
                            .should()
                            .resideInAPackage("..infrastructure.database.adapter..");
}
