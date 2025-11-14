package sources.serialisation;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

import java.util.List;

public class BasicSerialisation {
	/**
	 * A simple field comment
	 */
	@Configurable(validator = @Validator(fallback = false))
	public static int field = 42;

	@Configurable(value = "customName", validator = @Validator(fallback = false))
	public static int customField = 100;

	@Configurable(validator = @Validator(fallback = false))
	public static boolean booleanField = true;

	@Configurable(validator = @Validator(fallback = false))
	public static String stringField = "Hello, World!";

	@Configurable(validator = @Validator(fallback = false))
	public static double doubleField = 3.14;

	@Configurable(validator = @Validator(fallback = false))
	public static long longField = 123456789L;

	@Configurable(validator = @Validator(fallback = false))
	public static float floatField = 2.718f;

	@Configurable(validator = @Validator(fallback = false))
	public static char charField = 'A';

	@Configurable(validator = @Validator(fallback = false))
	public static byte byteField = 127;

	@Configurable(validator = @Validator(fallback = false))
	public static short shortField = 32767;

	@Configurable
	public static Integer nullableField = null;

	@Configurable(validator = @Validator(fallback = false))
	public static int[] intArrayField = {1, 2, 3, 4, 5};

	@Configurable(validator = @Validator(fallback = false))
	public static String[] stringArrayField = {"one", "two", "three"};

	@Configurable(validator = @Validator(fallback = false))
	public static List<Integer> integerListField = List.of(1, 2, 3, 4, 5);

	@Configurable(validator = @Validator(fallback = false))
	public static List<String> stringListField = List.of("one", "two", "three");

	@Configurable(validator = @Validator(fallback = false))
	public static List<List<Integer>> nestedListField = List.of(List.of(1, 2), List.of(3, 4));

	@Configurable(validator = @Validator(fallback = false))
	public static List<String[]> listOfStringArraysField = List.of(new String[]{"a", "b"}, new String[]{"c", "d"});

	@Configurable
	public static Day enumField = sources.serialisation.BasicSerialisation.Day.MONDAY;

	public enum Day {
		SUNDAY,
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY
	}
}