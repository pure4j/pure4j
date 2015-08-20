package org.pure4j.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.Renderer;
import javax.tools.Tool;

import com.sun.java.browser.net.ProxyInfo;

/**
 * Checks the 
 * 
 * @goal kite9diagrams
 * @requiresDependencyCollection runtime
 * @phase
 * 
 */
public class Kite9ProcessMojo extends AbstractMavenReport {

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected ArtifactRepository local;
	
	/**
	 * @parameter
	 */
	private String repositoryDirectory = "kite9repo";

	/**
	 * @parameter
	 */
	private String javadocDirectory = "apidocs";

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @component role=org.apache.maven.artifact.manager.WagonManager
	 */
	private WagonManager wagonManager;

	/**
	 * @parameter expression="${reportOutputDirectory}"
	 *            default-value="${project.reporting.outputDirectory}"
	 */
	private File reportingDir;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private Renderer siteRenderer;

	/**
	 * @parameter
	 */
	private Properties userProperties;

	public void executeReport(Locale arg) throws MavenReportException {
		// this sets up sensible default property settings, based on values from system properties
		Properties propsToUse = new Properties(System.getProperties());
		
		// add any properties in the kite9.properties file
		boolean loadedOK = loadPropertyFile(propsToUse);
		
		// get maven to override with maven-specific properties
		addKite9ReportDefaultLocation(propsToUse);
		addMavenProxySettings(propsToUse);
		addMavenClasspathSettings(propsToUse);
		addMavenJavadocReportLocation(propsToUse);
		
		if (!loadedOK  && ((userProperties == null) || (userProperties.size() ==0))) {
			// no preferences given - create the defaults
			try {
				PreferenceLoader.createDefaultPreferences("kite9.properties");
				getLog().warn("Created kite9.properties - please edit");
			} catch (IOException e) {
				getLog().warn("Could not create kite9.properties", e);
			}
		}
		
		// add specific maven configuration overrides
		propsToUse.putAll(userProperties);
		getLog().info("Using Properties: " + propsToUse);

		Tool t = new Tool();
		GenericXmlApplicationContext context = t.createSpringContext(propsToUse, this.getClass().getClassLoader());
		BasicKite9Runner runner = (BasicKite9Runner) context.getBean(BasicKite9Runner.class);
		Kite9Context kite9Context = (Kite9Context) context.getBean(Kite9Context.class);

		boolean addMap = new File(propsToUse.getProperty("javadoc-listener.docRoot")).exists();

		final Map<String, WorkItem> toInclude = new LinkedHashMap<String, WorkItem>();

		runner.getListeners().add(new BuildListener() {

			public boolean canProcess(WorkItem designItem) {
				return true;
			}

			public void finished() {
			}

			public void process(WorkItem designItem) throws Exception {
				toInclude.put(prepareCaption(designItem), designItem);

			}

		});
		runner.process();

		try {
			createReport(getSink(), toInclude, kite9Context, addMap);
		} catch (IOException e) {
			throw new MavenReportException("Could not generate maven kite9 report: ", e);
		}
	}

