/*
 * SweetHome3DDarkLaf.java
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
package com.eteks.sweethome3d.swing;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * FlatLaf dark theme, extended purely so FlatLaf's automatic per-class
 * <code>.properties</code> loading picks up this app's custom UI colors
 * from <code>SweetHome3DDarkLaf.properties</code>.
 */
public class SweetHome3DDarkLaf extends FlatDarkLaf {
  public static final String NAME = "SweetHome3DDarkLaf";

  @Override
  public String getName() {
    return NAME;
  }
}