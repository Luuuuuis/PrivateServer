package de.luuuuuis.privateserver.spigot.util.reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by @author yanjulang
 * on @date 28.06.2017
 */
public class Reflections {

    /**
     * Set a field value in an Object
     *
     * @param obj   to set the field
     * @param name  of the field
     * @param value for the field
     */
    public static void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception ignored) {
        }
    }

    /**
     * Get a Field value from an Object
     *
     * @param obj  to get the field from
     * @param name of the field
     * @return Object (Value)
     */
    public static Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Get a {@link Method} from a class
     *
     * @param clazz      to get the Method from
     * @param methodName name of the Method
     * @param parameters all Classes that are parameters of this Method
     * @return The Method
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        try {
            return clazz.getDeclaredMethod(methodName, parameters);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    /**
     * Get a {@link Constructor} from a Class
     *
     * @param clazz      to get the Constructor from
     * @param parameters
     * @return The Constructor
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameters) {
        try {
            return clazz.getConstructor(parameters);
        } catch (NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    /**
     * Get a {@link Boolean} from an Object
     *
     * @param obj       to get the boolean from
     * @param fieldName - the name of the field
     * @return boolean
     */
    public static boolean getBoolean(Object obj, String fieldName) {
        try {
            return obj.getClass().getDeclaredField(fieldName).getBoolean(obj);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return false;
        }
    }

    /**
     * Get a {@link String} from an Object
     *
     * @param obj       to get the String from
     * @param fieldName - the name of the field
     * @return String
     */
    public static String getString(Object obj, String fieldName) {
        try {
            return (String) obj.getClass().getDeclaredField(fieldName).get(obj);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return null;
        }
    }

    /**
     * Get a {@link Integer} from an Object
     *
     * @param obj       to get the Integer from
     * @param fieldName - the name of the field
     * @return int
     */
    public static int getInt(Object obj, String fieldName) {
        try {
            return obj.getClass().getDeclaredField(fieldName).getInt(obj);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return -1;
        }
    }

    /**
     * Get a {@link Double} from an Object
     *
     * @param obj       to get the Double from
     * @param fieldName - the name of the field
     * @return double
     */
    public static double getDouble(Object obj, String fieldName) {
        try {
            return obj.getClass().getDeclaredField(fieldName).getDouble(obj);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return -1;
        }
    }

}