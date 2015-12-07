package net.akehurst.build.gradle.equinox

import java.util.*

class EquinoxConfiguration {

	String application;
	String layout; //'gradle' or 'eclipse'

	Map<String, String> startInfo = new HashMap<>();

	void start(String bundleName) {
		this.startInfo.put(bundleName,"")	
	}

	void start(String bundleName, String level) {
		this.startInfo.put(bundleName,level)	
	}

}