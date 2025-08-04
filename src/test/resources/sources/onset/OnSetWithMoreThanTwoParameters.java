package sources.validator;

import com.bawnorton.configurable.Configurable;

public class OnSetWithMoreThanTwoParameters {
    @Configurable(onSet = "wrongNumberOfParameters")
    public static int field = 42;

    public static void wrongNumberOfParameters(Integer value, boolean fromSync, String extraParam) {
    }
}