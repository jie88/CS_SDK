package cn.everfine.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 *
 * describle : 快速  适配器
 */
public abstract class SISQuickAdapter<T> extends BaseAdapter {
  protected LayoutInflater mInflater;
  protected Context mContext;
  protected List<T> mDatas;
  protected final int mItemLayoutId;
  private boolean itemHide = false;


  public SISQuickAdapter(Context context, List<T> mDatas, int itemLayoutId) {
    this.mContext = context;
    this.mInflater = LayoutInflater.from(mContext);
    this.mDatas = mDatas;
    this.mItemLayoutId = itemLayoutId;
  }

  @Override
  public int getCount() {
    if (null == mDatas) {
      return 0;
    }
    return mDatas.size();
  }

  @Override
  public T getItem(int position) {
    if (null == mDatas) {
      return null;
    }
    if (mDatas.size() <= position) {
      return null;
    }
    return mDatas.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  /**
   * @param position
   */
  public void removeItem(int position) {
    mDatas.remove(position);
    notifyDataSetChanged();
  }

  public void removeItem(T t) {
    mDatas.remove(t);
    notifyDataSetChanged();
  }

  public void clearDatas() {
    mDatas.clear();
    notifyDataSetChanged();
  }

  public void addItem(T t) {
    mDatas.add(t);
    notifyDataSetChanged();
  }

  public void addItem(int index, T t) {
    mDatas.add(index, t);
    notifyDataSetChanged();
  }

  public void addAll(List<T> list, boolean flag) {
    if (flag) {
      mDatas.clear();
    }
    mDatas.addAll(list);
    notifyDataSetChanged();
  }

  public void addAll(int index, List<T> list) {
    mDatas.addAll(index, list);
    notifyDataSetChanged();
  }

  public boolean isItemHide() {
    return itemHide;
  }

  public void setItemHide(boolean itemHide) {
    this.itemHide = itemHide;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
//        if(itemHide){
//            convertView.setVisibility(View.INVISIBLE);
//        }
    final ViewHolder viewHolder = getViewHolder(position, convertView,
        parent);
    convert(viewHolder, getItem(position), position);
    return viewHolder.getConvertView();

  }

  public abstract void convert(ViewHolder helper, T item, int position);


  private ViewHolder getViewHolder(int position, View convertView,
                                   ViewGroup parent) {
    return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
        position);
  }


}
