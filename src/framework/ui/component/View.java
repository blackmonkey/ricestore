
package framework.ui.component;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.util.DrawUtil;

import main.config.Theme;
import main.util.Log;

/**
 * A View is class representing a visual element of a application.
 * Each View has position (in terms of the upper-left corner of its visual
 * bounds), width, height, and can be made visible or invisible.
 * View subclasses must implement a {@link #paint(Graphics)} method so that
 * they can be rendered.
 * <p>
 * The View's (left, top) position is always interpreted relative to the
 * position of its parent. The initial position of a View is (0,0).
 * <p>
 * A View can be a container which contains child Windows.
 * <p>
 * A View also contains layout information which tell their parents how they
 * want to be laid out in relation to each other or to the parent.
 * <p>
 * The layout information describes how big the view wants to be for both width
 * and height, and where the view wants to locate. For each dimension, it can
 * specify one of:
 * <ul>
 * <li>MATCH_PARENT, which means that the view wants to be as big as its parent
 * (minus padding)
 * <li>WRAP_CONTENT, which means that the view wants to be just big enough to
 * enclose its content (plus padding)
 * <li>an exact number
 * </ul>
 * <p>
 * Note that you cannot have a circular dependency between the size of the
 * Layout and the position of its children. For example, you cannot have a
 * Layout whose height is set to {@link #WRAP_CONTENT} and a child set to
 * {@link #ALIGN_PARENT_BOTTOM}.
 *
 * @author Oscar Cai
 */

public abstract class View extends Rect {

    /**
     * @name The height or width measure type of a View.
     * @{
     */

    /**
     * Rule that enlarges the View as big as its parent, minus the parent's
     * padding, if any.
     */
    public static final int MATCH_PARENT = -1;

    /**
     * Rule that wraps the View just large enough to fit its own internal
     * content, taking its own padding into account.
     */
    public static final int WRAP_CONTENT = -2;

    /**
     * Rule that sets the View to specified exact size, taking its own padding
     * into account.
     */
    public static final int EXACT_SIZE = -3;

    /* @}*/

    /**
     * @name Alignment edge of a View to its aligned View.
     * @{
     */

    /**
     * Rule that aligns a View's left edge with another View's left edge.
     */
    public static final int ALIGN_LEFT_WITH     = 0x00010000;

    /**
     * Rule that aligns a View's right edge with another View's right edge.
     */
    public static final int ALIGN_RIGHT_WITH    = 0x00020000;

    /**
     * Rule that aligns a View's top edge with another View's top edge.
     */
    public static final int ALIGN_TOP_WITH      = 0x00040000;

    /**
     * Rule that aligns a View's bottom edge with another View's bottom edge.
     */
    public static final int ALIGN_BOTTOM_WITH   = 0x00080000;

    /**
     * Rule that aligns a View's right edge with another View's left edge.
     */
    public static final int ALIGN_LEFT_OF       = 0x00100000;

    /**
     * Rule that aligns a View's left edge with another View's right edge.
     */
    public static final int ALIGN_RIGHT_OF      = 0x00200000;

    /**
     * Rule that aligns a View's bottom edge with another View's top edge.
     */
    public static final int ALIGN_ABOVE         = 0x00400000;

    /**
     * Rule that aligns a View's top edge with another View's bottom edge.
     */
    public static final int ALIGN_BELOW         = 0x00800000;

    /**
     * Rule that aligns a View's left edge with its parent's left edge, taking
     * parent's padding into account.
     */
    public static final int ALIGN_PARENT_LEFT   = 0x01000000;

    /**
     * Rule that aligns a View's right edge with its parent's right edge, taking
     * parent's padding into account.
     */
    public static final int ALIGN_PARENT_RIGHT  = 0x02000000;

    /**
     * Rule that aligns a View's top edge with its parent's top edge, taking
     * parent's padding into account.
     */
    public static final int ALIGN_PARENT_TOP    = 0x04000000;

    /**
     * Rule that aligns a View's bottom edge with its parent's bottom edge,
     * taking parent's padding into account.
     */
    public static final int ALIGN_PARENT_BOTTOM = 0x08000000;

    /**
     * Rule that aligns a View center in its parent horizontally and vertically.
     */
    public static final int ALIGN_PARENT_CENTER = 0x10000000;

    /* @}*/

    /**
     * @name Horizontal or vertical alignment position of a View on its
     * alignment edge.
     * @{
     */

    /**
     * Rule that aligns a View to the left on its horizontal alignment edge.
     */
    public static final int ALIGN_LEFT   = 0x00000001;

    /**
     * Rule that aligns a View to the right on its horizontal alignment edge.
     */
    public static final int ALIGN_RIGHT  = 0x00000002;

