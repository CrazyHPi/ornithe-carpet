package carpet.utils;

import net.minecraft.entity.living.mob.MobCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnReporter {
    public static final HashMap<Integer, HashMap<MobCategory, Pair<Integer, Integer>>> mobcaps = new HashMap<>();

    static {
        mobcaps.put(-1, new HashMap<>());
        mobcaps.put(0, new HashMap<>());
        mobcaps.put(1, new HashMap<>());
    }

    public static List<Text> printMobcapsForDimension(World world, int dim, String name, boolean multiLine) {
        List<Text> list = new ArrayList<>();


        return list;
    }
}
