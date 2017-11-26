/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.completion;

import com.google.common.base.Objects;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.xtend.idea.LightXtendTest;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("all")
public class XtendCompletionTest extends LightXtendTest {
  public void testClassKeyword() {
    this.complete("cl<caret>");
    this.assertLookupStrings("class");
  }
  
  public void testExtendsKeyword() {
    this.complete("class Foo ex<caret>");
    this.assertLookupStrings("extends");
  }
  
  public void testEmptyFile() {
    this.complete("");
    this.assertLookupStrings(
      "annotation", 
      "class", 
      "enum", 
      "import", 
      "interface", 
      "package");
  }
  
  public void testJvmParameterizedTypeReference_Type() {
    this.complete("class Foo extends Array<caret>");
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testTypeReferenceNoTypeArgs_Type() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("def Array<caret> foo() {}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testJvmParameterizedTypeReference_Type_03() {
    this.complete("class Foo <caret> {");
    this.assertLookupStrings(
      "extends", 
      "implements");
  }
  
  public void testXRelationalExpression_Type() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("def foo(Object foo) {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("foo instanceof Array<caret>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testXImportDeclaration_ImportedType() {
    this.complete("import <caret>");
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testXImportDeclaration_ImportedType_02() {
    this.complete("import java.util.Array<caret>");
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testAppliedXImportDeclaration() {
    this.complete("import java.util.Array<caret>");
    this.myFixture.type("\n");
    TestCase.assertEquals("import java.util.ArrayList", this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testAppliedXImportDeclaration_01() {
    this.complete("import ArrayLis<caret>");
    this.myFixture.type("\n");
    TestCase.assertEquals("import java.util.ArrayList", this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testAppliedTypeReferenceImportsType_01() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass extends Observa<caret> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.Observable");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MyClass extends Observable {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testAppliedTypeReferenceImportsType_02() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("import java.util.Observable");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class MyClass extends Observab<caret> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.Observable");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MyClass extends Observable {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testAppliedTypeReferenceImportsType_03() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass extends LocalCl<caret> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append("class LocalClass {}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("class MyClass extends LocalClass {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("class LocalClass {}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testAppliedTypeReferenceImportsType_04() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("Observ<caret> myField");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.Observable");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MyClass {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("Observable myField");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testAppliedTypeReferenceImportsType_05() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def Observ<caret> myMethod() {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.Observable");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MyClass {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def Observable myMethod() {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testAppliedTypeReferenceImportsType_06() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void myMethod() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("new Observ<caret>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.Observable");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MyClass {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void myMethod() {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("new Observable");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testXConstructorCall_Constructor() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("def foo() {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("new <caret>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class Bar {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("abstract class AbstractBar {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("interface InterfaceBar {}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("Bar"));
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("AbstractBar"));
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("InterfaceBar"));
  }
  
  public void testXTypeLiteral_Type() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("def foo() {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("typeof(<caret>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("Foo"));
  }
  
  public void testXAnnotation_AnnotationType() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Deprecat<caret>");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("Deprecated"));
  }
  
  public void testXAnnotation_AnnotationType_02() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Array<caret>");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testXAnnotation_Value() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@org.eclipse.xtend.lib.macro.Active(Array<caret>)");
    _builder.newLine();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testOverrideCompletion_01() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("overr<caret>");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("override equals(Object)"));
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("override hashCode()"));
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("override toString()"));
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("override clone()"));
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("override finalize()"));
  }
  
  public void testOverrideCompletion_02() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("override toString() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\'foo\'");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("overr<caret>");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("override toString()"));
  }
  
  public void testOverrideCompletion_03() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void equals() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\'foo\'");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("overr<caret>");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("override equals(Object)"));
  }
  
  public void testOverrideCompletion_04() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("toString<caret>");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("class MyClass {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("override toString() {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("throw new UnsupportedOperationException()");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
    TestCase.assertEquals("throw new UnsupportedOperationException()", this.myFixture.getEditor().getSelectionModel().getSelectedText());
  }
  
  public void testOverrideCompletion_05() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass extends MySuperClass<String> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("doStuff<caret>");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class MySuperClass<T> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def <X> T doStuff(java.util.List<T> myArg, X x) throws java.io.IOException {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return null");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.List");
    _builder_1.newLine();
    _builder_1.append("import java.io.IOException");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MyClass extends MySuperClass<String> {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("override <X> doStuff(List<String> myArg, X x) throws IOException {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("throw new UnsupportedOperationException()");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MySuperClass<T> {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def <X> T doStuff(java.util.List<T> myArg, X x) throws java.io.IOException {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("return null");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
    TestCase.assertEquals("throw new UnsupportedOperationException()", this.myFixture.getEditor().getSelectionModel().getSelectedText());
  }
  
  public void testOverrideCompletion_06() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class MyClass extends MySuperClass<String> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("ne<caret>");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class MySuperClass<T> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("new (java.util.List<T> myArg) throws java.io.IOException {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.List");
    _builder_1.newLine();
    _builder_1.append("import java.io.IOException");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MyClass extends MySuperClass<String> {");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("new (List<String> myArg) throws IOException {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("super(myArg)");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MySuperClass<T> {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("new (java.util.List<T> myArg) throws java.io.IOException {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
    int _indexOf = this.myFixture.getEditor().getDocument().getText().indexOf("super(myArg)");
    int _length = "super(myArg)".length();
    final int offset = (_indexOf + _length);
    TestCase.assertEquals(offset, this.myFixture.getEditor().getCaretModel().getCurrentCaret().getOffset());
  }
  
  public void testOverrideCompletion_07() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def void myMethods() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("val runnable = new MySuperClass<String>() {");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("doStuff<caret>");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("runnable.run()");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class MySuperClass<T> {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def <X> T doStuff(java.util.List<T> myArg, X x) throws java.io.IOException {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return null");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("import java.util.List");
    _builder_1.newLine();
    _builder_1.append("import java.io.IOException");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class Foo {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def void myMethods() {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("val runnable = new MySuperClass<String>() {");
    _builder_1.newLine();
    _builder_1.append("\t\t\t");
    _builder_1.append("override <X> doStuff(List<String> myArg, X x) throws IOException {");
    _builder_1.newLine();
    _builder_1.append("\t\t\t\t");
    _builder_1.append("throw new UnsupportedOperationException()");
    _builder_1.newLine();
    _builder_1.append("\t\t\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("runnable.run()");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.newLine();
    _builder_1.append("class MySuperClass<T> {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def <X> T doStuff(java.util.List<T> myArg, X x) throws java.io.IOException {");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("return null");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("}");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
    TestCase.assertEquals("throw new UnsupportedOperationException()", this.myFixture.getEditor().getSelectionModel().getSelectedText());
  }
  
  public void testOverrideCompletion_08() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\'foo\'");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("overr<caret>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("override toString()"));
  }
  
  public void testOverrideCompletion_09() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\'foo\'");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("overr<caret>");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("override toString()"));
  }
  
  public void testOverrideCompletion_10() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo(overr<caret>) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\'foo\'");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append(" ");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("override toString()"));
  }
  
  public void testOverrideCompletion_11() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def overr<caret> foo(overr<caret>) {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\'foo\'");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append(" ");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("override toString()"));
  }
  
  public void testSingleLineComment() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("// Array<caret>");
    _builder.newLine();
    _builder.append("class Foo {}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testMultiLineComment() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("/* Array<caret> */");
    _builder.newLine();
    _builder.append("class Foo {}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertTrue(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testStringLiteral_01() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\'Array<caret>\'");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testStringLiteral_02() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("\"Array<caret>\"");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testRichString_01() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo() \'");
    _builder.append("\'\'");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("Array<caret>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("\'");
    _builder.append("\'\'");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final List<String> lookupElementStrings = this.myFixture.getLookupElementStrings();
    TestCase.assertFalse(lookupElementStrings.toString(), lookupElementStrings.contains("ArrayList"));
  }
  
  public void testRichString_02() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo() \'");
    _builder.append("\'\'");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("<caret>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("\'");
    _builder.append("\'\'");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    this.myFixture.type("\n");
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("class Foo {");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("def foo() \'");
    _builder_1.append("\'\'");
    _builder_1.newLine();
    _builder_1.append("\t\t");
    _builder_1.append("«", "\t\t");
    _builder_1.append("»", "\t\t");
    _builder_1.newLineIfNotEmpty();
    _builder_1.append("\t");
    _builder_1.append("\'");
    _builder_1.append("\'\'");
    _builder_1.newLine();
    _builder_1.append("}");
    _builder_1.newLine();
    TestCase.assertEquals(_builder_1.toString(), this.myFixture.getEditor().getDocument().getText().toString());
  }
  
  public void testJavaTypeInExpressionContext() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Foo {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def foo() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("<caret>");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("\t");
    _builder.append("def zzz() {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    this.complete(_builder.toString());
    final Iterable<Pair<Integer, String>> lookupElementStrings = IterableExtensions.<String>indexed(this.myFixture.getLookupElementStrings());
    final Function1<Pair<Integer, String>, Boolean> _function = (Pair<Integer, String> it) -> {
      String _value = it.getValue();
      return Boolean.valueOf(Objects.equal(_value, "zzz"));
    };
    final Pair<Integer, String> methodProposal = IterableExtensions.<Pair<Integer, String>>findFirst(lookupElementStrings, _function);
    TestCase.assertNotNull(methodProposal);
    final Function1<Pair<Integer, String>, Boolean> _function_1 = (Pair<Integer, String> it) -> {
      String _value = it.getValue();
      return Boolean.valueOf(Objects.equal(_value, "ArrayList"));
    };
    final Pair<Integer, String> typeProposal = IterableExtensions.<Pair<Integer, String>>findFirst(lookupElementStrings, _function_1);
    TestCase.assertNotNull(typeProposal);
    Integer _key = methodProposal.getKey();
    Integer _key_1 = typeProposal.getKey();
    boolean _lessThan = (_key.compareTo(_key_1) < 0);
    TestCase.assertTrue(_lessThan);
  }
}
