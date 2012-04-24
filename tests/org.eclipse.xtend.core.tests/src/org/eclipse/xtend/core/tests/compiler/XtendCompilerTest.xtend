package org.eclipse.xtend.core.tests.compiler

import org.eclipse.xtend.core.tests.AbstractXtendTestCase
import org.eclipse.xtext.xbase.compiler.JvmModelGenerator
import com.google.inject.Inject
import org.eclipse.xtend.core.jvmmodel.IXtendJvmAssociations
import org.junit.Test
import org.junit.Ignore

class XtendCompilerTest extends AbstractXtendTestCase {
	
	@Inject JvmModelGenerator generator
	
	@Inject extension IXtendJvmAssociations 
	
	@Test
	def testDataClasses_01() { 
		assertCompilesTo('''
			@Data class Foo {
				String name
				boolean myFlag
				java.lang.Iterable<? extends Foo> references
			}
		''', '''
			import org.eclipse.xtend.lib.Data;
			
			@Data
			@SuppressWarnings("all")
			public class Foo {
			  private final String _name;
			  
			  public String getName() {
			    return this._name;
			  }
			  
			  private final boolean _myFlag;
			  
			  public boolean isMyFlag() {
			    return this._myFlag;
			  }
			  
			  private final Iterable<? extends Foo> _references;
			  
			  public Iterable<? extends Foo> getReferences() {
			    return this._references;
			  }
			  
			  public Foo(final String name, final boolean myFlag, final Iterable<? extends Foo> references) {
			    super();
			    this._name = name;
			    this._myFlag = myFlag;
			    this._references = references;
			  }
			  
			  @Override
			  public int hashCode() {
			    final int prime = 31;
			    int result = 1;
			    result = prime * result + ((_name== null) ? 0 : _name.hashCode());
			    result = prime * result + (_myFlag ? 1231 : 1237);
			    result = prime * result + ((_references== null) ? 0 : _references.hashCode());
			    return result;
			  }
			  
			  @Override
			  public boolean equals(final Object obj) {
			    if (this == obj)
			      return true;
			    if (obj == null)
			      return false;
			    if (getClass() != obj.getClass())
			      return false;
			    Foo other = (Foo) obj;
			    if (_name == null) {
			      if (other._name != null)
			        return false;
			    } else if (!_name.equals(other._name))
			      return false;
			    if (other._myFlag != _myFlag)
			      return false;
			    if (_references == null) {
			      if (other._references != null)
			        return false;
			    } else if (!_references.equals(other._references))
			      return false;
			    return true;
			  }
			  
			  @Override
			  public String toString() {
			    String superToString = super.toString();
			    if (superToString == null) superToString = "";
			    int start = superToString.indexOf('[');
			    int end = superToString.lastIndexOf(']');
			    superToString = (start == -1 || end == -1) ? "" : (superToString.substring(start + 1,end) + ", ");
			    return "Foo [" + superToString 
			         + "_name = " + _name + ", "
			         + "_myFlag = " + _myFlag + ", "
			         + "_references = " + _references + "]";
			  }
			}
		''')
	}
	@Test
	def testDataClasses_02() { 
		assertCompilesTo('''
			@Data class Foo {
				String name = 'foo'
			}
		''', '''
			import org.eclipse.xtend.lib.Data;
			
			@Data
			@SuppressWarnings("all")
			public class Foo {
			  private final String _name = "foo";
			  
			  public String getName() {
			    return this._name;
			  }
			  
			  public Foo() {
			    super();
			  }
			  
			  @Override
			  public int hashCode() {
			    final int prime = 31;
			    int result = 1;
			    result = prime * result + ((_name== null) ? 0 : _name.hashCode());
			    return result;
			  }
			  
			  @Override
			  public boolean equals(final Object obj) {
			    if (this == obj)
			      return true;
			    if (obj == null)
			      return false;
			    if (getClass() != obj.getClass())
			      return false;
			    Foo other = (Foo) obj;
			    if (_name == null) {
			      if (other._name != null)
			        return false;
			    } else if (!_name.equals(other._name))
			      return false;
			    return true;
			  }
			  
			  @Override
			  public String toString() {
			    String superToString = super.toString();
			    if (superToString == null) superToString = "";
			    int start = superToString.indexOf('[');
			    int end = superToString.lastIndexOf(']');
			    superToString = (start == -1 || end == -1) ? "" : (superToString.substring(start + 1,end) + ", ");
			    return "Foo [" + superToString 
			         + "_name = " + _name + "]";
			  }
			}
		''')
	}
	
