/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.xtend.core.idea.lang.XtendLanguage;
import org.eclipse.xtext.idea.tests.AbstractIdeaTestCase;
import org.eclipse.xtext.idea.tests.LibraryUtil;
import org.eclipse.xtext.idea.tests.LightToolingTest;
import org.eclipse.xtext.xbase.lib.Exceptions;

/**
 * @author dhuebner - Initial contribution and API
 */
@SuppressWarnings("all")
public abstract class XtendIdeaTestCase extends AbstractIdeaTestCase {
  @Override
  public void configureModule(final Module module, final ModifiableRootModel model, final ContentEntry entry) {
    try {
      LibraryUtil.addXtendLibrary(model);
      final VirtualFile srcGenFolder = VfsUtil.createDirectoryIfMissing(this.getProject().getBaseDir(), "xtend-gen");
      entry.addSourceFolder(srcGenFolder, this.isTestSource(srcGenFolder));
      LightToolingTest.addFacetToModule(module, XtendLanguage.INSTANCE.getID());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
