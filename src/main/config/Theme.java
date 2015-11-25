
package main.config;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.ui.effect.DropShadowEffect;
import framework.ui.effect.Effect;
import framework.ui.effect.PlainEffect;
import framework.util.DrawUtil;
import main.util.Log;

/**
 * Theme definitions
 *
 * @author Oscar Cai
 */

public final class Theme {
    private static final String TAG = "Theme";
    private static Image appIcon;
    private static Image transparentAppIcon;
    private static Image titlebarBackground;
    private static Image dialogBackground;
    private static Image[] buttonBackgrounds;
    private static Image softIcon;
    private static Image[] gaugeImages;
    private static Image iconBgSelected;
    private static Image menuRefresh;
    private static Image menuInstall;
    private static Image menuBack;
    private static Image menuExit;
    private static Image repeatedBg;
    private static int titlebarHeight;

    private static final int WHITE = 0x0FFFFFFFF;
    private static final int BLACK = 0x0FF000000;
    private static final int RED = 0x0FFFF0000;
    public static final int TRANSPARENT = 0;

    public static final int FORM_BGCOLOR = WHITE;
    public static final int DIALOG_BGCOLOR_BEGIN = 0x10808080;
    public static final int DIALOG_BGCOLOR_END = 0x80808080;

    public static final int SHADOW_COLOR = 0xC0000000;
    public static final int SHADOW_START_COLOR = 0xF0404040;
    public static final int SHADOW_END_COLOR = 0x20808080;

    public static final int TITLEBAR_BG_START_COLOR = 0xFF487DB3;
    public static final int TITLEBAR_BG_END_COLOR = 0xFF194E84;
    public static final int TITLEBAR_BG_BORDER_COLOR = 0xFF194E84;

    public static final Font TITLE_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
    public static final int TITLE_FGCOLOR = WHITE;
    public static final Effect TITLE_EFFECT = new DropShadowEffect(TITLE_FONT, TITLE_FGCOLOR, SHADOW_COLOR);

    public static final Font SUB_TITLE_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    public static final Effect SUB_TITLE_EFFECT = new PlainEffect(SUB_TITLE_FONT, BLACK);

    public static final Font DEFAULT_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    public static final int DEFAULT_FGCOLOR = BLACK;
    public static final Effect DEFAULT_EFFECT = new PlainEffect(DEFAULT_FONT, DEFAULT_FGCOLOR);

    public static final int APPINFO_BUTTON_FGCOLOR = 0xFF404040;
    public static final Effect APPINFO_BUTTON_EFFECT = new PlainEffect(DEFAULT_FONT, APPINFO_BUTTON_FGCOLOR);

    public static final int BORDER_SHADOW_WIDTH = 6;

    public static final int SCROLL_INDICATOR_WIDTH = 6;
    public static final int SCROLL_INDICATOR_ARC_WIDTH = 2;
    public static final int SCROLL_INDICATOR_START_COLOR = 0xB0B4B4B4;
    public static final int SCROLL_INDICATOR_END_COLOR = 0xB0828282;
    public static final int SCROLL_INDICATOR_BORDER_COLOR = 0xB05A5A5A;

    public static final int ITEM_GAP_SIZE = 10;
    public static final Effect GAUGE_EFFECT = new PlainEffect(DEFAULT_FONT, WHITE);
    public static final Effect DIALOG_DEFAULT_EFFECT = new PlainEffect(DEFAULT_FONT, WHITE);

    public static final int DIVIDER_DARK = 0xFF808080;
    public static final int DIVIDER_LIGHT = 0xFFF0F0F0;

