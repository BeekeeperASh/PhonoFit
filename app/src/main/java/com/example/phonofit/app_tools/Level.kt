package com.example.phonofit.app_tools

import com.example.phonofit.R

/**
 * A date class describing the entity level
 *
 * This class contains the information necessary to implement the levels in the application
 *
 * @param title is a string for the name of the level
 * @param imageResource provides the URL of the image for the level
 * @param description is a string for the description of the level
 * @param route specifies the next screen to go to
 */
data class Level(
    val title: String = "Title",
    val imageResource: String = "https://static.lenovo.com/ww/campaigns/2021/services/smart-lock/ww-smart-lock-device-lock-icon.png",
    val description: String = "Description",
    val route: String = "sub_screen",
)
