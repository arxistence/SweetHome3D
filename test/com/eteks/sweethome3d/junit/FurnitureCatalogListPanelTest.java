/*
 * FurnitureCatalogListPanelTest.java
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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTextField;

import junit.framework.TestCase;

import com.eteks.sweethome3d.io.DefaultFurnitureCatalog;
import com.eteks.sweethome3d.io.DefaultUserPreferences;
import com.eteks.sweethome3d.model.FurnitureCatalog;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.FurnitureCatalogListPanel;
import com.eteks.sweethome3d.swing.SwingViewFactory;
import com.eteks.sweethome3d.viewcontroller.FurnitureCatalogController;

/**
 * Tests the furniture catalog list panel's search field is styled with
 * FlatLaf's own cross-platform client properties instead of the dead
 * macOS-only Aqua "JTextField.variant" property.
 * @author Sweet Home 3D
 */
public class FurnitureCatalogListPanelTest extends TestCase {
  public void testSearchFieldUsesFlatLafProperties() {
    UserPreferences preferences = new DefaultUserPreferences();
    // List view (not tree view) is what constructs FurnitureCatalogListPanel -
    // see SwingViewFactory.createFurnitureCatalogView's isFurnitureCatalogViewedInTree() branch
    preferences.setFurnitureCatalogViewedInTree(false);
    FurnitureCatalog catalog = new DefaultFurnitureCatalog();
    FurnitureCatalogController controller = new FurnitureCatalogController(
        catalog, preferences, new SwingViewFactory(), null);
    FurnitureCatalogListPanel panel = (FurnitureCatalogListPanel)controller.getView();

    JTextField searchTextField = findSearchTextField(panel);
    assertNotNull("No JTextField found in panel", searchTextField);

    assertEquals("Wrong placeholder text",
        preferences.getLocalizedString(FurnitureCatalogListPanel.class, "searchTextField.placeholderText"),
        searchTextField.getClientProperty("JTextField.placeholderText"));
    assertEquals("Clear button not enabled",
        Boolean.TRUE, searchTextField.getClientProperty("JTextField.showClearButton"));
    assertTrue("Leading icon not set",
        searchTextField.getClientProperty("JTextField.leadingIcon") instanceof Icon);
    assertNull("Dead Aqua property should no longer be set",
        searchTextField.getClientProperty("JTextField.variant"));
  }

  private JTextField findSearchTextField(java.awt.Container container) {
    for (Component component : container.getComponents()) {
      if (component instanceof JTextField) {
        return (JTextField)component;
      } else if (component instanceof java.awt.Container) {
        JTextField found = findSearchTextField((java.awt.Container)component);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }
}