    /**
     * Rule that aligns a View to the top on its vertical alignment edge.
     */
    public static final int ALIGN_TOP    = 0x00000004;

    /**
     * Rule that aligns a View to the bottom on its vertical alignment edge.
     */
    public static final int ALIGN_BOTTOM = 0x00000008;

    /**
     * Rule that aligns a View to the center on its horizontal or vertical
     * alignment edge.
     */
    public static final int ALIGN_CENTER = 0x00000010;

    /* @}*/

    /**
     * @name Alignment rule mask.
     * @{
     */

    /**
     * Mask for the alignment edge rules.
     */
    public static final int ALIGN_EDGE_MASK     = 0x0FFFF0000;

    /**
     * Mask for the alignment position rules.
     */
    public static final int ALIGN_POSITION_MASK =  0x0000FFFF;

    /* @}*/

    /**
     * @name Layout measure flags
     * @{
     */

    /**
     * Indicates the width has been measured since latest size changed.
     */
    public static final int WIDTH_MEASURED = 0x01;

    /**
     * Indicates the width has been measured since latest size changed.
     */
    public static final int HEIGHT_MEASURED = 0x02;

    /* @}*/

    /**
     * Indicate the View is visible or not.
     */
    private boolean visible;

    /**
     * Parent View
     */
    protected View parent;

    /**
     * Specify the child views. Currently, the child views are only sorted by
     * adding order without by z-index order, while the first added child view
     * normally acts as layout anchor for other subsequent child views.
     * <p>
     * TODO: Sort child views by z-index order and adding order.
     */
    private Vector children;

    /**
     * Specify how to measure the width of the view. Can be one of the constants
     * {@link #WRAP_CONTENT}, {@link #MATCH_PARENT}, or {@link #EXACT_SIZE}
     */
    protected int widthType;

    /**
     * Specify how to measure the height of the view. Can be one of the constants
     * {@link #WRAP_CONTENT}, {@link #MATCH_PARENT}, or {@link #EXACT_SIZE}
     */
    protected int heightType;

    /**
     * Specify extra space on the left side of this view. This space is
     * inside this view's bounds. Must be a dimension value in pixels.
     */
    private int paddingLeft;

    /**
     * Specify extra space on the top side of this view. This space is
     * inside this view's bounds. Must be a dimension value in pixels.
     */
    private int paddingTop;

    /**
     * Specify extra space on the right side of this view. This space is
     * inside this view's bounds. Must be a dimension value in pixels.
     */
    private int paddingRight;

    /**
     * Specify extra space on the bottom side of this view. This space is
     * inside this view's bounds. Must be a dimension value in pixels.
     */
    private int paddingBottom;

    /**
     * Specify the horizontal alignment rules of this view to its aligned view.
     * Can be 0, or one of the following combinations:
     * <ul>
     * <li>[{@link #ALIGN_LEFT_WITH} | {@link #ALIGN_RIGHT_WITH}] | [
     * {@link #ALIGN_TOP} | {@link #ALIGN_BOTTOM}]</li>
     * <li>[{@link #ALIGN_LEFT_OF} | {@link #ALIGN_RIGHT_OF} |
     * {@link #ALIGN_PARENT_LEFT} | {@link #ALIGN_PARENT_RIGHT}] | [
     * {@link #ALIGN_TOP} | {@link #ALIGN_CENTER} | {@link #ALIGN_BOTTOM} | 0]</li>
     * <li>ALIGN_PARENT_CENTER</li>
     * </ul>
     * If horizontalAlign is 0, during layout, the View's left and width should
     * be directly used without any calculated, i.e the left and width have been
     * initialized.
     */
    private int horizontalAlign;

    /**
     * Specify the vertical alignment rules of this view to its aligned view.
     * Can be 0, or one of the following combinations:
     * <ul>
     * <li>[{@link #ALIGN_TOP_WITH} | {@link #ALIGN_BOTTOM_WITH}] | [
     * {@link #ALIGN_LEFT} | {@link #ALIGN_RIGHT}]</li>
     * <li>[{@link #ALIGN_ABOVE} | {@link #ALIGN_BELOW} |
     * {@link #ALIGN_PARENT_TOP} | {@link #ALIGN_PARENT_BOTTOM}] | [
     * {@link #ALIGN_LEFT} | {@link #ALIGN_CENTER} | {@link #ALIGN_RIGHT} | 0]</li>
     * <li>ALIGN_PARENT_CENTER</li>
     * </ul>
     * If verticalAlign is 0, during layout, the View's top and height should
     * be directly used without any calculated, i.e the top and height have been
     * initialized.
     */
    private int verticalAlign;

    /**
     * Specify the measure states of the View. See
     * <ul>
     * <li>{@link #WIDTH_MEASURED}</li>
     * <li>{@link #HEIGHT_MEASURED}</li>
     * </ul>
     */
    private int measureFlag;

