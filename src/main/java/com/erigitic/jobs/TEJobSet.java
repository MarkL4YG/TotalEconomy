/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.jobs;

import com.erigitic.jobs.actions.AbstractBlockAction;
import com.erigitic.jobs.actions.ActionFactory;
import com.erigitic.jobs.actions.TEFishAction;
import com.erigitic.jobs.actions.TEKillAction;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TEJobSet {

    private static final Logger logger = LoggerFactory.getLogger(TEJobSet.class);

    private final Map<String, AbstractBlockAction> breakActions = new HashMap<>();
    private final Map<String, AbstractBlockAction> placeActions = new HashMap<>();
    private final Map<String, TEFishAction> fishActions = new HashMap<>();
    private final Map<String, TEKillAction> killActions = new HashMap<>();

    public TEJobSet(ConfigurationNode node) {
        node.getChildrenMap().forEach((actionStr, targetNode) -> {
            if ((actionStr instanceof String) && targetNode != null) {
                final String action = (String) actionStr;
                switch (action) {
                    case "break":
                        registerBreakActions(targetNode);
                        break;

                    case "place":
                        registerPlaceActions(targetNode);
                        break;

                    case "catch":
                        registerFishActions(targetNode);
                        break;

                    case "kill":
                        registerKillActions(targetNode);
                        break;

                    default:
                        logger.warn("Unknown action: {}", action);
                }
            }
        });
    }

    private void registerBreakActions(ConfigurationNode actionNodes) {
        actionNodes.getChildrenMap().forEach((key, value) -> {
            final String blockIdentifier = (String) key;
            breakActions.put(blockIdentifier, ActionFactory.createBreakAction(value));
        });
    }

    private void registerPlaceActions(ConfigurationNode actionNodes) {
        actionNodes.getChildrenMap().forEach((key, value) -> {
            final String blockIdentifier = (String) key;
            placeActions.put(blockIdentifier, ActionFactory.createPlaceAction(value));
        });
    }

    private void registerFishActions(ConfigurationNode actionNodes) {
        actionNodes.getChildrenMap().forEach((key, value) -> {
            final String itemIdentifier = (String) key;
            fishActions.put(itemIdentifier, ActionFactory.createFishAction(value));
        });
    }

    private void registerKillActions(ConfigurationNode actionNodes) {
        actionNodes.getChildrenMap().forEach((key, value) -> {
            final String entityIdentifier = (String) key;
            killActions.put(entityIdentifier, ActionFactory.createKillAction(value));
        });
    }

    public Optional<AbstractBlockAction> getBreakAction(String blockId, String blockName) {
        AbstractBlockAction action = breakActions.getOrDefault(blockId, null);
        action = action != null ? action : breakActions.getOrDefault(blockName, null);
        return Optional.ofNullable(action);
    }

    public Optional<AbstractBlockAction> getPlaceAction(String blockId, String blockName) {
        AbstractBlockAction action = placeActions.getOrDefault(blockId, null);
        action = action != null ? action : placeActions.getOrDefault(blockName, null);
        return Optional.ofNullable(action);
    }

    public Optional<TEFishAction> getFishAction(String itemId, String itemName, String fishName) {
        TEFishAction action = fishActions.getOrDefault(itemId, null);
        action = action != null ? action : fishActions.getOrDefault(itemName, null);
        action = action != null ? action : fishActions.getOrDefault(fishName, null);
        return Optional.ofNullable(action);
    }

    public Optional<TEKillAction> getKillAction(String entityId, String entityName) {
        TEKillAction action = killActions.getOrDefault(entityId, null);
        action = action != null ? action : killActions.getOrDefault(entityName, null);
        return Optional.ofNullable(action);
    }

    public Iterable<? extends TEAction> getActions() {
        return Collections.emptyList();
    }
}
