/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtend.idea.config;

import com.google.common.base.Objects;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.framework.addSupport.FrameworkSupportInModuleConfigurable;
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FrameworkDetector;
import com.intellij.framework.detection.impl.FrameworkDetectorRegistry;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportConfigurable;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModelImpl;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportUtil;
import com.intellij.ide.util.newProjectWizard.OldFrameworkSupportProviderWrapper;
import com.intellij.ide.util.newProjectWizard.impl.FrameworkSupportCommunicator;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.IdeaModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainer;
import com.intellij.openapi.roots.ui.configuration.projectRoot.LibrariesContainerFactory;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.PlatformTestCase;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.testFramework.PsiTestUtil;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.xtend.core.idea.facet.XtendFacetConfiguration;
import org.eclipse.xtend.core.idea.facet.XtendFacetType;
import org.eclipse.xtend.core.idea.lang.XtendFileType;
import org.eclipse.xtend.core.idea.lang.XtendLanguage;
import org.eclipse.xtext.xbase.idea.facet.XbaseGeneratorConfigurationState;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * @author dhuebner - Initial contribution and API
 */
@SuppressWarnings("all")
public class XtendSupportConfigurableTest extends PsiTestCase {
  public void testPlainJavaOutputConfiguration_01() {
    final ModuleRootManager manager = ModuleRootManager.getInstance(this.myModule);
    TestCase.assertEquals(0, ((List<VirtualFile>)Conversions.doWrapArray(manager.getContentRoots())).size());
    this.addFrameworkSupport(this.myModule);
    TestCase.assertEquals(1, ((List<VirtualFile>)Conversions.doWrapArray(manager.getContentRoots())).size());
    final Facet<XtendFacetConfiguration> facet = IterableExtensions.<Facet<XtendFacetConfiguration>>head(FacetManager.getInstance(this.myModule).<Facet<XtendFacetConfiguration>>getFacetsByType(XtendFacetType.TYPEID));
    TestCase.assertNotNull(facet);
    final XbaseGeneratorConfigurationState xtendConfig = facet.getConfiguration().getState();
    TestCase.assertTrue(xtendConfig.getOutputDirectory().endsWith("xtend-gen"));
    TestCase.assertTrue(xtendConfig.getTestOutputDirectory().endsWith("xtend-gen"));
    final Function1<SourceFolder, Boolean> _function = (SourceFolder it) -> {
      boolean _xblockexpression = false;
      {
        final String urlToCheck = it.getFile().getPath().replace("file://", "");
        _xblockexpression = (Objects.equal(xtendConfig.getOutputDirectory(), urlToCheck) && (!it.isTestSource()));
      }
      return Boolean.valueOf(_xblockexpression);
    };
    TestCase.assertEquals(1, IterableExtensions.size(IterableExtensions.<SourceFolder>filter(((Iterable<SourceFolder>)Conversions.doWrapArray(IterableExtensions.<ContentEntry>head(((Iterable<ContentEntry>)Conversions.doWrapArray(manager.getContentEntries()))).getSourceFolders())), _function)));
  }
  