    /**
     * Specify the View to which this View aligns.
     */
    private View alignView;

    /**
     * Specify the background color of this view. If background color is not
     * transparent, bgImage must be null.
     */
    private int bgColor;

    /**
     * Specify the background image of this view. If background image is not
     * null, bgColor must be transparent.
     */
    private Image bgImage;

    /**
     * Specify the android style nine-patch background image of this view. If
     * background image is not null, bgColor must be transparent.
     */
    private Image ninePatchBgImage;

    /**
     * The dimension of content
     */
    private int contentWidth;
    private int contentHeight;

    /**
     * Create a default View, whose position is at the left-top corner of the
     * View's parent, while size is 0 x 0.
     */
    public View() {
        this(0, 0, 0, 0);
    }

    /**
     * Create a visible View with specified size, whose position is initially
     * at the left-top corner of the View's parent.
     *
     * @param width The width of the View
     * @param height The height of the View
     */
    public View(int width, int height) {
        this(0, 0, width, height);
    }

    /**
     * Creates a visible View with the specified dimensions.
     *
     * @param left The left bound of the View
     * @param top The top bound of the View
     * @param width The width of the View
     * @param height The height of the View
     */
    public View(int left, int top, int width, int height) {
        children = new Vector();
        moveTo(left, top);
        changeSize(width, height);
        setVisible(true);
        setBackgroundColor(Theme.TRANSPARENT);
    }

    /**
     * Call this when the View should be closed.
     */
    public void destroy() {
        View v;
        for (int i = 0; i < getChildCount(); i++) {
            v = getChild(i);
            v.destroy();
        }
        children.removeAllElements();
        bgImage = null;
        ninePatchBgImage = null;
        alignView = null;
        parent = null;
        onDestroy();
    }

    /**
     * Perform any final cleanup before a View is destroyed. This can
     * happen because the View is destroying (someone called {@link #destroy} on
     * it.
     * <p>
     * This method is usually implemented to free resources like threads that
     * are associated with an View, so that a destroyed View does not leave such
     * things around while the rest of its application is still running.
     * <p>
     * <em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em>
     *
     * @see #destroy
     */
    protected void onDestroy() {
    }

    public synchronized void moveTo(int left, int top) {
        setLeft(left);
        setTop(top);
    }

    /**
     * Change the View's size.
     * @param width the width to set.
     * @param height the height to set.
     */
    public synchronized void changeSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setLeft(int l) {
        left = l;
    }

    public void setTop(int t) {
        top = t;
    }

    public void setWidth(int w) {
        if (width != w) {
            width = w;
            measureFlag |= WIDTH_MEASURED;
        }
    }

    public void setHeight(int h) {
        if (height != h) {
            height = h;
            measureFlag |= HEIGHT_MEASURED;
        }
    }

    public void setContentWidth(int w) {
        if (contentWidth != w) {
            contentWidth = w;
            if (widthType == WRAP_CONTENT) {
                width = contentWidth + paddingLeft + paddingRight;
                measureFlag |= WIDTH_MEASURED;
            }
            update9PatchBackgroundImage();
        }
    }

    public void setContentHeight(int h) {
        if (contentHeight != h) {
            contentHeight = h;
            if (heightType == WRAP_CONTENT) {
                height = contentHeight + paddingTop + paddingBottom;
                measureFlag |= HEIGHT_MEASURED;
            }
            update9PatchBackgroundImage();
        }
    }

    public int getWidthType() {
        return widthType;
    }

    public int getHeightType() {
        return heightType;
    }

    /**
     * Set the paddings, in pixels.
     *
     * @param left the left padding size
     * @param top the top padding size
     * @param right the right padding size
     * @param bottom the bottom padding size
     */
    public void setPaddings(int left, int top, int right, int bottom) {
        setPaddingLeft(left);
        setPaddingRight(right);
        setPaddingTop(top);
        setPaddingBottom(bottom);
    }

    public void setPaddingLeft(int padding) {
        if (paddingLeft != padding) {
            paddingLeft = padding;
            if (widthType == WRAP_CONTENT) {
                width = contentWidth + paddingLeft + paddingRight;
                measureFlag |= WIDTH_MEASURED;
            }
        }
    }

    public void setPaddingRight(int padding) {
        if (paddingRight != padding) {
            paddingRight = padding;
            if (widthType == WRAP_CONTENT) {
                width = contentWidth + paddingLeft + paddingRight;
                measureFlag |= WIDTH_MEASURED;
            }
        }
    }

    public void setPaddingTop(int padding) {
        if (paddingTop != padding) {
            paddingTop = padding;
            if (heightType == WRAP_CONTENT) {
                height = contentHeight + paddingTop + paddingBottom;
                measureFlag |= HEIGHT_MEASURED;
            }
        }
    }

