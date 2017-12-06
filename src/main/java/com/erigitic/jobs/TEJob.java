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

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.math.BigDecimal;
import java.util.*;

public class TEJob {
    private String name;
    private BigDecimal salary;
    private List<String> sets = new ArrayList<>();
    private List<JobBasedRequirement> requirements = new ArrayList<>();

    private boolean isValid = false;

    public TEJob(ConfigurationNode node) {
        name = node.getKey().toString();
        salary = new BigDecimal(node.getNode("salary").getDouble(0));

        try {
            sets = node.getNode("sets").getList(TypeToken.of(String.class), new ArrayList<>());
            ConfigurationNode req = node.getNode("require");

            if (!req.isVirtual()) {

                if (req.getNode("job").isVirtual() && req.getNode("permission").isVirtual()) {

                    for (Map.Entry<Object, ? extends ConfigurationNode> objectEntry : req.getChildrenMap().entrySet()) {

                        JobBasedRequirement requirement = JobBasedRequirement.fromConfigNode(objectEntry.getValue());

                        if (requirement != null) {
                            requirements.add(requirement);
                        }
                    }

                } else {

                    JobBasedRequirement requirement = JobBasedRequirement.fromConfigNode(req);

                    if (requirement != null) {
                        requirements.add(requirement);
                    }

                }
            }

            isValid = true;
        } catch (ObjectMappingException e) {
            isValid = false;

            e.printStackTrace();
        }
    }

    public List<String> getSets() {
        return sets;
    }

    public boolean salaryEnabled() {
        return !salary.equals(BigDecimal.ZERO);
    }

    public String getName() { return name; }

    public BigDecimal getSalary() {
        return salary;
    }

    public List<JobBasedRequirement> getRequirements() {
        return requirements;
    }

    public boolean isValid() {
        return isValid;
    }

    public void saveRequirements(ConfigurationNode node) {
        if (requirements.isEmpty()) return;

        if (requirements.size() > 1) {

            JobBasedRequirement requirement = requirements.get(0);
            if (requirement.getNeededJob() != null) {
                node.getNode("job").setValue(requirement.getNeededJob());
                node.getNode("level").setValue(requirement.getNeededJobLevel());
            }

            if (requirement.getNeededPermission() != null) {
                node.getNode("permission").setValue(requirement.getNeededPermission());
            }

        } else {

            for (int i = 0; i < requirements.size(); i++) {

                JobBasedRequirement requirement = requirements.get(i);

                if (requirement.getNeededJob() != null) {
                    node.getNode(i+"", "job").setValue(requirement.getNeededJob());
                    node.getNode(i+"", "level").setValue(requirement.getNeededJobLevel());
                }

                if (requirement.getNeededPermission() != null) {
                    node.getNode(i+"", "permission").setValue(requirement.getNeededPermission());
                }
            }

        }

    }
}