	private boolean loadPropertyFile(Properties propsToUse) {
		// allow overriding of these properties from a kite9.properties file
		try {
			Properties kite9Properties = PreferenceLoader.getPreferences("kite9.properties");
			if (kite9Properties != null) {
				propsToUse.putAll(kite9Properties);
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public String prepareCaption(WorkItem wi) {
		StringBuffer out = new StringBuffer();
		out.append(Character.toUpperCase(wi.getName().charAt(0)));
		for (int i = 1; i < wi.getName().length(); i++) {
			char c0 = wi.getName().charAt(i - 1);
			char c1 = wi.getName().charAt(i);
			if (Character.isLowerCase(c0) && Character.isUpperCase(c1)) {
				out.append(" ");
			}
			out.append(c1);
		}

		out.append(" (");
		out.append(wi.getSubjectId());
		out.append(")");

		return out.toString();
	}

	private void createReport(Sink sink2, Map<String, WorkItem> toInclude, Kite9Context c, boolean hasJavadocs)
			throws IOException {
		List<String> contents = new ArrayList<String>(toInclude.keySet());
		Collections.sort(contents);

		final Sink sink = getSink();

		sink.head();
		sink.title();
		sink.text("Project Diagrams");
		sink.title_();
		sink.head_();
		sink.body();
		sink.section1();
		sink.sectionTitle1();
		sink.text("Contents");
		sink.sectionTitle1_();
		sink.list();
		int i = 0;
		for (String string : contents) {
			sink.listItem();
			sink.link("#d" + i++);
			sink.text(string);
			sink.link_();
			sink.listItem_();
			sink.text("\n");
		}
		sink.list_();
		sink.section1_();
		sink.section1();
		sink.sectionTitle1();
		sink.text("Diagrams");
		sink.sectionTitle1_();
		sink.section1_();
		sink.text("\n");

		i = 0;
		for (String string : contents) {
			WorkItem designItem = toInclude.get(string);
			File fileName = hasJavadocs ? RepositoryHelp.prepareFileName(designItem.getSubjectId(), designItem
					.getName(), javadocDirectory, false) : RepositoryHelp.prepareFileName(designItem.getSubjectId(),
					designItem.getName(), repositoryDirectory, false);

			InputStream is = c.getRepository().retrieve(designItem.getSubjectId(), designItem.getName(), "map");

			sink.anchor("d" + i++);
			sink.anchor_();
			sink.section2();
			sink.sectionTitle2();
			sink.text(string);
			sink.sectionTitle2_();

			// output the map
			if (hasJavadocs) {
				StringWriter os = new StringWriter(500);
				Kite9DiagramJavadocListener.processMap(new InputStreamReader(is), os, null, javadocDirectory
						+ File.separatorChar);
				is.close();
				os.close();
				sink.rawText("\n<map name=\"m" + i + "\">\n");
				sink.rawText(os.toString());
				sink.rawText("</map>\n");
			}
			// output the image
			SinkEventAttributeSet set = hasJavadocs ? new SinkEventAttributeSet(new String[] {
					SinkEventAttributes.USEMAP, "#m" + i }) : null;
			sink.figureGraphics(fileName.toString() + ".png", set);
			sink.text("\n");
			sink.section2_();
		}

		sink.section1_();
		sink.body_();
		sink.flush();

	}

	private void addKite9ReportDefaultLocation(Properties propsToUse) {
		propsToUse.put("repo.baseDir", reportingDir.toString() + File.separatorChar + repositoryDirectory);
	}

	private void addMavenJavadocReportLocation(Properties propsToUse) {
		propsToUse.put("javadoc-listener.docRoot", reportingDir.toString() + File.separatorChar + javadocDirectory);
	}

	private void addMavenClasspathSettings(Properties propsToUse) {
		String output = project.getBuild().getOutputDirectory();
		String testOutput = project.getBuild().getTestOutputDirectory();
		StringBuilder prop = new StringBuilder();
		prop.append(output+ File.pathSeparator + testOutput);
		
		for (Object a : project.getArtifacts()) {
			String s = local.getBasedir()+"/"+local.pathOf((Artifact) a);
			System.out.println(a+" ---> "+s);
			prop.append(File.pathSeparator+s);
		}

		propsToUse.put("context.classPath", prop.toString());
	}

	private void addMavenProxySettings(Properties propsToUse) {
		ProxyInfo pi = wagonManager.getProxy("http");
		if (pi != null) {
			propsToUse.put("diagram-server.proxyHost", pi.getHost());
			propsToUse.put("diagram-server.proxyUser", pi.getUserName());
			propsToUse.put("diagram-server.proxyPort", "" + pi.getPort());
			propsToUse.put("diagram-server.proxyPass", pi.getPassword());
		}
	}

	@Override
	protected String getOutputDirectory() {
		return reportingDir.getAbsolutePath();
	}

	@Override
	protected MavenProject getProject() {
		return project;
	}

	@Override
	protected Renderer getSiteRenderer() {
		return siteRenderer;
	}

	public String getDescription(Locale locale) {
		return "Kite9 Diagrams Report";
	}

	public String getName(Locale locale) {
		return "Kite9Report";
	}

	public String getOutputName() {
		return "kite9diagrams";
	}
}
