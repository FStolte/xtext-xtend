/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.autobuild;

import com.google.common.base.Objects;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.vfs.newvfs.persistent.PersistentFS;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import junit.framework.TestCase;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.core.idea.facet.XtendFacetType;
import org.eclipse.xtend.core.idea.lang.XtendLanguage;
import org.eclipse.xtend.idea.LightXtendTest;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.idea.build.XtextAutoBuilderComponent;
import org.eclipse.xtext.idea.resource.VirtualFileURIUtil;
import org.eclipse.xtext.idea.tests.LightToolingTest;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure0;
import org.junit.ComparisonFailure;

@SuppressWarnings("all")
public class IdeaIntegrationTest extends LightXtendTest {
  public void testManualDeletionOfGeneratedSourcesTriggersRebuild() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    final VirtualFile file = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertTrue(file.exists());
    final Runnable _function = () -> {
      try {
        file.delete(null);
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    final VirtualFile regenerated = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertTrue(regenerated.exists());
  }
  
  public void testNoChangeDoesntTouch() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    final VirtualFile file = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertTrue(file.exists());
    final long stamp = file.getModificationStamp();
    final Document document = PsiDocumentManager.getInstance(this.getProject()).getDocument(xtendFile);
    final Runnable _function = () -> {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("package otherPackage");
      _builder_1.newLine();
      _builder_1.append("class Foo {");
      _builder_1.newLine();
      _builder_1.append("\t");
      _builder_1.append("// doesn\'t go into target");
      _builder_1.newLine();
      _builder_1.append("}");
      _builder_1.newLine();
      document.setText(_builder_1);
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    final VirtualFile regenerated = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertEquals(stamp, regenerated.getModificationStamp());
  }
  
  public void testRemoveAndAddFacet() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile source = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    VirtualFile file = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertTrue(file.exists());
    final Runnable _function = () -> {
      final FacetManager mnr = FacetManager.getInstance(this.myModule);
      final ModifiableFacetModel model = mnr.createModifiableModel();
      final Function1<Facet<?>, Boolean> _function_1 = (Facet<?> it) -> {
        FacetTypeId _typeId = it.getTypeId();
        return Boolean.valueOf(Objects.equal(_typeId, XtendFacetType.TYPEID));
      };
      final Facet<?> facet = IterableExtensions.<Facet<?>>findFirst(((Iterable<Facet<?>>)Conversions.doWrapArray(mnr.getAllFacets())), _function_1);
      model.removeFacet(facet);
      model.commit();
      return;
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    final XtextAutoBuilderComponent autoBuilder = this.getProject().<XtextAutoBuilderComponent>getComponent(XtextAutoBuilderComponent.class);
    TestCase.assertTrue(IterableExtensions.isEmpty(autoBuilder.getGeneratedSources(VirtualFileURIUtil.getURI(source.getVirtualFile()))));
    TestCase.assertTrue(IterableExtensions.isEmpty(autoBuilder.getIndexState().getAllResourceDescriptions()));
    file = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertNull(file);
    LightToolingTest.addFacetToModule(this.myModule, XtendLanguage.INSTANCE.getID());
    TestCase.assertEquals(VirtualFileURIUtil.getURI(source.getVirtualFile()), IterableExtensions.<IResourceDescription>head(autoBuilder.getIndexState().getAllResourceDescriptions()).getURI());
    final Function1<URI, Boolean> _function_1 = (URI it) -> {
      return Boolean.valueOf(it.toString().endsWith("xtend-gen/otherPackage/Foo.java"));
    };
    TestCase.assertTrue(IterableExtensions.<URI>exists(autoBuilder.getGeneratedSources(VirtualFileURIUtil.getURI(source.getVirtualFile())), _function_1));
    file = this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java");
    TestCase.assertTrue(file.exists());
  }
  
  public void testJavaDeletionTriggersError() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void callToBar(Bar bar) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("bar.doStuff()");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("public class Bar {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("public void doStuff() {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.myFixture.addFileToProject("myPackage/Bar.java", _builder_1.toString());
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
    final Runnable _function = () -> {
      try {
        final VirtualFile javaFile = this.myFixture.findFileInTempDir("myPackage/Bar.java");
        javaFile.delete(null);
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    try {
      this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
      TestCase.fail("expecting errors");
    } catch (final Throwable _t) {
      if (_t instanceof ComparisonFailure) {
        final ComparisonFailure e = (ComparisonFailure)_t;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  public void testJavaChangeTriggersError() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void callToBar(Bar bar) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("bar.doStuff()");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    try {
      this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
      TestCase.fail("expecting errors");
    } catch (final Throwable _t) {
      if (_t instanceof ComparisonFailure) {
        final ComparisonFailure e = (ComparisonFailure)_t;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("public class Bar {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("public void doStuff() {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.myFixture.addFileToProject("myPackage/Bar.java", _builder_1.toString());
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
  }
  
  public void testCyclicResolution() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void callToFoo(Foo foo) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("foo.callToBar(this);");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(Bar bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.callToFoo(this)");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
  }
  
  public void testCyclicResolution2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar extends Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void someMethod() {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(Bar bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.someMethod");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
  }
  
  public void testCyclicResolution3() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar extends Foo<? extends Bar> {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void someMethod() {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo<T extends Bar> {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(T bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.someMethod");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
  }
  
  public void testCyclicResolution4() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public class Bar extends Foo<Bar> {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public void someMethod(Bar b) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addClass(_builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo<T extends Bar> {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void callToBar(T bar) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("bar.someMethod(bar)");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder_1.toString());
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
  }
  
  public void testDeleteGeneratedFolder() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("val list = OtherClass.getIt(\"foo\")");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package otherPackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("import java.util.List");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class OtherClass {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def static List<String> getIt(String x) {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("return #[x]");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.myFixture.addFileToProject("otherPackage/Bar.xtend", _builder_1.toString());
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("package otherPackage;");
    _builder_2.newLine();
    _builder_2.newLine();
    _builder_2.append("import java.util.List;");
    _builder_2.newLine();
    _builder_2.append("import otherPackage.OtherClass;");
    _builder_2.newLine();
    _builder_2.newLine();
    _builder_2.append("@SuppressWarnings(\"all\")");
    _builder_2.newLine();
    _builder_2.append("public class Foo {");
    _builder_2.newLine();
    _builder_2.append("  ");
    _builder_2.append("private final List<String> list = OtherClass.getIt(\"foo\");");
    _builder_2.newLine();
    _builder_2.append("}");
    _builder_2.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_2);
    final VirtualFile dir = this.myFixture.findFileInTempDir("xtend-gen");
    final Runnable _function = () -> {
      try {
        dir.delete(null);
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("package otherPackage;");
    _builder_3.newLine();
    _builder_3.newLine();
    _builder_3.append("import java.util.List;");
    _builder_3.newLine();
    _builder_3.append("import otherPackage.OtherClass;");
    _builder_3.newLine();
    _builder_3.newLine();
    _builder_3.append("@SuppressWarnings(\"all\")");
    _builder_3.newLine();
    _builder_3.append("public class Foo {");
    _builder_3.newLine();
    _builder_3.append("  ");
    _builder_3.append("private final List<String> list = OtherClass.getIt(\"foo\");");
    _builder_3.newLine();
    _builder_3.append("}");
    _builder_3.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_3);
  }
  
  /**
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=476412
   */
  public void testDeleteNonProjectFolderFromDisk() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    final File tmpDir = Files.createTempDir();
    final File f = new File(tmpDir, "dirToDelete");
    f.mkdirs();
    final VirtualFile vFile = VfsUtil.findFileByIoFile(f, false);
    final Runnable _function = () -> {
      try {
        VirtualFile _parent = vFile.getParent();
        VFileDeleteEvent _vFileDeleteEvent = new VFileDeleteEvent(this, _parent, true);
        PersistentFS.getInstance().processEvents(Collections.<VFileEvent>unmodifiableList(CollectionLiterals.<VFileEvent>newArrayList(_vFileDeleteEvent)));
        TestCase.assertTrue(org.eclipse.xtext.util.Files.sweepFolder(f.getParentFile()));
        TestCase.assertTrue(f.getParentFile().delete());
        return;
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    TestCase.assertFalse(vFile.exists());
  }
  
  public void testAffectedUpdated() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import java.util.List");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("val list = OtherClass.getIt(\"foo\")");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package otherPackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("@SuppressWarnings(\"all\")");
    _builder_1.newLine();
    _builder_1.append("public class Foo {");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("private final Object list /* Skipped initializer because of errors */;");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_1);
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("package otherPackage;");
    _builder_2.newLine();
    _builder_2.newLine();
    _builder_2.append("class OtherClass {");
    _builder_2.newLine();
    _builder_2.append("\t");
    _builder_2.append("public static java.util.List<String> getIt(CharSequence value) {");
    _builder_2.newLine();
    _builder_2.append("\t\t");
    _builder_2.append("return null");
    _builder_2.newLine();
    _builder_2.append("\t");
    _builder_2.append("}");
    _builder_2.newLine();
    _builder_2.append("}");
    _builder_2.newLine();
    this.myFixture.addFileToProject("otherPackage/OtherClass.java", _builder_2.toString());
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("package otherPackage;");
    _builder_3.newLine();
    _builder_3.newLine();
    _builder_3.append("import java.util.List;");
    _builder_3.newLine();
    _builder_3.append("import otherPackage.OtherClass;");
    _builder_3.newLine();
    _builder_3.newLine();
    _builder_3.append("@SuppressWarnings(\"all\")");
    _builder_3.newLine();
    _builder_3.append("public class Foo {");
    _builder_3.newLine();
    _builder_3.append("  ");
    _builder_3.append("private final List<String> list = OtherClass.getIt(\"foo\");");
    _builder_3.newLine();
    _builder_3.append("}");
    _builder_3.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_3);
    VirtualFile _findFileInTempDir = this.myFixture.findFileInTempDir("otherPackage/OtherClass.java");
    StringConcatenation _builder_4 = new StringConcatenation();
    _builder_4.append("package otherPackage;");
    _builder_4.newLine();
    _builder_4.newLine();
    _builder_4.append("class OtherClass {");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("public static java.util.List<String> getIt(CharSequence value) {");
    _builder_4.newLine();
    _builder_4.append("\t\t");
    _builder_4.append("return null");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("}");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("public static String[] getIt(String value) {");
    _builder_4.newLine();
    _builder_4.append("\t\t");
    _builder_4.append("return null");
    _builder_4.newLine();
    _builder_4.append("\t");
    _builder_4.append("}");
    _builder_4.newLine();
    _builder_4.append("}");
    _builder_4.newLine();
    this.myFixture.saveText(_findFileInTempDir, _builder_4.toString());
    StringConcatenation _builder_5 = new StringConcatenation();
    _builder_5.append("package otherPackage;");
    _builder_5.newLine();
    _builder_5.newLine();
    _builder_5.append("import otherPackage.OtherClass;");
    _builder_5.newLine();
    _builder_5.newLine();
    _builder_5.append("@SuppressWarnings(\"all\")");
    _builder_5.newLine();
    _builder_5.append("public class Foo {");
    _builder_5.newLine();
    _builder_5.append("  ");
    _builder_5.append("private final String[] list = OtherClass.getIt(\"foo\");");
    _builder_5.newLine();
    _builder_5.append("}");
    _builder_5.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_5);
  }
  
  public void testTraceFilesGeneratedAndDeleted() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    TestCase.assertTrue(this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java").exists());
    TestCase.assertTrue(this.myFixture.findFileInTempDir("xtend-gen/otherPackage/.Foo.java._trace").exists());
    VirtualFile _findFileInTempDir = this.myFixture.findFileInTempDir("otherPackage/Foo.xtend");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package otherPackage;");
    _builder_1.newLine();
    _builder_1.append("class OtherClass {");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.myFixture.saveText(_findFileInTempDir, _builder_1.toString());
    TestCase.assertNull(this.myFixture.findFileInTempDir("xtend-gen/otherPackage/Foo.java"));
    TestCase.assertNull(this.myFixture.findFileInTempDir("xtend-gen/otherPackage/.Foo.java._trace"));
    TestCase.assertTrue(this.myFixture.findFileInTempDir("xtend-gen/otherPackage/OtherClass.java").exists());
    TestCase.assertTrue(this.myFixture.findFileInTempDir("xtend-gen/otherPackage/.OtherClass.java._trace").exists());
  }
  
  public void testActiveAnnotation() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.append("import org.eclipse.xtend.lib.macro.Data");
    _builder.newLine();
    _builder.newLine();
    _builder.append("@Data class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String myField");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package otherPackage;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("import org.eclipse.xtend.lib.Data;");
    _builder_1.newLine();
    _builder_1.append("import org.eclipse.xtext.xbase.lib.Pure;");
    _builder_1.newLine();
    _builder_1.append("import org.eclipse.xtext.xbase.lib.util.ToStringHelper;");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("@Data");
    _builder_1.newLine();
    _builder_1.append("@SuppressWarnings(\"all\")");
    _builder_1.newLine();
    _builder_1.append("public class Foo {");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("private final String _myField;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public Foo(final String myField) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("super();");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("this._myField = myField;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Override");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public int hashCode() {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("final int prime = 31;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("int result = 1;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("result = prime * result + ((this._myField== null) ? 0 : this._myField.hashCode());");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return result;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Override");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public boolean equals(final Object obj) {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (this == obj)");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return true;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (obj == null)");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (getClass() != obj.getClass())");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("Foo other = (Foo) obj;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("if (this._myField == null) {");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("if (other._myField != null)");
    _builder_1.newLine();
    _builder_1.append("        ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("} else if (!this._myField.equals(other._myField))");
    _builder_1.newLine();
    _builder_1.append("      ");
    _builder_1.append("return false;");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return true;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Override");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public String toString() {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("String result = new ToStringHelper().toString(this);");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return result;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("@Pure");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("public String getMyField() {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("return this._myField;");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    this.assertFileContents("xtend-gen/otherPackage/Foo.java", _builder_1);
  }
  
  public void testMoveFile() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import mypackage.Bar");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void callToBar(Bar bar) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("bar.doStuff()");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    final VirtualFile vf = xtendFile.getVirtualFile();
    final URI before = URI.createURI("temp:///src/otherPackage/Foo.xtend");
    final URI after = URI.createURI("temp:///src/Foo.xtend");
    TestCase.assertNull(this.getIndex().getResourceDescription(after));
    TestCase.assertNotNull(this.getIndex().getResourceDescription(before));
    final Procedure0 _function = () -> {
      final Runnable _function_1 = () -> {
        try {
          vf.move(null, vf.getParent().getParent());
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      };
      ApplicationManager.getApplication().runWriteAction(_function_1);
    };
    this.getBuilder().runOperation(_function);
    TestCase.assertNotNull(this.getIndex().getResourceDescription(after));
    TestCase.assertNull(this.getIndex().getResourceDescription(before));
  }
  
  public void testRenameFile() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder.toString());
    final URI before = URI.createURI("temp:///src/mypackage/Foo.xtend");
    final URI after = URI.createURI("temp:///src/mypackage/Bar.xtend");
    TestCase.assertNull(this.getIndex().getResourceDescription(after));
    TestCase.assertNotNull(this.getIndex().getResourceDescription(before));
    final Procedure0 _function = () -> {
      this.myFixture.renameElement(xtendFile, "Bar.xtend");
    };
    this.getBuilder().runOperation(_function);
    TestCase.assertNotNull(this.getIndex().getResourceDescription(after));
    TestCase.assertNull(this.getIndex().getResourceDescription(before));
  }
  
  public void testRenameReference() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package mypackage");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Foo {}");
    _builder.newLine();
    this.myFixture.addFileToProject("mypackage/Foo.xtend", _builder.toString());
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("package mypackage");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Bar extends Foo {}");
    _builder_1.newLine();
    final String model = _builder_1.toString();
    final PsiFile xtendFile = this.myFixture.addFileToProject("mypackage/Bar.xtend", model);
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
    final int referenceOffset = model.indexOf("Foo");
    this.myFixture.openFileInEditor(xtendFile.getVirtualFile());
    this.myFixture.getEditor().getCaretModel().moveToOffset(referenceOffset);
    final Procedure0 _function = () -> {
      this.myFixture.renameElementAtCaret("Zonk");
    };
    this.getBuilder().runOperation(_function);
    this.myFixture.testHighlighting(true, true, true, xtendFile.getVirtualFile());
  }
  
  public void testNonSourceFile() {
    final Runnable _function = () -> {
      final ModifiableRootModel model = ModuleRootManager.getInstance(this.myFixture.getModule()).getModifiableModel();
      final ContentEntry contentEntry = IterableExtensions.<ContentEntry>head(((Iterable<ContentEntry>)Conversions.doWrapArray(model.getContentEntries())));
      final SourceFolder sourceFolder = IterableExtensions.<SourceFolder>head(((Iterable<SourceFolder>)Conversions.doWrapArray(contentEntry.getSourceFolders())));
      contentEntry.removeSourceFolder(sourceFolder);
      model.commit();
    };
    ApplicationManager.getApplication().runWriteAction(_function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package otherPackage");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("otherPackage/Foo.xtend", _builder.toString());
    TestCase.assertNull(this.getIndex().getResourceDescription(URI.createURI("temp:///src/otherPackage/Foo.xtend")));
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
    _builder.append("package excluded");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.myFixture.addFileToProject("excluded/Foo.xtend", _builder.toString());
    TestCase.assertNull(this.getIndex().getResourceDescription(URI.createURI("temp:///src/excluded/Foo.xtend")));
  }
  
  public void assertFileContents(final String path, final CharSequence sequence) {
    try {
      final VirtualFile file = this.myFixture.findFileInTempDir(path);
      if ((file == null)) {
        TestCase.fail(("Expected a file for " + path));
      }
      InputStream _inputStream = file.getInputStream();
      Charset _charset = file.getCharset();
      InputStreamReader _inputStreamReader = new InputStreamReader(_inputStream, _charset);
      TestCase.assertEquals(sequence.toString(), CharStreams.toString(_inputStreamReader));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
