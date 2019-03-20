package net.sarasarasa.lifeup.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomTextInputLayout extends TextInputLayout {
    private Object collapsingTextHelper;
    private Rect bounds;
    private Method recalculateMethod;

    public CustomTextInputLayout(Context context) {
        this(context, null);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        adjustBounds();
    }

    private void init() {
        try {
            Field cthField = TextInputLayout.class.getDeclaredField("collapsingTextHelper");
            cthField.setAccessible(true);
            collapsingTextHelper = cthField.get(this);

            Field boundsField = collapsingTextHelper.getClass().getDeclaredField("collapsedBounds");
            boundsField.setAccessible(true);
            bounds = (Rect) boundsField.get(collapsingTextHelper);

            recalculateMethod = collapsingTextHelper.getClass().getDeclaredMethod("recalculate");
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            collapsingTextHelper = null;
            bounds = null;
            recalculateMethod = null;
            e.printStackTrace();
        }
    }

    private void adjustBounds() {
        if (collapsingTextHelper == null) {
            return;
        }

        try {
            bounds.left = getEditText().getLeft() + getEditText().getPaddingLeft();
            recalculateMethod.invoke(collapsingTextHelper);
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}