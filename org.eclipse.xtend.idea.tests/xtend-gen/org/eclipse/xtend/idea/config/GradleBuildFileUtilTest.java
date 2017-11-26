/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.config;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiFile;
import junit.framework.TestCase;
import org.eclipse.xtend.core.idea.config.GradleBuildFileUtility;
import org.eclipse.xtend.core.idea.config.XtendLibraryConfigurator;
import org.eclipse.xtend.idea.LightXtendTest;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.idea.util.PlatformUtil;
import org.eclipse.xtext.util.XtextVersion;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.blocks.GrClosableBlock;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrApplicationStatement;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression;

/**
 * @author dhuebner - Initial contribution and API
 */
@SuppressWarnings("all")
public class GradleBuildFileUtilTest extends LightXtendTest {
  private final GradleBuildFileUtility util = new GradleBuildFileUtility();
  
  public void testSetupGradleBuildEmptyFile() {
    PsiFile _addFileToProject = this.myFixture.addFileToProject("build.gradle", "");
    final GroovyFile buildFile = ((GroovyFile) _addFileToProject);
    TestCase.assertNotNull(buildFile);
    final Runnable _function = () -> {
      this.util.setupGradleBuild(this.myModule, buildFile);
    };
    WriteCommandAction.runWriteCommandAction(this.myFixture.getProject(), _function);
    final Runnable _function_1 = () -> {
      this.util.setupGradleBuild(this.myModule, buildFile);
    };
    WriteCommandAction.runWriteCommandAction(this.myFixture.getProject(), _function_1);
    this.assertTree(buildFile);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("buildscript {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("repositories {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("jcenter()");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("classpath \'org.xtext:xtext-gradle-plugin:");
    String _xtendGradlePluginVersion = XtextVersion.getCurrent().getXtendGradlePluginVersion();
    _builder.append(_xtendGradlePluginVersion, "        ");
    _builder.append("\'");
    _builder.newLineIfNotEmpty();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append("apply plugin: \'org.xtext.xtend\' ");
    TestCase.assertEquals(_builder.toString().trim(), buildFile.getText());
  }
  
  public void testSetupGradleBuildFileWithContent() {
    PsiFile _addFileToProject = this.myFixture.addFileToProject("build.gradle", "buildscript{dependencies{}}");
    final GroovyFile buildFile = ((GroovyFile) _addFileToProject);
    TestCase.assertNotNull(buildFile);
    final Runnable _function = () -> {
      this.util.setupGradleBuild(this.myModule, buildFile);
    };
    WriteCommandAction.runWriteCommandAction(this.myFixture.getProject(), _function);
    final Runnable _function_1 = () -> {
      this.util.setupGradleBuild(this.myModule, buildFile);
    };
    WriteCommandAction.runWriteCommandAction(this.myFixture.getProject(), _function_1);
    this.assertTree(buildFile);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("buildscript{dependencies{");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("classpath \'org.xtext:xtext-gradle-plugin:");
    String _xtendGradlePluginVersion = XtextVersion.getCurrent().getXtendGradlePluginVersion();
    _builder.append(_xtendGradlePluginVersion, "    ");
    _builder.append("\'");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("repositories {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("jcenter()");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append("apply plugin: \'org.xtext.xtend\' ");
    TestCase.assertEquals(_builder.toString().trim(), buildFile.getText());
  }
  
  public void testAddDependencyEmptyFile() {
    PsiFile _addFileToProject = this.myFixture.addFileToProject("build.gradle", "");
    final GroovyFile buildFile = ((GroovyFile) _addFileToProject);
    TestCase.assertNotNull(buildFile);
    final Runnable _function = () -> {
      String _string = XtendLibraryConfigurator.xtendLibMavenId().toString();
      String _plus = ("compile \'" + _string);
      String _plus_1 = (_plus + "\'");
      this.util.addDependency(buildFile, _plus_1);
    };
    WriteCommandAction.runWriteCommandAction(this.myFixture.getProject(), _function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("dependencies {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("compile \'");
    String _string = XtendLibraryConfigurator.xtendLibMavenId().toString();
    _builder.append(_string, "    ");
    _builder.append("\'");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    TestCase.assertEquals(_builder.toString(), buildFile.getText());
  }
  
  public void testAddDependencyFileWithContent() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("dependencies {}");
    PsiFile _addFileToProject = this.myFixture.addFileToProject("build.gradle", _builder.toString());
    final GroovyFile buildFile = ((GroovyFile) _addFileToProject);
    TestCase.assertNotNull(buildFile);
    final Runnable _function = () -> {
      String _string = XtendLibraryConfigurator.xtendLibMavenId().toString();
      String _plus = ("compile \'" + _string);
      String _plus_1 = (_plus + "\'");
      this.util.addDependency(buildFile, _plus_1);
    };
    WriteCommandAction.runWriteCommandAction(this.myFixture.getProject(), _function);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("dependencies {");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("compile \'");
    String _string = XtendLibraryConfigurator.xtendLibMavenId().toString();
    _builder_1.append(_string, "    ");
    _builder_1.append("\'");
    _builder_1.newLineIfNotEmpty();
    _builder_1.append("}");
    TestCase.assertEquals(_builder_1.toString(), buildFile.getText());
  }
  
  public void testIsGradleModule() {
    TestCase.assertTrue(new PlatformUtil().isGradleInstalled());
    TestCase.assertFalse(this.util.isGradleedModule(this.myFixture.getModule()));
  }
  
  public void assertTree(final GroovyFile buildFile) {
    TestCase.assertEquals(2, buildFile.getStatements().length);
    final Function1<GrMethodCallExpression, Boolean> _function = (GrMethodCallExpression it) -> {
      String _text = it.getInvokedExpression().getText();
      return Boolean.valueOf(Objects.equal(_text, "buildscript"));
    };
    final Iterable<GrMethodCallExpression> bsCol = IterableExtensions.<GrMethodCallExpression>filter(Iterables.<GrMethodCallExpression>filter(((Iterable<?>)Conversions.doWrapArray(buildFile.getStatements())), GrMethodCallExpression.class), _function);
    TestCase.assertEquals(1, IterableExtensions.size(bsCol));
    final GrMethodCallExpression bs = IterableExtensions.<GrMethodCallExpression>head(bsCol);
    TestCase.assertEquals("buildscript", bs.getInvokedExpression().getText());
    final Iterable<GrMethodCallExpression> children = Iterables.<GrMethodCallExpression>filter(((Iterable<?>)Conversions.doWrapArray(IterableExtensions.<GrClosableBlock>head(((Iterable<GrClosableBlock>)Conversions.doWrapArray(bs.getClosureArguments()))).getChildren())), GrMethodCallExpression.class);
    TestCase.assertEquals(2, IterableExtensions.size(children));
    final Function1<GrMethodCallExpression, Boolean> _function_1 = (GrMethodCallExpression it) -> {
      String _text = it.getInvokedExpression().getText();
      return Boolean.valueOf(Objects.equal(_text, "dependencies"));
    };
    final GrMethodCallExpression dps = IterableExtensions.<GrMethodCallExpression>head(IterableExtensions.<GrMethodCallExpression>filter(children, _function_1));
    TestCase.assertNotNull(dps);
    TestCase.assertEquals(3, dps.getChildren().length);
    final GrClosableBlock closureBlock = IterableExtensions.<GrClosableBlock>head(((Iterable<GrClosableBlock>)Conversions.doWrapArray(dps.getClosureArguments())));
    TestCase.assertEquals(1, closureBlock.getStatements().length);
    final GrApplicationStatement clEntry = IterableExtensions.<GrApplicationStatement>head(Iterables.<GrApplicationStatement>filter(((Iterable<?>)Conversions.doWrapArray(closureBlock.getStatements())), GrApplicationStatement.class));
    TestCase.assertNotNull(clEntry);
    TestCase.assertEquals("classpath", clEntry.getInvokedExpression().getText());
    TestCase.assertTrue(clEntry.getArgumentList().getText().startsWith("\'org.xtext:xtext-gradle-plugin:"));
    final Function1<GrMethodCallExpression, Boolean> _function_2 = (GrMethodCallExpression it) -> {
      String _text = it.getInvokedExpression().getText();
      return Boolean.valueOf(Objects.equal(_text, "repositories"));
    };
    final GrMethodCallExpression repos = IterableExtensions.<GrMethodCallExpression>head(IterableExtensions.<GrMethodCallExpression>filter(children, _function_2));
    TestCase.assertNotNull(repos);
    final GrMethodCallExpression jcenterEntry = IterableExtensions.<GrMethodCallExpression>head(Iterables.<GrMethodCallExpression>filter(((Iterable<?>)Conversions.doWrapArray(IterableExtensions.<GrClosableBlock>head(((Iterable<GrClosableBlock>)Conversions.doWrapArray(repos.getClosureArguments()))).getStatements())), GrMethodCallExpression.class));
    TestCase.assertEquals("jcenter", jcenterEntry.getInvokedExpression().getText());
  }
}
