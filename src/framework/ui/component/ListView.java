
package framework.ui.component;

import main.config.Theme;

/**
 * A view that shows items in a vertically scrolling list. Currently, ListView
 * only allows up to one choice.
 *
 * @author Oscar Cai
 */

public class ListView extends ScrollView {

    /**
     * Indicates there is no selected item.
     */
    public static final int NO_SELECTION = -1;

    private int preDataCount;
    private int selectIndex;
    private ListAdapter adapter;
    private TextView emptyView;

    /**
     * Create a ListView.
     *
     * @param adapter the adapter containing the data to be displayed by this view.
     * @param emptyMessage the message to show if there is no data.
     */
    public ListView(ListAdapter adapter, String emptyMessage) {
        preDataCount = -1; // -1 means not initialized, 0 means no data.
        selectIndex = NO_SELECTION;

        emptyView = new TextView(emptyMessage, Theme.LIST_TITLE_EFFECT);
        emptyView.setPaddings(Theme.LIST_ITEM_PADDING,
                              Theme.LIST_ITEM_PADDING * 3,
                              Theme.LIST_ITEM_PADDING,
                              Theme.LIST_ITEM_PADDING);

        setAdapter(adapter);
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            adapter.setListView(this);
            /* adapter.setListView() will invoke onDataSetChanged(). */
        } else {
            onDataSetChanged();
        }
    }

    public ListAdapter getAdapter() {
        return adapter;
    }

    /**
     * Clear all data of this ListView.
     */
    public void clear() {
        preDataCount = -1; // -1 means not initialized, 0 means no data.
        selectIndex = NO_SELECTION;
        resetScrollPosition();
        if (adapter != null) {
            adapter.removeAllElements();
            onDataSetChanged();
        }
    }

    synchronized private void setSelect(int i) {
        if (adapter == null || adapter.isEmpty()) {
            return;
        }

        if (i < 0) {
            i = 0;
        } else if (i >= adapter.size()) {
            i = adapter.size() - 1;
        }
        if (selectIndex != i) {
            int j;
            selectIndex = i;
            for (j = -1, i = 0; i < contentView.getChildCount(); i++) {
                View v = contentView.getChild(i);
                if (isItemView(v)) {
                    j++;
                    if (selectIndex == j) {
                        // Count divider for scrolling
                        Rect rct = v.getScreenRect();
                        rct.height += Theme.LIST_DIVIDER_HEIGHT;
                        scrollToRect(rct);

                        v.setBackgroundImage(Theme.getListItemSelectedBg(v.getWidth(), v.getHeight()));
                    } else {
                        v.setBackgroundImage(Theme.getListItemBg(j, v.getWidth(), v.getHeight()));
                    }
                }
            }
        }
    }

    public void selectPrevious() {
        setSelect(selectIndex - 1);
    }

    public void selectNext() {
        setSelect(selectIndex + 1);
    }

    private boolean isItemView(View v) {
        return v instanceof Container;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (emptyView != null) {
            emptyView.destroy();
            emptyView = null;
        }
    }

    synchronized public void onDataSetChanged() {
        initComponents();

        int h = 0;
        if (adapter == null || adapter.isEmpty()) {
            contentView.removeAllChild(true);

            emptyView.setAlignment(contentView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
            contentView.addChild(emptyView);
            h = emptyView.getHeight();

            preDataCount = 0;
        } else {
            if (contentView.getChild(0) == emptyView) {
                contentView.removeChild(emptyView);
            }

            /* update persist data items. */
            View v;
            int i, j, vHeight;
            int persistCount = Math.min(preDataCount, adapter.size());
            for (i = 0, j = 0; i < contentView.getChildCount() && j < persistCount; i++) {
                v = contentView.getChild(i);
                if (isItemView(v)) {
                    vHeight = adapter.updateView(j, (Container) v);
                    v.setContentHeight(vHeight);
                    j++;
                }
                h += v.getHeight();
            }

            /* count last persist divider */
            if (j >= persistCount && i < contentView.getChildCount()) {
                i++;
                if (i < contentView.getChildCount()) {
                    v = contentView.getChild(i);
                    h += v.getHeight();
                }
            }

            if (preDataCount < adapter.size()) {
                /* add new data items */
                for (; j < adapter.size(); j++) {
                    v = adapter.getView(j, contentView);
                    contentView.addChild(v);
                    h += v.getHeight();

                    Divider divider = new Divider(v, MATCH_PARENT, Theme.LIST_DIVIDER_HEIGHT,
                            View.ALIGN_LEFT_WITH, View.ALIGN_BELOW, Divider.HORIZONTAL);
                    contentView.addChild(divider);
                    h += divider.getHeight();
                }
            } else {
                /* remove useless old data items */
                while (i < contentView.getChildCount()) {
                    v = contentView.getChild(contentView.getChildCount() - 1);
                    contentView.removeChild(v);
                    v.destroy();
                }
            }

            preDataCount = adapter.size();
        }

        contentView.setContentHeight(h);

        if (getParent() != null) {
            layout();
        }
    }

    synchronized public void onPointerPressed(int x, int y) {
        if (!contains(x, y) || adapter == null || adapter.isEmpty()) {
            return;
        }

        View curSelectedItem = null;
        View pressedItem = null;
        int pressedIndex = NO_SELECTION;
        for (int i = 0, j = -1; i < contentView.getChildCount(); i++) {
            View v = contentView.getChild(i);
            if (isItemView(v)) {
                j++;
                if (j == selectIndex) {
                    curSelectedItem = v;
                }
                if (v.contains(x, y)) {
                    pressedItem = v;
                    pressedIndex = j;
                }
            }
        }

        if (pressedItem != null && pressedItem != curSelectedItem) {
            if (curSelectedItem != null) {
                curSelectedItem.setBackgroundImage(Theme.getListItemBg(selectIndex, curSelectedItem.getWidth(), curSelectedItem.getHeight()));
            }
            pressedItem.setBackgroundImage(Theme.getListItemSelectedBg(pressedItem.getWidth(), pressedItem.getHeight()));
            selectIndex = pressedIndex;
        }
    }

    public int getClickedItemIndex(int x, int y) {
        if (!contains(x, y) || adapter == null || adapter.isEmpty()) {
            return NO_SELECTION;
        }

        View curSelectedItem = null;
        View clickedItem = null;
        int clickedIndex = NO_SELECTION;
        for (int i = 0, j = -1; i < contentView.getChildCount(); i++) {
            View v = contentView.getChild(i);
            if (isItemView(v)) {
                j++;
                if (j == selectIndex) {
                    curSelectedItem = v;
                }
                if (v.contains(x, y)) {
                    clickedItem = v;
                    clickedIndex = j;
                }
            }
        }

        if (curSelectedItem == null || clickedItem != curSelectedItem) {
            clickedIndex = NO_SELECTION;
        }
        return clickedIndex;
    }

    public int getSelectedItemIndex() {
        return selectIndex;
    }

    public Object getItem(int index) {
        if (adapter != null && !adapter.isEmpty() &&
            index >= 0 && index < adapter.size()) {
            return adapter.elementAt(index);
        }
        return null;
    }
}