	@Test
	def testBug372864() { 
		assertCompilesTo('''
			class Foo {
				def protected String testSwitch(Object e) {
				    switch e {
				       String : return ""
				       default: null
				    }
				}
			}
		''', '''
			@SuppressWarnings("all")
			public class Foo {
			  protected String testSwitch(final Object e) {
			    String _switchResult = null;
			    boolean _matched = false;
			    if (!_matched) {
			      if (e instanceof String) {
			        final String _string = (String)e;
			        _matched=true;
			        return "";
			      }
			    }
			    if (!_matched) {
			      _switchResult = null;
			    }
			    return _switchResult;
			  }
			}
		''')
	}
	
	@Test
	def testJavaLangReflectImport() { 
		assertCompilesTo('''
			package foo
			class Bar {
				def void doStuff(java.lang.reflect.Method m) {}
			}
		''', '''
			package foo;

			import java.lang.reflect.Method;
			
			@SuppressWarnings("all")
			public class Bar {
			  public void doStuff(final Method m) {
			  }
			}
		''')
	}
	@Test
	def testFoo() { 
		assertCompilesTo('''
			public class Foo  {
			    def foo() {
			        switch null {
			            case null : [Object it|it]
			            case null : [Integer it|it]
			        }
			    }    
			}
		''', '''
			import com.google.common.base.Objects;
			import org.eclipse.xtext.xbase.lib.Functions.Function1;
			
			@SuppressWarnings("all")
			public class Foo {
			  public Object foo() {
			    Object _switchResult = null;
			    final Object _switchValue = null;
			    boolean _matched = false;
			    if (!_matched) {
			      if (Objects.equal(_switchValue,null)) {
			        _matched=true;
			        final Function1<Object,Object> _function = new Function1<Object,Object>() {
			            public Object apply(final Object it) {
			              return it;
			            }
			          };
			        _switchResult = _function;
			      }
			    }
			    if (!_matched) {
			      if (Objects.equal(_switchValue,null)) {
			        _matched=true;
			        final Function1<Integer,Integer> _function_1 = new Function1<Integer,Integer>() {
			            public Integer apply(final Integer it) {
			              return it;
			            }
			          };
			        _switchResult = _function_1;
			      }
			    }
			    return _switchResult;
			  }
			}
		''')
	}
	
	@Test
	def testTryCatch() { 
		assertCompilesTo('''
			package foo
			class Bar {
				def void doStuff(java.lang.reflect.Method m) {
					try {
						// do nothing
					} catch (java.io.IOException e) {
						throw e
					} catch (Exception e) {
						throw e
					} finally {
						// do nothing
					}
				}
			}
		''', '''
			package foo;
			
			import java.io.IOException;
			import java.lang.reflect.Method;
			import org.eclipse.xtext.xbase.lib.Exceptions;
			
			@SuppressWarnings("all")
			public class Bar {
			  public void doStuff(final Method m) {
			    try {
			      try {
			      } catch (final Throwable _t) {
			        if (_t instanceof IOException) {
			          final IOException e = (IOException)_t;
			          throw e;
			        } else if (_t instanceof Exception) {
			          final Exception e_1 = (Exception)_t;
			          throw e_1;
			        } else {
			          throw Exceptions.sneakyThrow(_t);
			        }
			      } finally {
			      }
			    } catch (Exception _e) {
			      throw Exceptions.sneakyThrow(_e);
			    }
			  }
			}
		''')
	}

