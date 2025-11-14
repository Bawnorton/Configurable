package sources.serialisation;

import com.bawnorton.configurable.Configurable;

import java.util.List;

public class ComplexSavingModifications {
	@Configurable
	public static List<List<String[]>> field = List.of(
			List.of(new String[]{"a", "b"}, new String[]{"c", "d"}),
			List.of(new String[]{"e", "f"}, new String[]{"g", "h"})
	);
}