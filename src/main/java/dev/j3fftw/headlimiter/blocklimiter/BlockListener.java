package dev.j3fftw.headlimiter.blocklimiter;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockBreakEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.ChunkPosition;

import dev.j3fftw.headlimiter.HeadLimiter;

public class BlockListener implements Listener {

    public BlockListener(@Nonnull HeadLimiter headLimiter) {
        headLimiter.getServer().getPluginManager().registerEvents(this, headLimiter);
    }

    @EventHandler
    public void onSlimefunItemPlaced(@Nonnull SlimefunBlockPlaceEvent event) {
        SlimefunItem slimefunItem = event.getSlimefunItem();
        String id = slimefunItem.getId();
        int definedLimit = HeadLimiter.getSlimefunItemLimit(id);

        ChunkPosition chunkPosition = new ChunkPosition(event.getBlockPlaced().getChunk());
        ChunkContent content = BlockLimiter.getInstance().getChunkContent(chunkPosition);

        if (content == null) {
            // Content is null so no blocks are currently in this chunk, lets set one up - event can continue
            content = new ChunkContent();
            content.incrementAmount(id);
            BlockLimiter.getInstance().setChunkContent(chunkPosition, content);
        } else if (content.getCurrentAmount(id) < definedLimit) {
            // This chunk can take more of the specified item type
            content.incrementAmount(id);
        }

        // We want to still add to the map but do not cancel if definedLimit is 0.
        if (definedLimit == 0) {
            // No limit has been set, nothing required for HeadLimiter
            return;
        }

        // Chunk has hit its limit for this type, time to deny the placement
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot place any more of this item within this chunk.");
    }

    @EventHandler
    public void onSlimefunItemBroken(@Nonnull SlimefunBlockBreakEvent event) {
        SlimefunItem slimefunItem = event.getSlimefunItem();
        String id = slimefunItem.getId();
        int definedLimit = HeadLimiter.getSlimefunItemLimit(id);
        if (definedLimit == 0) {
            // No limit has been set, nothing required for HeadLimiter
            return;
        }

        ChunkPosition chunkPosition = new ChunkPosition(event.getBlockBroken().getChunk());
        ChunkContent content = BlockLimiter.getInstance().getChunkContent(chunkPosition);

        if (content == null) {
            // Content is null so no blocks are currently in this chunk, shouldn't be possible, but never mind
            return;
        }

        // This chunk can take more of the specified item type
        content.decrementAmount(id);

    }

}