	@Test	
	def testClosureSneakyThrow() {
		assertCompilesTo('''
			import java.io.File
			import java.io.IOException
			import java.util.Collections
			
			class Foo  {     
			   def bar() {               
			       try {       
			           newArrayList("file1.ext").map(f| new File(f).canonicalFile) 
			       } catch(IOException o) {  
			           Collections::<File>emptyList
			       } 
			   }
			}
		''','''
			import java.io.File;
			import java.io.IOException;
			import java.util.ArrayList;
			import java.util.Collections;
			import java.util.List;
			import org.eclipse.xtext.xbase.lib.CollectionLiterals;
			import org.eclipse.xtext.xbase.lib.Exceptions;
			import org.eclipse.xtext.xbase.lib.Functions.Function1;
			import org.eclipse.xtext.xbase.lib.ListExtensions;
			
			@SuppressWarnings("all")
			public class Foo {
			  public List<File> bar() {
			    List<File> _xtrycatchfinallyexpression = null;
			    try {
			      ArrayList<String> _newArrayList = CollectionLiterals.<String>newArrayList("file1.ext");
			      final Function1<String,File> _function = new Function1<String,File>() {
			          public File apply(final String f) {
			            try {
			              File _file = new File(f);
			              File _canonicalFile = _file.getCanonicalFile();
			              return _canonicalFile;
			            } catch (Exception _e) {
			              throw Exceptions.sneakyThrow(_e);
			            }
			          }
			        };
			      List<File> _map = ListExtensions.<String, File>map(_newArrayList, _function);
			      _xtrycatchfinallyexpression = _map;
			    } catch (final Throwable _t) {
			      if (_t instanceof IOException) {
			        final IOException o = (IOException)_t;
			        List<File> _emptyList = Collections.<File>emptyList();
			        _xtrycatchfinallyexpression = _emptyList;
			      } else {
			        throw Exceptions.sneakyThrow(_t);
			      }
			    }
			    return _xtrycatchfinallyexpression;
			  }
			}
		''')
	}
	
	@Test
	def testFieldInitialization_01() { 
		assertCompilesTo('''
			package foo
			class Bar {
				String s1 = null
				protected String s2 = ''
				public String s3 = s2
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar {
			  private String s1 = null;
			  
			  protected String s2 = "";
			  
			  public String s3 = this.s2;
			}
		''')
	}
	
	@Test
	def testFieldInitialization_02() { 
		assertCompilesTo('''
			package foo
			class Bar {
				String s0 = s1
				static String s1 = null
				protected static String s2 = ''
				public static String s3 = s2
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar {
			  private String s0 = Bar.s1;
			  
			  private static String s1 = null;
			  
			  protected static String s2 = "";
			  
			  public static String s3 = Bar.s2;
			}
		''')
	}
	
	@Test
	def testFieldInitialization_03() { 
		assertCompilesTo('''
			package foo
			class Bar {
				String s = newArrayList.toString
			}
		''', '''
			package foo;
			
			import java.util.ArrayList;
			import org.eclipse.xtext.xbase.lib.CollectionLiterals;
			import org.eclipse.xtext.xbase.lib.Functions.Function0;
			
			@SuppressWarnings("all")
			public class Bar {
			  private String s = new Function0<String>() {
			    public String apply() {
			      ArrayList<Object> _newArrayList = CollectionLiterals.<Object>newArrayList();
			      String _string = _newArrayList.toString();
			      return _string;
			    }
			  }.apply();
			}
		''')
	}
	
	@Test
	def testFieldInitialization_04() { 
		assertCompilesTo('''
			package foo
			class Bar {
				String s = toString + super.toString
			}
		''', '''
			package foo;

			import org.eclipse.xtext.xbase.lib.Functions.Function0;

			@SuppressWarnings("all")
			public class Bar {
			  private String s = new Function0<String>() {
			    public String apply() {
			      String _string = Bar.this.toString();
			      String _string_1 = Bar.super.toString();
			      String _plus = (_string + _string_1);
			      return _plus;
			    }
			  }.apply();
			}
		''')
	}
	
	@Test
	def testConstructorDeclaration_01() { 
		assertCompilesTo('''
			package foo
			class Bar {
				new() {
					super()
				}
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar {
			  public Bar() {
			    super();
			  }
			}
		''')
	}
	
	@Test
	def testConstructorDeclaration_02() { 
		assertCompilesTo('''
			package foo
			class Bar {
				new() {
					this(123)
				}
				new(int a) {
					super()
				}
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar {
			  public Bar() {
			    this(123);
			  }
			  
			  public Bar(final int a) {
			    super();
			  }
			}
		''')
	}
	
	@Test
	def testConstructorDeclaration_03() { 
		assertCompilesTo('''
			package foo
			class Bar {
				new() {
					this(123.toString)
				}
				new(String s) {}
			}
		''', '''
			package foo;
			
			import org.eclipse.xtext.xbase.lib.Functions.Function0;

			@SuppressWarnings("all")
			public class Bar {
			  public Bar() {
			    this(new Function0<String>() {
			      public String apply() {
			        String _string = Integer.valueOf(123).toString();
			        return _string;
			      }
			    }.apply());
			  }
			  
			  public Bar(final String s) {
			  }
			}
		''')
	}
	
