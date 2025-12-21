package com.company.andy.common.util;

import com.company.andy.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.company.andy.common.exception.ErrorCode.SYSTEM_ERROR;
import static com.company.andy.common.util.CommonUtils.mongoConcatFields;
import static org.junit.jupiter.api.Assertions.*;

class CommonUtilsTest {

    @Test
    void should_throw_exception_when_string_is_null() {
        assertThrows(IllegalArgumentException.class, () -> {
            CommonUtils.requireNonBlank(null, "string is null");
        });
    }

    @Test
    void should_throw_exception_when_string_is_empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            CommonUtils.requireNonBlank("", "string is empty");
        });
    }

    @Test
    void should_throw_exception_when_string_is_blank() {
        assertThrows(IllegalArgumentException.class, () -> {
            CommonUtils.requireNonBlank("  ", "string is blank");
        });
    }

    @Test
    void should_get_single_parameterized_argument_class() {
        TestClassImpl theObject = new TestClassImpl();
        Class<?> theParameterClass = CommonUtils.singleParameterizedArgumentClassOf(theObject.getClass());
        assertEquals(String.class, theParameterClass);
    }

    @Test
    void should_throw_exception_for_two_parameterized_arguments() {
        TestClassWithTwoParameterizeArgumentsImpl theObject = new TestClassWithTwoParameterizeArgumentsImpl();
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            CommonUtils.singleParameterizedArgumentClassOf(theObject.getClass());
        });
        assertEquals(SYSTEM_ERROR, exception.getCode());
        assertTrue(exception.getMessage().contains("Expecting exactly one parameterized type argument"));
    }

    @Test
    void should_throw_exception_for_non_class_parameterized_arguments() {
        TestNonClassImpl theObject = new TestNonClassImpl();
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            CommonUtils.singleParameterizedArgumentClassOf(theObject.getClass());
        });
        assertEquals(SYSTEM_ERROR, exception.getCode());
        assertTrue(exception.getMessage().contains("The argument type"));
    }

    @Test
    void should_throw_exception_for_non_parameterized_super_class() {
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            CommonUtils.singleParameterizedArgumentClassOf(String.class);
        });
        assertEquals(SYSTEM_ERROR, exception.getCode());
        assertTrue(exception.getMessage().contains("The super class of"));
    }

    @Test
    void should_concat_mongo_fields() {
        assertEquals("a.b", mongoConcatFields("a", "b"));
    }

    @Test
    void should_throw_exception_for_concat_mongo_fields_if_provided_with_null() {
        assertThrows(NullPointerException.class, () -> {
            mongoConcatFields(null);
        });
    }

    @Test
    void should_throw_exception_for_concat_mongo_fields_if_provided_with_some_null_value() {
        assertThrows(IllegalArgumentException.class, () -> {
            mongoConcatFields("a", null);
        });
    }

    @Test
    void should_throw_exception_for_concat_mongo_fields_if_provided_with_some_empty_value() {
        assertThrows(IllegalArgumentException.class, () -> {
            mongoConcatFields("a", "");
        });
    }

    @Test
    void should_throw_exception_for_concat_mongo_fields_if_provided_with_some_blank_value() {
        assertThrows(IllegalArgumentException.class, () -> {
            mongoConcatFields("a", "  ");
        });
    }
}

abstract class TestClass<T> {
}

class TestClassImpl extends TestClass<String> {
}

class TestNonClassImpl extends TestClass<List<String>> {
}

abstract class TestClassWithTwoParameterizeArguments<T1, T2> {
}

class TestClassWithTwoParameterizeArgumentsImpl extends TestClassWithTwoParameterizeArguments<String, String> {
}