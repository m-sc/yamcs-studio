//package org.yamcs.studio.threedwidget;
///*
// * Copyright LWJGL. All rights reserved.
// * License terms: https://www.lwjgl.org/license
// */
//
//
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.opengl.GLCanvas;
//import org.eclipse.swt.opengl.GLData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swt.widgets.Listener;
//import org.lwjgl.glfw.*;
//import org.lwjgl.opengl.*;
//import org.lwjgl.system.*;
//
//import java.nio.*;
//import java.util.*;
//
//import static org.test.util.GLFWUtil.*;
//import static org.lwjgl.glfw.Callbacks.*;
//import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.system.MemoryStack.*;
//import static org.lwjgl.system.MemoryUtil.*;
//
///** The Gears demo implemented using GLFW. */
//public class Gears {
//
//    private Callback debugProc;
//
// 
//
//    private int xpos;
//    private int ypos;
//    private int width;
//    private int height;
//
//    private GLXGears gears;
//
// 
//
//    void run(Composite parent) {
//        try {
//            init(parent);
//
//            startLoop();
//        } finally {
//            try {
//                destroy();
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
//    }
//
//    private void framebufferSizeChanged(long window, int width, int height) {
//        if (width == 0 || height == 0) {
//            return;
//        }
//
//        gears.setSize(width, height);
//    }
//
//    private GLCanvas init(Composite parent) {
////        GLFWErrorCallback.createPrint().set();
////        if (!glfwInit()) {
////            throw new IllegalStateException("Unable to initialize glfw");
////        }
////
////        glfwDefaultWindowHints();
////        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
////        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,1);
////        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
////        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
////        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
////        //glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
////        if (Platform.get() == Platform.MACOSX) {
////            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
////        }
////        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
////
////        int WIDTH  = 300;
////        int HEIGHT = 300;
////
////        long window = glfwCreateWindow(WIDTH, HEIGHT, "GLFW Gears Demo", NULL, NULL);
////        if (window == NULL) {
////            throw new RuntimeException("Failed to create the GLFW window");
////        }
////
////        /*
////        // This code did the equivalent of glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE) before GLFW 3.3
////        if ( Platform.get() == Platform.MACOSX ) {
////            long cocoaWindow = glfwGetCocoaWindow(window);
////
////            long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
////            long contentView = invokePPP(cocoaWindow, sel_getUid("contentView"), objc_msgSend);
////
////            invokePPV(contentView, sel_getUid("setWantsBestResolutionOpenGLSurface:"), false, objc_msgSend);
////
////            boolean bool = invokePPZ(contentView, sel_getUid("wantsBestResolutionOpenGLSurface"), objc_msgSend);
////            System.out.println("wantsBestResolutionOpenGLSurface = " + bool);
////        }
////        */
////
////        glfwSetWindowSizeLimits(window, WIDTH, HEIGHT, GLFW_DONT_CARE, GLFW_DONT_CARE);
////        //glfwSetWindowAspectRatio(window, 1, 1);
////
////        long monitor = glfwGetPrimaryMonitor();
////
////        GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(monitor));
////        // Center window
////        glfwSetWindowPos(
////            window,
////            (vidmode.width() - WIDTH) / 2,
////            (vidmode.height() - HEIGHT) / 2
////        );
//        
//        
//        GLData data = new GLData();
//        data.doubleBuffer = true;
//        GLCanvas canvas = new GLCanvas(parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE, data);
//        canvas.setCurrent();
//        GL.createCapabilities();
//        final Rectangle rect = new Rectangle(0, 0, 0, 0);
//        canvas.addListener(SWT.Resize, new Listener() {
//            public void handleEvent(Event event) {
//                Rectangle bounds = canvas.getBounds();
//                rect.width = bounds.width;
//                rect.height = bounds.height;
//            }
//        });
//        
//
////        glfwSetKeyCallback(window, (windowHnd, key, scancode, action, mods) -> {
////            if (action != GLFW_RELEASE) {
////                return;
////            }
////
////            switch (key) {
////                case GLFW_KEY_ESCAPE:
////                    glfwSetWindowShouldClose(windowHnd, true);
////                    break;
////                case GLFW_KEY_A:
////                    glfwRequestWindowAttention(windowHnd);
////                    break;
////                case GLFW_KEY_F:
////                    if (glfwGetWindowMonitor(windowHnd) == NULL) {
////                        try (MemoryStack s = stackPush()) {
////                            IntBuffer a = s.ints(0);
////                            IntBuffer b = s.ints(0);
////
////                            glfwGetWindowPos(windowHnd, a, b);
////                            xpos = a.get(0);
////                            ypos = b.get(0);
////
////                            glfwGetWindowSize(windowHnd, a, b);
////                            width = a.get(0);
////                            height = b.get(0);
////                        }
////                        glfwSetWindowMonitor(windowHnd, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
////                        glfwSwapInterval(1);
////                    }
////                    break;
////                case GLFW_KEY_G:
////                    glfwSetInputMode(windowHnd, GLFW_CURSOR, glfwGetInputMode(windowHnd, GLFW_CURSOR) == GLFW_CURSOR_NORMAL
////                        ? GLFW_CURSOR_DISABLED
////                        : GLFW_CURSOR_NORMAL
////                    );
////                    break;
////                case GLFW_KEY_O:
////                    glfwSetWindowOpacity(window, glfwGetWindowOpacity(window) == 1.0f ? 0.5f : 1.0f);
////                    break;
////                case GLFW_KEY_R:
////                    glfwSetWindowAttrib(windowHnd, GLFW_RESIZABLE, 1 - glfwGetWindowAttrib(windowHnd, GLFW_RESIZABLE));
////                    break;
////                case GLFW_KEY_U:
////                    glfwSetWindowAttrib(windowHnd, GLFW_DECORATED, 1 - glfwGetWindowAttrib(windowHnd, GLFW_DECORATED));
////                    break;
////                case GLFW_KEY_W:
////                    if (glfwGetWindowMonitor(windowHnd) != NULL) {
////                        glfwSetWindowMonitor(windowHnd, NULL, xpos, ypos, width, height, 0);
////                    }
////                    break;
////            }
////        });
////
////        glfwSetFramebufferSizeCallback(window, this::framebufferSizeChanged);
////
////        glfwSetWindowRefreshCallback(window, windowHnd -> {
////            gears.render();
////            gears.animate();
////            glfwSwapBuffers(windowHnd);
////        });
////
////        glfwMakeContextCurrent(window);
////        GL.createCapabilities();
////        debugProc = GLUtil.setupDebugMessageCallback();
//
//        gears = new GLXGears();
//
//        glfwSwapInterval(1);
////        glfwShowWindow(window);
////
////        glfwInvoke(window, null, this::framebufferSizeChanged);
////
////        this.window = window;
//        return canvas;
//    }
//
//    private void loop() {
//        long lastUpdate = System.currentTimeMillis();
//
//        int frames = 0;
//        while (!glfwWindowShouldClose(window)) {
//            glfwPollEvents();
//
//            gears.render();
//            gears.animate();
//
//            glfwSwapBuffers(window);
//
//            frames++;
//
//            long time = System.currentTimeMillis();
//
//            int UPDATE_EVERY = 5; // seconds
//            if (UPDATE_EVERY * 1000L <= time - lastUpdate) {
//                lastUpdate = time;
//
//                System.out.printf("%d frames in %d seconds = %.2f fps\n", frames, UPDATE_EVERY, (frames / (float)UPDATE_EVERY));
//                frames = 0;
//            }
//        }
//    }
//
//    private void destroy() {
//        //memFree(GL.getCapabilities().getAddressBuffer());
//        GL.setCapabilities(null);
//
//        if (debugProc != null) {
//            debugProc.free();
//        }
//
//        if (window != NULL) {
//            glfwFreeCallbacks(window);
//            glfwDestroyWindow(window);
//        }
//
//        glfwTerminate();
//        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
//    }
//
//}