	@Test
	def testSneakyThrow() { 
		assertCompilesTo('''
			package foo
			class Bar {
				def void doStuff() {
					throw new java.io.IOException()
				}
			}
		''', '''
			package foo;

			import java.io.IOException;
			import org.eclipse.xtext.xbase.lib.Exceptions;
			
			@SuppressWarnings("all")
			public class Bar {
			  public void doStuff() {
			    try {
			      IOException _iOException = new IOException();
			      throw _iOException;
			    } catch (Exception _e) {
			      throw Exceptions.sneakyThrow(_e);
			    }
			  }
			}
		''')
	}
	
	@Test
	def testSneakyThrow_01() { 
		assertCompilesTo('''
			package foo
			
			import java.io.IOException
			
			class Bar {
				def void doStuff() throws IOException {
					throw new IOException()
				}
			}
		''', '''
			package foo;

			import java.io.IOException;
			
			@SuppressWarnings("all")
			public class Bar {
			  public void doStuff() throws IOException {
			    IOException _iOException = new IOException();
			    throw _iOException;
			  }
			}
		''')
	}

	@Test
	def testSimple() { 
		assertCompilesTo('''
			package foo
			class Bar {
				def Integer doStuff(String x) {x.length}
			}
		''', '''
			package foo;
			
			@SuppressWarnings("all")
			public class Bar {
			  public Integer doStuff(final String x) {
			    int _length = x.length();
			    return Integer.valueOf(_length);
			  }
			}
		''')
	}

	@Test
	def testConstructor() { 
		assertCompilesTo('''
			package foo
			class Bar {
				def foo() {new String()}
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar {
			  public String foo() {
			    String _string = new String();
			    return _string;
			  }
			}
		''')
	}

	@Test
	def testExtends() {
		assertCompilesTo('''
			package foo
			class Bar extends Thread {
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar extends Thread {
			}
		''')
	}

	@Test
	def testExtendsException() {
		assertCompilesTo('''
			package foo
			
			class NoSuchElementException extends java.util.NoSuchElementException {
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class NoSuchElementException extends java.util.NoSuchElementException {
			}
		''')
	}

	@Test
	def testVisibilityOfDispatchMethods() {
		assertCompilesTo('''
			package foo
			class NoSuchElementException {
				def dispatch void foo(String s) {}
				def dispatch void foo(Object s) {}
			}
		''', '''
			package foo;

			import java.util.Arrays;

			@SuppressWarnings("all")
			public class NoSuchElementException {
			  protected void _foo(final String s) {
			  }
			  
			  protected void _foo(final Object s) {
			  }
			  
			  public void foo(final Object s) {
			    if (s instanceof String) {
			      _foo((String)s);
			      return;
			    } else if (s != null) {
			      _foo(s);
			      return;
			    } else {
			      throw new IllegalArgumentException("Unhandled parameter types: " +
			        Arrays.<Object>asList(s).toString());
			    }
			  }
			}
		''')
	}
	
	@Test
	def testParenthesisInDispatchMethodsGuards() {
		assertCompilesTo('''
			package foo
			class MyType {
				def dispatch void foo(String s, CharSequence other) {}
				def dispatch void foo(String s, Void other) {}
				def dispatch void foo(Object s, Object other) {}
			}
		''', '''
			package foo;

			import java.util.Arrays;

			@SuppressWarnings("all")
			public class MyType {
			  protected void _foo(final String s, final CharSequence other) {
			  }
			  
			  protected void _foo(final String s, final Void other) {
			  }
			  
			  protected void _foo(final Object s, final Object other) {
			  }
			  
			  public void foo(final Object s, final Object other) {
			    if (s instanceof String
			         && other instanceof CharSequence) {
			      _foo((String)s, (CharSequence)other);
			      return;
			    } else if (s instanceof String
			         && other == null) {
			      _foo((String)s, (Void)null);
			      return;
			    } else if (s != null
			         && other != null) {
			      _foo(s, other);
			      return;
			    } else {
			      throw new IllegalArgumentException("Unhandled parameter types: " +
			        Arrays.<Object>asList(s, other).toString());
			    }
			  }
			}
		''')
	}
	
