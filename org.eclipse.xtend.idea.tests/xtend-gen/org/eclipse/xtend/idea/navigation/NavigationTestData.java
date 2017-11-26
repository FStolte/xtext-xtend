/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.navigation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;

@Accessors
@SuppressWarnings("all")
public class NavigationTestData {
  public final static String NAVIGATION_ELEMENT_START_OFFSET = "<navigationElementStartOffset>";
  
  public final static String NAVIGATION_ELEMENT_END_OFFSET = "<navigationElementEndOffset>";
  
  public final static String REFERENCE_OFFSET = "<referenceOffset>";
  
  private final static List<String> OFFSET_MARKERS = Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList(NavigationTestData.NAVIGATION_ELEMENT_START_OFFSET, NavigationTestData.NAVIGATION_ELEMENT_END_OFFSET, NavigationTestData.REFERENCE_OFFSET));
  
  private final String model;
  
  private final int navigationElementStartOffset;
  
  private final int navigationElementEndOffset;
  
  private final int referenceOffset;
  
  public NavigationTestData(final String model) {
    final HashMap<String, Integer> offsetMarkers = CollectionLiterals.<String, Integer>newHashMap();
    this.model = this.removeOffsetMarkers(model, offsetMarkers);
    this.navigationElementStartOffset = (offsetMarkers.get(NavigationTestData.NAVIGATION_ELEMENT_START_OFFSET)).intValue();
    this.navigationElementEndOffset = (offsetMarkers.get(NavigationTestData.NAVIGATION_ELEMENT_END_OFFSET)).intValue();
    this.referenceOffset = (offsetMarkers.get(NavigationTestData.REFERENCE_OFFSET)).intValue();
  }
  
  protected String removeOffsetMarkers(final String model, final Map<String, Integer> offsetMarkers) {
    String _xblockexpression = null;
    {
      final Pair<String, Integer> offsetMarker = this.findFirstOffsetMarker(model);
      Integer _value = offsetMarker.getValue();
      boolean _equals = ((_value).intValue() == (-1));
      if (_equals) {
        return model;
      }
      offsetMarkers.put(offsetMarker.getKey(), offsetMarker.getValue());
      _xblockexpression = this.removeOffsetMarkers(this.removeOffsetMarker(model, offsetMarker), offsetMarkers);
    }
    return _xblockexpression;
  }
  
  protected String removeOffsetMarker(final String model, final Pair<String, Integer> offsetMarker) {
    String _substring = model.substring(0, (offsetMarker.getValue()).intValue());
    Integer _value = offsetMarker.getValue();
    int _length = offsetMarker.getKey().length();
    int _plus = ((_value).intValue() + _length);
    String _substring_1 = model.substring(_plus);
    return (_substring + _substring_1);
  }
  
  protected Pair<String, Integer> findFirstOffsetMarker(final String model) {
    final Function2<Pair<String, Integer>, Pair<String, Integer>, Pair<String, Integer>> _function = (Pair<String, Integer> $0, Pair<String, Integer> $1) -> {
      Pair<String, Integer> _xifexpression = null;
      Integer _value = $0.getValue();
      boolean _equals = ((_value).intValue() == (-1));
      if (_equals) {
        _xifexpression = $1;
      } else {
        Pair<String, Integer> _xifexpression_1 = null;
        Integer _value_1 = $1.getValue();
        boolean _equals_1 = ((_value_1).intValue() == (-1));
        if (_equals_1) {
          _xifexpression_1 = $0;
        } else {
          Pair<String, Integer> _xifexpression_2 = null;
          Integer _value_2 = $0.getValue();
          Integer _value_3 = $1.getValue();
          boolean _lessThan = (_value_2.compareTo(_value_3) < 0);
          if (_lessThan) {
            _xifexpression_2 = $0;
          } else {
            _xifexpression_2 = $1;
          }
          _xifexpression_1 = _xifexpression_2;
        }
        _xifexpression = _xifexpression_1;
      }
      return _xifexpression;
    };
    return IterableExtensions.<Pair<String, Integer>>reduce(this.findOffsetMarkers(model), _function);
  }
  
  protected List<Pair<String, Integer>> findOffsetMarkers(final String model) {
    final Function1<String, Pair<String, Integer>> _function = (String it) -> {
      int _indexOf = model.indexOf(it);
      return Pair.<String, Integer>of(it, Integer.valueOf(_indexOf));
    };
    return ListExtensions.<String, Pair<String, Integer>>map(NavigationTestData.OFFSET_MARKERS, _function);
  }
  
  @Pure
  public String getModel() {
    return this.model;
  }
  
  @Pure
  public int getNavigationElementStartOffset() {
    return this.navigationElementStartOffset;
  }
  
  @Pure
  public int getNavigationElementEndOffset() {
    return this.navigationElementEndOffset;
  }
  
  @Pure
  public int getReferenceOffset() {
    return this.referenceOffset;
  }
}
