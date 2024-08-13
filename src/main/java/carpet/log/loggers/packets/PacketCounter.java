package carpet.log.loggers.packets;

public class PacketCounter {
    public static long totalOut = 0;
    public static long totalIn = 0;

    public static void reset() {
        totalIn = 0L;
        totalOut = 0L;
    }
}
