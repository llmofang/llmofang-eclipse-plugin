package com.llmofang.android.eclipse.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;



public class ToolsClassLoaderDelegate implements ClassLoaderDelegateHook, HookConfigurator
{
	private boolean flag;
	  private URLClassLoader theToolsLoader;

	  public ToolsClassLoaderDelegate()
	  {
	    this.theToolsLoader = createURLClassloader();
	  }

	  public void addHooks(HookRegistry hookRegistry)
	  {
	    hookRegistry.addClassLoaderDelegateHook(this);
	  }

	  public Class postFindClass(String name, BundleClassLoader classLoader, BundleData data)
	    throws ClassNotFoundException
	  {
	    if ((data.getSymbolicName().startsWith("com.llmofang")) && (this.theToolsLoader != null) && (!this.flag) && (name.startsWith("com.sun.tools"))) {
	      try {
	        this.flag = true;
	        return this.theToolsLoader.loadClass(name);
	      } catch (Throwable t) {
	        t.printStackTrace();
	      } finally {
	        this.flag = false;
	      }
	    }

	    return null;
	  }

	  private URLClassLoader createURLClassloader()
	  {
	    try {
	    	File javaHome;
	      try {
	        javaHome = new File(System.getProperty("java.home")).getCanonicalFile();
	      }
	      catch (IOException e)
	      {
	        
	        throw new IllegalStateException("Unable to locate java home", e);
	      }
	    
	      if (!javaHome.exists()) {
	        throw new IllegalStateException("The java home '" + javaHome.getAbsolutePath() + "' does not exist");
	      }

	      File jarFile = new File(javaHome.getParent(), "lib/tools.jar");

	      if (jarFile.exists()) {
	        URL url = jarFile.getCanonicalFile().toURI().toURL();
	        return new URLClassLoader(new URL[] { url }, null);
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }

	    return null;
	  }

	  public String postFindLibrary(String arg0, BundleClassLoader arg1, BundleData arg2)
	  {
	    return null;
	  }

	  public URL postFindResource(String arg0, BundleClassLoader arg1, BundleData arg2)
	    throws FileNotFoundException
	  {
	    return null;
	  }

	  public Enumeration postFindResources(String arg0, BundleClassLoader arg1, BundleData arg2)
	    throws FileNotFoundException
	  {
	    return null;
	  }

	  public Class preFindClass(String arg0, BundleClassLoader arg1, BundleData arg2)
	    throws ClassNotFoundException
	  {
	    return null;
	  }

	  public String preFindLibrary(String arg0, BundleClassLoader arg1, BundleData arg2)
	    throws FileNotFoundException
	  {
	    return null;
	  }

	  public URL preFindResource(String arg0, BundleClassLoader arg1, BundleData arg2)
	    throws FileNotFoundException
	  {
	    return null;
	  }

	  public Enumeration preFindResources(String arg0, BundleClassLoader arg1, BundleData arg2)
	    throws FileNotFoundException
	  {
	    return null;
	  }

}
