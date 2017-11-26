/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.macro;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import junit.framework.TestCase;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.core.idea.macro.IdeaProcessorProvider;
import org.eclipse.xtend.core.macro.XAnnotationExtensions;
import org.eclipse.xtend.core.xtend.XtendFile;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtend.idea.LightXtendTest;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures;
import org.junit.Test;

@SuppressWarnings("all")
public class IdeaProcessorProviderTest extends LightXtendTest {
  @Inject
  @Extension
  private XAnnotationExtensions _xAnnotationExtensions;
  
  @Inject
  private IdeaProcessorProvider provider;
  
  public void testData() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("import org.eclipse.xtend.lib.annotations.Data");
    _builder.newLine();
    _builder.newLine();
    _builder.append("@Data class Foo {}");
    _builder.newLine();
    this.configureByText(_builder.toString());
    EObject _head = IterableExtensions.<EObject>head(this.getXtextFile().getResource().getContents());
    final XtendFile file = ((XtendFile) _head);
    final JvmType processorType = this._xAnnotationExtensions.getProcessorType(IterableExtensions.<XAnnotation>head(IterableExtensions.<XtendTypeDeclaration>head(file.getXtendTypes()).getAnnotations()));
    final Object processor = this.provider.getProcessorInstance(processorType);
    TestCase.assertEquals("DataProcessor", processor.getClass().getSimpleName());
  }
  
  @Test
  public void testLoadClass() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("import org.eclipse.xtend.lib.annotations.Data");
      _builder.newLine();
      _builder.newLine();
      _builder.append("@Data class Foo {}");
      _builder.newLine();
      this.configureByText(_builder.toString());
      EObject _head = IterableExtensions.<EObject>head(this.getXtextFile().getResource().getContents());
      final XtendFile file = ((XtendFile) _head);
      final JvmType processorType = this._xAnnotationExtensions.getProcessorType(IterableExtensions.<XAnnotation>head(IterableExtensions.<XtendTypeDeclaration>head(file.getXtendTypes()).getAnnotations()));
      final ClassLoader classLoader = this.provider.getClassLoader(file);
      TestCase.assertNull(classLoader.loadClass(String.class.getName()).getClassLoader());
      TestCase.assertEquals(TransformationContext.class.getClassLoader(), classLoader.loadClass(TransformationContext.class.getName()).getClassLoader());
      TestCase.assertEquals(TransformationContext.class.getClassLoader(), classLoader.loadClass(Procedures.class.getName()).getClassLoader());
      TestCase.assertEquals(TransformationContext.class.getClassLoader(), classLoader.loadClass(Iterables.class.getName()).getClassLoader());
      TestCase.assertEquals(classLoader, classLoader.loadClass(processorType.getIdentifier()).getClassLoader());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
