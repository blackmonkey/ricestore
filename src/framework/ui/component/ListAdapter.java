
package framework.ui.component;

import java.util.Vector;

import main.config.Theme;

/**
 * A ListAdapter object acts as a bridge between an {@link ListView} and the
 * underlying data for that view. The ListAdapter provides access to the data
 * items. The ListAdapter is also responsible for making a {@link View} for
 * each item in the data set.
 *
 * @author Oscar Cai
 */

public abstract class ListAdapter extends Vector {

    protected ListView listView;

    /**
     * Create a ListAdapter from the specified data set.
     *
     * @param dataSet the data set to copy.
     */
    public ListAdapter(Vector dataSet) {
        super(dataSet == null ? 10 : dataSet.size());
        if (dataSet != null) {
            for (int i = 0; i < dataSet.size(); i++) {
                addElement(dataSet.elementAt(i));
            }
        }
    }

    public void setListView(ListView v) {
        listView = v;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        if (listView != null) {
            listView.onDataSetChanged();
        }
    }

    /**
     * Get a View that displays the data at the specified position in the data
     * set.
     *
     * @param position The position of the item within the adapter's data set
     * of the item whose view we want.
     * @param container The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    public View getView(int position, View container) {
        View alignView;
        int hAlign;
        int vAlign;

        if (position == 0) {
            alignView = container;
            hAlign = View.ALIGN_PARENT_LEFT;
            vAlign = View.ALIGN_PARENT_TOP;
        } else {
            // align to latest Divider in the container.
            alignView = container.getChild(container.getChildCount() - 1);
            hAlign = View.ALIGN_LEFT_WITH;
            vAlign = View.ALIGN_BELOW;
        }

        Container itemView = new Container();
        itemView.setAlignment(alignView, View.MATCH_PARENT, View.WRAP_CONTENT, hAlign, vAlign);
        itemView.setContentWidth(container.getWidth() - Theme.LIST_ITEM_PADDING * 2);
        itemView.setPaddings(Theme.LIST_ITEM_PADDING,
                Theme.LIST_ITEM_PADDING,
                Theme.LIST_ITEM_PADDING,
                Theme.LIST_ITEM_PADDING);

        int height = bindView(position, itemView);
        itemView.setContentHeight(height);
        itemView.setBackgroundImage(Theme.getListItemBg(position, container.getWidth(), itemView.getHeight()));

        return itemView;
    }

    /**
     * Bind the View that displays the data at the specified position in the data
     * set.
     *
     * @param position The position of the item within the adapter's data set
     * of the item whose view we want.
     * @param itemView The item View that to bind concrete content.
     * @return the content height of itemView to set.
     */
    protected abstract int bindView(int position, Container itemView);

    /**
     * Update the View that displays the data at the specified position in the data
     * set.
     *
     * @param position The position of the item within the adapter's data set
     * of the item whose view we want.
     * @param itemView The item View that to bind concrete content.
     * @return the content height of itemView to set.
     */
    protected abstract int updateView(int position, Container itemView);
}
