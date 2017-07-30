package com.veltpvp.nirvana.bukkit.game;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class GameUtils {


    /*
        CodingBat came in handy am I right System LOL
     */
    public static List<String> recursiveSplitString(String message, List<String> previous, int maxLength) {
        StringBuilder current = new StringBuilder();
        for (String text : message.split(" ")) {
            if (current.length() > maxLength) {
                previous.add(current.toString().trim());
                return recursiveSplitString(message.substring(message.indexOf(current.toString()) + current.length()), previous, maxLength);
            } else {
                current.append(text).append(" ");
            }
        }

        previous.add(current.toString().trim());

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

    public static class MutedHorse extends EntityHorse {

        static {
            try {
                Field cField = EntityTypes.class.getDeclaredField("c");
                Field dField = EntityTypes.class.getDeclaredField("d");
                Field fField = EntityTypes.class.getDeclaredField("f");

                cField.setAccessible(true);
                dField.setAccessible(true);
                fField.setAccessible(true);

                ((Map)cField.get(null)).put("EntityHorse", MutedHorse.class);
                ((Map)dField.get(null)).put(MutedHorse.class, "EntityHorse");
                ((Map)fField.get(null)).put(MutedHorse.class, 100);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public MutedHorse(org.bukkit.World world) {
            super(((CraftWorld)world).getHandle());
        }

        @Override
        protected String t() {
            return null;
        }

        @Override
        protected String cv() {
            return null;
        }
    }

}
