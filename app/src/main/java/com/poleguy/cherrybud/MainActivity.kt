package com.poleguy.cherrybud


import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.poleguy.cherrybud.niuedu.ListTree
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.xmlpull.v1.XmlPullParserException
import treebuilder.*
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.util.*

// https://stackoverflow.com/questions/31297246/activity-appcompatactivity-fragmentactivity-and-actionbaractivity-when-to-us
class MainActivity : AppCompatActivity(),  HandlePathOzListener.SingleUri, PopupMenu.OnMenuItemClickListener {

    private lateinit var handlePathOz: HandlePathOz
    var path : String = "none"

    //保存数据的集合
    private val tree = ListTree()

    //从ListTreeAdapter派生的Adapter
    internal var adapter: ExampleListTreeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        // https://github.com/android/views-widgets-samples/blob/master/ViewPager2/app/src/main/java/androidx/viewpager2/integration/testapp/BaseCardActivity.kt
        //var viewPager: ViewPager2 = findViewById(R.id.pager)

        handlePathOz = HandlePathOz(this, this)
        // Example of a call to a native method
        //sample_text.text = stringFromJNI()

        // get reference to button
        val button = findViewById<Button>(R.id.button)

        // https://stackoverflow.com/questions/49697630/open-file-choose-in-android-app-using-kotlin
        button.setOnClickListener {

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)



