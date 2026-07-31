/*
 * WizardBannerIconTest.java
 *
 * Sweet Home 3D, Copyright (c) 2024 Space Mushrooms <info@sweethome3d.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.eteks.sweethome3d.junit;

import javax.swing.undo.UndoableEditSupport;

import junit.framework.TestCase;

import com.eteks.sweethome3d.io.DefaultUserPreferences;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.FileContentManager;
import com.eteks.sweethome3d.swing.SwingViewFactory;
import com.eteks.sweethome3d.viewcontroller.BackgroundImageWizardController;
import com.eteks.sweethome3d.viewcontroller.ImportedFurnitureWizardController;
import com.eteks.sweethome3d.viewcontroller.ImportedTextureWizardController;

/**
 * Tests that the 3 wizard banner icons resolve to their SVG counterparts.
 * @author Sweet Home 3D
 */
public class WizardBannerIconTest extends TestCase {
  public void testWizardBannerIconsAreSvg() {
    UserPreferences preferences = new DefaultUserPreferences();
    SwingViewFactory viewFactory = new SwingViewFactory();
    FileContentManager contentManager = new FileContentManager(preferences);

    ImportedTextureWizardController textureController =
        new ImportedTextureWizardController(preferences, viewFactory, contentManager);
    assertTrue("importedTextureWizard icon should be .svg",
        textureController.getStepIcon().getFile().endsWith(".svg"));

    BackgroundImageWizardController backgroundController = new BackgroundImageWizardController(
        new Home(), preferences, viewFactory, contentManager, new UndoableEditSupport());
    assertTrue("backgroundImageWizard icon should be .svg",
        backgroundController.getStepIcon().getFile().endsWith(".svg"));

    ImportedFurnitureWizardController furnitureController =
        new ImportedFurnitureWizardController(preferences, viewFactory, contentManager);
    assertTrue("importedFurnitureWizard icon should be .svg",
        furnitureController.getStepIcon().getFile().endsWith(".svg"));
  }
}
