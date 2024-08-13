package carpet;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.NoSuchElementException;

public class SharedConstants {
    public static final String carpetVersion = FabricLoader.getInstance().getModContainer("carpet")
            .orElseThrow(() -> new NoSuchElementException("No value present")).getMetadata().getVersion().toString();

    public static final Logger LOG = LogManager.getLogger("carpet");
}
