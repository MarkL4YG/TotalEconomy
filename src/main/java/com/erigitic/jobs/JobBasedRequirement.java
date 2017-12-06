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

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Requirement notation that is usable in various places such as higher job tiers
 */
public class JobBasedRequirement {

    private String reqJob;
    private int reqJobLevel;
    private String reqPermission;

    /**
     * Build a new requirement representation from a ConfigurationNode
     * {@code null} means the node cannot be interpreted as a requirement
     * @return requirement|null
     */
    public static JobBasedRequirement fromConfigNode(ConfigurationNode node) {

        String job = node.getNode("job").getString(null);
        int level = node.getNode("level").getInt(0);
        String permission = node.getNode("permission").getString(null);

        if ((job != null && (job.trim().isEmpty()))
            || level == 0) {
            job = null;
        }

        if (permission != null && permission.trim().isEmpty()) {
            permission = null;
        }

        // If any are set this requirement is valid
        if (job != null || permission != null) {
            return new JobBasedRequirement(job, level, permission);
        }
        // Requirement is invalid
        return null;
    }

    public JobBasedRequirement(String reqJob, int reqJobLevel, String reqPermission) {
        this.reqJob = reqJob;
        this.reqJobLevel = reqJobLevel;
        this.reqPermission = reqPermission;
    }

    public int getNeededJobLevel() {
        return reqJobLevel;
    }

    public String getNeededJob() {
        return reqJob;
    }

    public String getNeededPermission() {
        return reqPermission;
    }

    @Override
    public String toString() {
        // Construct a readable representation of this requirement
        String s = "JBRequirement[";
        if (reqJob != null) {
            s += " job=" + reqJob + '@' + reqJobLevel;
        }
        if (reqPermission != null) {
            s += " perm=" + reqPermission;
        }
        s += " ]";
        return s;
    }
}
