/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.config;

import com.google.inject.Inject;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.Consumer;
import junit.framework.TestCase;
import org.eclipse.xtend.core.idea.config.XtendLibraryConfigurator;
import org.eclipse.xtend.core.idea.framework.XtendLibraryDescription;
import org.eclipse.xtend.core.idea.intentions.XtendIntentionsProvider;
import org.eclipse.xtend.idea.LightXtendTest;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * @author dhuebner - Initial contribution and API
 */
@SuppressWarnings("all")
public class XtendLibraryManagerTest extends LightXtendTest {
  @Inject
  private XtendLibraryConfigurator manager;
  
  private Procedure1<? super Module> libraryAdder;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    final Procedure1<Module> _function = (Module module) -> {
      final Consumer<ModifiableRootModel> _function_1 = (ModifiableRootModel rootModel) -> {
        this.manager.ensureXtendLibAvailable(rootModel);
      };
      ModuleRootModificationUtil.updateModel(module, _function_1);
    };
    this.libraryAdder = _function;
  }
  
  @Override
  protected void configureModule(final Module module, final ModifiableRootModel model, final ContentEntry contentEntry) {
  }
  
  public void testNoLibErrors() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<error descr=\"Couldn\'t find the mandatory library \'org.eclipse.xtext.xbase.lib\' 2.8.0 or higher on the project\'s classpath.\">class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def static void main(String... args) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("<error descr=\"The method println(String) is undefined\">println</error>(\"Foo\")");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}</error>");
    _builder.newLine();
    this.configureByText(_builder.toString());
    this.myFixture.checkHighlighting();
  }
  
  public void testLibAdded() {
    this.libraryAdder.apply(this.myModule);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def static void main(String... args) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("println(\"Foo\")");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.configureByText(_builder.toString());
    this.myFixture.checkHighlighting();
  }
  
  public void testXtendLibMissingIntention() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def static void main(String... args) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("println(\"Foo\")");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.assertIntentionApplication(XtendIntentionsProvider.AddXtendLibToClassPathIntentionAction.TEXT, _builder.toString());
  }
  
  public long assertIntentionApplication(final String intentionId, final String source) {
    long _xblockexpression = (long) 0;
    {
      final PsiFile file = this.configureByText(source);
      final IntentionAction intention = this.myFixture.findSingleIntention(intentionId);
      final Runnable _function = () -> {
        final Runnable _function_1 = () -> {
          intention.invoke(file.getProject(), this.getEditor(), file);
          PsiDocumentManager.getInstance(this.getProject()).commitAllDocuments();
        };
        ApplicationManager.getApplication().runWriteAction(_function_1);
      };
      CommandProcessor.getInstance().executeCommand(this.getProject(), _function, "", "");
      _xblockexpression = this.myFixture.checkHighlighting();
    }
    return _xblockexpression;
  }
  
  public void testLibNotAddedTwice() {
    this.libraryAdder.apply(this.myModule);
    this.libraryAdder.apply(this.myModule);
    final Function1<OrderEntry, Boolean> _function = (OrderEntry it) -> {
      return Boolean.valueOf(it.getPresentableName().startsWith(XtendLibraryDescription.XTEND_LIBRARY_NAME));
    };
    final Iterable<OrderEntry> xtendlibs = IterableExtensions.<OrderEntry>filter(((Iterable<OrderEntry>)Conversions.doWrapArray(ModuleRootManager.getInstance(this.myModule).getOrderEntries())), _function);
    TestCase.assertEquals("Xtend libraries in module", 1, IterableExtensions.size(xtendlibs));
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def static void main(String... args) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("println(\"Foo\")");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.configureByText(_builder.toString());
    this.myFixture.checkHighlighting();
  }
}
