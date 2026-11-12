package carpet.patches;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketFlow;

public class ConnectionFake extends Connection {
    public ConnectionFake(PacketFlow flow) {
        super(flow);
    }

    @Override
    public void disableAutoRead() {
        //noop
    }
}
