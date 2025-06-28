package com.namuplanet.base.view

import android.animation.ObjectAnimator
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView

open class BaseAdapter(val duration: Long = DURATION) :
    RecyclerView.Adapter<BaseViewHolder<DisplayableItem, ItemListener>>() {

    val holderProviders = HashMap<Int, ViewHolderProvider>()
    val displayableItems = ArrayList<DisplayableItem>()
    var lastItemPosition = -1
        protected set
    var isAnimateItemEnabled = false

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        holderProviders[viewType]?.create(parent) as? BaseViewHolder<DisplayableItem, ItemListener>
            ?: parent.resources.getResourceEntryName(viewType).let {
                throw IllegalStateException("Not found $it type holder provider.")
            }

    override fun getItemCount() = displayableItems.size

    override fun getItemViewType(position: Int) = displayableItems[position].type()

    override fun onBindViewHolder(
        holder: BaseViewHolder<DisplayableItem, ItemListener>,
        position: Int
    ) {
        displayableItems[position].let {
            holder.bind(it, holderProviders[it.type()]?.itemListener)
        }
        if (isAnimateItemEnabled) {
            animateItemView(position > lastItemPosition, holder)
        }
        lastItemPosition = position
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<DisplayableItem, ItemListener>) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
    }

    open fun addHolder(holderProvider: ViewHolderProvider) {
        holderProviders[holderProvider.layoutId()] = holderProvider
    }

    open fun setData(data: List<DisplayableItem>) {
        displayableItems.clear()
        displayableItems.addAll(data)
        notifyDataSetChanged()
    }

    open fun setData(data: DisplayableItem, position: Int) {
        displayableItems[position] = data
        notifyItemChanged(position)
    }

    open fun addData(data: List<DisplayableItem>, position: Int = displayableItems.size) {
        displayableItems.addAll(position, data)
        notifyItemRangeInserted(position, data.size)
    }

    open fun clearData() {
        displayableItems.clear()
        notifyDataSetChanged()
    }

    open fun notifyItemChanged(positions: List<Int>) {
        positions.forEach(::notifyItemChanged)
    }

    open fun removeData(positions: List<Int>) {
        positions.forEach {
            displayableItems.removeAt(it)
            notifyItemRemoved(it)
        }
    }

    private fun animateItemView(
        scrollUp: Boolean,
        holder: BaseViewHolder<DisplayableItem, ItemListener>
    ) {
        if (scrollUp) {
            ObjectAnimator.ofFloat(holder.itemView, "translationY", DISTANCE, 0f).apply {
                duration = DURATION
                interpolator = OvershootInterpolator()
                start()
            }
        } else {
            ObjectAnimator.ofFloat(holder.itemView, "translationY", -DISTANCE, 0f).apply {
                duration = DURATION
                interpolator = OvershootInterpolator()
                start()
            }
        }
    }

    companion object {
        private const val DURATION = 1200L
        private const val DISTANCE = 120f
    }
}

open class BasePayloadsAdapter : BaseAdapter() {

    override fun onBindViewHolder(
        holder: BaseViewHolder<DisplayableItem, ItemListener>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (holder is BasePayloadsViewHolder) {
            displayableItems[position].let {
                holder.bind(it, holderProviders[it.type()]?.itemListener, payloads)
            }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }
}

open class BaseDynamicDataAdapter : BaseAdapter() {
    var dynamicData: BaseDynamicData? = null

    override fun onBindViewHolder(
        holder: BaseViewHolder<DisplayableItem, ItemListener>,
        position: Int
    ) {
        if (holder is BaseDynamicViewHolder) {
            displayableItems[position].let {
                holder.bind(it, holderProviders[it.type()]?.itemListener, dynamicData)
            }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }
}

open class BaseSwipeLoopAdapter : BaseAdapter() {
    override fun onBindViewHolder(
        holder: BaseViewHolder<DisplayableItem, ItemListener>,
        position: Int
    ) {
        displayableItems[position % displayableItems.size].let {
            holder.bind(it, holderProviders[it.type()]?.itemListener)
        }
    }

    override fun getItemCount() =
        if (displayableItems.size > 1) Integer.MAX_VALUE else displayableItems.size

    override fun getItemViewType(position: Int) =
        displayableItems[position % displayableItems.size].type()
}

interface BaseDynamicData