	@Test
	def testParenthesisInDispatchMethodsGuards_reordered() {
		assertCompilesTo('''
			package foo
			class MyType {
				def dispatch void foo(Object s, Object other) {}
				def dispatch void foo(String s, Void other) {}
				def dispatch void foo(String s, CharSequence other) {}
			}
		''', '''
			package foo;

			import java.util.Arrays;

			@SuppressWarnings("all")
			public class MyType {
			  protected void _foo(final Object s, final Object other) {
			  }
			  
			  protected void _foo(final String s, final Void other) {
			  }
			  
			  protected void _foo(final String s, final CharSequence other) {
			  }
			  
			  public void foo(final Object s, final Object other) {
			    if (s instanceof String
			         && other instanceof CharSequence) {
			      _foo((String)s, (CharSequence)other);
			      return;
			    } else if (s instanceof String
			         && other == null) {
			      _foo((String)s, (Void)null);
			      return;
			    } else if (s != null
			         && other != null) {
			      _foo(s, other);
			      return;
			    } else {
			      throw new IllegalArgumentException("Unhandled parameter types: " +
			        Arrays.<Object>asList(s, other).toString());
			    }
			  }
			}
		''')
	}

	@Test
	def testNoUncessaryCastInDispatchMethods() {
		assertCompilesTo('''
			package foo
			class MyType {
				def dispatch void foo(StringBuilder s, boolean b, String other) {}
				def dispatch void foo(StringBuffer s, boolean b, String other) {}
			}
		''', '''
			package foo;
			
			import java.util.Arrays;
			
			@SuppressWarnings("all")
			public class MyType {
			  protected void _foo(final StringBuilder s, final boolean b, final String other) {
			  }
			  
			  protected void _foo(final StringBuffer s, final boolean b, final String other) {
			  }
			  
			  public void foo(final Object s, final boolean b, final String other) {
			    if (s instanceof StringBuffer) {
			      _foo((StringBuffer)s, b, other);
			      return;
			    } else if (s instanceof StringBuilder) {
			      _foo((StringBuilder)s, b, other);
			      return;
			    } else {
			      throw new IllegalArgumentException("Unhandled parameter types: " +
			        Arrays.<Object>asList(s, b, other).toString());
			    }
			  }
			}
		''')
	}

	@Test
	@Ignore
	def testExtendsArrayList_01()  {
		assertCompilesTo('''
			package foo
			class MyList<T> extends java.util.ArrayList<T> {}
		''', '''
			package foo;
			
			import java.util.ArrayList;
			
			@SuppressWarnings("all")
			public class MyList<T> extends ArrayList<T> {
			}
		''')
	}

	@Test
	@Ignore
	def testExtendsArrayList_02() {
		assertCompilesTo('''
			package foo
			class StringList extends java.util.ArrayList<String> {
			}
		''', '''
			package foo;
			
			import java.util.ArrayList;
			
			@SuppressWarnings("all")
			public class StringList<T> extends ArrayList<String> {
			}
		''')
	}

	@Test
	def testImplements() {
		assertCompilesTo('''
			package foo
			class Bar implements Cloneable, java.io.Serializable {
			}
		''', '''
			package foo;

			import java.io.Serializable;
			
			@SuppressWarnings("all")
			public class Bar implements Cloneable, Serializable {
			}
		''')
	}

	@Test
	def testConstructor_2() {
		assertCompilesTo('''
			package foo
			class Bar {
				def foo() {new java.util.HashMap<String,java.io.Serializable>()}
			}
		''', '''
			package foo;

			import java.io.Serializable;
			import java.util.HashMap;

			@SuppressWarnings("all")
			public class Bar {
			  public HashMap<String,Serializable> foo() {
			    HashMap<String,Serializable> _hashMap = new HashMap<String,Serializable>();
			    return _hashMap;
			  }
			}
		''')
	}

	@Test
	def testAnnotation() {
		assertCompilesTo('''
			package foo
			class Bar {
				@com.google.inject.Inject String string
			}
		''', '''
			package foo;

			import com.google.inject.Inject;

			@SuppressWarnings("all")
			public class Bar {
			  @Inject
			  private String string;
			}
		''')
	}
	
