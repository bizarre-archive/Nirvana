package com.veltpvp.nirvana.game;

import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.util.UnsafeList;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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

    public static class Freezing {

        private static final int FREEZE_ENTITY_ID = -1;

        public static void freeze(Player player) {
            Location location = player.getLocation();

            while (!location.getBlock().getType().isSolid()) {
                location.subtract(0, 1, 0);
            }
            location.add(0, 1, 0);

            player.teleport(location);

            try {
                PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving();
                setField(spawnPacket, int.class, 0, FREEZE_ENTITY_ID);
                setField(spawnPacket, int.class, 1, (byte) 65);
                setField(spawnPacket, int.class, 2, (int) Math.floor(location.getX() * 32.0D));
                setField(spawnPacket, int.class, 3, (int) Math.floor(location.getY() * 32.0D));
                setField(spawnPacket, int.class, 4, (int) Math.floor(location.getZ() * 32.0D));
                DataWatcher watcher = new DataWatcher(null);
                watcher.a(0, (byte) 0x20);
                setField(spawnPacket, DataWatcher.class, 0, watcher);

                PacketPlayOutAttachEntity attachPacket = new PacketPlayOutAttachEntity();
                setField(attachPacket, int.class, 0, 0);
                setField(attachPacket, int.class, 1, player.getEntityId());
                setField(attachPacket, int.class, 2, FREEZE_ENTITY_ID);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnPacket);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(attachPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void unfreeze(Player player) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(FREEZE_ENTITY_ID));
        }

        private static void setField(Object object, Class<?> type, int index, Object value) throws NoSuchFieldException, IllegalAccessException {
            int i = 0;

            for (Field field : object.getClass().getDeclaredFields()) {
                if (field.getType().equals(type) && i++ == index) {
                    field.setAccessible(true);
                    field.set(object, value);
                    break;
                }
            }
        }

    }

}
