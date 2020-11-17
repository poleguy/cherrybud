package com.poleguy.cherrybud

//class CollectionDemoFragment : Fragment() {
//    // When requested, this adapter returns a DemoObjectFragment,
//    // representing an object in the collection.
//    private lateinit var demoCollectionAdapter: DemoCollectionAdapter
//    private lateinit var viewPager: ViewPager2
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.collection_demo, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        demoCollectionAdapter = DemoCollectionAdapter(this)
//        viewPager = view.findViewById(R.id.pager)
//        viewPager.adapter = demoCollectionAdapter
//    }
//}
//
//class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
//
//    override fun getItemCount(): Int = 100
//
//    override fun createFragment(position: Int): Fragment {
//        // Return a NEW fragment instance in createFragment(int)
//        val fragment = DemoObjectFragment()
//        fragment.arguments = Bundle().apply {
//            // Our object is just an integer :-P
//            putInt(ARG_OBJECT, position + 1)
//        }
//        return fragment
//    }
//}
//
//private const val ARG_OBJECT = "object"
//
//// Instances of this class are fragments representing a single
//// object in our collection.
//class DemoObjectFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_collection_object, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
//            val textView: TextView = view.findViewById(android.R.id.text1)
//            textView.text = getInt(ARG_OBJECT).toString()
//        }
//    }
//}