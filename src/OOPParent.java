import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

// OOPParent.java:
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = OOPParents.class)
public @interface OOPParent {
    Class<?> parent();
    boolean isVirtual() default false;
}
