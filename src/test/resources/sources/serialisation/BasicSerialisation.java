package sources.serialisation;

import com.bawnorton.configurable.Configurable;
import java.util.List;

public class BasicSerialisation {
    /**
     * A simple field comment
     */
    @Configurable
    public static int field = 42;

    @Configurable("customName")
    public static int customField = 100;

    @Configurable
    public static boolean booleanField = true;

    @Configurable
    public static String stringField = "Hello, World!";

    @Configurable
    public static double doubleField = 3.14;

    @Configurable
    public static long longField = 123456789L;

    @Configurable
    public static float floatField = 2.718f;

    @Configurable
    public static char charField = 'A';

    @Configurable
    public static byte byteField = 127;

    @Configurable
    public static short shortField = 32767;

    @Configurable
    public static Integer nullableField = null;

    @Configurable
    public static int[] intArrayField = {1, 2, 3, 4, 5};

    @Configurable
    public static String[] stringArrayField = {"one", "two", "three"};

    @Configurable
    public static List<Integer> integerListField = List.of(1, 2, 3, 4, 5);

    @Configurable
    public static List<String> stringListField = List.of("one", "two", "three");

    @Configurable
    public static List<List<Integer>> nestedListField = List.of(List.of(1, 2), List.of(3, 4));

    @Configurable
    public static List<String[]> listOfStringArraysField = List.of(new String[]{"a", "b"}, new String[]{"c", "d"});
}