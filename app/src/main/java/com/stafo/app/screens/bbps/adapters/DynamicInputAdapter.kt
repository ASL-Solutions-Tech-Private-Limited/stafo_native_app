package com.stafo.app.screens.bbps.adapters

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.stafo.app.databinding.ItemDynamicInputBinding
import com.stafo.app.screens.bbps.dataClasses.BillerDetailsResponse
import com.stafo.app.utils.getInputTypeUtil
import org.json.JSONObject

class DynamicInputAdapter(
    private val context: Context,
    private val paramList: List<BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerInputParams.ParamInfo>,
    private val recyclerView: RecyclerView? = null
) : RecyclerView.Adapter<DynamicInputAdapter.InputViewHolder>() {

    private val inputMap = mutableMapOf<String, String>()

    inner class InputViewHolder(val binding: ItemDynamicInputBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var textWatcher: TextWatcher? = null

        fun bind(
            param: BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerInputParams.ParamInfo,
            position: Int
        ) {
            // Remove old listener
            binding.etInput.removeTextChangedListener(textWatcher)
            textWatcher = null
            binding.etInput.setOnClickListener(null)
            binding.tilInput.error = null

            binding.tilInput.hint = param.paramName
            val editText = binding.etInput

            // Input type
            editText.inputType = getInputTypeUtil(param.dataType)

            // maxLength filter only if > 0
            param.maxLength?.toIntOrNull()?.takeIf { it > 0 }?.let {
                editText.filters = arrayOf(InputFilter.LengthFilter(it))
            } ?: run {
                editText.filters = emptyArray() // No filter
            }

            // Get existing value
            val existingValue = inputMap[param.paramName ?: ""] ?: ""

            if (!param.values.isNullOrEmpty()) {
                setupDropdownField(editText, param, existingValue)
            } else {
                setupRegularField(editText, param, existingValue)
            }
        }

        private fun setupDropdownField(
            editText: TextInputEditText,
            param: BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerInputParams.ParamInfo,
            existingValue: String
        ) {
            val values = param.values?.split(",")?.map { it.trim() } ?: emptyList()

            editText.apply {
                isFocusable = false
                isClickable = true
                isCursorVisible = false
                isLongClickable = false

                setText(existingValue)

                setOnClickListener {
                    showSelectionDialog(param, values) { selectedValue ->
                        setText(selectedValue)
                        inputMap[param.paramName ?: ""] = selectedValue
                        binding.tilInput.error = null
                    }
                }
            }
        }

        private fun setupRegularField(
            editText: TextInputEditText,
            param: BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerInputParams.ParamInfo,
            existingValue: String
        ) {
            editText.apply {
                isFocusable = true
                isFocusableInTouchMode = true
                isClickable = true
                isCursorVisible = true
                isLongClickable = true

                setText(existingValue)
                setSelection(text?.length ?: 0)
            }

            textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val newValue = s.toString()
                    inputMap[param.paramName ?: ""] = newValue
                    binding.tilInput.error = null
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }

            editText.addTextChangedListener(textWatcher)
        }

        private fun showSelectionDialog(
            param: BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerInputParams.ParamInfo,
            values: List<String>,
            onSelected: (String) -> Unit
        ) {
            AlertDialog.Builder(context)
                .setTitle("Select ${param.paramName}")
                .setItems(values.toTypedArray()) { dialog, which ->
                    val selectedValue = values[which]
                    onSelected(selectedValue)
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputViewHolder {
        val binding = ItemDynamicInputBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InputViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
        holder.bind(paramList[position], position)
    }

    override fun getItemCount(): Int = paramList.size

    fun getInputData(): JSONObject {
        val json = JSONObject()
        inputMap.forEach { (key, value) ->
            json.put(key, value)
        }
        return json
    }

    fun validateInputs(): Boolean {
        var allValid = true

        paramList.forEachIndexed { index, param ->
            val value = inputMap[param.paramName ?: ""] ?: ""
            val isRequired = param.isOptional == "false"
            val textInputLayout = getTextInputLayoutForIndex(index)

            when {
                isRequired && value.isBlank() -> {
                    textInputLayout?.error = "${param.paramName} is required"
                    allValid = false
                }

                value.isNotEmpty() && !param.regEx.isNullOrBlank() -> {
                    try {
                        if (!Regex(param.regEx).matches(value)) {
                            textInputLayout?.error = "Invalid ${param.paramName} format"
                            allValid = false
                        }
                    } catch (e: Exception) {
                        // Ignore invalid regex
                    }
                }

                value.isNotEmpty() && param.minLength?.toIntOrNull()
                    ?.let { value.length < it } == true -> {
                    textInputLayout?.error =
                        "${param.paramName} must be at least ${param.minLength} characters"
                    allValid = false
                }

                else -> {
                    textInputLayout?.error = null
                }
            }
        }

        return allValid
    }

    private fun getTextInputLayoutForIndex(index: Int): TextInputLayout? {
        return recyclerView?.findViewHolderForAdapterPosition(index)
            ?.let { it as? InputViewHolder }
            ?.binding?.tilInput
    }

    fun clearAllInputs() {
        inputMap.clear()
        notifyDataSetChanged()
    }

    fun setInputValue(paramName: String, value: String) {
        inputMap[paramName] = value
        val position = paramList.indexOfFirst { it.paramName == paramName }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    private fun getInputTypeUtil(dataType: String?): Int {
        return when (dataType?.uppercase()) {
            "NUMERIC" -> InputType.TYPE_CLASS_NUMBER
            "ALPHANUMERIC" -> InputType.TYPE_CLASS_TEXT
            "ALPHA" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            else -> InputType.TYPE_CLASS_TEXT
        }
    }
}




