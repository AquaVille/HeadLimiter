package dev.j3fftw.headlimiter;

import java.io.File;
import java.util.Objects;

import dev.j3fftw.headlimiter.blocklimiter.Group;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import dev.j3fftw.headlimiter.blocklimiter.BlockLimiter;

public final class HeadLimiter extends JavaPlugin implements Listener {

    private static HeadLimiter instance;
    private BlockLimiter blockLimiter;


    @Override
    public void onEnable() {
        instance = this;
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        Utils.loadPermissions();

        getServer().getPluginManager().registerEvents(this, this);

        Objects.requireNonNull(getCommand("headlimiter")).setExecutor(new CountCommand());

        this.blockLimiter = new BlockLimiter(this);
        loadConfig();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public boolean isCargo(SlimefunItem sfItem) {
        return sfItem.isItem(SlimefunItems.CARGO_INPUT_NODE.item())
            || sfItem.isItem(SlimefunItems.CARGO_OUTPUT_NODE.item())
            || sfItem.isItem(SlimefunItems.CARGO_OUTPUT_NODE_2.item())
            || sfItem.isItem(SlimefunItems.CARGO_CONNECTOR_NODE.item())
            || sfItem.isItem(SlimefunItems.CARGO_MANAGER.item());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        final Block block = e.getBlock();

        if (!e.isCancelled()
            && (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD)
            && !Utils.canBypass(player)
        ) {
            final SlimefunItem sfItem = SlimefunItem.getByItem(e.getItemInHand());
            if (sfItem != null
                && isCargo(sfItem)
            ) {
                final int maxAmount = Utils.getMaxHeads(player);
                Utils.count(
                    block.getChunk(),
                    result -> Utils.onCheck(player, block, maxAmount, result.getTotal(), sfItem)
                );
            }
        }
    }

    public BlockLimiter getBlockLimiter() {
        return blockLimiter;
    }

    public static HeadLimiter getInstance() {
        return instance;
    }

    public void loadConfig() {
        ConfigurationSection configurationSection = instance.getConfig().getConfigurationSection("block-limits");
        if (configurationSection == null) {
            throw new IllegalStateException("No configuration for groups is available.");
        }
        for (String key : configurationSection.getKeys(false)) {
            BlockLimiter.getInstance().getGroups().add(new Group(Objects.requireNonNull(configurationSection.getConfigurationSection(key))));
        }
    }
}
