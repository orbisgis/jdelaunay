package org.jdelaunay.delaunay.display;
/**
 * Delaunay Package.
 *
 * @author Jean-Yves MARTIN, Erwan BOCHER
 * @date 2009-01-12
 * @version 1.0
 */

import java.awt.Graphics;
import javax.swing.JFrame;

import org.jdelaunay.delaunay.ConstrainedMesh;


public class MeshDrawer extends JFrame {
        private static final long serialVersionUID = 1L;
        private ConstrainedMesh myMesh;

        /**
         * Default constructor.
         */
        public MeshDrawer() {
                super("Display Panel");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(1200, 720);
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
			myMesh.displayObject(g);
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