package sources.field;

import com.bawnorton.configurable.Configurable;

public class DuplicateField {
	@Configurable("field")
	public static int field2 = 43;

	@Configurable
	public static int field = 42;
}