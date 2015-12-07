/**
 * Copyright (C) 2015 Dr. David H. Akehurst (http://dr.david.h.akehurst.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.akehurst.build.resolver.p2.osgi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.osgi.internal.framework.EquinoxConfiguration;
import org.eclipse.osgi.internal.framework.EquinoxContainer;
import org.eclipse.osgi.internal.location.BasicLocation;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleActivator;

import net.akehurst.eclipse.simple.equinox.Utils;

public class SimpleOsgi {
	
	public SimpleOsgi() {
		this.agent = new SimpleProvisioningAgent();
	}
	
	IProvisioningAgent agent;
	public IProvisioningAgent getProvisioningAgent() {
		return this.agent;
	}
	List<BundleActivator> activators = new ArrayList<>();

	public <T extends BundleActivator> void startDummyBundle(Class<T> activatorClass) {
		try {
			BundleActivator a = activatorClass.getConstructor().newInstance();
			activators.add(a);
			a.start(new SimpleBundleContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerService(String serviceName, Object serviceObject) {
		this.agent.registerService(serviceName, serviceObject);
	}
	
	public void registerBundleServices() {
		String property = "";
		URL defaultValue = null;
		try {
			defaultValue = new URL("file://defaultValue");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean isReadOnly = false;
		String dataAreaPrefix = "dataAreaPrefix";
		EquinoxConfiguration environmentInfo = new EquinoxContainer(null).getConfiguration();
		Location l = new BasicLocation(property, defaultValue, isReadOnly, dataAreaPrefix, environmentInfo);

		SimpleBundleContext.registerService(Location.class, l);

		// SimplerBundleContext.registerService(SignedContentFactory.class, l);
	}

}
