package me.hsgamer.gitbfawehandler;

import me.hsgamer.gamesinthebox.GamesInTheBox;
import me.hsgamer.gamesinthebox.core.addon.object.Addon;
import me.hsgamer.gamesinthebox.core.bukkit.config.BukkitConfig;
import me.hsgamer.gamesinthebox.core.config.Config;
import me.hsgamer.gamesinthebox.feature.BlockFeature;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GitbFaweHandler extends Addon {
    Plugin plugin;

    @Override
    public void onEnable() {
        try {
            plugin = JavaPlugin.getPlugin(GamesInTheBox.class).getPluginLoader().loadPlugin(getClassLoader().getFile());
        } catch (InvalidPluginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (plugin != null) {
            JavaPlugin.getPlugin(GamesInTheBox.class).getPluginLoader().disablePlugin(plugin);
        }
    }

    @Override
    public void onPostEnable() {
        JavaPlugin.getPlugin(GamesInTheBox.class)
                .getArenaManager()
                .getFeature(BlockFeature.class)
                .setBlockHandler(new FaweBlockHandler());
    }

    @Override
    protected Config createConfig() {
        return new BukkitConfig(new File(getDataFolder(), "config.yml"));
    }
}