    public void setPaddingBottom(int padding) {
        if (paddingBottom != padding) {
            paddingBottom = padding;
            if (heightType == WRAP_CONTENT) {
                height = contentHeight + paddingTop + paddingBottom;
                measureFlag |= HEIGHT_MEASURED;
            }
        }
    }

    /**
     * Set background color of this view.
     *
     * @param color exact color value, should be AARRGGBB
     */
    public void setBackgroundColor(int color) {
        bgColor = color;
        bgImage = null;
    }

    /**
     * Set background image of this view.
     *
     * @param img exact image instance.
     */
    public void setBackgroundImage(Image img) {
        bgColor = Theme.TRANSPARENT;
        bgImage = img;
    }

    /**
     * Set Android style nine-patch background image of this view.
     * <p>
     * <b>NOTE:</b>
     * <ul>
     * <li>Before invoke this method, it is better that this view's content
     * size is set.</li>
     * <li>After invoke this method, the previous set paddings will be replaced
     * by the paddings with the nine-patch image.</li>
     * </ul>
     *
     * @param img exact image instance.
     */
    public void set9PatchBackgroundImage(Image img) {
        if (ninePatchBgImage != null) {
            ninePatchBgImage = null; // GC the old nine-patch background image
        }
        ninePatchBgImage = img;
        update9PatchBackgroundImage();
    }

    private void update9PatchBackgroundImage() {
        if (ninePatchBgImage == null) {
            return;
        }

        Image bg;
        if (getContentWidth() > 0 || getContentHeight() > 0) {
            bg = DrawUtil.stretch9PatchImage(ninePatchBgImage, getContentWidth(), getContentHeight(), true);
        } else {
            bg = ninePatchBgImage;
        }
        setBackgroundImage(bg);

        int hPadding = (bg.getWidth() - getContentWidth()) >> 1;
        int vPadding = (bg.getHeight() - getContentHeight()) >> 1;
        setPaddings(hPadding, vPadding, hPadding, vPadding);
    }

    public void setVisible(boolean shown) {
        visible = shown;
    }

    public synchronized void insertChild(View v, int position) {
        if (v != null) {
            if (children.isEmpty()) {
                addChild(v);
            } else if (position >= 0 && position <= children.size()) {
                children.insertElementAt(v, position);
                v.setParent(this);
            }
        }
    }

    public synchronized void addChild(View v) {
        if (v != null) {
            children.addElement(v);
            v.setParent(this);
        }
    }

    public synchronized void removeChild(View v) {
        if (v != null) {
            children.removeElement(v);
            v.setParent(null);
        }
    }

    public synchronized void removeAllChild(boolean destroyItem) {
        for (int i = 0; i < children.size(); i++) {
            View v = (View) children.elementAt(i);
            v.setParent(null);
            if (destroyItem) {
                v.destroy();
                v = null;
            }
        }
        children.removeAllElements();
    }

    public void setParent(View v) {
        parent = v;
    }

