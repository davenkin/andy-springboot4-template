package com.company.andy.archunit.util;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.core.domain.JavaModifier.*;

public class ArchUnitUtils {
    public static ArchCondition<JavaClass> havePrivateNoArgConstructor() {
        return new ArchCondition<>("have a private no-arg constructor") {
            @Override
            public void check(JavaClass clazz, ConditionEvents events) {
                boolean hasPrivateNoArgCtor = clazz.getConstructors().stream()
                        .anyMatch(ctor -> ctor.getParameters().isEmpty() && ctor.getModifiers().contains(PRIVATE));
                String message = hasPrivateNoArgCtor
                        ? String.format("Class %s has a private no-arg constructor.", clazz.getName())
                        : String.format("Class %s does not have a private no-arg constructor.", clazz.getName());

                events.add(new SimpleConditionEvent(clazz, hasPrivateNoArgCtor, message));
            }
        };
    }

    public static ArchCondition<JavaClass> haveNonPublicNoArgConstructor() {
        return new ArchCondition<>("have a private no-arg constructor") {
            @Override
            public void check(JavaClass clazz, ConditionEvents events) {
                boolean hasPrivateNoArgCtor = clazz.getConstructors().stream()
                        .anyMatch(ctor -> ctor.getParameters().isEmpty() &&
                                          (ctor.getModifiers().contains(PRIVATE) || ctor.getModifiers().contains(PROTECTED)));
                String message = hasPrivateNoArgCtor
                        ? String.format("Class %s has a private no-arg constructor.", clazz.getName())
                        : String.format("Class %s does not have a private no-arg constructor.", clazz.getName());

                events.add(new SimpleConditionEvent(clazz, hasPrivateNoArgCtor, message));
            }
        };
    }

    public static ArchCondition<JavaClass> notHaveBuilderMethod() {
        return new ArchCondition<>("have no builder method") {
            @Override
            public void check(JavaClass clazz, ConditionEvents events) {
                boolean hasNoBuilderMethod = clazz.getMethods()
                        .stream().noneMatch(it -> it.getName().equalsIgnoreCase("builder"));
                String message = hasNoBuilderMethod
                        ? String.format("Class %s does not have a builder method.", clazz.getName())
                        : String.format("Class %s containers a builder method.", clazz.getName());

                events.add(new SimpleConditionEvent(clazz, hasNoBuilderMethod, message));
            }
        };
    }

    public static ArchCondition<JavaClass> notHaveSetterMethods() {
        return new ArchCondition<>("have no setter methods") {
            @Override
            public void check(JavaClass clazz, ConditionEvents events) {
                boolean hasNoSetterMethods = clazz.getMethods()
                        .stream().noneMatch(it -> it.getName().startsWith("set"));
                String message = hasNoSetterMethods
                        ? String.format("Class %s does not have setter methods.", clazz.getName())
                        : String.format("Class %s containers setter methods.", clazz.getName());

                events.add(new SimpleConditionEvent(clazz, hasNoSetterMethods, message));
            }
        };
    }

    public static DescribedPredicate<JavaClass> areConcreteClasses() {
        return new DescribedPredicate<>("are concrete classes") {
            @Override
            public boolean test(JavaClass javaClass) {
                return !javaClass.getModifiers().contains(ABSTRACT) && !javaClass.isInterface();
            }
        };
    }
}
