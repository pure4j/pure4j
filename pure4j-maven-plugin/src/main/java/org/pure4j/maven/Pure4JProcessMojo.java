package org.pure4j.maven;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.pure4j.processor.PurityChecker;
import org.pure4j.processor.SpringProjectModelFactory;

@Mojo(name = "pure4j", 
	defaultPhase=LifecyclePhase.VERIFY, 
	requiresDependencyResolution=ResolutionScope.RUNTIME, 
	
	requiresProject=true)
public class Pure4JProcessMojo extends AbstractMojo {
	
	@Component
	private MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Beginning Pure4J Analysis");
			String output = project.getBuild().getOutputDirectory();
			String testOutput = project.getBuild().getTestOutputDirectory();
			PurityChecker pc = new PurityChecker(getClassLoader(project));
			SpringProjectModelFactory spmf = new SpringProjectModelFactory(
				new String[] { output, testOutput }, false);
			
			spmf.setPattern("**/*.class");
			
			MojoCallback mc = new MojoCallback(getLog());
			
			pc.checkModel(spmf.createProjectModel(mc), mc);
			getLog().info("Finished Pure4J Analysis");
		} catch (Exception e) {
			throw new MojoExecutionException("Pure4J Checker Failed: ", e);
		}
	}

	public static ClassLoader getClassLoader(MavenProject project) throws Exception {
	    List<String> classPathElements = compileClassPathElements(project);
	    List<URL> classpathElementUrls = new ArrayList<>(classPathElements.size());
	    for (String classPathElement : classPathElements) {
	        classpathElementUrls.add(new File(classPathElement).toURI().toURL());
	    }
	    return new URLClassLoader(
	        classpathElementUrls.toArray(new URL[classpathElementUrls.size()]),
	        Thread.currentThread().getContextClassLoader()
	    );
	}

	@SuppressWarnings("unchecked")
	private static List<String> compileClassPathElements(MavenProject project) throws Exception {
	    return new ArrayList<String>(project.getCompileClasspathElements());
	}
}
