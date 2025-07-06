package dev.j3fftw.headlimiter.blocklimiter;

import javax.annotation.Nonnull;

import dev.j3fftw.headlimiter.HeadLimiter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockBreakEvent;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunBlockPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.ChunkPosition;

import java.util.Objects;

public class BlockListener implements Listener {

    @EventHandler
    public void onSlimefunItemPlaced(@Nonnull SlimefunBlockPlaceEvent event) {
        SlimefunItem slimefunItem = event.getSlimefunItem();
        String slimefunItemId = slimefunItem.getId();
        int definedLimit = BlockLimiter.getInstance().getPlayerLimitByItem(event.getPlayer(), slimefunItem);

        if (definedLimit == -1) {
            // No limit has been set, nothing required for HeadLimiter
            return;
        }

        ChunkPosition chunkPosition = new ChunkPosition(event.getBlockPlaced().getChunk());
        ChunkContent content = BlockLimiter.getInstance().getChunkContent(chunkPosition);

        if (content == null) {
            // Content is null so no blocks are currently in this chunk, lets set one up - event can continue
            content = new ChunkContent();
            content.incrementAmount(slimefunItemId);
            BlockLimiter.getInstance().setChunkContent(chunkPosition, content);
        } else if (content.getGroupTotal(slimefunItemId) < definedLimit) {
            // This chunk can take more of the specified item type
            content.incrementAmount(slimefunItemId);
        } else {
            // Chunk has hit its limit for this type, time to deny the placement
            event.setCancelled(true);
            String message = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(HeadLimiter.getInstance().getConfig().getString("message")));
            event.getPlayer().sendMessage(message);
        }
    }

    @EventHandler
    public void onSlimefunItemBroken(@Nonnull SlimefunBlockBreakEvent event) {
        SlimefunItem slimefunItem = event.getSlimefunItem();
        String slimefunItemId = slimefunItem.getId();
        int definedLimit = BlockLimiter.getInstance().getPlayerLimitByItem(event.getPlayer(), slimefunItem);
        if (definedLimit == -1) {
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
        content.decrementAmount(slimefunItemId);

    }

}
