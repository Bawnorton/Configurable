package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class OnSetDoesntExist {
    @Configurable(onSet = "abc")
    public static int field = 42;
}