    public static final Font NUMBER_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD | Font.STYLE_ITALIC, Font.SIZE_MEDIUM);
    public static final Effect NUMBER_EFFECT = new PlainEffect(NUMBER_FONT, RED);
    public static final Effect NUMBER_LABEL_EFFECT = new PlainEffect(NUMBER_FONT, BLACK);

    public static final int LIST_ITEM_PADDING = 3;
    public static final int LIST_DIVIDER_HEIGHT = 1;
    public static final Font LIST_TITLE_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
    public static final Font LIST_SUMMARY_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_MEDIUM);
    public static final Effect LIST_TITLE_EFFECT = new PlainEffect(LIST_TITLE_FONT, BLACK);
    public static final Effect LIST_SUMMARY_EFFECT = new PlainEffect(LIST_SUMMARY_FONT, BLACK);

    public static final int LIST_ITEM_ODD_BGCOLOR_START = 0x80E0E6ED;
    public static final int LIST_ITEM_ODD_BGCOLOR_END = 0x805E7CA2;
    public static final int LIST_ITEM_EVEN_BGCOLOR_START = 0x80CFE7D3;
    public static final int LIST_ITEM_EVEN_BGCOLOR_END = 0x80468C53;
    public static final int LIST_ITEM_SELECTED_BGCOLOR_START = 0x80FFEEDD;
    public static final int LIST_ITEM_SELECTED_BGCOLOR_END = 0x80FFC891;
    private static Image listItemOddBg;
    private static Image listItemEvenBg;
    private static Image listItemSelectedBg;

    public static final Font MENU_TITLE_FONT = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    public static final Effect MENU_TITLE_EFFECT = new PlainEffect(MENU_TITLE_FONT, BLACK);
    public static final int MENU_ITEM_BGCOLOR = 0xE0FFFFFF;
    public static final int MENU_ITEM_DIVIDER_SIZE = 1;

    public static final int SELECTED_BGCOLOR = 0xFFFFC891;

    public static void init() {
        try {
            appIcon = Image.createImage("/app_icon.png");
            transparentAppIcon = Image.createImage("/transparent_app_icon.png");
            dialogBackground = Image.createImage("/dialog_bg.png");
            softIcon = Image.createImage("/soft_icon.png");
            iconBgSelected = Image.createImage("/icon_bg_selected.png");
            menuRefresh = Image.createImage("/menu_refresh.png");
            menuInstall = Image.createImage("/menu_install.png");
            menuBack = Image.createImage("/menu_back.png");
            menuExit = Image.createImage("/menu_exit.png");

            titlebarHeight = Math.max(TITLE_FONT.getHeight(), appIcon.getHeight()) * 3 / 2;

            buttonBackgrounds = new Image[2];
            buttonBackgrounds[0] = Image.createImage("/button_bg_normal.png");
            buttonBackgrounds[1] = Image.createImage("/button_bg_selected.png");

            gaugeImages = new Image[12];
            for (int i = 0; i < 9; i++) {
                gaugeImages[i] = Image.createImage("/gauge0" + (i + 1) + ".png");
            }
            for (int i = 9; i < 12; i++) {
                gaugeImages[i] = Image.createImage("/gauge" + (i + 1) + ".png");
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to load image resources", e);
        }
    }

    public static int getTitlebarHeight() {
        return titlebarHeight;
    }

    public static Image getAppIcon() {
        return appIcon;
    }

    public static Image getTransparentAppIcon() {
        return transparentAppIcon;
    }

    public static void initTitlebarBackground(int width) {
        if (titlebarBackground != null) {
            return;
        }

        int[] argb = DrawUtil.getVerticalGradientRgb(width, titlebarHeight,
                TITLEBAR_BG_START_COLOR, TITLEBAR_BG_END_COLOR, TITLEBAR_BG_BORDER_COLOR);
        titlebarBackground = Image.createRGBImage(argb, width, titlebarHeight, true);
    }

    public static Image getTitlebarBackground() {
        return titlebarBackground;
    }

    public static Image getDialog9PatchBackground() {
        return dialogBackground;
    }

    public static Image getSoftIcon() {
        return softIcon;
    }

    public static Image[] getGaugeImages() {
        return gaugeImages;
    }

    public static Image[] getButtonBackgrounds() {
        return buttonBackgrounds;
    }

    public static Image getIconBackgroudnSelected() {
        return iconBgSelected;
    }

    public static Image getMenuRefreshIcon() {
        return menuRefresh;
    }

    public static Image getMenuInstallIcon() {
        return menuInstall;
    }

    public static Image getMenuBackIcon() {
        return menuBack;
    }

    public static Image getMenuExitIcon() {
        return menuExit;
    }

    public static Image getListItemBg(int position, int width, int height) {
        Image res;
        if (position % 2 == 0) {
            if (listItemEvenBg == null) {
                int[] argb = DrawUtil.getVerticalGradientRgb(width, height,
                        LIST_ITEM_EVEN_BGCOLOR_START, LIST_ITEM_EVEN_BGCOLOR_END, TRANSPARENT);
                listItemEvenBg = Image.createRGBImage(argb, width, height, true);
            }
            res = listItemEvenBg;
        } else {
            if (listItemOddBg == null) {
                int[] argb = DrawUtil.getVerticalGradientRgb(width, height,
                        LIST_ITEM_ODD_BGCOLOR_START, LIST_ITEM_ODD_BGCOLOR_END, TRANSPARENT);
                listItemOddBg = Image.createRGBImage(argb, width, height, true);
            }
            res = listItemOddBg;
        }
        return res;
    }

    public static Image getListItemSelectedBg(int width, int height) {
        if (listItemSelectedBg == null) {
            int[] argb = DrawUtil.getVerticalGradientRgb(width, height,
                    LIST_ITEM_SELECTED_BGCOLOR_START, LIST_ITEM_SELECTED_BGCOLOR_END, TRANSPARENT);
            listItemSelectedBg = Image.createRGBImage(argb, width, height, true);
        }
        return listItemSelectedBg;
    }

    public static void initRepeatedBg(int width, int height) {
        if (repeatedBg == null) {
            try {
                Image cell = Image.createImage("/repeated_cell.png");
                repeatedBg = Image.createImage(width, height);
                Graphics g = repeatedBg.getGraphics();
                for (int y = 0; y < height; y += cell.getHeight()) {
                    for (int x = 0; x < width; x += cell.getWidth()) {
                        g.drawImage(cell, x, y, Graphics.LEFT | Graphics.TOP);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to load image resources", e);
            }

        }
    }

    public static Image getRepeatedBg() {
        return repeatedBg;
    }
}
