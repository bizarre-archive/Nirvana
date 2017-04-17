package us.ikari.nirvana.game;

import net.minecraft.server.v1_7_R4.EntityCreature;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.List;

public class GameUtils {


    /*
        CodingBat came in handy am I right System LOL
     */
    public static List<String> recursiveSplitString(String message, List<String> previous, int maxLength) {
        String current = "";
        for (String text : message.split(" ")) {
            if (current.length() > maxLength) {
                previous.add(current.trim());
                return recursiveSplitString(message.substring(message.indexOf(current) + current.length()), previous, maxLength);
            } else {
                current += text + " ";
            }
        }

        previous.add(current.trim());

        return previous;
    }

    public static void removeIntelligence(LivingEntity entity) {
        EntityCreature entityLiving;
        try {
            entityLiving = ((CraftCreature) entity).getHandle();
        } catch (Exception ex) {
            return;
        }

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            Field goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
            Field targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");

            goalSelector.setAccessible(true);
            targetSelector.setAccessible(true);
            bField.setAccessible(true);
            cField.setAccessible(true);

            bField.set(goalSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector.get(entityLiving), new UnsafeList<PathfinderGoalSelector>());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
