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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.repository.AuthenticationFailedException;
import org.eclipse.equinox.internal.p2.repository.Transport;

public class SimpleTransport extends Transport {

	@Override
	public IStatus download(URI toDownload, OutputStream target, long startPos, IProgressMonitor monitor) {
		try {
			IOUtils.copy(toDownload.toURL().openStream(), target);
			return Status.OK_STATUS;
		} catch (Exception ex) {
			return Status.OK_STATUS;
		}
	}

	@Override
	public IStatus download(URI toDownload, OutputStream target, IProgressMonitor monitor) {
		return download(toDownload, target, -1, monitor);
	}

	@Override
	public InputStream stream(URI toDownload, IProgressMonitor monitor)
			throws FileNotFoundException, CoreException, AuthenticationFailedException {
		return null;
	}

	@Override
	public long getLastModified(URI toDownload, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException, AuthenticationFailedException {
		return 0;
	}

}
