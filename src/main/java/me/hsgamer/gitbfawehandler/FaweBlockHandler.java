package me.hsgamer.gitbfawehandler;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.hsgamer.gamesinthebox.blockutil.extra.box.BlockBox;
import me.hsgamer.gamesinthebox.feature.BlockFeature;
import me.hsgamer.gamesinthebox.probabilitylib.ProbabilityCollection;
import me.hsgamer.gamesinthebox.util.Utils;
import me.hsgamer.gamesinthebox.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FaweBlockHandler implements BlockFeature.BlockHandler {
    private final GitbFaweHandler plugin;

    public FaweBlockHandler(GitbFaweHandler plugin) {
        this.plugin = plugin;
    }

    private BlockFeature.BlockProcess setBlocks(com.sk89q.worldedit.world.World bukkitWorld, Set<BlockVector3> blockVectors, Pattern pattern) {
        CompletableFuture<Void> blockFuture = new CompletableFuture<>();
        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(bukkitWorld)
                    .fastMode(true)
                    .changeSetNull()
                    .limitUnlimited()
                    .compile()
                    .build()
            ) {
                session.setBlocks(blockVectors, pattern);
            } finally {
                blockFuture.complete(null);
            }
        });
        return new BlockFeature.BlockProcess() {
            @Override
            public boolean isDone() {
                return blockFuture.isDone();
            }

            @Override
            public void cancel() {
                Utils.cancelSafe(task);
            }
        };
    }

    private BlockFeature.BlockProcess setBlocks(World world, BlockBox blockBox, Pattern pattern) {
        com.sk89q.worldedit.world.World bukkitWorld = BukkitAdapter.adapt(world);
        CuboidRegion region = new CuboidRegion(
                bukkitWorld,
                BlockVector3.at(blockBox.minX, blockBox.minY, blockBox.minZ),
                BlockVector3.at(blockBox.maxX, blockBox.maxY, blockBox.maxZ)
        );
        CompletableFuture<Void> blockFuture = new CompletableFuture<>();
        BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(bukkitWorld)
                    .fastMode(true)
                    .changeSetNull()
                    .limitUnlimited()
                    .compile()
                    .build()
            ) {
                session.setBlocks((Region) region, pattern);
            } finally {
                blockFuture.complete(null);
            }
        });
        return new BlockFeature.BlockProcess() {
            @Override
            public boolean isDone() {
                return blockFuture.isDone();
            }

            @Override
            public void cancel() {
                Utils.cancelSafe(task);
            }
        };
    }

    @Override
    public BlockFeature.BlockProcess setRandomBlocks(World world, BlockBox blockBox, ProbabilityCollection<XMaterial> probabilityCollection) {
        RandomPattern randomPattern = new RandomPattern();
        probabilityCollection.iterator().forEachRemaining(element -> {
            Material material = element.getObject().parseMaterial();
            if (material != null) {
                randomPattern.add(BukkitAdapter.asBlockType(material), element.getProbability());
            }
        });
        return setBlocks(world, blockBox, randomPattern);
    }

    @Override
    public BlockFeature.BlockProcess clearBlocks(World world, BlockBox blockBox) {
        return setBlocks(world, blockBox, BukkitAdapter.asBlockType(Material.AIR));
    }

    @Override
    public BlockFeature.BlockProcess clearBlocks(Collection<Location> collection) {
        if (collection.isEmpty()) {
            return new BlockFeature.BlockProcess() {
                @Override
                public boolean isDone() {
                    return true;
                }

                @Override
                public void cancel() {
                    // EMPTY
                }
            };
        }
        Set<BlockVector3> blockVectors = new HashSet<>();
        com.sk89q.worldedit.world.World bukkitWorld = null;
        for (Location location : collection) {
            if (bukkitWorld == null) {
                bukkitWorld = BukkitAdapter.adapt(location.getWorld());
            }
            blockVectors.add(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
        if (bukkitWorld == null) {
            return new BlockFeature.BlockProcess() {
                @Override
                public boolean isDone() {
                    return true;
                }

                @Override
                public void cancel() {
                    // EMPTY
                }
            };
        }
        return setBlocks(bukkitWorld, blockVectors, BukkitAdapter.asBlockType(Material.AIR));
    }

    @Override
    public void clearBlocksFast(World world, BlockBox blockBox) {
        com.sk89q.worldedit.world.World bukkitWorld = BukkitAdapter.adapt(world);
        CuboidRegion region = new CuboidRegion(
                bukkitWorld,
                BlockVector3.at(blockBox.minX, blockBox.minY, blockBox.minZ),
                BlockVector3.at(blockBox.maxX, blockBox.maxY, blockBox.maxZ)
        );
        try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder()
                .world(bukkitWorld)
                .fastMode(true)
                .forceWNA()
                .changeSetNull()
                .limitUnlimited()
                .compile()
                .build()
        ) {
            session.setBlocks((Region) region, BukkitAdapter.asBlockType(Material.AIR));
        }
    }

    @Override
    public void clearBlocksFast(Collection<Location> collection) {
        if (collection.isEmpty()) {
            return;
        }
        Set<BlockVector3> blockVectors = new HashSet<>();
        com.sk89q.worldedit.world.World bukkitWorld = null;
        for (Location location : collection) {
            if (bukkitWorld == null) {
                bukkitWorld = BukkitAdapter.adapt(location.getWorld());
            }
            blockVectors.add(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
        if (bukkitWorld == null) {
            return;
        }
        try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder()
                .world(bukkitWorld)
                .fastMode(true)
                .forceWNA()
                .changeSetNull()
                .limitUnlimited()
                .compile()
                .build()
        ) {
            session.setBlocks(blockVectors, BukkitAdapter.asBlockType(Material.AIR));
        }
    }
}
