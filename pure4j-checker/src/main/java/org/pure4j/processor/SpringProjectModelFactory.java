package org.pure4j.processor;

import java.io.IOException;

import org.pure4j.model.ProjectModel;
import org.pure4j.model.ProjectModelImpl;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * This class uses spring & asm functionality to scan the contents of the
 * project to build the project model.
 * 
 * 
 * @author moffatr
 * 
 */
public class SpringProjectModelFactory {
	
	boolean logging = false;

	public SpringProjectModelFactory(String[] classpathElements, boolean logging) {
		this.paths = classpathElements;
		this.logging = logging;
	}
	
	
	

	public ProjectModel createProjectModel(Callback cb) throws IOException {
		int fileCount = 0; 
		ClassFileModelBuilder cfmb = new ClassFileModelBuilder(logging);
		for (int i = 0; i < paths.length; i++) {
			cb.send("Path: "+paths[i]);
			FileSystemResourceLoader loader = new FileSystemResourceLoader();
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(loader);
			String ending = "/"+ ((basePackage.length() > 0) ? basePackage.replace(".", "/") + "/" : "") + pattern;
			
			if (paths[i].startsWith("/")) {
				paths[i] = "file:" + paths[i];
			}
			
			if (paths[i].endsWith(".jar")) {
				paths[i] = "jar:"+ paths[i] + "!";
			} 
			
			String packageSearchPath =paths[i]+ending;

			cb.send("Searching: "+packageSearchPath);
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			cb.send("Found "+resources.length+" resources");

			for (Resource resource : resources) {
				fileCount++;
				cfmb.visit(resource);
			}

			cb.send("Found: "+resources.length+" matches");

		}

		ProjectModelImpl model = cfmb.getModel();
		
		cb.send(
				"Created project model with " + fileCount + " files and "
						+ model.getClassCount() + " classes");

		return model;

	}
	
	private String basePackage = "";
	private String pattern = "**.class";
	private String[] paths;
	
	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	
	public String[] getClasspathElements() {
		return paths;
	}

	public void setClasspathElements(String[] classpathElements) {
		this.paths = classpathElements;
	}


}
