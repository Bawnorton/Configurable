package sources.serialisation;

import com.bawnorton.configurable.Configurable;

public class GroupSerialisation {
	@Configurable(group = "abc")
	public static int intValue = 1;

	@Configurable(group = "abc")
	public static String stringValue = "test";

	@Configurable(group = "abc")
	public static boolean booleanValue = true;

	@Configurable
	public static double doubleValue = 3.14;

	@Configurable
	public static String anotherStringValue = "another test";

	@Configurable(group = "abc.nested")
	public static String nestedStringValue = "nested test";

	@Configurable(group = "abc.nested.deep.value")
	public static String deepNestedStringValue = "deep nested test";

	@Configurable(group = "abc.nested")
	public static int nestedIntValue = 42;
}