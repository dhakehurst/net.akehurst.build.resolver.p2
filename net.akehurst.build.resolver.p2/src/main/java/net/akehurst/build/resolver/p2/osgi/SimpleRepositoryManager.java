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

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.artifact.repository.ArtifactRepositoryManager;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.repository.IRepository;

import net.akehurst.eclipse.simple.equinox.SimpleArtifactRepositoryFactory;

public class SimpleRepositoryManager extends ArtifactRepositoryManager {

	public SimpleRepositoryManager(IProvisioningAgent agent) {
		super(agent);
	}

	@Override
	protected String[] getAllSuffixes() {
		return new String []{"artifacts.xml"};
	}
	
	@Override
	protected IExtension[] findMatchingRepositoryExtensions(String suffix, String type) {
		IExtension e = new SimpleExtension();
		return new IExtension[] { e };
	}
	
	@Override
	protected Object createExecutableExtension(IExtension extension, String element) {
		return new SimpleArtifactRepositoryFactory();
	}
	
	/*
	 * code influenced by the similar eclipse equinox Repository Manager
	 */
	@Override
	protected IRepository<IArtifactKey> doCreateRepository(URI location, String name, String type, Map<String, String> properties) throws ProvisionException {
		Assert.isNotNull(name);
		Assert.isNotNull(type);
		IRepository<IArtifactKey> result = null;
		try {
			//check if it already exists
			boolean loaded = false;
			try {
				loadRepository(location, (IProgressMonitor) null, type, 0);
				loaded = true;
			} catch (ProvisionException e) {
				//expected - 
			}
			if (loaded) {
				throw new ProvisionException("REPOSITORY_EXISTS");
			}
			//if doesn't exist, then create it
			result = factoryCreate(location, name, type, properties, null);
			if (result == null) {
				throw new ProvisionException("REPOSITORY_FAILED_READ");
			}
			clearNotFound(location);
			addRepository(result, false, null);
		} finally {
		}
		return result;
	}
	
	private void clearNotFound(URI location) {
		List<URI> badRepos;
		if (unavailableRepositories != null) {
			badRepos = unavailableRepositories.get();
			if (badRepos != null) {
				badRepos.remove(location);
				return;
			}
		}
	}
}
