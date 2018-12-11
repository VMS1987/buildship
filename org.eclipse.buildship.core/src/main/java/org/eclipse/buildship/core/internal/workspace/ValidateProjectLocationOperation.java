/*
 * Copyright (c) 2017 the original author or authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.buildship.core.internal.workspace;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.gradle.tooling.model.eclipse.EclipseProject;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.buildship.core.internal.UnsupportedConfigurationException;

/**
 * Verifies that none of the modules are located in the Eclipse workspace root and each project has a unique location.
 *
 * @author Donat Csikos
 */
public final class ValidateProjectLocationOperation {

    private static final File WORKSPACE_ROOT = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

    private final Set<EclipseProject> projects;

    public ValidateProjectLocationOperation(Set<? extends EclipseProject> projects) {
        this.projects = ImmutableSet.copyOf(projects);
    }

    public void run(IProgressMonitor monitor) {

        ListMultimap<File, String> locationToProjectNames = ArrayListMultimap.create();
        for (EclipseProject project : this.projects) {
            locationToProjectNames.put(project.getProjectDirectory(), project.getName());
        }

        for (File location : locationToProjectNames.keySet()) {
            List<String> projectNames = locationToProjectNames.get(location);
            if (projectNames.size() > 1) {
                throw new UnsupportedConfigurationException(String.format("The following projects all located in the %s directory: %s", location.getAbsolutePath(), Joiner.on(", ").join(projectNames)));
            } else if (location.equals(WORKSPACE_ROOT)) {
                throw new UnsupportedConfigurationException(String.format("Project %s location matches workspace root %s", projectNames.get(0), WORKSPACE_ROOT.getAbsolutePath()));
            }
        }
    }
}
