/**
 *
 * jDelaunay is a library dedicated to the processing of Delaunay and constrained
 * Delaunay triangulations from PSLG inputs.
 *
 * This library is developed at French IRSTV institute as part of the AvuPur and Eval-PDU project,
 * funded by the French Agence Nationale de la Recherche (ANR) under contract
 * ANR-07-VULN-01 and ANR-08-VILL-0005-01 .
 *
 * jDelaunay is distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2010-2012 IRSTV FR CNRS 2488
 *
 * jDelaunay is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * jDelaunay is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * jDelaunay. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.jdelaunay.delaunay.display;

import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;

/**
 * Delaunay Package.
 *
 * @author Jean-Yves Martin, Erwan BOCHER
 */
public class MeshRenderer extends JFrame {
        private static final long serialVersionUID = 1L;
        private ConstrainedMesh myMesh;

        /**
         * Default constructor.
         */
        public MeshRenderer() {
                super("Display Panel");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final int height = 1200;
		final int width = 720;
                setSize(height, width);
                setVisible(true);

                this.myMesh = null;
        }

        /**
         * Set thhe mesh to be displayed by this frame.
         * @param myMesh
         */
        public final void add(ConstrainedMesh myMesh) {
                this.myMesh = myMesh;
        }

        /**
         * Paint the frame
         * @param g
         */
	@Override
        public final void paint(Graphics g) {
                if (myMesh != null) {
                    try {
                        myMesh.displayObject(g);
                    } catch (DelaunayError ex) {
                        throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
                    }
		}
        }

        /**
         * Refresh (redraw) the frame.
         */
        public final void refresh() {
                this.invalidate();
                this.repaint();
        }

        
}