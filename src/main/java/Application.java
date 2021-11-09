import classes.*;

public class Application {
    public static void main(String[] args) {

        MyAnnotationContext.run();

        System.out.println(MyAnnotationContext.getFromContext(Person.class));
        System.out.println(MyAnnotationContext.getFromContext(Dog.class));
        System.out.println(MyAnnotationContext.getFromContext(Cat.class));

    }
}
