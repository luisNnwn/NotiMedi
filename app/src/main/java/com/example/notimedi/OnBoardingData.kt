package com.example.notimedi

import androidx.compose.ui.graphics.Color
import com.example.notimedi.ui.theme.DotActive
import com.example.notimedi.ui.theme.DotInactive

data class OnBoardingData(
    val image: Int,
    val backgroundColor: Color = DotActive,
    val mainColor: Color = DotInactive,
    val mainText:String,
    val subText:String
)
