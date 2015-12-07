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

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.internal.framework.FilterImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class SimpleBundleContext implements BundleContext {

	static Map<Class<?>, Object> registeredServices = new HashMap<>();
	public static <T, U extends T> void registerService(Class<T> clazz, U service) {
		registeredServices.put(clazz, service);
	}
	
	@Override
	public String getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle installBundle(String location, InputStream input) throws BundleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle installBundle(String location) throws BundleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getBundle(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle[] getBundles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServiceListener(ServiceListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeServiceListener(ServiceListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBundleListener(BundleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeBundleListener(BundleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFrameworkListener(FrameworkListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFrameworkListener(FrameworkListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceRegistration<?> registerService(String[] clazzes, Object service, Dictionary<String, ?> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
		try {
			registeredServices.put(Class.forName(clazz), service);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory,
			Dictionary<String, ?> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceReference<?> getServiceReference(String clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
		return (ServiceReference<S>)new SimplerServiceReference(clazz, registeredServices.get(clazz));
	}

	@Override
	public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter)
			throws InvalidSyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S> S getService(ServiceReference<S> reference) {
		if (reference instanceof SimplerServiceReference) {
			return ((SimplerServiceReference) reference).getService();
		} else {
			return null;
		}
	}

	@Override
	public boolean ungetService(ServiceReference<?> reference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getDataFile(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Filter createFilter(String filter) throws InvalidSyntaxException {
		return FilterImpl.newInstance(filter);
	}

	@Override
	public Bundle getBundle(String location) {
		// TODO Auto-generated method stub
		return null;
	}

}
