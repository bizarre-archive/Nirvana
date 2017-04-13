package us.ikari.nirvana;

import org.bukkit.plugin.java.JavaPlugin;

public class Nirvana extends JavaPlugin {

    private static Nirvana instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    public static Nirvana getInstance() {
        return instance;
    }
}
