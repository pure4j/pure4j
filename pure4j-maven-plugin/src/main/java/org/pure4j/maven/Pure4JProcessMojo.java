package org.pure4j.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.pure4j.processor.PurityChecker;
import org.pure4j.processor.SpringProjectModelFactory;

@Mojo(name = "pure4j", 
	defaultPhase=LifecyclePhase.VERIFY, 
	requiresDependencyResolution=ResolutionScope.RUNTIME, 
	
	requiresProject=true)
public class Pure4JProcessMojo extends AbstractMojo {
	
	@Parameter
	private String[] pureFiles;
	
	@Component
	private MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoCallback mc = new MojoCallback(getLog());
		try {
			getLog().info("Beginning Pure4J Analysis");
			String output = project.getBuild().getOutputDirectory();
			String testOutput = project.getBuild().getTestOutputDirectory();
			PurityChecker pc = new PurityChecker(new SubClassLoader(project), pureFiles);
			SpringProjectModelFactory spmf = new SpringProjectModelFactory(
				new String[] { output, testOutput }, false);
			spmf.setPattern("**/*.class");
			pc.checkModel(spmf.createProjectModel(mc), mc);
			mc.listPures();
			getLog().info("Finished Pure4J Analysis");
		} catch (Exception e) {
			throw new MojoExecutionException("Pure4J Checker Failed: ", e);
		}
		
		if (mc.hasErrors()) {
			throw new MojoExecutionException("Pure4J Analysis completed with one or more errors");
		}
	}
	
	class SubClassLoader extends URLClassLoader {

		public SubClassLoader(MavenProject project) throws Exception {
			super(getClassPathElements(project), Thread.currentThread().getContextClassLoader());
		}

		@Override
		public URL findResource(String name) {
			getLog().info("Loading pure list: "+name);
			URL out = super.findResource(name);
			return out;
		}		
	}

	@SuppressWarnings("unchecked")
	private static URL[] getClassPathElements(MavenProject project) throws Exception {
		ArrayList<String> out = new ArrayList<String>();
		out.addAll(project.getCompileClasspathElements());
		out.add(project.getBuild().getOutputDirectory());
		out.add(project.getBuild().getTestOutputDirectory());
		List<URL> classpathElementUrls = new ArrayList<URL>(out.size());
	    for (String classPathElement : out) {
	        classpathElementUrls.add(new File(classPathElement).toURI().toURL());
	    }
	    System.out.println("urls: "+classpathElementUrls);
	    return (URL[]) classpathElementUrls.toArray(new URL[classpathElementUrls.size()]);
	}
}
