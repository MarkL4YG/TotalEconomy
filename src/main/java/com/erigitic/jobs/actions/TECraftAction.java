package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TECraftAction<X, V extends BaseValue<X>> extends AbstractTEAction<ItemStackSnapshot> {

    private final Key<V> key;
    private final Map<X, TEActionReward> keyedRewards = new HashMap<>();

    public TECraftAction(TEActionReward baseReward, Key<V> key) {
        super(baseReward);
        this.key = key;
    }

    public void addKeyedReward(X targetValue, TEActionReward reward) {
        keyedRewards.put(targetValue, reward);
    }

    @Override
    public Optional<TEActionReward> getRewardFor(ItemStackSnapshot trigger) {
        int quantity = trigger.getQuantity();

        if (key != null) {
            Optional<X> optKeyedValue = trigger.get(key);
            if (optKeyedValue.isPresent()) {
                return getKeyedReward(optKeyedValue.get(), quantity);
            }
        } else {
            return Optional.of(makeReward(
                    getBaseReward().getMoneyReward() * quantity,
                    getBaseReward().getExpReward() * quantity
            ));
        }

        return Optional.empty();
    }

    private Optional<TEActionReward> getKeyedReward(X value, int quantity) {
        return Optional.ofNullable(keyedRewards.getOrDefault(value, null))
                .map(reward -> makeReward(
                        reward.getMoneyReward() * quantity,
                        reward.getExpReward() * quantity
                ));
    }

    @Override
    public Text toText() {
        return null;
    }
}
