import com.bawnorton.configurable.Configurable;

public class SingleField {
    /**
     * A single field annotated with @Configurable.
     * Default value is 42.
     */
    @Configurable
    public static int field = 42;
}