    /**
     * Set the view alignment in its parent.
     *
     * @param v The view to which this view aligns.
     * @param widthType width measure type of this View, see {@link #widthType}.
     * @param heightType height measure type of this View, see {@link #heightType}.
     * @param horizontal horizontal alignment, see {@link #horizontalAlign}.
     * @param vertical vertical alignment, see {@link #verticalAlign}.
     */
    public void setAlignment(View v, int widthType, int heightType, int horizontal, int vertical) {
        if ((widthType != EXACT_SIZE || heightType != EXACT_SIZE) && v == null) {
            throw new IllegalArgumentException("Null view!");
        }

        if (widthType != MATCH_PARENT && widthType != WRAP_CONTENT && widthType != EXACT_SIZE) {
            throw new IllegalArgumentException("Unknown width measure type : " + widthType);
        }
        if (heightType != MATCH_PARENT && heightType != WRAP_CONTENT && heightType != EXACT_SIZE) {
            throw new IllegalArgumentException("Unknown height measure type : " + heightType);
        }
        if (widthType == MATCH_PARENT && parent != null && parent.widthType == WRAP_CONTENT) {
            throw new IllegalArgumentException(
                    "widthType cannot be MATCH_PARENT when parent's widthType is WRAP_CONTENT!");
        }
        if (heightType == MATCH_PARENT && parent != null && parent.heightType == WRAP_CONTENT) {
            throw new IllegalArgumentException(
                    "heightType cannot be MATCH_PARENT when parent's heightType is WRAP_CONTENT!");
        }

        this.widthType = widthType;
        this.heightType = heightType;

        int alignEdge = horizontal & ALIGN_EDGE_MASK;
        int alignPosition = horizontal & ALIGN_POSITION_MASK;
        if (horizontal != 0
            && ((alignEdge != ALIGN_LEFT_WITH && alignEdge != ALIGN_RIGHT_WITH
                 && alignEdge != ALIGN_LEFT_OF && alignEdge != ALIGN_RIGHT_OF
                 && alignEdge != ALIGN_PARENT_LEFT && alignEdge != ALIGN_PARENT_RIGHT
                 && alignEdge != ALIGN_PARENT_CENTER)
                || (alignPosition != ALIGN_TOP
                    && alignPosition != ALIGN_CENTER
                    && alignPosition != ALIGN_BOTTOM
                    && alignPosition != 0))) {
            throw new IllegalArgumentException("Unknown horizontal alignment : " + horizontal);
        }

        alignEdge = vertical & ALIGN_EDGE_MASK;
        alignPosition = vertical & ALIGN_POSITION_MASK;
        if (vertical != 0
            && ((alignEdge != ALIGN_TOP_WITH && alignEdge != ALIGN_BOTTOM_WITH
                 && alignEdge != ALIGN_ABOVE && alignEdge != ALIGN_BELOW
                 && alignEdge != ALIGN_PARENT_TOP && alignEdge != ALIGN_PARENT_BOTTOM
                 && alignEdge != ALIGN_PARENT_CENTER)
                || (alignPosition != ALIGN_LEFT
                    && alignPosition != ALIGN_CENTER
                    && alignPosition != ALIGN_RIGHT
                    && alignPosition != 0))) {
            throw new IllegalArgumentException("Unknown vertical alignment : " + vertical);
        }

        alignView = v;
        horizontalAlign = horizontal;
        verticalAlign = vertical;
    }

    /**
     * Get the left padding size.
     *
     * @return exact size
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Get the top padding size.
     *
     * @return exact size
     */
    public int getPaddingTop() {
        return paddingTop;
    }

    /**
     * Get the right padding size.
     *
     * @return exact size
     */
    public int getPaddingRight() {
        return paddingRight;
    }

    /**
     * Get the bottom padding size.
     *
     * @return exact size
     */
    public int getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * Get the View to which View aligns.
     *
     * @return exact View instance.
     */
    public View getAlignView() {
        return alignView;
    }

    /**
     * Whether the View is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Get the parent View of the View.
     */
    public final View getParent() {
        return parent;
    }

    /**
     * Get the specified child View.
     *
     * @param index the child View's index.
     */
    public final View getChild(int index) {
        return (View) children.elementAt(index);
    }

    /**
     * Get the count of the child views.
     *
     * @return the exact count.
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Get the content area of the View on screen, including its internal padding.
     *
     * @return the rectangle of the View's bounds.
     */
    public Rect getScreenRect() {
        Rect rect = new Rect();
        if (parent != null) {
            Rect parentRect = parent.getScreenRect();
            // left and top have count parent paddings.
            rect.moveTo(parentRect.getLeft() + left,
                        parentRect.getTop() + top);
        } else {
            rect.moveTo(left, top);
        }
        rect.changeSize(width, height);
        return rect;
    }

    private String getSizeTypeName(int type) {
        switch (type) {
        case MATCH_PARENT:
            return "MATCH_PARENT";
        case WRAP_CONTENT:
            return "WRAP_CONTENT";
        case EXACT_SIZE:
            return "EXACT_SIZE";
        default:
            return "UNKNOWN:" + type;
        }
    }

    /**
     * Get the width of the concrete content. Currently, only support left-top
     * aligned content.
     *
     * @return exact size.
     */
    public int getContentWidth() {
        return contentWidth;
    }

    /**
     * Get the height of the concrete content. Currently, only support left-top
     * aligned content.
     *
     * @return exact size.
     */
    public int getContentHeight() {
        return contentHeight;
    }

    private int getBrotherHorizontalMiddleOffsetTo(View v) {
        int dstMiddle = v.getLeft() + v.getPaddingLeft() + v.getContentWidth() / 2;
        int middle = getLeft() + getPaddingLeft() + getContentWidth() / 2;
        return dstMiddle - middle;
    }

    private int getBrotherVerticalMiddleOffsetTo(View v) {
        int dstMiddle = v.getTop() + v.getPaddingTop() + v.getContentHeight() / 2;
        int middle = getTop() + getPaddingTop() + getContentHeight() / 2;
        return dstMiddle - middle;
    }

    private int getParentHorizontalMiddleOffsetTo(View p) {
        int dstMiddle = p.getPaddingLeft() + p.getContentWidth() / 2;
        int middle = getLeft() + getPaddingLeft() + getContentWidth() / 2;
        return dstMiddle - middle;
    }

