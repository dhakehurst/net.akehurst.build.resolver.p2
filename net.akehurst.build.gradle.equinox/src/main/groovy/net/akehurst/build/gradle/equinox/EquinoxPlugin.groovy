package net.akehurst.build.gradle.equinox

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File
import java.nio.file.Files;
import java.util.jar.JarFile;

import org.gradle.api.*
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*

class EquinoxPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
	project.apply plugin: 'osgi'
	project.apply plugin: 'java'
	
	project.extensions.create("equinox", EquinoxConfiguration.class);
	
//	project.task('buildEquinoxApplication',type:Copy, dependsOn:'jar') {
//		from project.configurations.runtime
//		into "${project.buildDir}/dependencies"
//		from project.tasks.jar.outputs.files
//		into "${project.buildDir}/dependencies"	
//	}

	project.task('osgify') << {
		Files.createDirectories(Paths.get("${project.buildDir}/app/plugins"))
		project.configurations.runtime.resolvedConfiguration.resolvedArtifacts*.each {  ar ->
			Converter.osgify(ar.file,ar.name,"${project.buildDir}/app/plugins")
		}
		project.copy {
			 from project.tasks.jar.outputs.files
			 into "${project.buildDir}/app/plugins"
		}
	}
//	project.tasks.osgify.dependsOn 'buildEquinoxApplication'
	project.tasks.build.dependsOn 'osgify'
	
	project.task('writeConfigIniFile') << {
		File dir = project.file("${project.buildDir}/app/configuration")
		File file = project.file("${project.buildDir}/app/configuration/config.ini")
		dir.mkdirs()
		file.createNewFile()
		file.write("""eclipse.application=${project.equinox.application}
osgi.framework=file\\:plugins/org.eclipse.osgi_3.10.100.v20150529-1857.jar
org.osgi.framework.executionenvironment=JavaSE-1.6,J2SE-1.6,J2SE-1.5,J2SE-1.4,J2SE-1.3,J2SE-1.2,JRE-1.1,CDC-1.1/Foundation-1.1,CDC-1.0/Foundation-1.0,OSGi/Minimum-1.2,OSGi/Minimum-1.1,OSGi/Minimum-1.0
osgi.bundles.defaultStartLevel=4
osgi.bundles=\\
""")
		project.tasks.jar.outputs.files.each {
			file.append( "reference\\:file\\:"+it.name+",\\" + System.lineSeparator())
		}
		
		
		project.configurations.runtime.resolvedConfiguration.resolvedArtifacts*.each {  ar ->
			if (project.equinox.startInfo.containsKey( ar.name) ) {
				String level = project.equinox.startInfo.get(ar.name)
				if (level.isEmpty()) {
					file.append( "reference\\:file\\:"+ar.file.name+"@start,\\" + System.lineSeparator())
				} else {
					file.append( "reference\\:file\\:"+ar.file.name+"@${level}:start,\\" + System.lineSeparator())
				}	
			} else {
				file.append( "reference\\:file\\:"+ar.file.name+",\\" + System.lineSeparator())
			}
		}

	}
	project.tasks.build.dependsOn 'writeConfigIniFile'

  }
 
}
