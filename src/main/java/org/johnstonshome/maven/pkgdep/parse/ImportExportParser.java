/*
 * Licensed Materials - Property of Simon Johnston (simon@johnstonshome.org)
 * (c) Copyright Simon Johnston 2009-2010. All rights reserved.
 *
 * For full license details, see the file LICENSE inncluded in the
 * distribution of this code.
 *
 */
package org.johnstonshome.maven.pkgdep.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.johnstonshome.maven.pkgdep.model.Artifact;
import org.johnstonshome.maven.pkgdep.model.Package;
import org.johnstonshome.maven.pkgdep.model.VersionNumber;

/**
 * Parse OSGi resources for import and export package declarations and return a
 * list of all packages found.
 * 
 * @author simonjo (simon@johnstonshome.org)
 * 
 */
public class ImportExportParser {

    public static final String  PLUGIN_GROUP             = "org.apache.felix";   //$NON-NLS-1$
    public static final String  PLUGIN_ARTIFACT          = "maven-bundle-plugin"; //$NON-NLS-1$

    private static final String PLUGIN_ELEM_INSTRUCTIONS = "instructions";       //$NON-NLS-1$

    private static final String EXPORT_PACKAGE_DECL      = "Export-Package";     //$NON-NLS-1$
    private static final String IMPORT_PACKAGE_DECL      = "Import-Package";     //$NON-NLS-1$
    private static final String BUNDLE_VERSION_DECL      = "Bundle-Version";     //$NON-NLS-1$

    private static final String WILDCARD                 = "*";                  //$NON-NLS-1$
    private static final String EXCLUDE                  = "!";                  //$NON-NLS-1$
    private static final String DECL_SEPARATOR           = ",";                  //$NON-NLS-1$
    private static final String ATTR_SEPARATOR           = ";";                  //$NON-NLS-1$
    private static final String ATTR_ASSIGN              = "=";                  //$NON-NLS-1$
    private static final String ATTR_VERSION             = "version";            //$NON-NLS-1$

    /**
     * Parse an OSGi MANIFEST.MF file for any Export-Package declarations.
     * 
     * @param manifest
     *            the manifest file
     * @param srcDirectory
     *            the directory containing source files, to read packages
     *            from if a wildcard is specified.
     * @param defaultArtifact
     *            the default target artifact
     * @return a list of all exported packages
     */
    public List<Package> parseManifestExports(final File manifest,
            final String srcDirectory, final Artifact defaultArtifact) {
        return parseManifestDependencies(manifest, EXPORT_PACKAGE_DECL,
                srcDirectory, defaultArtifact);
    }

    /**
     * Parse an OSGi MANIFEST.MF file for any Import-Package declarations.
     * 
     * @param manifest
     *            the manifest file
     * @param srcDirectory
     *            the directory containing source files, to read packages
     *            from if a wildcard is specified.
     * @param defaultArtifact
     *            the default target artifact
     * @return a list of all imported packages
     */
    public List<Package> parseManifestImports(final File manifest,
            final String srcDirectory, final Artifact defaultArtifact) {
        return parseManifestDependencies(manifest, IMPORT_PACKAGE_DECL,
                srcDirectory, defaultArtifact);
    }

    /**
     * Parse the Maven pom.xml file for any Export-Package declarations.
     * 
     * @param project 
     *            the Maven project model, to resolve the Felix
     *            OSGi plugin content.
     * @param defaultArtifact
     *            the default target artifact
     * @return a list of all exported packages
     */
    public List<Package> parsePomExports(final MavenProject project,
            final Artifact defaultArtifact) {
        return parsePomDependencies(project, EXPORT_PACKAGE_DECL,
                defaultArtifact);
    }

    /**
     * Parse the Maven pom.xml file for any Import-Package declarations.
     * 
     * @param project 
     *            the Maven project model, to resolve the Felix
     *            OSGi plugin content.
     * @param defaultArtifact
     *            the default target artifact
     * @return a list of all imported packages
     */
    public List<Package> parsePomImports(final MavenProject project,
            final Artifact defaultArtifact) {
        return parsePomDependencies(project, EXPORT_PACKAGE_DECL,
                defaultArtifact);
    }