	@Test
	def testSuperCall() {
		assertCompilesTo('''
			package x class Y extends Object {
				override boolean equals(Object p){
					if ('foo' == p)
						return true
					else
						super.equals(p) 
				} 
			}
		''', '''
			package x;

			import com.google.common.base.Objects;

			@SuppressWarnings("all")
			public class Y {
			  public boolean equals(final Object p) {
			    boolean _xifexpression = false;
			    boolean _equals = Objects.equal("foo", p);
			    if (_equals) {
			      return true;
			    } else {
			      boolean _equals_1 = super.equals(p);
			      _xifexpression = _equals_1;
			    }
			    return _xifexpression;
			  }
			}
		''')
	}
	
	@Test
	def testCreateExtension() {
		assertCompilesTo('''
			package foo
			class Bar {
				def create result: new StringBuilder() aBuilder(String x) {
					result.append(x)
				}
			}
		''', '''
			package foo;

			import java.util.ArrayList;
			import java.util.HashMap;
			import org.eclipse.xtext.xbase.lib.CollectionLiterals;
			
			@SuppressWarnings("all")
			public class Bar {
			  public StringBuilder aBuilder(final String x) {
			    final ArrayList<?>_cacheKey = CollectionLiterals.newArrayList(x);
			    final StringBuilder _result;
			    synchronized (_createCache_aBuilder) {
			      if (_createCache_aBuilder.containsKey(_cacheKey)) {
			        return _createCache_aBuilder.get(_cacheKey);
			      }
			      StringBuilder _stringBuilder = new StringBuilder();
			      _result = _stringBuilder;
			      _createCache_aBuilder.put(_cacheKey, _result);
			    }
			    _init_aBuilder(_result, x);
			    return _result;
			  }
			  
			  private final HashMap<ArrayList<?>,StringBuilder> _createCache_aBuilder = CollectionLiterals.newHashMap();
			  
			  private void _init_aBuilder(final StringBuilder result, final String x) {
			    result.append(x);
			  }
			}
		''');
	}

	@Test
	def testJavaDocs() {
		assertCompilesTo('''
			package foo
			/**
			 * I am Bar
			 */
			class Bar {
				/**
				 */
				def foo() {}
				
				/**
				 * I am bar,
				 * really!
				 */
				int bar
			}
		''', '''
			package foo;

			/**
			 * I am Bar
			 */
			@SuppressWarnings("all")
			public class Bar {
			  public Object foo() {
			    return null;
			  }
			  
			  /**
			   * I am bar,
			   * really!
			   */
			  private int bar;
			}
			''');
	}
	
	@Test
	def testStaticMethod() {
		assertCompilesTo('''
			package foo
			class Bar {
				def static foo() { 42 }
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar {
			  public static int foo() {
			    return 42;
			  }
			}
			''');
	}
	
	@Test
	def testStaticField() {
		assertCompilesTo('''
			package foo
			class Bar {
				static int foo
			}
		''', '''
			package foo;

			@SuppressWarnings("all")
			public class Bar {
			  private static int foo;
			}
			''');
	}
	
	@Test
	def testNestedClosureWithIt() {
		assertCompilesTo('''
			class X {
				def foo() {
					val (String)=>String function = [ [String it | it].apply(it) ]
					function.apply('foo')
				}
			}
		''','''
			import org.eclipse.xtext.xbase.lib.Functions.Function1;
			
			@SuppressWarnings("all")
			public class X {
			  public String foo() {
			    String _xblockexpression = null;
			    {
			      final Function1<String,String> _function = new Function1<String,String>() {
			          public String apply(final String it) {
			            final Function1<String,String> _function = new Function1<String,String>() {
			                public String apply(final String it) {
			                  return it;
			                }
			              };
			            String _apply = _function.apply(it);
			            return _apply;
			          }
			        };
			      final Function1<? super String,? extends String> function = _function;
			      String _apply = function.apply("foo");
			      _xblockexpression = (_apply);
			    }
			    return _xblockexpression;
			  }
			}
		''')
	}
	
	@Test
	def testNestedClosureSuperCall() {
		assertCompilesTo('''
			class X {
				def foo() {
					[| [| super.toString ].apply ].apply
				}
			}
		''','''
			import org.eclipse.xtext.xbase.lib.Functions.Function0;
			
			@SuppressWarnings("all")
			public class X {
			  public String foo() {
			    final Function0<String> _function = new Function0<String>() {
			        public String apply() {
			          final Function0<String> _function = new Function0<String>() {
			              public String apply() {
			                String _string = X.super.toString();
			                return _string;
			              }
			            };
			          String _apply = _function.apply();
			          return _apply;
			        }
			      };
			    String _apply = _function.apply();
			    return _apply;
			  }
			}
		''')
	}

