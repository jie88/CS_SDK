package com.cs.sis_sdk.ui.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * describle :
 */
public class ViewHolder {
    private final SparseArray<View> mViews;
    private static int mPosition;
    private View mConvertView;
    private ViewHolder(Context context, ViewGroup parent, int layoutId,
                       int position) {
        mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        mConvertView.setTag(this);
    }

    /**
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        mPosition=position;
        return (ViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        if (TextUtils.isEmpty(text)){
            view.setText("");
        } else {
            view.setText(text);
        }
        return this;
    }

    /**
     * 传颜色值，不能直接传颜色ID
     * @param viewId
     * @param textColor
     * @return
     */
    public ViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public ViewHolder setTextLeftDrawable(int viewId, Drawable textLeftDrawable) {
        TextView view = getView(viewId);
        if (textLeftDrawable != null) {
            textLeftDrawable.setBounds(0, 0, textLeftDrawable.getMinimumWidth(), textLeftDrawable.getMinimumHeight());
        }
        view.setCompoundDrawables(textLeftDrawable, null, null, null);
        return this;
    }

    public ViewHolder setTextLeftDrawable(int viewId, Drawable textLeftDrawable, int width, int height) {
        TextView view = getView(viewId);
        if (textLeftDrawable != null) {
            textLeftDrawable.setBounds(0, 0, width, height);
        }
        view.setCompoundDrawables(textLeftDrawable, null, null, null);
        return this;
    }


    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }


    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);

        return this;
    }

    public ViewHolder setVisibility(int viewId, int visible) {
        View view = getView(viewId);
        view.setVisibility(visible);
        return this;
    }

    public int getPosition() {
        return mPosition;
    }


}
