package me.hsgamer.gitbfawehandler;

import me.hsgamer.gamesinthebox.GamesInTheBox;
import me.hsgamer.gamesinthebox.feature.BlockFeature;
import org.bukkit.plugin.java.JavaPlugin;

public final class GitbFaweHandler extends JavaPlugin {

    @Override
    public void onEnable() {
        JavaPlugin.getPlugin(GamesInTheBox.class)
                .getArenaManager()
                .getFeature(BlockFeature.class)
                .setBlockHandler(new FaweBlockHandler(this));
    }
}
