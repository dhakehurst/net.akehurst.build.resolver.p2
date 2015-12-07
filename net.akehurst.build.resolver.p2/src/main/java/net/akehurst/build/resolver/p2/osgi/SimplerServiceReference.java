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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

public class SimplerServiceReference implements ServiceReference<Object> {

	public SimplerServiceReference(Class<?> clazz, Object service) {
		this.clazz = clazz;
		this.service = service;
	}
	Class<?> clazz;
	Object service;
	public <T> T getService() {
		return (T)this.service;
	}
	
	@Override
	public Object getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String[] getPropertyKeys() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Bundle getBundle() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Bundle[] getUsingBundles() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isAssignableTo(Bundle bundle, String className) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int compareTo(Object reference) {
		// TODO Auto-generated method stub
		return 0;
	}
}
