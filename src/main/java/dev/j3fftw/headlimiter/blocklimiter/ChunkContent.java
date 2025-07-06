package dev.j3fftw.headlimiter.blocklimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

public class ChunkContent {

    private final Map<String, Integer> contentMap = new HashMap<>();

    public int getCurrentAmount(@Nonnull String itemId) {
        return this.contentMap.getOrDefault(itemId, 0);
    }

    public int getGroupTotal(@Nonnull String itemId) {
        Set<Group> groupSet = BlockLimiter.getInstance().getGroups();

        for (Group group : groupSet) {
            if (group.contains(itemId)) {
                int amount = 0;
                for (String item : group.getItems()) {
                    amount += getCurrentAmount(item);
                }
                return amount;
            }
        }

        return -1;
    }

    public void incrementAmount(@Nonnull String itemId) {
        int amount = getCurrentAmount(itemId);
        setAmount(itemId, amount + 1);
    }

    public void decrementAmount(@Nonnull String itemId) {
        int amount = getCurrentAmount(itemId);
        setAmount(itemId, Math.max(0, amount - 1));
    }

    public void setAmount(@Nonnull String itemId, int amount) {
        contentMap.put(itemId, amount);
    }
}
