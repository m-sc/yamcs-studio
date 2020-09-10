package org.yamcs.studio.widgetvideofeed;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Composite;

public class VideoFeedFigure extends AbstractSWTWidgetFigure<Composite> {

    private double min = 0;
    private double max = 100;
    private double value = 50;

    @Override
    protected void paintClientArea(Graphics graphics) {
        super.paintClientArea(graphics);

        graphics.setBackgroundColor(getBackgroundColor());
        graphics.fillRectangle(getClientArea());

        graphics.setBackgroundColor(getForegroundColor());

        // Coerce drawing value in range
        double coercedValue = value;
        if (value < min) {
            coercedValue = min;
        } else if (value > max) {
            coercedValue = max;
        }
        int valueLength = (int) ((coercedValue - min) * getClientArea().height / (max - min));
        graphics.fillRectangle(getClientArea().x,
                getClientArea().y + getClientArea().height - valueLength,
                getClientArea().width, valueLength);
        frame = SWT_AWT.new_Frame(locationComp);
    }

    public void setMin(double min) {
        this.min = min;
        repaint();
    }

    public void setMax(double max) {
        this.max = max;
        repaint();
    }

    public void setValue(double value) {
        this.value = value;
        repaint();
    }

    public double getValue() {
        return value;
    }
}
