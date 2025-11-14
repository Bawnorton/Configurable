package source.field;

import com.bawnorton.configurable.Configurable;

public class FieldWithoutStaticModifier {
	@Configurable
	public int field = 42;
}