    private int getParentVerticalMiddleOffsetTo(View p) {
        int dstMiddle = p.getPaddingTop() + p.getContentHeight() / 2;
        int middle = getTop() + getPaddingTop() + getContentHeight() / 2;
        return dstMiddle - middle;
    }

    /**
     * Layout the View's content, i.e. the child views if has.
     * <p>
     * There are several limitations in current implementation:
     * <ul>
     * <li>Before invoke a View's layout(), if the View has parent, the position
     * and size of its parent must be figured out.</li>
     * <li>Before invoke a View's layout(), if the View aligns to/with some other
     * View, the position and size of the other View must be figured out.</li>
     * <li>The first child of the View should has concrete alignment.</li>
     * </ul>
     */
    public synchronized void layout() {
        String thisClass = getClass().getName();

        /**
         * Change self size if layout parameters specify match parent.
         */
        if (parent != null) {
            String parentClass = parent.getClass().getName();
            if (widthType == MATCH_PARENT) {
                if (parent.widthType == WRAP_CONTENT) {
                    warnUnsupported("Doesn't support WRAP_CONTENT in " + parentClass +
                            " while MATCH_PARENT in " + thisClass + "!");
                    return;
                } else if ((parent.measureFlag & WIDTH_MEASURED) == 0) {
                    /**
                     * TODO: Handle this situation, i.e. the parent width has
                     * not been figured out when child needs it.
                     */
                    warnUnsupported(
                        "Doesn't support " + parentClass +
                        " width has not been figured out when " + thisClass +
                        " needs it!");
                    return;
                } else {
                    /**
                     * FIXME: Current calculating sets every MATCH_PARENT width
                     * type child as wide as its parent. Thus, only last one on
                     * the same line can be shown.
                     * TODO: Handle the situation that there are other brother
                     * whose widthType is MATCH_PARENT
                     */
                    int w = parent.getWidth() - parent.getPaddingLeft() -
                            parent.getPaddingRight();
                    setWidth(w > 0 ? w : 0);
                }
            }
            if (heightType == MATCH_PARENT) {
                if (parent.heightType == WRAP_CONTENT) {
                    warnUnsupported(
                        "Doesn't support WRAP_CONTENT in " + parentClass +
                        " while MATCH_PARENT in " + thisClass + "!");
                    return;
                } else if ((parent.measureFlag & HEIGHT_MEASURED) == 0) {
                    /**
                     * TODO: Handle this situation, i.e. the parent height has
                     * not been figured out when child needs it.
                     */
                    warnUnsupported(
                        "Doesn't support " + parentClass +
                        " height has not been figured out when " + thisClass +
                        " needs it!");
                    return;
                } else {
                    /**
                     * FIXME: Current calculating sets every MATCH_PARENT width
                     * type child as high as its parent. Thus, only last one on
                     * the same column can be shown.
                     * TODO: Handle the situation that there are other brother
                     * whose heightType is MATCH_PARENT
                     */
                    int h = parent.getHeight() - parent.getPaddingTop() -
                            parent.getPaddingBottom();
                    setHeight(h > 0 ? h : 0);
                }
            }
        }

        /**
         * To here, the width/height of the View is either figured out or not
         * due to widthType/heightType is WRAP_CONTENT.
         */

        /**
         * Layout child Views.
         */
        Rect childArea = new Rect();
        View v;
        for (int i = 0; i < getChildCount(); i++) {
            v = getChild(i);
            v.layout();
            childArea.add((Rect)v);
        }

        /**
         * Change self size if layout parameters specify wrap content, and there
         * are child views, and the size is still not figured out.
         */
        if (getChildCount() > 0 && (measureFlag & (WIDTH_MEASURED | HEIGHT_MEASURED)) == 0) {
            setContentWidth(childArea.getWidth());
            setContentHeight(childArea.getHeight());
        }

        // Check measure state once more
        if ((measureFlag & (WIDTH_MEASURED | HEIGHT_MEASURED)) == 0) {
            throw new IllegalStateException(
                "It's strange that the width and height are still not figured out here: "
                + thisClass + " width="
                + width + ", height=" + height + ", widthType="
                + getSizeTypeName(widthType) + ", heightType="
                + getSizeTypeName(heightType));
        }

        /**
         * Layout the View according to its alignment parameters.
         */
        int alignEdge, alignPosition;
        int l, t;
        if (horizontalAlign != 0 && alignView != null) {
            alignEdge = horizontalAlign & ALIGN_EDGE_MASK;
            alignPosition = horizontalAlign & ALIGN_POSITION_MASK;
            switch (alignEdge) {
            case ALIGN_LEFT_WITH:
                switch (alignPosition) {
                case ALIGN_TOP:
                    moveTo(alignView.getLeft(), alignView.getTop() - getHeight());
                    break;

                case ALIGN_BOTTOM:
                    moveTo(alignView.getLeft(), alignView.getTop() + alignView.getHeight());
                    break;

                case 0:
                    setLeft(alignView.getLeft());
                    break;
                }
                break;

            case ALIGN_RIGHT_WITH:
                l = alignView.getLeft() + alignView.getWidth() - getWidth();
                switch (alignPosition) {
                case ALIGN_TOP:
                    moveTo(l, alignView.getTop() - getHeight());
                    break;

                case ALIGN_BOTTOM:
                    moveTo(l, alignView.getTop() + alignView.getHeight());
                    break;

                case 0:
                    setLeft(l);
                    break;
                }
                break;

            case ALIGN_LEFT_OF:
                l = alignView.getLeft() - getWidth();
                switch (alignPosition) {
                case ALIGN_TOP:
                    moveTo(l, alignView.getTop());
                    break;

                case ALIGN_CENTER:
                    moveTo(l, getTop() + getBrotherVerticalMiddleOffsetTo(alignView));
                    break;

                case ALIGN_BOTTOM:
                    moveTo(l, alignView.getTop() + alignView.getHeight());
                    break;

                case 0:
                    setLeft(l);
                    break;
                }
                break;

            case ALIGN_RIGHT_OF:
                l = alignView.getLeft() + alignView.getWidth();
                switch (alignPosition) {
                case ALIGN_TOP:
                    moveTo(l, alignView.getTop());
                    break;

                case ALIGN_CENTER:
                    moveTo(l, getTop() + getBrotherVerticalMiddleOffsetTo(alignView));
                    break;

                case ALIGN_BOTTOM:
                    moveTo(l, alignView.getTop() + alignView.getHeight());
                    break;

                case 0:
                    setLeft(l);
                    break;
                }
                break;

            case ALIGN_PARENT_LEFT:
                switch (alignPosition) {
                case ALIGN_TOP:
                    moveTo(alignView.getPaddingLeft(), alignView.getPaddingTop());
                    break;

                case ALIGN_CENTER:
                    moveTo(alignView.getPaddingLeft(), getTop() + getParentVerticalMiddleOffsetTo(alignView));
                    break;

                case ALIGN_BOTTOM:
                    moveTo(alignView.getPaddingLeft(), alignView.getHeight() - alignView.getPaddingBottom() - getHeight());
                    break;

                case 0:
                    setLeft(alignView.getPaddingLeft());
                    break;
                }
                break;

            case ALIGN_PARENT_RIGHT:
                l = alignView.getWidth() - alignView.getPaddingRight() - getWidth();
                switch (alignPosition) {
                case ALIGN_TOP:
                    moveTo(l, alignView.getPaddingTop());
                    break;

                case ALIGN_CENTER:
                    moveTo(l, getTop() + getParentVerticalMiddleOffsetTo(alignView));
                    break;

                case ALIGN_BOTTOM:
                    moveTo(l, alignView.getHeight() - alignView.getPaddingBottom() - getHeight());
                    break;

                case 0:
                    setLeft(l);
                    break;
                }
                break;

            case ALIGN_PARENT_CENTER:
                setLeft(getLeft() + getParentHorizontalMiddleOffsetTo(alignView));
                break;
            }
        }

        if (verticalAlign != 0 && alignView != null) {
            alignEdge = verticalAlign & ALIGN_EDGE_MASK;
            alignPosition = verticalAlign & ALIGN_POSITION_MASK;
            switch (alignEdge) {
            case ALIGN_TOP_WITH:
                switch (alignPosition) {
                case ALIGN_LEFT:
                    moveTo(alignView.getLeft() - getWidth(), alignView.getTop());
                    break;

                case ALIGN_RIGHT:
                    moveTo(alignView.getLeft() + alignView.getWidth(), alignView.getTop());
                    break;

                case 0:
                    setTop(alignView.getTop());
                    break;
                }
                break;

            case ALIGN_BOTTOM_WITH:
                t = alignView.getTop() + alignView.getHeight() - getHeight();
                switch (alignPosition) {
                case ALIGN_LEFT:
                    moveTo(alignView.getLeft() - getWidth(), t);
                    break;

                case ALIGN_RIGHT:
                    moveTo(alignView.getLeft() + alignView.getWidth(), t);
                    break;

                case 0:
                    setTop(t);
                    break;
                }
                break;

            case ALIGN_ABOVE:
                t = alignView.getTop() - getHeight();
                switch (alignPosition) {
                case ALIGN_LEFT:
                    moveTo(alignView.getLeft(), t);
                    break;

                case ALIGN_CENTER:
                    moveTo(getLeft() + getBrotherHorizontalMiddleOffsetTo(alignView), t);
                    break;

                case ALIGN_RIGHT:
                    moveTo(alignView.getLeft() + alignView.getWidth(), t);
                    break;

                case 0:
                    setTop(t);
                    break;
                }
                break;

            case ALIGN_BELOW:
                t = alignView.getTop() + alignView.getHeight();
                switch (alignPosition) {
                case ALIGN_LEFT:
                    moveTo(alignView.getLeft(), t);
                    break;

                case ALIGN_CENTER:
                    moveTo(getLeft() + getBrotherHorizontalMiddleOffsetTo(alignView), t);
                    break;

                case ALIGN_RIGHT:
                    moveTo(alignView.getLeft() + alignView.getWidth(), t);
                    break;

                case 0:
                    setTop(t);
                    break;
                }
                break;

            case ALIGN_PARENT_TOP:
                switch (alignPosition) {
                case ALIGN_LEFT:
                    moveTo(alignView.getPaddingLeft(), alignView.getPaddingTop());
                    break;

                case ALIGN_CENTER:
                    moveTo(getLeft() + getParentHorizontalMiddleOffsetTo(alignView), alignView.getPaddingTop());
                    break;

                case ALIGN_RIGHT:
                    moveTo(alignView.getWidth() - alignView.getPaddingRight() - getWidth(), alignView.getPaddingTop());
                    break;

                case 0:
                    setTop(alignView.getPaddingTop());
                    break;
                }
                break;

            case ALIGN_PARENT_BOTTOM:
                t = alignView.getHeight() - alignView.getPaddingBottom() - getHeight();
                switch (alignPosition) {
                case ALIGN_LEFT:
                    moveTo(alignView.getPaddingLeft(), t);
                    break;

                case ALIGN_CENTER:
                    moveTo(getLeft() + getParentHorizontalMiddleOffsetTo(alignView), t);
                    break;

                case ALIGN_RIGHT:
                    moveTo(alignView.getWidth() - alignView.getPaddingRight() - getWidth(), t);
                    break;

                case 0:
                    setTop(t);
                    break;
                }
                break;

            case ALIGN_PARENT_CENTER:
                setTop(getTop() + getParentVerticalMiddleOffsetTo(alignView));
                break;
            }
        }
    }

