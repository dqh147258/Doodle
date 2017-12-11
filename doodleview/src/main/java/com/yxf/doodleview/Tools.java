package com.yxf.doodleview;

import android.animation.Animator;
import android.animation.AnimatorSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quehuang.du on 2017/12/7.
 */

class Tools {
    public static void playAnimationSequentially(Animator... animators) {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        set.start();
    }
}
