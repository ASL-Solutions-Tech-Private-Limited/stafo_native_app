package com.asl_emp_mng.app.base.model

import androidx.fragment.app.Fragment
import java.io.Serializable

data class HomeBaseFragmentChangeModel(var fragment: Fragment?= null,
                                       var type: String = "") : Serializable

data class ToolbarChangesModel(var isScreenName: String = "",
                               var isShowBack: Boolean = false,
                               var appBarColorIsWhite:Boolean= true) : Serializable