            //sample_text.text = "blahblah"

        }

        //sample_text.text = "blah"

        //val list = listOf("a","b","c")
        //populateTreeData(list)

    }

    fun populateTree() {
        // ExampleListTreeAdapter

        //创建后台数据：一棵树
        //创建组们，是root node，所有parent为null
        val groupNode1 = tree.addNode(null, "特别关心", R.layout.contacts_group_item)
        val groupNode2 = tree.addNode(null, "two 我的好友", R.layout.contacts_group_item)
        val groupNode3 = tree.addNode(null, "朋友", R.layout.contacts_group_item)
        val groupNode4 = tree.addNode(null, "家人", R.layout.contacts_group_item)
        val groupNode5 = tree.addNode(null, "five 同学", R.layout.contacts_group_item)

        //第二层
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        var contact = ExampleListTreeAdapter.ContactInfo(bitmap, "mno 王二", "[在线]我是王二")
        val contactNode1 = tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "jkl 王三", "[在线]我是王三")
        val contactNode2 = tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "ghi 王四", "[离线]我没有状态")
        tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "def 王五", "[离线]我没有状态")
        tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)

        //第三层
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "abc 东邪", "[离线]出来还价")
        var n: ListTree.TreeNode = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "李圆圆", "[离线]昨天出门没出去")
        n = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false

        adapter = ExampleListTreeAdapter(tree, this)
        listView.layoutManager = LinearLayoutManager(this)
        listView.setAdapter(adapter)

    }

    private fun displayTree() {
        // displays tree in list view

        adapter = ExampleListTreeAdapter(tree, this)
        listView.layoutManager = LinearLayoutManager(this)
        listView.setAdapter(adapter)

    }

    //On Completion (Sucess or Error)
    //If there is a cancellation or error.
    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        //Hide Progress

        //Now you can work with real path:
        Toast.makeText(this, "The real path is: ${pathOz.path} \n The type is: ${pathOz.type}", Toast.LENGTH_SHORT).show()
        path = pathOz.path
        xmlParse(path)
        //Handle any Exception (Optional)
        tr?.let {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // https://www.raywenderlich.com/2705552-introduction-to-android-activities-with-kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // https://stackoverflow.com/questions/55182578/how-to-read-plain-text-file-in-kotlin
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            //sample_text.text = selectedFile.toString()
            if (selectedFile != null)    {
                val infile: InputStream? = contentResolver.openInputStream(selectedFile)
                if (infile != null) {
                    //sample_text.text = infile.bufferedReader().use { it.readLine()
                    //    it.readLine()
                    //    it.readLine()
                    //}
                    //xmlParse(infile)
                    data?.data?.also { it ->
                        handlePathOz.getRealPath(it)
                    }
                    //xmlParse(selectedFile)
                }
            }

            //sample_text.text = File(path).readText()
        } else {
            //sample_text.text = "none"
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
           // System.loadLibrary("native-lib")
        }
    }

    // https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj =
                arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentUri?.let { context.getContentResolver().query(it, proj, null, null, null) }
            val column_index: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: 0
            cursor?.moveToFirst()
            cursor?.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    fun xmlParse(path: String) {
        // https://developer.android.com/reference/org/xmlpull/v1/XmlPullParser
            try {
                println("test")
                //val fileName = getRealPathFromURI(null, selectedFile)
                val fileName = path
                println(path)
                val reader = FileReader(fileName)
                val builder = TreeBuilder()
                val xmlTree = builder.parseXML(reader)
                println(xmlTree)

                var curr: TreeNode // node we're parsing currently
                curr = xmlTree.child
                //list.add(curr.toString())

                // in order tree traversal without recursion
                var stack: Stack<TreeNode> = Stack<TreeNode>()
                var parentStack : Stack<ListTree.TreeNode> = Stack<ListTree.TreeNode>()

                // https://www.geeksforgeeks.org/inorder-tree-traversal-without-recursion/
                var level = 0
                var done = false
                while (!done) {
                    // traverse the tree
                    while (!done) {
                        if (("node" in curr.toString())) {
                            var name: String = "none"
                            var tn: TagNode = curr as TagNode
                            var al: AttributeList = tn.attrList as AttributeList
                            if (al != null) {
                                var it: Iterator<Attribute> = al.iterator as Iterator<Attribute>
                                for (attr in it) {
                                    if (attr.name == "name") {
                                        name = attr.value
                                        break
                                    }
                                }
                            }
                            // add it to the view
                            var node : ListTree.TreeNode? = null
                            val data = NodeData(name, level, curr)
                            node = if (parentStack.isEmpty()) {
                                tree.addNode(null, data, R.layout.contacts_group_item)
                            } else {
                                tree.addNode(parentStack.lastElement(), data, R.layout.contacts_group_item)
                            }


                            println(name)
                            if(curr.child != null) {
                                // place pointer to a tree node on the stack before traversing the node's subtree
                                stack.push(curr)
                                parentStack.push(node)
                                // if there are children go ahead and do them next
                                curr = curr.child
                                level += 1
                                println(level)
                                continue
                            }
                        }
                        if (curr.sibling == null ) {
                            if (stack.isNotEmpty()) {
                                // reached end of siblings
                                // go back up a level
                                level -= 1
                                println(level)
                                curr = stack.pop()
                                parentStack.pop()
                                if (curr.sibling != null) {
                                    curr = curr.sibling
                                } else {
                                    break
                                }
                            } else {
                                done = true
                                break // stack is empty, so we're done
                            }
                        } else {
                            curr = curr.sibling // update current node
                        }
                    }
                }

                displayTree()

                // put the contents of this tree into the tree viewer
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            }
        }

override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
        R.id.action_add_item -> {
            //向当前行增加一个儿子
            val node = adapter!!.currentNode
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
            val contact = ExampleListTreeAdapter.ContactInfo(
                bitmap, "New contact", "[离线]我没有状态")
            val childNode = tree.addNode(node, contact, R.layout.contacts_contact_item)
            adapter!!.notifyTreeItemInserted(node, childNode)
            return true
        }
        R.id.action_clear_children -> {
            //清空所有的儿子们
            val node = adapter!!.currentNode
            val range = tree.clearDescendant(node)
            adapter!!.notifyItemRangeRemoved(range!!.first, range.second)
            return true
        }
        else -> return false
    }
}

    //http://bearcave.com/software/java/xml/treebuilder.html
}