	@Test
	def testExplicitBoxingUnboxing() {
		assertCompilesTo('''
			class X {
				def foo(int p0, Integer p1) {
					foo(p1,p0)
				}
			}
		''','''
			@SuppressWarnings("all")
			public class X {
			  public Object foo(final int p0, final Integer p1) {
			    Object _foo = this.foo((p1).intValue(), Integer.valueOf(p0));
			    return _foo;
			  }
			}
		''')
	}
	
	@Test
	def testOptionalSemicola() {
		assertCompilesTo('''
			package foo;
			
			import java.io.File;
			
			public class Foo {
				private int bar;
				
				private File baz;
			}
		''', '''
			package foo;
			
			import java.io.File;
			
			@SuppressWarnings("all")
			public class Foo {
			  private int bar;
			  
			  private File baz;
			}
		''')
	}
	
	@Test
	def testNoDeclarationForFeatureCalls() {
		assertCompilesTo('''
			package foo;
			
			public class Foo {
				private static String FOO = "Holla";
				
				private String baz = FOO;
			}
		''', '''
			package foo;
			
			@SuppressWarnings("all")
			public class Foo {
			  private static String FOO = "Holla";
			  
			  private String baz = Foo.FOO;
			}
		''')
	}
	
	@Test
	def testNoDeclarationForFeatureCalls_02() {
		assertCompilesTo('''
			package foo;
			
			public class Foo {
				private static String FOO = "Holla";
				private String foo = "Holla";
				
				def void test(String x, String y, int integer) {
					new Foo().test(foo, FOO, Integer::MAX_VALUE)
				}
			}
		''', '''
			package foo;
			
			@SuppressWarnings("all")
			public class Foo {
			  private static String FOO = "Holla";
			  
			  private String foo = "Holla";
			  
			  public void test(final String x, final String y, final int integer) {
			    Foo _foo = new Foo();
			    _foo.test(this.foo, Foo.FOO, Integer.MAX_VALUE);
			  }
			}
		''')
	}
	
	@Test
	def testStringExtensionInlined_01() {
		assertCompilesTo('''
			package foo;
			
			public class Foo {
				def String returnString(String x, String y) {
					x + '' + y
				}
			}
		''', '''
			package foo;
			
			@SuppressWarnings("all")
			public class Foo {
			  public String returnString(final String x, final String y) {
			    String _plus = (x + "");
			    String _plus_1 = (_plus + y);
			    return _plus_1;
			  }
			}
		''')
	}
	
	@Test
	def testNoVarForReturnExpression() {
		assertCompilesTo('''
			package foo;
			
			public class Foo {
				def String returnString(String x, String y) {
					return x + '' + y
				}
			}
		''', '''
			package foo;
			
			@SuppressWarnings("all")
			public class Foo {
			  public String returnString(final String x, final String y) {
			    String _plus = (x + "");
			    return (_plus + y);
			  }
			}
		''')
	}
	
	@Test
	def testNoVarForRightHandSideOfVarDecl() {
		assertCompilesTo('''
			package foo;
			
			public class Foo {
				def String returnString(String x, String y) {
					val z = x + '' + y
					return z
				}
			}
		''', '''
			package foo;
			
			@SuppressWarnings("all")
			public class Foo {
			  public String returnString(final String x, final String y) {
			    String _plus = (x + "");
			    final String z = (_plus + y);
			    return z;
			  }
			}
		''')
	}
	@Test
	def testJavaKeywordsUsed() {
		assertCompilesTo('''
			package foo;
			
			public class Foo {
				def void someMethod(String assert) {
					val synchronized = newArrayList('foo')
					for (volatile : synchronized) {
						switch final : volatile {
							case final.length > 2 : synchronized.forEach(break | break + final)
						}
					}
				}
			}
		''', '''
			package foo;
			
			import java.util.ArrayList;
			import org.eclipse.xtext.xbase.lib.CollectionLiterals;
			import org.eclipse.xtext.xbase.lib.IterableExtensions;
			import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
			
			@SuppressWarnings("all")
			public class Foo {
			  public void someMethod(final String assert_) {
			    final ArrayList<String> synchronized_ = CollectionLiterals.<String>newArrayList("foo");
			    for (final String volatile_ : synchronized_) {
			      final String final_ = volatile_;
			      boolean _matched = false;
			      if (!_matched) {
			        int _length = final_.length();
			        boolean _greaterThan = (_length > 2);
			        if (_greaterThan) {
			          _matched=true;
			          final Procedure1<String> _function = new Procedure1<String>() {
			              public void apply(final String break_) {
			                /*(break_ + final_);*/
			              }
			            };
			          IterableExtensions.<String>forEach(synchronized_, _function);
			        }
			      }
			    }
			  }
			}
		''')
	}
	
