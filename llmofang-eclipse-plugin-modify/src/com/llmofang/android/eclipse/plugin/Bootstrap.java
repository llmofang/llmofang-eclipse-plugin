package com.llmofang.android.eclipse.plugin;


import com.newrelic.agent.compile.RewriterAgent;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.eclipse.ui.IStartup;

public class Bootstrap
  implements IStartup
{
  public void earlyStartup()
  {
    String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
    int p = nameOfRunningVM.indexOf('@');
    String pid = nameOfRunningVM.substring(0, p);
    String jarFilePath = null;
    try
    {
      jarFilePath = RewriterAgent.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString();

      jarFilePath = new File(jarFilePath).getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try
    {
    	System.setProperty("Hinge.AgentArgs","logfile=HingeLogFile;debug=true;");
	      VirtualMachine vm = VirtualMachine.attach(pid);
	      vm.loadAgent(jarFilePath, System.getProperty("Hinge.AgentArgs"));
	      vm.detach();
    }
    catch (NoClassDefFoundError e)
    {
      Util.dialogMessage("Unfortunately, New Relic was unable to load properly.  It's likely you're attempting to run Eclipse using the JRE instead of the JDK.  To run Eclipse using the JDK, you can either prepend the JDK to your system PATH or launch Eclipse with the -vm <path/to/jdk/javaw.exe> argument.  For more information, see https://newrelic.com/docs/mobile-apps/eclipse-installation.");
      throw new RuntimeException(e);
    } catch (Exception e) {
      String message = "Unfortunately, New Relic was unable to load properly.  Please contact support@newrelic.com and include the following error message: " + 
        e.getMessage();
      Util.dialogMessage(message);
      throw new RuntimeException(e);
    }

    Util.NRSingleton.getInstance().setBooted();
  }
}