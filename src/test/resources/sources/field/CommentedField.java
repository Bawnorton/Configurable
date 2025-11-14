package sources.field;

import com.bawnorton.configurable.Configurable;

public class CommentedField {
	/**
	 * A single field annotated with @Configurable.
	 * Default value is 42.
	 */
	@Configurable
	public static int field = 42;
}