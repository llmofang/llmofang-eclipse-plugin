package com.llmofang.android.eclipse.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.llmofang.android.agent.LLMoFang;

public class Install
  implements IObjectActionDelegate
{
final String localAgentVersion = LLMoFang.getVersion();
  private ISelection selection;
  IProject project;
  public void run(IAction action)
  {
	  if ((this.selection instanceof IStructuredSelection)) {
	      Object selected = ((IStructuredSelection)this.selection).getFirstElement();
	      if ((selected instanceof IProject))
	        this.project = ((IProject)selected);
	      else if ((selected instanceof IAdaptable))
	        this.project = ((IProject)((IAdaptable)selected).getAdapter(IProject.class));
	      else {
	        return;
	      }


      if (!Util.NRSingleton.getInstance().isBooted()) {
        Util.dialogMessage("Unfortunately, New Relic was unable to load and cannot continue.  Please run Eclipse with the -consoleLog option and forward the log to support@newrelic.com");
        return;
      }
      try
      {
        File jarFile = new File(LLMoFang.class.getProtectionDomain().getCodeSource().getLocation().getPath().toString());
        IProject project=this.project;
        String s=this.project.getLocation().toString();
        File libFile = this.project.getLocation().append("libs").append("llmofang.android.jar").toFile();

        addNature();
        if (libFile.exists())
        {
          String currentVersion = jarVersion(libFile);

          if (isOtherVersion(libFile)) {
            copyFile(jarFile, libFile);
            Util.dialogMessage("Update complete, I found version " + currentVersion + " of the agent and replaced it with " + this.localAgentVersion);
          } else {
            copyFile(jarFile, libFile);
            Util.dialogMessage("Hurray!  It looks like you already have the latest version (" + this.localAgentVersion + ") of the New Relic Agent installed.");
          }
        } else {
          File libDir = this.project.getLocation().append("libs").toFile();

          if (!libDir.exists()) {
            libDir.mkdirs();
          }
          copyFile(jarFile, libFile);

          Util.dialogMessage(
            "Congratulations!  Version " + this.localAgentVersion + " of the New Relic Android Agent has been installed in your libs directory.  " + 
            "Please check that newrelic.android.jar is included on your build path.  Additionally, make sure you've added the code necessary " + 
            "to boot the agent to your app's initilization section and we look forward to seeing you at https://rpm.newrelic.com.");
        }

        this.project.refreshLocal(2, null);
      } catch (IOException e) {
        Util.dialogMessage("Woops, something went wrong while trying to copy the New Relic agent jar: " + e.getMessage());
        e.printStackTrace();
      } catch (CoreException e) {
        Util.dialogMessage("Woops, something went wrong while trying to add the New Relic nature to your project: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }

  public void setActivePart(IAction arg0, IWorkbenchPart arg1)
  {
  }

  private void addNature() throws CoreException {
    IProjectDescription desc = this.project.getDescription();
    String[] natures = desc.getNatureIds();
    String[] newNatures = new String[natures.length + 1];

    System.arraycopy(natures, 0, newNatures, 0, natures.length);
    newNatures[natures.length] = "com.llmofang.android.eclipse.plugin.LLMoFangNature";
    desc.setNatureIds(newNatures);
    this.project.setDescription(desc, null);
  }

  private boolean isOtherVersion(File jar) throws IOException {
    String existingVersion = jarVersion(jar);

    if (this.localAgentVersion.equals(existingVersion)) {
      return false;
    }
    return true;
  }

  private String jarVersion(File jar) throws IOException
  {
    Manifest manifest = new JarFile(jar).getManifest();

    return manifest.getMainAttributes().getValue("Implementation-Version");
  }

  private static void copyFile(File sourceFile, File destFile) throws IOException {
    if (!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;
    try
    {
      source = new FileInputStream(sourceFile).getChannel();
      destination = new FileOutputStream(destFile).getChannel();
      destination.transferFrom(source, 0L, source.size());
    } finally {
      if (source != null) {
        source.close();
      }
      if (destination != null)
        destination.close();
    }
  }
}