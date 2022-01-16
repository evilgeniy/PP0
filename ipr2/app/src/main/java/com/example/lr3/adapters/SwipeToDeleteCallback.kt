package com.example.lr3.adapters

import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.lr3.R
import android.graphics.Canvas


class SwipeToDeleteCallback(
    val notesAdapter: NotesAdapter
): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    private val icon: Drawable? = ContextCompat.getDrawable(notesAdapter.context,
        R.drawable.ic_delete_sweep_white_36dp)
    private val background: ColorDrawable = ColorDrawable(Color.RED)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        notesAdapter.deleteItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(
            c, recyclerView, viewHolder, dX,
            dY, actionState, isCurrentlyActive
        )
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20
        val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
        val iconTop = itemView.top + (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2
        val iconBottom = iconTop + (icon?.intrinsicHeight ?: 0)

        if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - (icon?.intrinsicWidth ?: 0)
            val iconRight = itemView.right - iconMargin
            icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset,
                itemView.top, itemView.right, itemView.bottom
            )
        } else { // view is unSwiped or swiped to Left
            background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon?.draw(c)
    }
}