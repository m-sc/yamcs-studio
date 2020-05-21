package org.yamcs.studio.threedwidget;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL20.*;

public class My3DFigure extends AbstractSWTWidgetFigure<GLCanvas> {

    private AbstractBaseEditPart editpart;
    private Composite composite;

    public My3DFigure(AbstractBaseEditPart editpart) {
        super(editpart);
        this.editpart = editpart;
        this.composite = (Composite) editpart.getViewer().getControl();
    }

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
        graphics.fillRectangle(getClientArea().x, getClientArea().y + getClientArea().height - valueLength,
                getClientArea().width, valueLength);
    }

    public void setMin(double min) {
        this.min = min;
        //// repaint();
    }

    public void setMax(double max) {
        this.max = max;
        // r////epaint();
    }

    public void setValue(double value) {
        this.value = value;
        // repaint();
    }

    public double getValue() {
        return value;
    }

    @Override
    protected GLCanvas createSWTWidget(Composite parent, int style) {
        GLData data = new GLData();
        data.doubleBuffer = true;
        GLCanvas canvas = new GLCanvas(parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE, data);
        canvas.setCurrent();
        GL.createCapabilities();
        final Rectangle rect = new Rectangle(0, 0, 0, 0);
        canvas.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                Rectangle bounds = canvas.getBounds();
                rect.width = bounds.width;
                rect.height = bounds.height;
            }
        });

        glClearColor(0.01f, 0.01f, 0.1f, 1.0f);

        // Create a simple shader program
        int program = glCreateProgram();

        glLinkProgram(program);
        glUseProgram(program);
        final int rotLocation = glGetUniformLocation(program, "rot");
        final int aspectLocation = glGetUniformLocation(program, "aspect");

        My3DFigure figure = this;
        GLXGears gears = new GLXGears();

        Display display = parent.getDisplay();
        display.asyncExec(new Runnable() {
            float rot;
            long lastTime = System.nanoTime();
            float r = 1.0f, g = 1.0f, b = 1.0f;
            int frameCount = 0;
            float lastTimeFps = lastTime;

            public void run() {
                if (!canvas.isDisposed()) {
                    canvas.setCurrent();
                    glClear(GL_COLOR_BUFFER_BIT);
                    glViewport(0, 0, rect.width, rect.height);

                    float aspect = (float) rect.width / rect.height;
                    glUniform1f(aspectLocation, aspect);
                    // glUniform1f(rotLocation, rot);
                    // glDrawElements(GL11.GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

                    gears.render();
                    gears.animate(figure.getValue());

                    canvas.swapBuffers();
                    display.asyncExec(this);

                    long thisTime = System.nanoTime();
                    float delta = (thisTime - lastTime) / 1E9f;
                    rot += delta;
                    rot = (float) figure.getValue();
                    if (rot > 2.0 * Math.PI) {
                        rot -= 2.0f * (float) Math.PI;
                    }
                    lastTime = thisTime;
                    frameCount++;
                    float deltaFps = (thisTime - lastTimeFps) / 1E9f;
                    if (deltaFps > 1) {
                        System.out.println("FPS:" + frameCount);
                        lastTimeFps = thisTime;
                        frameCount = 0;
                    }

                }
            }
        });

//        long thisTime = System.nanoTime();
//        float delta = (thisTime - lastTime) / 1E9f;
//        rot += delta;
//        if (rot > 2.0 * Math.PI) {
//            rot -= 2.0f * (float) Math.PI;
//        }
//        lastTime = thisTime;
//        
        return canvas;
    }
}
