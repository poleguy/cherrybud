package com.poleguy.cherrybud

//package qst.com.app4cotlin

import android.graphics.Bitmap
import android.os.Parcelable
//import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import com.poleguy.cherrybud.niuedu.ListTree
import com.poleguy.cherrybud.niuedu.ListTreeAdapter
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import treebuilder.TreeNode

//import com.niuedu.ListTree
//import com.niuedu.ListTreeAdapter

// holds node data for display and manipulation
// TreeNode is a reference to underlying XML to allow eventual modification
@Parcelize
class NodeData(val name: String, val level: Int, val treeNode: @RawValue TreeNode) : Parcelable {
    private var data: TreeNode = treeNode

    fun getContent() : String {

        var content = ""
        if (data.child != null) {
            var curChild : TreeNode = data.child
            while (true) {
                // loop through all children and render text (etc.)
                // todo: make recursive
                // in order tree traversal without recursion
                var stack: Stack<TreeNode> = Stack<TreeNode>()
                if ("rich_text" in curChild.toString()) {
                    content +=  curChild?.child?.toString()
                } else if ("TEXT" in curChild.type.toString()) {
                    // don't print extra newlines
                } else {
                    // unhandled: encoded_png, etc.
                    content += curChild.toString()
                }
                if (curChild.sibling != null) {
                    curChild = curChild.sibling
                } else {
                    break
                }
            }
        //}
        //val content = if (data.secondChild != null && "rich_text" in data.secondChild.toString()) {
        //    data.secondChild.child.toString()
        } else {
            // no content
            ""
        }
        return content
    }
    //# https://www.sitepoint.com/transfer-data-between-activities-with-android-parcelable/
}

@Parcelize
class NodeDataStr(val str: String) : Parcelable {
    private var data: String = str

    fun getContent() : String {
        return data
    }
    //# https://www.sitepoint.com/transfer-data-between-activities-with-android-parcelable/
}


class ExampleListTreeAdapter(tree: ListTree, listener : PopupMenu.OnMenuItemClickListener, click_listener: OpenFileClicked) :
        ListTreeAdapter<ExampleListTreeAdapter.BaseViewHolder>(tree){

    //行上弹出菜单的侦听器
    private val itemMenuClickListener : PopupMenu.OnMenuItemClickListener
    private val clickListener : OpenFileClicked

    //记录弹出菜单是在哪个行上出现的

    //Record on which line the pop-up menu appears
    var currentNode: ListTree.TreeNode? = null

    //保存子行信息的类
    class ContactInfo(
            val bitmap: Bitmap, //头像,用于设置给ImageView
            var title: String, //标题
            var detail: String //描述
    )

    init{
        itemMenuClickListener = listener
        clickListener = click_listener
    }

    override fun onCreateNodeView(parent: ViewGroup?, viewType: Int): BaseViewHolder? {
        val inflater = LayoutInflater.from(parent!!.context)

        //创建不同的行View
        if (viewType == R.layout.contacts_group_item) {
            //注意！此处有一个不同！最后一个参数必须传true！
            val view = inflater.inflate(viewType, parent, true)
            //用不同的ViewHolder包装
            return GroupViewHolder(view)
        } else if (viewType == R.layout.contacts_contact_item) {
            //注意！此处有一个不同！最后一个参数必须传true！
            val view = inflater.inflate(viewType, parent, true)
            //用不同的ViewHolder包装
            return ContactViewHolder(view)
        } else {
            return null
        }
    }

    override fun onBindNodeViewHolder(viewHoler: BaseViewHolder?, position: Int) {
        //get node at the position
        val node = tree.getNodeByPlaneIndex(position)

        if (node.layoutResId == R.layout.contacts_group_item) {
            //group node
            val data = node.data as NodeData
            //val content = data.getContent()
            //val content = data.name.take(4)
            //val title = "${data.name} ${data.level} $content"
            val title = "${data.name}"

            val gvh = viewHoler as GroupViewHolder
            gvh.textViewTitle.text = title
            gvh.textViewCount.text = "0/" + node.childrenCount
            gvh.aSwitch.isChecked = node.isChecked
        } else if (node.layoutResId == R.layout.contacts_contact_item) {
            //child node
            val info = node.data as ContactInfo

            val cvh = viewHoler as ContactViewHolder
            cvh.imageViewHead.setImageBitmap(info.bitmap)
            cvh.textViewTitle.text = info.title
            cvh.textViewDetail.text = info.detail
            cvh.aSwitch.isChecked = node.isChecked
        }
    }

    //组行和联系人行的Holder基类
    open inner class BaseViewHolder(itemView: View) : ListTreeViewHolder(itemView)

    //将ViewHolder声明为Adapter的内部类，反正外面也用不到
    internal inner class GroupViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var textViewCount: TextView = itemView.findViewById(R.id.textViewCount)
        var aSwitch: Switch = itemView.findViewById(R.id.switchChecked)
        var textViewMenu: TextView = itemView.findViewById(R.id.textViewMenu)

        init {

            //应响应点击事件而不是CheckedChange事件，因为那样会引起事件的递归触发
            //Should respond to the click event instead of the CheckedChange event, because that will cause the event to be triggered recursively
            aSwitch.setOnClickListener {
                val planeIndex = adapterPosition
                val node = tree.getNodeByPlaneIndex(planeIndex)
                node.isChecked = !node.isChecked
                //改变所有的子孙们的状态
                val count = tree.setDescendantChecked(planeIndex, node.isChecked)
                notifyItemRangeChanged(planeIndex, count + 1)

                // set node selected and unselect previously selected node
                node.isSelected = true
                //node.curSelected = node

            }

            //点了PopMenu控件，弹出PopMenu
            //Click the PopMenu control, PopMenu pops up
            textViewMenu.setOnClickListener { v ->
                val nodePlaneIndex = adapterPosition
                val node = tree.getNodeByPlaneIndex(nodePlaneIndex)
                currentNode = node
                //val popup = PopupMenu(v.context, v)
                //popup.setOnMenuItemClickListener(itemMenuClickListener)
                //val inflater = popup.menuInflater
                //inflater.inflate(R.menu.menu_item, popup.menu)
                //popup.show()

                // immediately open node:
                clickListener.openNode()

            }
        }
    }

    internal inner class ContactViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var imageViewHead: ImageView = itemView.findViewById(R.id.imageViewHead)
        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var textViewDetail: TextView = itemView.findViewById(R.id.textViewDetail)
        var aSwitch: Switch = itemView.findViewById(R.id.switchChecked)

        init {

            //应响应点击事件而不是CheckedChange事件，因为那样会引起事件的递归触发
            //Should respond to the click event instead of the CheckedChange event, because that will cause the event to be triggered recursively
            aSwitch.setOnClickListener {
                val nodePlaneIndex = adapterPosition
                val node = tree.getNodeByPlaneIndex(nodePlaneIndex)
                node.isChecked = !node.isChecked
                //改变所有的子孙们的状态
                //Change the state of all children and grandchildren
                val count = tree.setDescendantChecked(nodePlaneIndex, node.isChecked)
                notifyItemRangeChanged(nodePlaneIndex, count + 1)
            }
        }
    }

}