	@Test
	def testExtensionField() { 
		assertCompilesTo(
			'''
				class NoNPE {
					extension String
					def useExtension() {
						_string.toString
					}
				}
			''', '''
				@SuppressWarnings("all")
				public class NoNPE {
				  private String _string;
				  
				  public String useExtension() {
				    String _string = this._string.toString();
				    return _string;
				  }
				}
			''')
	}
	
	@Test
	def testReturnType() {
		assertCompilesTo(
			'''
				import test.ReturnTypeUsesTypeParameter
				class MyClass implements ReturnTypeUsesTypeParameter {
				
					override <LocalName extends CharSequence> accept(LocalName param) {
						null
					}
				}
			''', '''
				import test.ReturnTypeUsesTypeParameter;
				import test.ReturnTypeUsesTypeParameter.Inner;
				
				@SuppressWarnings("all")
				public class MyClass implements ReturnTypeUsesTypeParameter {
				  public <LocalName extends CharSequence> Inner<LocalName> accept(final LocalName param) {
				    return null;
				  }
				}
			''')
		
	}
	
	@Test
	def testReturnType_02() {
		assertCompilesTo(
			'''
				import test.ReturnTypeUsesTypeParameter
				class MyClass implements ReturnTypeUsesTypeParameter {
				
					override <LocalName extends CharSequence> accept(LocalName param) {
						[ if (true) it?.apply(param) ] 
					}
				}
			''', '''
				import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
				import test.ReturnTypeUsesTypeParameter;
				import test.ReturnTypeUsesTypeParameter.Inner;
				
				@SuppressWarnings("all")
				public class MyClass implements ReturnTypeUsesTypeParameter {
				  public <LocalName extends CharSequence> Inner<LocalName> accept(final LocalName param) {
				    final Procedure1<Procedure1<? super LocalName>> _function = new Procedure1<Procedure1<? super LocalName>>() {
				        public void apply(final Procedure1<? super LocalName> it) {
				          if (true) {
				            if (it!=null) it.apply(param);
				          }
				        }
				      };
				    return new Inner<LocalName>() {
				        public void useProcedure(Procedure1<? super LocalName> p0) {
				          _function.apply(p0);
				        }
				    };
				  }
				}
			''')
		
	}
	
	@Test
	def testBug373482() {
		assertCompilesTo(
			'''
				class X {
					def method() «"'''«"»logSomething«"»'''"»
					def void logSomething() {println("zonk")}
				}
			''', '''
				import org.eclipse.xtend2.lib.StringConcatenation;
				import org.eclipse.xtext.xbase.lib.InputOutput;
				
				@SuppressWarnings("all")
				public class X {
				  public CharSequence method() {
				    StringConcatenation _builder = new StringConcatenation();
				    this.logSomething();
				    return _builder;
				  }
				  
				  public void logSomething() {
				    InputOutput.<String>println("zonk");
				  }
				}
			''')
	}
	
	@Test
	def testAnnotationSupport() {
		assertCompilesTo(
			'''
				class X {
					@Property val String x = 'hello'
				}
			''', '''
				@SuppressWarnings("all")
				public class X {
				  private final String _x = "hello";
				  
				  public String getX() {
				    return this._x;
				  }
				}
			''')
		
	}

	def assertCompilesTo(CharSequence input, CharSequence expected) {
		val file = file(input.toString(), true)
		val inferredType = file.getXtendClasses.head.getInferredType
		val javaCode = generator.generateType(inferredType);
		XtendCompilerTest::assertEquals(expected.toString, javaCode.toString);
	}
	
}