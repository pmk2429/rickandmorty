package com.delivery.rickandmorty.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.delivery.rickandmorty.R;


public class HorizontalProgressBar extends View {

  //actual dot radius
  private int mDotRadius = 5;

  //Bounced Dot Radius
  private int mBounceDotRadius = 8;

  //to get identified in which position dot has to bounce
  private int mDotPosition;

  //specify how many dots you need in a progressbar
  private int mDotAmount = 10;

  public HorizontalProgressBar(Context context) {
    super(context);
  }

  public HorizontalProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  //Method to draw your customized dot on the canvas
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    Paint paint = new Paint();

    // set the color for the dot that you want to draw
    paint.setColor(getResources().getColor(R.color.colorPrimaryDark));

    // function to create dot
    createDot(canvas, paint);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    //Animation called when attaching to the window, i.e to your screen
    startAnimation();
  }

  private void createDot(Canvas canvas, Paint paint) {

    //here i have setted progress bar with 10 dots , so repeat and wnen i = mDotPosition  then increase the radius of dot i.e mBounceDotRadius
    for (int i = 0; i < mDotAmount; i++) {
      if (i == mDotPosition) {
        canvas.drawCircle(10 + (i * 20), mBounceDotRadius, mBounceDotRadius, paint);
      } else {
        canvas.drawCircle(10 + (i * 20), mBounceDotRadius, mDotRadius, paint);
      }
    }


  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width;
    int height;

    int calculatedWidth = (20 * 9);

    width = calculatedWidth;
    height = (mBounceDotRadius * 2);

    setMeasuredDimension(width, height);
  }

  private void startAnimation() {
    BounceAnimation bounceAnimation = new BounceAnimation();
    bounceAnimation.setDuration(100);
    bounceAnimation.setRepeatCount(Animation.INFINITE);
    bounceAnimation.setInterpolator(new LinearInterpolator());
    bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {

      }

      @Override
      public void onAnimationRepeat(Animation animation) {
        mDotPosition++;
        if (mDotPosition == mDotAmount) {
          mDotPosition = 0;
        }
      }
    });
    startAnimation(bounceAnimation);
  }


  private class BounceAnimation extends Animation {
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
      super.applyTransformation(interpolatedTime, t);
      invalidate();
    }
  }
}
