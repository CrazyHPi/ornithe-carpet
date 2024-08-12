package carpet;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;

public interface SharedConstants {
    String carpetVersion = FabricLoader.getInstance().getModContainer("carpet")
            .orElseThrow(() -> new NoSuchElementException("No value present")).getMetadata().getVersion().toString();

    Logger LOG = LogManager.getLogger("carpet");

    static <T> T absurd() {
        throw new AssertionError("absurd");
    }
}
