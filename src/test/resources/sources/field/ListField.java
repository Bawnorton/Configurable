package sources.field;

import com.bawnorton.configurable.Configurable;

import java.util.List;

public class ListField {
	@Configurable
	public static List<Integer> listField = List.of(1, 2, 3, 4, 5);
}