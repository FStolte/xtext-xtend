/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.execution;

import com.google.common.base.Objects;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.JavaCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.testFramework.MapDataContext;
import com.intellij.testIntegration.JavaTestFramework;
import com.intellij.testIntegration.TestFramework;
import com.intellij.util.containers.ContainerUtilRt;
import java.util.Collections;
import junit.framework.TestCase;
import org.eclipse.xtend.core.idea.execution.XtendApplicationConfigurationProducer;
import org.eclipse.xtend.core.idea.execution.XtendJunitClassConfigurationProducer;
import org.eclipse.xtend.core.idea.execution.XtendJunitMethodConfigurationProducer;
import org.eclipse.xtend.idea.XtendIdeaTestCase;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.psi.impl.BaseXtextFile;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * @author dhuebner - Initial contribution and API
 */
@SuppressWarnings("all")
public class TraceBasedConfigurationProducerTest extends XtendIdeaTestCase {
  @Override
  protected boolean isTestSource(final VirtualFile srcFolder) {
    return true;
  }
  
  public void testApplicationConfiguration_1() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("/**");
      _builder.newLine();
      _builder.append("* Test");
      _builder.newLine();
      _builder.append("*/");
      _builder.newLine();
      _builder.append("class XtendMainClass {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("/** test method */");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def static void m|ain(String[] args) {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("println(\"Hello\")");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("println(\"World\")");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      String code = _builder.toString();
      final int cursorIdx = code.indexOf("|");
      code = code.replace("|", "");
      Pair<String, String> _mappedTo = Pair.<String, String>of("XtendMainClass.xtend", code);
      final VirtualFile file = this.addFile(_mappedTo);
      final PsiFile xtendFile = this.getPsiManager().findFile(file);
      TestCase.assertTrue((xtendFile instanceof BaseXtextFile));
      final PsiElement sourceElement = xtendFile.getViewProvider().findElementAt(cursorIdx);
      final ApplicationConfiguration configuration = this.<ApplicationConfiguration>createConfiguration(sourceElement, XtendApplicationConfigurationProducer.class);
      TestCase.assertEquals(Collections.<Module>singleton(this.getModule()), ContainerUtilRt.<Module>newHashSet(configuration.getModules()));
      TestCase.assertTrue(PsiMethodUtil.hasMainMethod(configuration.getMainClass()));
      TestCase.assertEquals("XtendMainClass", configuration.getMainClass().getQualifiedName());
      TestCase.assertEquals("XtendMainClass", configuration.getName());
      TraceBasedConfigurationProducerTest.checkCanRun(configuration);
      final ApplicationConfiguration sameConfiguration = this.<ApplicationConfiguration>createConfiguration(sourceElement, XtendApplicationConfigurationProducer.class);
      final RunConfigurationProducer<ApplicationConfiguration> producer = RunConfigurationProducer.<XtendApplicationConfigurationProducer>getInstance(XtendApplicationConfigurationProducer.class);
      TestCase.assertTrue(producer.isConfigurationFromContext(sameConfiguration, this.createContext(sourceElement)));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testApplicationConfiguration_2() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("import java.util.List");
      _builder.newLine();
      _builder.append("|");
      _builder.newLine();
      _builder.newLine();
      _builder.append("/**");
      _builder.newLine();
      _builder.append("* Test");
      _builder.newLine();
      _builder.append("*/");
      _builder.newLine();
      _builder.append("class XtendMainClass {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("/** test method */");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def static void main(String[] args) {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("println(\"Hello\")");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("println(\"World\")");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      String code = _builder.toString();
      final int cursorIdx = code.indexOf("|");
      code = code.replace("|", "");
      Pair<String, String> _mappedTo = Pair.<String, String>of("XtendMainClass.xtend", code);
      final VirtualFile file = this.addFile(_mappedTo);
      final PsiFile xtendFile = this.getPsiManager().findFile(file);
      TestCase.assertTrue((xtendFile instanceof BaseXtextFile));
      final PsiElement sourceElement = xtendFile.getViewProvider().findElementAt(cursorIdx);
      final ConfigurationContext context = this.createContext(sourceElement);
      final XtendApplicationConfigurationProducer producer = RunConfigurationProducer.<XtendApplicationConfigurationProducer>getInstance(XtendApplicationConfigurationProducer.class);
      final ConfigurationFromContext confFromContext = producer.createConfigurationFromContext(context);
      TestCase.assertNotNull(confFromContext);
      TraceBasedConfigurationProducerTest.checkCanRun(confFromContext.getConfiguration());
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testApplicationConfigurationNoSelection() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("class XtendMainClass {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def static void main(String[] args) {");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      Pair<String, String> _mappedTo = Pair.<String, String>of("XtendMainClass.xtend", _builder.toString());
      final VirtualFile file = this.addFile(_mappedTo);
      final PsiFile xtendFile = this.getPsiManager().findFile(file);
      final ApplicationConfiguration conf = this.<ApplicationConfiguration>createConfiguration(xtendFile, XtendApplicationConfigurationProducer.class);
      TraceBasedConfigurationProducerTest.checkCanRun(conf);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testJunitConfigurationNoSelection() {
    try {
      this.addJunit4Lib(this.getModule());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("import org.junit.Assert");
      _builder.newLine();
      _builder.append("import org.junit.Test");
      _builder.newLine();
      _builder.newLine();
      _builder.append("class XtendJunitClass extends Assert{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("@Test");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def void testMethod() {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("assertTrue(true)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("@Test");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def void testMethod2() {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("assertTrue(true)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      Pair<String, String> _mappedTo = Pair.<String, String>of("XtendJunitClass.xtend", _builder.toString());
      final VirtualFile file = this.addFile(_mappedTo);
      final PsiFile xtendFile = this.getPsiManager().findFile(file);
      final JUnitConfiguration conf = this.<JUnitConfiguration>createConfiguration(xtendFile, XtendJunitClassConfigurationProducer.class);
      TestCase.assertEquals("XtendJunitClass", conf.getPersistentData().MAIN_CLASS_NAME);
      TestCase.assertEquals("class", conf.getPersistentData().TEST_OBJECT);
      TraceBasedConfigurationProducerTest.checkCanRun(conf);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testJunitConfigurationMethod_1() {
    try {
      this.addJunit4Lib(this.getModule());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("import org.junit.Assert");
      _builder.newLine();
      _builder.append("import org.junit.Test");
      _builder.newLine();
      _builder.newLine();
      _builder.append("class XtendJunitClass extends Assert{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("@Test");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def void test|Method() {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("assertTrue(true)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("@Test");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def void testMethod2() {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("assertTrue(true)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      String code = _builder.toString();
      final int cursorIdx = code.indexOf("|");
      code = code.replace("|", "");
      Pair<String, String> _mappedTo = Pair.<String, String>of("XtendJunitClass.xtend", code);
      final VirtualFile file = this.addFile(_mappedTo);
      final PsiFile xtendFile = this.getPsiManager().findFile(file);
      TestCase.assertTrue((xtendFile instanceof BaseXtextFile));
      final PsiElement sourceElement = xtendFile.getViewProvider().findElementAt(cursorIdx);
      final JUnitConfiguration configuration = this.<JUnitConfiguration>createConfiguration(sourceElement, XtendJunitMethodConfigurationProducer.class);
      TestCase.assertEquals("XtendJunitClass", configuration.getPersistentData().MAIN_CLASS_NAME);
      TestCase.assertEquals("method", configuration.getPersistentData().TEST_OBJECT);
      TestCase.assertEquals("testMethod", configuration.getPersistentData().METHOD_NAME);
      TraceBasedConfigurationProducerTest.checkCanRun(configuration);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void testJunitConfigurationMethod_2() {
    try {
      this.addJunit4Lib(this.getModule());
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("import org.junit.Assert");
      _builder.newLine();
      _builder.append("import org.junit.Test");
      _builder.newLine();
      _builder.newLine();
      _builder.append("class XtendJunitClass extends Assert{");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("@Test");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def void testMethod() {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("assertTrue(true)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("@Test");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("def void testM|ethod2() {");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("assertTrue(true)");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("}");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      String code = _builder.toString();
      final int cursorIdx = code.indexOf("|");
      code = code.replace("|", "");
      Pair<String, String> _mappedTo = Pair.<String, String>of("XtendMainClass.xtend", code);
      final VirtualFile file = this.addFile(_mappedTo);
      final PsiFile xtendFile = this.getPsiManager().findFile(file);
      TestCase.assertTrue((xtendFile instanceof BaseXtextFile));
      final PsiElement sourceElement = xtendFile.getViewProvider().findElementAt(cursorIdx);
      final JUnitConfiguration configuration = this.<JUnitConfiguration>createConfiguration(sourceElement, XtendJunitMethodConfigurationProducer.class);
      TestCase.assertEquals("XtendJunitClass", configuration.getPersistentData().MAIN_CLASS_NAME);
      TestCase.assertEquals("method", configuration.getPersistentData().TEST_OBJECT);
      TestCase.assertEquals("testMethod2", configuration.getPersistentData().METHOD_NAME);
      TraceBasedConfigurationProducerTest.checkCanRun(configuration);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected void addJunit4Lib(final Module module) {
    final TestFramework[] frameworks = Extensions.<TestFramework>getExtensions(TestFramework.EXTENSION_NAME);
    final Function1<TestFramework, Boolean> _function = (TestFramework it) -> {
      String _name = it.getName();
      return Boolean.valueOf(Objects.equal("JUnit4", _name));
    };
    final TestFramework junit4 = IterableExtensions.<TestFramework>findFirst(((Iterable<TestFramework>)Conversions.doWrapArray(frameworks)), _function);
    boolean _isLibraryAttached = junit4.isLibraryAttached(module);
    boolean _not = (!_isLibraryAttached);
    if (_not) {
      if ((junit4 instanceof JavaTestFramework)) {
        ((JavaTestFramework)junit4).setupLibrary(module);
      }
    }
  }
  
  protected <T extends RunConfiguration> T createConfiguration(final PsiElement psiElement, final Class<? extends RunConfigurationProducer<T>> clazz) {
    final ConfigurationContext context = this.createContext(psiElement);
    final RunConfigurationProducer<T> producer = RunConfigurationProducer.<RunConfigurationProducer<T>>getInstance(clazz);
    TestCase.assertNotNull(producer);
    final ConfigurationFromContext fromContext = producer.createConfigurationFromContext(context);
    TestCase.assertNotNull(fromContext);
    RunConfiguration _configuration = fromContext.getConfiguration();
    return ((T) _configuration);
  }
  
  private ConfigurationContext createContext(@NotNull final PsiElement psiClass) {
    final MapDataContext dataContext = new MapDataContext();
    return this.createContext(psiClass, dataContext);
  }
  
  private ConfigurationContext createContext(@NotNull final PsiElement psiClass, @NotNull final MapDataContext dataContext) {
    dataContext.<Project>put(CommonDataKeys.PROJECT, this.myProject);
    Module _data = LangDataKeys.MODULE.getData(dataContext);
    boolean _tripleEquals = (_data == null);
    if (_tripleEquals) {
      dataContext.<Module>put(LangDataKeys.MODULE, ModuleUtilCore.findModuleForPsiElement(psiClass));
    }
    dataContext.<Location<?>>put(Location.DATA_KEY, PsiLocation.<PsiElement>fromPsiElement(psiClass));
    return ConfigurationContext.getFromContext(dataContext);
  }
  
  public static JavaParameters checkCanRun(final RunConfiguration configuration) throws ExecutionException {
    final RunProfileState state = ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), configuration).build().getState();
    TestCase.assertNotNull(state);
    TestCase.assertTrue((state instanceof JavaCommandLine));
    try {
      configuration.checkConfiguration();
    } catch (final Throwable _t) {
      if (_t instanceof RuntimeConfigurationError) {
        final RuntimeConfigurationError e = (RuntimeConfigurationError)_t;
        String _message = e.getMessage();
        String _plus = ("cannot run: " + _message);
        TestCase.fail(_plus);
      } else if (_t instanceof RuntimeConfigurationException) {
        final RuntimeConfigurationException e_1 = (RuntimeConfigurationException)_t;
        String _message_1 = e_1.getMessage();
        String _plus_1 = ("cannot run: " + _message_1);
        TestCase.fail(_plus_1);
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    return ((JavaCommandLine) state).getJavaParameters();
  }
}
