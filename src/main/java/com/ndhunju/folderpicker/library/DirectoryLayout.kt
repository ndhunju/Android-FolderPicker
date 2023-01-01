package com.ndhunju.folderpicker.library

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ListView
import android.widget.RelativeLayout
import com.ndhunju.folderpicker.R

/**
 * This view has 3 child views,
 * 1. [ControlsLayout] - Shows controllers to navigate the directory
 * 2. [ListView] - Shows list of folders in current folder
 * 3. [Button] - to choose current folder
 * 4. [Button] - to cancel/dismiss the dialog
 */
class DirectoryLayout(context: Context?) : RelativeLayout(context) {

    var chooseButton: Button
    val cancelButton: Button
    var directoryListView: ListView
    var controlsLayout: ControlsLayout

    init {
        controlsLayout = ControlsLayout(getContext())
        controlsLayout.id = generateViewId()
        addView(controlsLayout, LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        ).apply {
            addRule(ALIGN_PARENT_TOP)
        })

        chooseButton = Button(getContext())
        chooseButton.id = generateViewId()
        chooseButton.background = null
        chooseButton.text = getString(R.string.str_choose)
        addView(chooseButton, LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        ).apply {
            addRule(ALIGN_PARENT_BOTTOM)
            addRule(ALIGN_PARENT_END)
        })

        cancelButton = Button(getContext())
        cancelButton.id = generateViewId()
        cancelButton.background = null
        cancelButton.text = getString(android.R.string.cancel)
        addView(cancelButton, LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        ).apply {
            addRule(ALIGN_PARENT_BOTTOM)
            addRule(START_OF, chooseButton.id)
        })

        // Create ListView to bind with adapter
        directoryListView = ListView(getContext())
        addView(directoryListView, LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        ).apply {
            addRule(BELOW, controlsLayout.getId())
            addRule(ABOVE, chooseButton.id)
        })

    }

    fun getString(id: Int): String {
        return context.getString(id)
    }

}