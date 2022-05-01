package me.hsgamer.gitbfawehandler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "GITB-FaweHandler-FakePlugin", version = "1.0.0")
@Dependency("WorldEdit")
@Dependency("GamesInTheBox")
@ApiVersion(ApiVersion.Target.v1_13)
public class FakeJavaPlugin extends JavaPlugin {
}
