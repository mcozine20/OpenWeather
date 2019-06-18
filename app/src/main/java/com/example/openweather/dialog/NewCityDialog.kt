package com.example.openweather.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.example.openweather.R
import com.example.openweather.activities.ScrollingActivity
import com.example.openweather.data.City
import kotlinx.android.synthetic.main.new_city_dialog.view.*
import java.lang.RuntimeException

class NewCityDialog : DialogFragment() {

    interface CityHandler {
        fun cityCreated(item: City)
        fun cityUpdated(item: City)
        fun deleteAllCities()
    }

    private lateinit var cityHandler: CityHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is CityHandler) {
            cityHandler = context
        } else {
            throw RuntimeException(
                getString(R.string.cityhandler_interface_error))
        }
    }

    private lateinit var etCityName: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(getString(R.string.new_city))

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_city_dialog, null
        )
        etCityName = rootView.etCity
        builder.setView(rootView)

        val arguments = this.arguments

        if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
            val cityItem = arguments.getSerializable(ScrollingActivity.KEY_ITEM_TO_EDIT) as City

            etCityName.setText(cityItem.cityName)

            builder.setTitle(getString(R.string.edit_city))
        }

        builder.setPositiveButton(getString(R.string.ok)) {
                dialog, witch -> // empty
        }

        return builder.create()
    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etCityName.text.isNotEmpty()) {
                val arguments = this.arguments
                if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_ITEM_TO_EDIT)) {
                    handleCityEdit()
                } else {
                    handleCityCreate()
                }

                dialog.dismiss()
            } else {
                etCityName.error = getString(R.string.empty_field_error)
            }
        }
    }

    private fun handleCityCreate() {
        cityHandler.cityCreated(
            City(null, etCityName.text.toString())
        )
    }

    private fun handleCityEdit() {
        val cityToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_ITEM_TO_EDIT
        ) as City
        cityToEdit.cityName = etCityName.text.toString()

        cityHandler.cityUpdated(cityToEdit)
    }

}
