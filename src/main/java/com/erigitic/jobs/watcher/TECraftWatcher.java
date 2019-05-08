package com.erigitic.jobs.watcher;

import com.erigitic.jobs.TEActionReward;
import com.erigitic.jobs.TEJobSet;
import com.erigitic.jobs.actions.AbstractTEAction;
import com.erigitic.jobs.actions.TECraftAction;
import com.erigitic.main.TotalEconomy;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.TypeTokens;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

import java.util.List;
import java.util.Optional;

public class TECraftWatcher extends AbstractWatcher {

    public TECraftWatcher(TotalEconomy totalEconomy) {
        super(totalEconomy);
    }

    @Listener
    public void onPlayerCraftItem(CraftItemEvent.Craft event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            ItemStackSnapshot craftedStack = event.getCrafted();
            String craftedId = craftedStack.getType().getId();
            String craftedName = craftedStack.getType().getName();

            List<TEJobSet> applicableSets = getJobManager().getSetsApplicableTo(player);

            Optional<?> baseVal = craftedStack.get(Keys.DYE_COLOR);

            if (baseVal.isPresent() && baseVal.get() instanceof Enum) {
                String name = ((Enum) baseVal.get()).name();
                name = null;
            }

            // Calculate the largest applicable reward (money-wise)
            final TEActionReward[] largestReward = {null};
            for (TEJobSet set : applicableSets) {
                final Optional<TECraftAction> optAction = set.getCraftAction(craftedId, craftedName);

                // For all sets, having a kill action for this entityId or entityName, check the rewards.
                // Always keep the largest reward found.
                optAction.map(AbstractTEAction::getBaseReward)
                        .ifPresent(reward -> {
                            if (largestReward[0] == null
                                    || largestReward[0].getMoneyReward() < reward.getMoneyReward()) {
                                largestReward[0] = reward;
                            }
                        });
            }

        }

    }
}
