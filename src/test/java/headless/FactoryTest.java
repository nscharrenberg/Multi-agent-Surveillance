package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FactoryTest {

    @DisplayName("Factory Reset Successful")
    @Test
    void testFactoryResetSuccess() {
        Factory.init();
        Factory.getGameRepository().setGameMode(GameMode.EXPLORATION);

        Assertions.assertEquals(Factory.getGameRepository().getGameMode(), GameMode.EXPLORATION);

        Factory.reset();

        Assertions.assertNull(Factory.getGameRepository().getGameMode());
    }
}
