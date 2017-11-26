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
import com.google.inject.Provider;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.impl.ModifiableModelCommitter;
import com.intellij.openapi.roots.ui.configuration.actions.ModuleDeleteProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.PsiTestCase;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.xtend.core.idea.facet.XtendFacetConfiguration;
import org.eclipse.xtend.core.idea.lang.XtendLanguage;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.idea.build.XtextAutoBuilderComponent;
import org.eclipse.xtext.idea.tests.LibraryUtil;
import org.eclipse.xtext.idea.tests.LightToolingTest;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.impl.ChunkedResourceDescriptions;
import org.eclipse.xtext.util.Files;
import org.eclipse.xtext.xbase.idea.facet.XbaseGeneratorConfigurationState;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
@SuppressWarnings("all")
public class MultiModuleTest extends PsiTestCase {
  public void testTwoModulesWithDependency() {
    try {
      final Module moduleA = this.createModule("moduleA");
      final Module moduleB = this.createModule("moduleB");
      ModuleRootModificationUtil.addDependency(moduleB, moduleA);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("class OtherClass extends MyClass {");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final PsiFile referencing = this.createFile(moduleB, "OtherClass.xtend", _builder.toString());
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("class MyClass {");
      _builder_1.newLine();
      _builder_1.append("}");
      _builder_1.newLine();
      final PsiFile referenced = this.createFile(moduleA, "MyClass.xtend", _builder_1.toString());
      VirtualFile _virtualFile = referencing.getVirtualFile();
      VirtualFile _parent = null;
      if (_virtualFile!=null) {
        _parent=_virtualFile.getParent();
      }
      VirtualFile _findChild = null;
      if (_parent!=null) {
        _findChild=_parent.findChild("xtend-gen");
      }
      VirtualFile _findChild_1 = null;
      if (_findChild!=null) {
        _findChild_1=_findChild.findChild("OtherClass.java");
      }
      final VirtualFile generatedReferencing = _findChild_1;
      VirtualFile _virtualFile_1 = referenced.getVirtualFile();
      VirtualFile _parent_1 = null;
      if (_virtualFile_1!=null) {
        _parent_1=_virtualFile_1.getParent();
      }
      VirtualFile _findChild_2 = null;
      if (_parent_1!=null) {
        _findChild_2=_parent_1.findChild("xtend-gen");
      }
      VirtualFile _findChild_3 = null;
      if (_findChild_2!=null) {
        _findChild_3=_findChild_2.findChild("MyClass.java");
      }
      final VirtualFile generatedReferenced = _findChild_3;
      TestCase.assertNotNull(generatedReferencing);
      TestCase.assertNotNull(generatedReferenced);
      TestCase.assertNull(referenced.getVirtualFile().getParent().findChild("xtend-gen").findChild("OtherClass.java"));
      StringConcatenation _builder_2 = new StringConcatenation();
      _builder_2.append("public class OtherClass extends MyClass {");
      _builder_2.newLine();
      _builder_2.append("}");
      _builder_2.newLine();
      this.assertFileContains(generatedReferencing, _builder_2.toString());
      StringConcatenation _builder_3 = new StringConcatenation();
      _builder_3.append("public class MyClass {");
      _builder_3.newLine();
      _builder_3.append("}");
      _builder_3.newLine();
      this.assertFileContains(generatedReferenced, _builder_3.toString());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testTwoModulesWithoutDependency() {
    try {
      final Module moduleA = this.createModule("moduleA");
      final Module moduleB = this.createModule("moduleB");
      VirtualFile _baseDir = this.getProject().getBaseDir();
      String _plus = ("Module basedir" + _baseDir);
      InputOutput.<String>println(_plus);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("class OtherClass extends MyClass {");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final PsiFile referencing = this.createFile(moduleB, "OtherClass.xtend", _builder.toString());
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("class MyClass {");
      _builder_1.newLine();
      _builder_1.append("}");
      _builder_1.newLine();
      final PsiFile referenced = this.createFile(moduleA, "MyClass.xtend", _builder_1.toString());
      final Provider<VirtualFile> _function = () -> {
        return referencing.getVirtualFile().getParent().findChild("xtend-gen").findChild("OtherClass.java");
      };
      final Provider<VirtualFile> generatedReferencing = _function;
      final Provider<VirtualFile> _function_1 = () -> {
        return referenced.getVirtualFile().getParent().findChild("xtend-gen").findChild("MyClass.java");
      };
      final Provider<VirtualFile> generatedReferenced = _function_1;
      TestCase.assertNotNull(generatedReferencing.get());
      TestCase.assertNotNull(generatedReferenced.get());
      TestCase.assertNull(referenced.getVirtualFile().getParent().findChild("xtend-gen").findChild("OtherClass.java"));
      VirtualFile _get = generatedReferencing.get();
      StringConcatenation _builder_2 = new StringConcatenation();
      _builder_2.append("public class OtherClass /* implements MyClass  */{");
      _builder_2.newLine();
      _builder_2.append("}");
      _builder_2.newLine();
      this.assertFileContains(_get, _builder_2.toString());
      VirtualFile _get_1 = generatedReferenced.get();
      StringConcatenation _builder_3 = new StringConcatenation();
      _builder_3.append("public class MyClass {");
      _builder_3.newLine();
      _builder_3.append("}");
      _builder_3.newLine();
      this.assertFileContains(_get_1, _builder_3.toString());
      ModuleRootModificationUtil.addDependency(moduleB, moduleA);
      VirtualFile _get_2 = generatedReferencing.get();
      StringConcatenation _builder_4 = new StringConcatenation();
      _builder_4.append("public class OtherClass extends MyClass {");
      _builder_4.newLine();
      _builder_4.append("}");
      _builder_4.newLine();
      this.assertFileContains(_get_2, _builder_4.toString());
      VirtualFile _get_3 = generatedReferenced.get();
      StringConcatenation _builder_5 = new StringConcatenation();
      _builder_5.append("public class MyClass {");
      _builder_5.newLine();
      _builder_5.append("}");
      _builder_5.newLine();
      this.assertFileContains(_get_3, _builder_5.toString());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testDeleteModuleWithDependency() {
    try {
      final Module moduleA = this.createModule("moduleA");
      final Module moduleB = this.createModule("moduleB");
      ModuleRootModificationUtil.addDependency(moduleB, moduleA);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("class OtherClass extends MyClass {");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final PsiFile referencing = this.createFile(moduleB, "OtherClass.xtend", _builder.toString());
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("class MyClass {");
      _builder_1.newLine();
      _builder_1.append("}");
      _builder_1.newLine();
      final PsiFile referenced = this.createFile(moduleA, "MyClass.xtend", _builder_1.toString());
      VirtualFile _virtualFile = referencing.getVirtualFile();
      VirtualFile _parent = null;
      if (_virtualFile!=null) {
        _parent=_virtualFile.getParent();
      }
      VirtualFile _findChild = null;
      if (_parent!=null) {
        _findChild=_parent.findChild("xtend-gen");
      }
      VirtualFile _findChild_1 = null;
      if (_findChild!=null) {
        _findChild_1=_findChild.findChild("OtherClass.java");
      }
      final VirtualFile generatedReferencing = _findChild_1;
      VirtualFile _virtualFile_1 = referenced.getVirtualFile();
      VirtualFile _parent_1 = null;
      if (_virtualFile_1!=null) {
        _parent_1=_virtualFile_1.getParent();
      }
      VirtualFile _findChild_2 = null;
      if (_parent_1!=null) {
        _findChild_2=_parent_1.findChild("xtend-gen");
      }
      VirtualFile _findChild_3 = null;
      if (_findChild_2!=null) {
        _findChild_3=_findChild_2.findChild("MyClass.java");
      }
      final VirtualFile generatedReferenced = _findChild_3;
      TestCase.assertNotNull(generatedReferencing);
      TestCase.assertNotNull(generatedReferenced);
      final Runnable _function = () -> {
        final ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(moduleA).getModifiableModel();
        final ModuleManager moduleManager = ModuleManager.getInstance(this.getProject());
        final ModifiableModuleModel modifiableModuleModel = moduleManager.getModifiableModel();
        final List<ModifiableRootModel> otherModules = Collections.<ModifiableRootModel>unmodifiableList(CollectionLiterals.<ModifiableRootModel>newArrayList());
        ModuleDeleteProvider.removeModule(moduleA, modifiableModel, otherModules, modifiableModuleModel);
        ModifiableModelCommitter.multiCommit(otherModules, modifiableModuleModel);
        return;
      };
      ApplicationManager.getApplication().runWriteAction(_function);
      final Function1<IResourceDescription, Boolean> _function_1 = (IResourceDescription it) -> {
        return Boolean.valueOf(it.getURI().toFileString().endsWith("OtherClass.xtend"));
      };
      TestCase.assertTrue(IterableExtensions.<IResourceDescription>exists(this.getIndex().getAllResourceDescriptions(), _function_1));
      final Function1<IResourceDescription, Boolean> _function_2 = (IResourceDescription it) -> {
        return Boolean.valueOf(it.getURI().toFileString().endsWith("MyClass.xtend"));
      };
      TestCase.assertFalse("Deleted module file removed from index", IterableExtensions.<IResourceDescription>exists(this.getIndex().getAllResourceDescriptions(), _function_2));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testTwoModulesDifferentLanguageVersion() {
    try {
      Module _createModule = this.createModule("moduleA");
      final Procedure1<Module> _function = (Module it) -> {
        this.setJavaTargetVersion(it, LanguageLevel.JDK_1_7);
        LibraryUtil.addXtendLibrary(it);
      };
      final Module moduleA = ObjectExtensions.<Module>operator_doubleArrow(_createModule, _function);
      Module _createModule_1 = this.createModule("moduleB");
      final Procedure1<Module> _function_1 = (Module it) -> {
        this.setJavaTargetVersion(it, LanguageLevel.JDK_1_8);
        LibraryUtil.addXtendLibrary(it);
      };
      final Module moduleB = ObjectExtensions.<Module>operator_doubleArrow(_createModule_1, _function_1);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("class ClassA {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("val f = [boolean it | 42]");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      final PsiFile classA = this.createFile(moduleA, "ClassA.xtend", _builder.toString());
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("class ClassB {");
      _builder_1.newLine();
      _builder_1.append("\t");
      _builder_1.append("val f = [boolean it | 42]");
      _builder_1.newLine();
      _builder_1.append("}");
      _builder_1.newLine();
      final PsiFile classB = this.createFile(moduleB, "ClassB.xtend", _builder_1.toString());
      VirtualFile _virtualFile = classA.getVirtualFile();
      VirtualFile _parent = null;
      if (_virtualFile!=null) {
        _parent=_virtualFile.getParent();
      }
      VirtualFile _findChild = null;
      if (_parent!=null) {
        _findChild=_parent.findChild("xtend-gen");
      }
      VirtualFile _findChild_1 = null;
      if (_findChild!=null) {
        _findChild_1=_findChild.findChild("ClassA.java");
      }
      final VirtualFile generatedA = _findChild_1;
      VirtualFile _virtualFile_1 = classB.getVirtualFile();
      VirtualFile _parent_1 = null;
      if (_virtualFile_1!=null) {
        _parent_1=_virtualFile_1.getParent();
      }
      VirtualFile _findChild_2 = null;
      if (_parent_1!=null) {
        _findChild_2=_parent_1.findChild("xtend-gen");
      }
      VirtualFile _findChild_3 = null;
      if (_findChild_2!=null) {
        _findChild_3=_findChild_2.findChild("ClassB.java");
      }
      final VirtualFile generatedB = _findChild_3;
      TestCase.assertNotNull(generatedA);
      TestCase.assertNotNull(generatedB);
      final String aString = Files.readStreamIntoString(generatedA.getInputStream());
      TestCase.assertFalse(aString, aString.contains("->"));
      final String bString = Files.readStreamIntoString(generatedB.getInputStream());
      TestCase.assertTrue(bString, bString.contains("->"));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void assertFileContains(final VirtualFile file, final String string) {
    try {
      InputStream _inputStream = file.getInputStream();
      InputStreamReader _inputStreamReader = new InputStreamReader(_inputStream);
      final String result = CharStreams.toString(_inputStreamReader);
      TestCase.assertEquals(string, result);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected void setJavaTargetVersion(final Module module, final LanguageLevel level) {
    final Function1<FacetType<?, ?>, Boolean> _function = (FacetType<?, ?> it) -> {
      String _stringId = it.getStringId();
      String _iD = XtendLanguage.INSTANCE.getID();
      return Boolean.valueOf(Objects.equal(_stringId, _iD));
    };
    final FacetType<?, ?> facetType = IterableExtensions.<FacetType<?, ?>>findFirst(((Iterable<FacetType<?, ?>>)Conversions.doWrapArray(FacetTypeRegistry.getInstance().getFacetTypes())), _function);
    final Facet facet = FacetManager.getInstance(module).getFacetByType(facetType.getId());
    FacetConfiguration _configuration = facet.getConfiguration();
    XbaseGeneratorConfigurationState _state = ((XtendFacetConfiguration) _configuration).getState();
    _state.setTargetJavaVersion(level.getPresentableText());
  }
  
  @Override
  protected Module createModule(final String moduleName) {
    final Module module = super.createModule(moduleName);
    LightToolingTest.addFacetToModule(module, XtendLanguage.INSTANCE.getID());
    return module;
  }
  
  protected ChunkedResourceDescriptions getIndex() {
    final XtextResourceSet rs = new XtextResourceSet();
    this.getBuilder().installCopyOfResourceDescriptions(rs);
    final ChunkedResourceDescriptions index = ChunkedResourceDescriptions.findInEmfObject(rs);
    return index;
  }
  
  protected XtextAutoBuilderComponent getBuilder() {
    return this.getProject().<XtextAutoBuilderComponent>getComponent(XtextAutoBuilderComponent.class);
  }
}
