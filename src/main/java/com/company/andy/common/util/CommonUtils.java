package com.company.andy.common.util;

import com.company.andy.common.exception.ServiceException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.company.andy.common.exception.ErrorCode.SYSTEM_ERROR;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class CommonUtils {

    public static String requireNonBlank(String str, String message) {
        if (isBlank(str)) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    public static Class<?> singleParameterizedArgumentClassOf(Class<?> aClass) {
        Type genericSuperclass = aClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType theSuperclass) {
            Type[] typeArguments = theSuperclass.getActualTypeArguments();
            if (typeArguments.length != 1) {
                throw new ServiceException(SYSTEM_ERROR,
                        "Expecting exactly one parameterized type argument for[" + aClass.getName() + "],but got " + typeArguments.length + ". ");
            }

            Type actualTypeArgument = typeArguments[0];
            if (actualTypeArgument instanceof Class<?>) {
                return (Class<?>) actualTypeArgument;
            } else {
                throw new ServiceException(SYSTEM_ERROR,
                        "The argument type[" + actualTypeArgument.getTypeName() + "] is not of Class type for: " + aClass.getName());
            }
        } else {
            throw new ServiceException(SYSTEM_ERROR,
                    "The super class of [" + aClass.getName() + "] is not of parameterized type.");
        }
    }

    public static String mongoConcatFields(String... fields) {
        requireNonNull(fields, "Fields cannot be null.");
        for (String arg : fields) {
            requireNonBlank(arg, "Element of fields is blank.");
        }
        return String.join(".", fields);
    }

}
