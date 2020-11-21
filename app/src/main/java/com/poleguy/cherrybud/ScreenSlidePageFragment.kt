package com.poleguy.cherrybud

import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_screen_slide_page.*

class ScreenSlidePageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false)

        val text: TextView? = view?.findViewById(R.id.slider_content)

        text?.setOnClickListener {v:View -> buttonClicked(v)}
        return view
    }

//# https://stackoverflow.com/questions/16539029/change-textview-inside-fragment

    // https://www.techotopia.com/index.php/Using_Fragments_in_Android_Studio_-_A_Kotlin_Example

    private fun buttonClicked(view: View) {
       setText()
    }

    fun setText() {
        //https://stackoverflow.com/questions/26939759/android-getintent-from-a-fragment
        val data: String? = activity!!.intent.getStringExtra("EXTRA_DATA")
        //slider_content.text = "blah"
        slider_content.text = data
    }
}