package com.mateuszgabrynowicz.simplenumberlist.numbers_list

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mateuszgabrynowicz.simplenumberlist.R
import com.mateuszgabrynowicz.simplenumberlist.common.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_numbers_list.*
import javax.inject.Inject

class NumbersListActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var numberValidator: NumberValidator
    lateinit var viewModel: NumbersListViewModel
    private var numbersAdapter: NumbersListAdapter? = null

    private var inputDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers_list)

        initializeRecyclerView()
        initializeViewModel()
        handleDialogsOnCreate()
        scrollToPosition()

        add_number_fab.setOnClickListener {
            showAddNumberDialog()
        }
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NumbersListViewModel::class.java)
        viewModel.numberListLiveData.observe(this, Observer { viewState ->
            when (viewState) {
                is ViewState.Populated -> numbersAdapter?.addNumbers(viewState.data)
                is ViewState.Empty,
                is ViewState.Error -> showErrorDialog(getErrorMessage(viewState))
            }
            showLoading(viewState is ViewState.Loading)
        })
        viewModel.initialLoad()
    }

    private fun getErrorMessage(viewState: ViewState<List<Int>>?): String {
        return if (viewState is ViewState.Error) resources.getString(R.string.error_message)
        else resources.getString(R.string.no_content_message)
    }

    private fun handleDialogsOnCreate() {
        handleInputDialogOnCreate()
        handleErrorDialogOnCreate()
    }

    private fun handleErrorDialogOnCreate() {
        viewModel.errorDialogState?.let {
            showErrorDialog(it.dialogData)
        }
        viewModel.errorDialogState = null
    }

    private fun handleInputDialogOnCreate() {
        viewModel.inputDialogState?.let {
            showAddNumberDialog(it.dialogData, it.inputError)
        }
        viewModel.inputDialogState = null
    }

    private fun showLoading(isLoading: Boolean) {
        progress_bar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorDialog(errorMessage: String) {
        errorDialog = AlertDialog.Builder(this)
            .setTitle(R.string.error_title)
            .setMessage(errorMessage)
            .setPositiveButton(R.string.try_again_button_label) { dialog, _ ->
                viewModel.loadMoreNumbers()
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun initializeRecyclerView() {
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        numbersAdapter = NumbersListAdapter()
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = numbersAdapter
        recycler_view.addOnScrollListener(object : LoadMoreScrollListener(layoutManager) {
            override fun loadMoreItems() {
                viewModel.loadMoreNumbers()
            }
        })
    }

    private fun showAddNumberDialog(inputData: String? = null, errorData: String? = null) {
        val view = View.inflate(this, R.layout.add_number_dialog, null)
        val numberEditText = view.findViewById<TextInputEditText>(R.id.number_edit_text)
        inputData?.let { numberEditText.setText(it) }
        val numberTextInputLayout = view.findViewById<TextInputLayout>(R.id.number_text_input_layout)
        errorData?.let {
            numberTextInputLayout.isErrorEnabled = true
            numberTextInputLayout.error = it
        }
        inputDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .show()
            .apply {
                /*setting OnClick after dialog was created in order to not close the dialog instantly after user pressed
         the button*/
                getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                    validateNumberInput(
                        numberEditText.text.toString().toIntOrNull(), numberTextInputLayout, this
                    )
                }
            }
    }

    private fun validateNumberInput(
        number: Int?, numberTextInputLayout: TextInputLayout, dialog: AlertDialog
    ) {
        when (numberValidator.validateNumber(number, numbersAdapter as IAlreadyAddedChecker)) {
            ValidationResult.CORRECT_VALUE -> numberTextInputLayout.isErrorEnabled = false
            ValidationResult.WRONG_VALUE -> numberTextInputLayout.error =
                resources.getString(R.string.wrong_number_error)
            ValidationResult.EMPTY_VALUE -> numberTextInputLayout.error =
                resources.getString(R.string.empty_value_error)
            ValidationResult.ALREADY_ADDED -> numberTextInputLayout.error =
                resources.getString(R.string.number_already_added_error)
        }
        if (!numberTextInputLayout.isErrorEnabled && number != null) {
            viewModel.addNumber(number)
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveScrollPosition()
        recycler_view.clearOnScrollListeners()
        handleDialogsOnDestroy()
    }

    private fun handleDialogsOnDestroy() {
        handleErrorDialogOnDestroy()
        handleInputDialogOnDestroy()
    }

    private fun handleInputDialogOnDestroy() {
        inputDialog?.let {
            if(!it.isShowing) return@let
            val inputData = it.findViewById<TextInputEditText>(R.id.number_edit_text)
            val inputError = it.findViewById<TextInputLayout>(R.id.number_text_input_layout)?.error?.toString()
            viewModel.inputDialogState = DialogState(inputData?.text.toString(), inputError)
            it.dismiss()
        }
        inputDialog = null
    }

    private fun handleErrorDialogOnDestroy() {
        errorDialog?.let {
            if(!it.isShowing) return@let
            viewModel.errorDialogState = DialogState(it.findViewById<TextView>(android.R.id.message)?.text.toString())
            it.dismiss()
        }
        errorDialog = null
    }

    private fun scrollToPosition() {
        val layoutManager: LinearLayoutManager = recycler_view.layoutManager as LinearLayoutManager
        layoutManager.scrollToPosition(viewModel.scrollPosition)
    }

    private fun saveScrollPosition() {
        val layoutManager: LinearLayoutManager = recycler_view.layoutManager as LinearLayoutManager
        viewModel.scrollPosition = layoutManager.findFirstVisibleItemPosition()
    }
}
