/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.validation;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.xtend.idea.LightXtendTest;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * @author kosyakov - Initial contribution and API
 */
@SuppressWarnings("all")
public class XtendIdeaValidationTests extends LightXtendTest {
  public void testWrongPackage() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package <error descr=\"The declared package \'my.foo.pack\' does not match the expected package \'\'\">my.foo.pack</error>");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.configureByText(_builder.toString());
    this.myFixture.checkHighlighting();
  }
  
  public void testExcludedFile() {
    final Runnable _function = () -> {
      try {
        final ModifiableRootModel model = ModuleRootManager.getInstance(this.myFixture.getModule()).getModifiableModel();
        final ContentEntry contentEntry = IterableExtensions.<ContentEntry>head(((Iterable<ContentEntry>)Conversions.doWrapArray(model.getContentEntries())));
        final VirtualFile excludedDir = VfsUtil.createDirectoryIfMissing(contentEntry.getFile(), "excluded");
        contentEntry.addExcludeFolder(excludedDir);
        model.commit();
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage");
    _builder.newLine();
    _builder.append("class Foo extends <error descr=\"Bar cannot be resolved to a type.\"><error descr=\"Superclass must be a class\">Bar</error></error> {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final VirtualFile file = this.myFixture.addFileToProject("excluded/Foo.xtend", _builder.toString()).getVirtualFile();
    this.myFixture.testHighlighting(true, true, true, file);
  }
}
