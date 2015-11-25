
package main.util;

import javax.microedition.lcdui.Graphics;

import framework.ui.component.View;

public class Log {
    private static final boolean DEBUG = true;

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String tag, String msg, Exception e) {
        dump(tag, msg, e);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Exception e) {
        dump(tag, msg, e);
    }

    private static void dump(String tag, String msg, Exception e) {
        if (DEBUG) {
            System.out.println("[" + tag + "]" + msg);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }

    public static void markBorder(Graphics g, View win) {
        if (DEBUG) {
            g.setClip(0, 0, 240, 320);
            g.setColor(0x0FF0000);
            g.drawRect(win.getLeft(), win.getTop(), win.getWidth(), win.getHeight());
        }
    }
}
