import annotations.Bean;
import annotations.Component;
import annotations.Configuration;
import com.google.common.reflect.ClassPath;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyAnnotationContext {

    public static final Map<Class<?>, Object> context = new HashMap<>();

    public static void run() {
        getClassesFromPackage("src/main/java/classes")
                .stream()
                .filter(cls -> cls.isAnnotationPresent(Component.class))
                .forEach(MyAnnotationContext::loadComponents);

        getClassesFromPackage("src/main/java/config")
                .stream()
                .filter(cls -> cls.isAnnotationPresent(Configuration.class))
                .forEach(MyAnnotationContext::loadBeans);
    }

    public static Object getFromContext(Class<?> cls) {
        return context.get(cls);
    }

    private static void loadBeans(Class<?> cls) {
        List<Method> methods = List.of(cls.getMethods()).stream()
                .filter(m -> m.isAnnotationPresent(Bean.class))
                .collect(Collectors.toList());

        try {
            final var instance = cls.newInstance();
            for (Method m : methods) {
                context.put(m.getReturnType(), m.invoke(instance));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void loadComponents(Class<?> cls) {
        try {
            context.put(cls, cls.newInstance());
        } catch (ReflectiveOperationException e) {
            System.err.println("No default constructor in Component class");
            e.printStackTrace();
        }

    }

    private static List<Class<?>> getClassesFromPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        assert loader != null : "loader equals null";

        File pack = new File(packageName);
        final var list = pack.listFiles();
        assert list != null;

        final Pattern pattern = Pattern.compile("(?<name>[a-z]+$)");
        final Matcher matcher = pattern.matcher(packageName);
        matcher.find();

        try {

            List<File> files = Arrays.stream(list)
                    .filter(file -> file.getName().endsWith(".java"))
                    .collect(Collectors.toList());

            for(File f : files) {
                classes.add(Class.forName(matcher.group("name") + "." + f.getName().substring(0, f.getName().length() - 5)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes;
    }

}
