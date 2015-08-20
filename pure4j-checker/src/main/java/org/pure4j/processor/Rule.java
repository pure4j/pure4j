package org.pure4j.processor;

import org.pure4j.model.ProjectModel;


public interface Rule {

	public void checkModel(ProjectModel pm, Callback cb);
}