  public void testPlainJavaOutputConfiguration_02() {
    final Computable<Module> _function = () -> {
      try {
        final Module module = this.createModule("module1");
        final VirtualFile moduleRoot = VfsUtil.createDirectoryIfMissing(module.getProject().getBaseDir(), module.getName());
        final VirtualFile srcDirVf = VfsUtil.createDirectoryIfMissing(moduleRoot, "src/main/java");
        final VirtualFile testDirVf = VfsUtil.createDirectoryIfMissing(moduleRoot, "src/test/java");
        PsiTestUtil.addContentRoot(module, moduleRoot);
        PsiTestUtil.addSourceRoot(module, srcDirVf);
        PsiTestUtil.addSourceRoot(module, testDirVf, true);
        return module;
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    final Module module = WriteCommandAction.<Module>runWriteCommandAction(this.getProject(), _function);
    final ModuleRootManager manager = ModuleRootManager.getInstance(module);
    final VirtualFile[] srcFolders = manager.getSourceRoots(true);
    TestCase.assertEquals(2, ((List<VirtualFile>)Conversions.doWrapArray(srcFolders)).size());
    this.addFrameworkSupport(module);
    final Facet<XtendFacetConfiguration> facet = IterableExtensions.<Facet<XtendFacetConfiguration>>head(FacetManager.getInstance(module).<Facet<XtendFacetConfiguration>>getFacetsByType(XtendFacetType.TYPEID));
    TestCase.assertNotNull(facet);
    final XbaseGeneratorConfigurationState xtendConfig = facet.getConfiguration().getState();
    String _outputDirectory = xtendConfig.getOutputDirectory();
    String _testOutputDirectory = xtendConfig.getTestOutputDirectory();
    boolean _equals = Objects.equal(_outputDirectory, _testOutputDirectory);
    TestCase.assertFalse(_equals);
    TestCase.assertTrue(xtendConfig.getOutputDirectory().endsWith("module1/src/main/xtend-gen"));
    TestCase.assertTrue(xtendConfig.getTestOutputDirectory().endsWith("module1/src/test/xtend-gen"));
    final VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(true);
    final Function1<VirtualFile, Boolean> _function_1 = (VirtualFile it) -> {
      boolean _xblockexpression = false;
      {
        final String urlToCheck = it.getPath().replace("file://", "");
        String _outputDirectory_1 = xtendConfig.getOutputDirectory();
        _xblockexpression = Objects.equal(_outputDirectory_1, urlToCheck);
      }
      return Boolean.valueOf(_xblockexpression);
    };
    TestCase.assertEquals(1, IterableExtensions.size(IterableExtensions.<VirtualFile>filter(((Iterable<VirtualFile>)Conversions.doWrapArray(sourceRoots)), _function_1)));
    final Function1<VirtualFile, Boolean> _function_2 = (VirtualFile it) -> {
      boolean _xblockexpression = false;
      {
        final String urlToCheck = it.getPath().replace("file://", "");
        String _testOutputDirectory_1 = xtendConfig.getTestOutputDirectory();
        _xblockexpression = Objects.equal(_testOutputDirectory_1, urlToCheck);
      }
      return Boolean.valueOf(_xblockexpression);
    };
    TestCase.assertEquals(1, IterableExtensions.size(IterableExtensions.<VirtualFile>filter(((Iterable<VirtualFile>)Conversions.doWrapArray(sourceRoots)), _function_2)));
  }
  
  public void testPlainJavaOutputConfiguration_03() {
    final ModuleRootManager manager = ModuleRootManager.getInstance(this.myModule);
    TestCase.assertEquals(0, ((List<VirtualFile>)Conversions.doWrapArray(manager.getContentRoots())).size());
    this.addFrameworkSupportUsingDetector(this.myModule);
    TestCase.assertEquals(1, ((List<VirtualFile>)Conversions.doWrapArray(manager.getContentRoots())).size());
    final Facet<XtendFacetConfiguration> facet = IterableExtensions.<Facet<XtendFacetConfiguration>>head(FacetManager.getInstance(this.myModule).<Facet<XtendFacetConfiguration>>getFacetsByType(XtendFacetType.TYPEID));
    TestCase.assertNotNull(facet);
    final XbaseGeneratorConfigurationState xtendConfig = facet.getConfiguration().getState();
    TestCase.assertTrue(xtendConfig.getOutputDirectory().endsWith("xtend-gen"));
    TestCase.assertTrue(xtendConfig.getTestOutputDirectory().endsWith("xtend-gen"));
    final Function1<SourceFolder, Boolean> _function = (SourceFolder it) -> {
      boolean _xblockexpression = false;
      {
        final String urlToCheck = it.getFile().getPath().replace("file://", "");
        _xblockexpression = (Objects.equal(xtendConfig.getOutputDirectory(), urlToCheck) && (!it.isTestSource()));
      }
      return Boolean.valueOf(_xblockexpression);
    };
    TestCase.assertEquals(1, IterableExtensions.size(IterableExtensions.<SourceFolder>filter(((Iterable<SourceFolder>)Conversions.doWrapArray(IterableExtensions.<ContentEntry>head(((Iterable<ContentEntry>)Conversions.doWrapArray(manager.getContentEntries()))).getSourceFolders())), _function)));
  }
  
  protected void addFrameworkSupportUsingDetector(final Module moduleToHandle) {
    Project _project = this.getProject();
    new WriteCommandAction.Simple(_project) {
      @Override
      protected void run() throws Throwable {
        XtendSupportConfigurableTest.this.createContentRoot(moduleToHandle);
        final IdeaModifiableModelsProvider modifiableModelsProvider = new IdeaModifiableModelsProvider();
        final ModifiableFacetModel model = modifiableModelsProvider.getFacetModifiableModel(moduleToHandle);
        try {
          final FacetType facetType = FacetTypeRegistry.getInstance().findFacetType(XtendFacetType.TYPEID.toString());
          final FacetConfiguration facetConfiguration = facetType.createDefaultConfiguration();
          final Integer detId = IterableExtensions.<Integer>head(FrameworkDetectorRegistry.getInstance().getDetectorIds(XtendFileType.INSTANCE));
          FrameworkDetector _detectorById = FrameworkDetectorRegistry.getInstance().getDetectorById((detId).intValue());
          final FacetBasedFrameworkDetector detector = ((FacetBasedFrameworkDetector) _detectorById);
          final Facet facet = FacetManager.getInstance(moduleToHandle).<Facet, FacetConfiguration>createFacet(facetType, 
            facetType.getDefaultFacetName(), facetConfiguration, null);
          model.addFacet(facet);
          modifiableModelsProvider.commitFacetModifiableModel(moduleToHandle, model);
          final ModifiableRootModel rootModel = modifiableModelsProvider.getModuleModifiableModel(moduleToHandle);
          detector.setupFacet(facet, rootModel);
          modifiableModelsProvider.commitModuleModifiableModel(rootModel);
        } finally {
          model.commit();
        }
      }
    }.execute().throwException();
  }
  
  protected ContentEntry createContentRoot(final Module moduleToHandle) {
    try {
      ContentEntry _xblockexpression = null;
      {
        final VirtualFile root = PlatformTestCase.getVirtualFile(PlatformTestCase.createTempDir("contentRoot"));
        _xblockexpression = PsiTestUtil.addContentRoot(moduleToHandle, root);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  protected void addFrameworkSupport(final Module moduleToHandle) {
    Project _project = this.getProject();
    new WriteCommandAction.Simple(_project) {
      @Override
      protected void run() throws Throwable {
        XtendSupportConfigurableTest.this.createContentRoot(moduleToHandle);
        final ModifiableRootModel model = ModuleRootManager.getInstance(moduleToHandle).getModifiableModel();
        final FrameworkSupportInModuleProvider provider = FrameworkSupportUtil.findProvider(XtendLanguage.INSTANCE.getID(), 
          FrameworkSupportUtil.getAllProviders());
        Project _project = this.getProject();
        LibrariesContainer _createContainer = LibrariesContainerFactory.createContainer(this.getProject());
        final FrameworkSupportModelImpl myFrameworkSupportModel = new FrameworkSupportModelImpl(_project, "", _createContainer);
        final FrameworkSupportInModuleConfigurable configurable = provider.createConfigurable(myFrameworkSupportModel);
        try {
          ArrayList<FrameworkSupportConfigurable> selectedConfigurables = new ArrayList<FrameworkSupportConfigurable>();
          IdeaModifiableModelsProvider _ideaModifiableModelsProvider = new IdeaModifiableModelsProvider();
          configurable.addSupport(moduleToHandle, model, _ideaModifiableModelsProvider);
          if ((configurable instanceof OldFrameworkSupportProviderWrapper.FrameworkSupportConfigurableWrapper)) {
            selectedConfigurables.add(((OldFrameworkSupportProviderWrapper.FrameworkSupportConfigurableWrapper)configurable).getConfigurable());
          }
          FrameworkSupportCommunicator[] _extensions = FrameworkSupportCommunicator.EP_NAME.getExtensions();
          for (final FrameworkSupportCommunicator communicator : _extensions) {
            communicator.onFrameworkSupportAdded(moduleToHandle, model, selectedConfigurables, myFrameworkSupportModel);
          }
        } finally {
          model.commit();
          boolean _isDisposed = Disposer.isDisposed(configurable);
          boolean _not = (!_isDisposed);
          if (_not) {
            Disposer.dispose(configurable);
          }
        }
      }
    }.execute().throwException();
  }
}