    private void warnUnsupported(String msg) {
        try {
            throw new Exception(msg);
        } catch (Exception e) {
            Log.e("RiceStore.View", msg, e);
        }
    }

    public void paint(Graphics g) {
        /**
         * If not visible, skip painting own area and children
         */
        if (!isVisible()) {
            return;
        }

        // Calculate screen bounds first.
        Rect screenRect = getScreenRect();

        /**
         * Paint background
         */
        if (bgColor != Theme.TRANSPARENT) {
            int alpha = DrawUtil.getAlpha(bgColor);
            if (alpha == 0x0FF) {
                g.setColor(bgColor);
                g.fillRect(screenRect.getLeft(), screenRect.getTop(),
                           screenRect.getWidth(), screenRect.getHeight());
            } else if (alpha > 0) {
                int[] argb = new int[screenRect.getWidth() * screenRect.getHeight()];
                for (int i = 0; i < argb.length; i++) {
                    argb[i] = bgColor;
                }
                g.drawRGB(argb, 0, screenRect.getWidth(),
                          screenRect.getLeft(), screenRect.getTop(),
                          screenRect.getWidth(), screenRect.getHeight(),
                          true);
            }
        }
        if (bgImage != null) {
            g.drawImage(bgImage, screenRect.getLeft(), screenRect.getTop(), Graphics.LEFT | Graphics.TOP);
        }

        /**
         * Paint any text/image content owned by the view.
         */
        paintContent(g, screenRect.getLeft(), screenRect.getTop(), screenRect.getWidth(), screenRect.getHeight());

        /**
         * Paint child views.
         */
        for (int i = 0; i < getChildCount(); i++) {
            getChild(i).paint(g);
        }
    }

    /**
     * Paint any text/image content owned by the view.
     *
     * @param g the drawing Graphics context
     * @param left the left bound of drawing area
     * @param top the top bound of drawing area
     * @param width the width of drawing area
     * @param height the height of drawing area
     */
    protected abstract void paintContent(Graphics g, int left, int top, int width, int height);

    /**
     * Handle key events.
     *
     * @param keyCode the key code of the key event
     * @return true if the key event is handled in side, false otherwise.
     */
    public boolean handleKeyPressed(int keyCode) {
        return false;
    }

    /**
     * Whether the specified point is in the view's screen rectangle.
     * @param x
     * @param y
     * @return
     */
    public boolean contains(int x, int y) {
        Rect rct = getScreenRect();
        return x >= rct.left && x < rct.left + rct.width && y >= rct.top && y < rct.top + rct.height;
    }
}
