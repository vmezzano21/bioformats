//
// ImagePreview.java
//

/*
LOCI 4D Data Browser package for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Francis Wong, Curtis Rueden and Melissa Linkert.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

// Adapted from FileChooserDemo2

package loci.plugins.browser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.*;
import loci.formats.*;

/** ImagePreview.java is a 1.4 example used by FileChooserDemo2.java. */
public class ImagePreview extends JComponent
  implements PropertyChangeListener
{
  ImageIcon thumbnail = null;
  File file = null;

  public ImagePreview(JFileChooser fc) {
    setPreferredSize(new Dimension(100, 50));
    fc.addPropertyChangeListener(this);
  }

  public void loadImage() {
    try {
      if (file == null) {
        thumbnail = null;
        return;
      }

      //Don't use createImageIcon (which is a wrapper for getResource)
      //because the image we're trying to load is probably not one
      //of this program's own resources.
      if (LociDataBrowser.DEBUG) {
        System.err.println("file path: "+file.getAbsolutePath());
      }

      ImageReader ir = new ImageReader();
      FormatReader fr = (FormatReader) ir.getReader(file.getAbsolutePath());
      BufferedImage image = fr.openThumbImage(file.getAbsolutePath(),0);

      thumbnail = new ImageIcon(image);
    }
    catch (Exception e) { e.printStackTrace(); }
  }

  public void propertyChange(PropertyChangeEvent e) {
    boolean update = false;
    String prop = e.getPropertyName();

    //If the directory changed, don't show an image.
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      file = null;
      update = true;
    }
    //If a file became selected, find out which one.
    else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      file = (File) e.getNewValue();
      update = true;
    }

    //Update the preview accordingly.
    if (update) {
      thumbnail = null;
      if (isShowing()) {
        loadImage();
        repaint();
      }
    }
  }

  protected void paintComponent(Graphics g) {
    if (thumbnail == null) loadImage();
    if (thumbnail != null) {
      int x = getWidth()/2 - thumbnail.getIconWidth()/2;
      int y = getHeight()/2 - thumbnail.getIconHeight()/2;
      if (y < 0) y = 0;
      if (x < 5) x = 5;
      thumbnail.paintIcon(this, g, x, y);
    }
  }

}
