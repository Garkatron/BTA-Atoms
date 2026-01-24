package deus.btd.pipeline.registry;

import deus.btd.pipeline.project.ProjectProcessed;

import java.util.ArrayList;
import java.util.List;

public final class AtomProjectRegistry {

    private AtomProjectRegistry() {}

    public static final List<ProjectProcessed> PROJECTS = new ArrayList<>();

    public static void clear() {
        PROJECTS.clear();
    }

	public static void addAll(List<ProjectProcessed> projects) {
		PROJECTS.addAll(projects);
	}

    public static void add(ProjectProcessed project) {
        PROJECTS.add(project);
    }

    public static List<ProjectProcessed> all() {
        return new ArrayList<>(PROJECTS);
    }
}
