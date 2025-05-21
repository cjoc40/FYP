package com.example.fyp2.pages

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import androidx.annotation.DrawableRes
import com.example.fyp2.R

enum class Player(@DrawableRes val imageResource: Int) {
    X(R.drawable.x3),
    O(R.drawable.o2),
    Red(R.drawable.redchecker),
    Black(R.drawable.blackchecker)
}

enum class Difficulty {
    EASY, MEDIUM, HARD
}
