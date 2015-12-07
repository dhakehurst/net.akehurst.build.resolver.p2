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
package net.akehurst.build.resolver.p2;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.repository.CacheManager;
import org.eclipse.equinox.internal.p2.repository.Transport;
import org.eclipse.equinox.internal.provisional.p2.core.eventbus.IProvisioningEventBus;
import org.eclipse.equinox.internal.provisional.p2.core.eventbus.ProvisioningListener;
import org.eclipse.equinox.p2.core.IAgentLocation;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.repository.artifact.ArtifactKeyQuery;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.akehurst.build.resolver.p2.osgi.SimpleOsgi;
import net.akehurst.build.resolver.p2.osgi.SimpleRepositoryManager;
import net.akehurst.build.resolver.p2.osgi.SimpleTransport;
import net.akehurst.eclipse.simple.equinox.SimplerArtifactRepository;
import net.akehurst.eclipse.simple.equinox.Utils;

public class SimpleOsgiP2Resolver {

	public static SimpleOsgi createRequiredOsgi(Path agentLocationDataAreaPath) {

		SimpleOsgi osgi = new SimpleOsgi();
		// start required bundles
		osgi.startDummyBundle(org.eclipse.equinox.internal.p2.artifact.repository.Activator.class);
		osgi.startDummyBundle(org.eclipse.equinox.internal.p2.repository.Activator.class);
		osgi.startDummyBundle(org.eclipse.equinox.internal.security.auth.AuthPlugin.class);
		osgi.startDummyBundle(org.eclipse.equinox.internal.p2.transport.ecf.Activator.class);

		// register required services
		osgi.registerService(Transport.SERVICE_NAME, new SimpleTransport());
		osgi.registerService(IAgentLocation.SERVICE_NAME, new IAgentLocation() {
			@Override
			public URI getDataArea(String namespace) {
				URI uri = agentLocationDataAreaPath.toUri();
				return uri;
			}

			@Override
			public URI getRootLocation() {
				return null;
			}
		});
		osgi.registerService(IProvisioningEventBus.SERVICE_NAME, new IProvisioningEventBus() {
			@Override
			public void dispatchEvent(ProvisioningListener eventListener, ProvisioningListener listenerObject, int eventAction, EventObject eventObject) {
			}

			@Override
			public void addListener(ProvisioningListener toAdd) {
			}

			@Override
			public void removeListener(ProvisioningListener toRemove) {
			}

			@Override
			public void publishEvent(EventObject event) {
			}

			@Override
			public void close() {
			}
		});
		osgi.registerService(CacheManager.SERVICE_NAME, Utils.getCachManager(osgi.getProvisioningAgent()));

		osgi.registerBundleServices();

		return osgi;
	}

	private final static Logger LOG = LoggerFactory.getLogger(SimpleOsgiP2Resolver.class);

	public SimpleOsgiP2Resolver(SimpleOsgi osgi, URI localCacheLocationUri) {
		LOG.trace("SimpleOsgiP2Resolver");
		this.repoManager = new SimpleRepositoryManager(osgi.getProvisioningAgent());

		try {
			this.local = this.repoManager.loadRepository(localCacheLocationUri, new NullProgressMonitor());
		} catch (ProvisionException e) {
			// this will happen if the local repo does not exist yet
		}
		if (null == this.local) {
			try {
				this.local = this.repoManager.createRepository(localCacheLocationUri, "localP2Cache", "", null);
			} catch (ProvisionException e) {
				e.printStackTrace();
			}
		}
	}

	IArtifactRepository local;
	SimpleRepositoryManager repoManager;

	public Set<String> fetchVersions(URI remoteP2location, String artifactId) throws OsgiP2ResolverException {
		LOG.trace("SimpleOsgiP2Resolver.fetchVersions");
		HashSet<String> result = new HashSet<>();
		try {
			IArtifactRepository remoteRepo = this.repoManager.loadRepository(remoteP2location, new NullProgressMonitor());
			IQuery<IArtifactKey> query = new ArtifactKeyQuery(null, artifactId, null);
			IQueryResult<IArtifactKey> res = remoteRepo.query(query, new NullProgressMonitor());
			for (IArtifactKey ak : res) {
				String v = ak.getVersion().getOriginal();
				if (v.contains("-")) {
					v = v.substring(0, v.indexOf('-'));
				}
				result.add(v);
			}
		} catch (ProvisionException ex) {
			throw new OsgiP2ResolverException("Cannot fetchVersions for " + artifactId + " in " + remoteP2location, ex);
		}
		return result;
	}

	public URI resolve(URI remoteP2location, String artifactId, String versionRangeString) throws OsgiP2ResolverException {
		LOG.trace("SimpleOsgiP2Resolver.resolve");
		OutputStream destination = null;
		try {
			String classifier = "osgi.bundle";
			IArtifactDescriptor localDescriptor = this.fetchDescriptor(this.local, classifier, artifactId, versionRangeString);
			if (null != localDescriptor && this.local.contains(localDescriptor)) {
				return ((SimplerArtifactRepository) this.local).createLocation(localDescriptor);
			} else {
				IArtifactRepository remoteRepo = this.repoManager.loadRepository(remoteP2location, new NullProgressMonitor());
				IArtifactDescriptor remoteDescriptor = this.fetchDescriptor(remoteRepo, classifier, artifactId, versionRangeString);
				if (null == remoteDescriptor) {
					throw new OsgiP2ResolverException("Cannot fetchDescriptor for "+artifactId+":"+versionRangeString+" in "+remoteRepo, null);
				} else {
					if (remoteRepo.contains(remoteDescriptor)) {
						localDescriptor = this.local.createArtifactDescriptor(remoteDescriptor.getArtifactKey());
						destination = this.local.getOutputStream(localDescriptor);
						IStatus s = remoteRepo.getArtifact(remoteDescriptor, destination, new NullProgressMonitor());
						if (Status.OK_STATUS == s) {
							return ((SimplerArtifactRepository) this.local).createLocation(localDescriptor);
						} else {
							return null;
						}
					}
				}
			}

		} catch (ProvisionException e) {
			throw new OsgiP2ResolverException("Cannot resolve " + artifactId + " in " + remoteP2location, e);
		} finally {
			if (null != destination) {
				try {
					destination.flush();
					destination.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	IArtifactDescriptor fetchDescriptor(IArtifactRepository repo, String classifier, String id, String versionRangeString) {
		VersionRange range = new VersionRange(versionRangeString);
		IQuery<IArtifactKey> query = new ArtifactKeyQuery(classifier, id, range);
		IQueryResult<IArtifactKey> qr = repo.query(query, new NullProgressMonitor());
		if (qr.isEmpty()) {
			return null;
		} else {
			IArtifactKey akey = qr.iterator().next();
			IArtifactDescriptor artifactDescription = repo.createArtifactDescriptor(akey);
			return artifactDescription;
		}
	}

	// void initialiseECF() {
	// String name = "ecf.base";
	// IContainerInstantiator exten = new org.eclipse.ecf.core.BaseContainer.Instantiator();
	// String description = "ECF Base Container";
	// boolean server = false;
	// boolean hidden = false;
	// ContainerTypeDescription scd = new ContainerTypeDescription(name, (IContainerInstantiator) exten, description,
	// server, hidden);
	// ContainerFactory.getDefault().addDescription(scd);
	// }
}