    /**
     * Parse Export-Package declarations, each of which is a comma separated
     * list of packages with additional attributes that may include a version
     * specification.
     * 
     * Syntax:
     * 
     * <pre>
     * export-string: export-directive [',' export-directive]*
     * export-directive: package-decl [';' attribute]*
     * attribute: key '=' value
     * </pre>
     * 
     * @param exportString
     *            the string to parse
     * @param srcDirectory
     *            the source directory (to resolve wildcard packages)
     * @param defaultArtifact
     *            the default target artifact
     * @return
     */
    public List<Package> parseExport(final String exportString,
            final String srcDirectory, final Artifact defaultArtifact) {
        final List<String> excludes = new LinkedList<String>();
        final List<Package> packages = new LinkedList<Package>();
        final String[] exports = exportString.replaceAll("[\\n\\r \\t]+", "")
                .split(DECL_SEPARATOR);
        for (final String export : exports) {
            String packageName = export;
            String version = defaultArtifact.getVersion().toCanonicalString();
            boolean exclude = packageName.startsWith(EXCLUDE);
            if (exclude) {
                packageName = packageName.substring(1);
            }
            if (export.contains(";")) {
                packageName = export.substring(0,
                        export.indexOf(ATTR_SEPARATOR));
                final String[] attributes = export.split(ATTR_SEPARATOR);
                for (final String attribute : attributes) {
                    final String[] kvPair = attribute.split(ATTR_ASSIGN);
                    if (kvPair[0].trim().equals(ATTR_VERSION)) {
                        version = kvPair[1].trim();
                        if (version.startsWith("\"") && version.endsWith("\"")) {
                            version = version
                                    .substring(1, version.length() - 1);
                        }
                    }
                }
            }
            if (packageName.endsWith(WILDCARD)) {
                // NOTE: do not handle wildcards except at end
                packageName = packageName
                        .substring(0, packageName.length() - 1);
                final String packageFolder = packageName.replaceAll("\\.",
                        System.getProperty("file.separator"));
                final File folder = new File(srcDirectory, packageFolder);
                if (folder.exists() && folder.isDirectory()) {
                    final File[] contents = folder.listFiles();
                    for (final File file : contents) {
                        if (file.exists() && file.isDirectory()) {
                            String newPackageName = packageName
                                    + file.getName();
                            if (exclude) {
                                excludes.add(newPackageName);
                            } else {
                                final Package actual = new Package(
                                        newPackageName);
                                actual.addArtifact(new VersionNumber(version),
                                        defaultArtifact);
                                packages.add(actual);
                            }
                        }
                    }
                }
            } else {
                if (exclude) {
                    excludes.add(packageName);
                } else {
                    final Package actual = new Package(packageName);
                    actual.addArtifact(new VersionNumber(version),
                            defaultArtifact);
                    packages.add(actual);
                }
            }
        }
        final Iterator<Package> iterator = packages.iterator();
        while (iterator.hasNext()) {
            if (excludes.contains(iterator.next().getName())) {
                iterator.remove();
            }
        }
        return packages;
    }

    /*
     * Parse a MANIFEST.MF file
     */
    private List<Package> parseManifestDependencies(final File manifest,
            final String declaration, final String srcDirectory,
            final Artifact defaultArtifact) {
        final StringBuilder packages = new StringBuilder();
        final String declarationFinal = declaration + ":";
        String bundleVersion = null;

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(manifest));
            String line = null;
            boolean found = false;
            while ((line = input.readLine()) != null) {
                if (found) {
                    if (line.startsWith(" ")) {
                        packages.append(line);
                    } else {
                        found = false;
                    }
                } else if (line.startsWith(declarationFinal)) {
                    found = true;
                    packages.append(line.substring(declarationFinal.length()));
                } else if (line.startsWith(BUNDLE_VERSION_DECL)) {
                    bundleVersion = line.substring(line.indexOf(" ")).trim();
                }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        }
        /*
         * Use the bundle version as the default, if no specific version
         * specified for any package declaration.
         */
        final Artifact artifact = bundleVersion == null ? defaultArtifact
                : new Artifact(defaultArtifact.getGroupId(),
                        defaultArtifact.getArtifactId(), new VersionNumber(
                                bundleVersion));
        return parseExport(packages.toString(), srcDirectory, artifact);
    }

    /*
     * Parse the POM
     */
    private List<Package> parsePomDependencies(final MavenProject project,
            final String declaration, final Artifact defaultArtifact) {
        final List<Package> packages = new LinkedList<Package>();
        if (project.getBuildPlugins() != null) {
            for (final Object plugin : project.getBuildPlugins()) {
                final Plugin realPlugin = (Plugin) plugin;
                if (realPlugin.getGroupId().equals(PLUGIN_GROUP)
                        && realPlugin.getArtifactId().equals(PLUGIN_ARTIFACT)) {
                    final Xpp3Dom configuration = (Xpp3Dom) realPlugin
                            .getConfiguration();
                    if (configuration != null) {
                        final Xpp3Dom instructions = configuration
                                .getChild(PLUGIN_ELEM_INSTRUCTIONS);
                        if (instructions != null) {
                            final Xpp3Dom[] exports = instructions
                                    .getChildren(declaration);
                            if (exports != null) {
                                for (final Xpp3Dom export : exports) {
                                    packages.addAll(parseExport(export
                                            .getValue(), project.getBuild()
                                            .getSourceDirectory(),
                                            defaultArtifact));
                                }
                            }
                        }
                    }
                }
            }
        }
        return packages;
    }
}
