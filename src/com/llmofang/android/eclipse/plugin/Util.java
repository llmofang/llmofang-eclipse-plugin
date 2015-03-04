package com.llmofang.android.eclipse.plugin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class Util
{
  public static void dialogMessage(String message)
  {
    final String threadMessage = message;

    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        MessageDialog.openInformation(PlatformUI.getWorkbench().getModalDialogShellProvider().getShell(), "LLMoFang", threadMessage);
      }
    });
  }

  public static class NRSingleton
  {
    private static NRSingleton instance = null;
    private boolean booted = false;

    public static NRSingleton getInstance()
    {
      if (instance == null) {
        instance = new NRSingleton();
      }

      return instance;
    }

    public void setBooted() {
      this.booted = true;
    }

    public boolean isBooted() {
      return this.booted;
    }
  }
}