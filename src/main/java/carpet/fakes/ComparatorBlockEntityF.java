package carpet.fakes;

public interface ComparatorBlockEntityF {
    // CM: instant comparator logger, stored in world time modulo 3.
    // This is to allow for further tile tick scheduling in the same tick before the tile tick is processed
    int[] scheduledOutputSignal = new int[3];

    void setScheduledOutputSignal(int index, int value);

    int[] getScheduledOutputSignal();

    boolean[] buggy = new boolean[3];

    void setBuggy(boolean[] boolArr);

    void setBuggy(int index, boolean value);

    boolean[] getBuggy();
}
