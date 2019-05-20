package com.mateuszgabrynowicz.simplenumberlist.numbers_list

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mateuszgabrynowicz.simplenumberlist.R
import com.mateuszgabrynowicz.simplenumberlist.common.IAlreadyAddedChecker
import com.mateuszgabrynowicz.simplenumberlist.common.LoadMoreScrollListener
import com.mateuszgabrynowicz.simplenumberlist.common.ValidationResult
import com.mateuszgabrynowicz.simplenumberlist.common.ViewState
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers_list)

        initializeRecyclerView()
        initializeViewModel()
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
                is ViewState.Error -> showDialog(viewState)
            }
            showLoading(viewState is ViewState.Loading)
        })
        viewModel.initialLoad()
    }

    private fun showLoading(isLoading: Boolean) {
        progress_bar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showDialog(viewState: ViewState<List<Int>>) {
        val errorMessage = if(viewState is ViewState.Error) R.string.error_message else R.string.no_content_message
        AlertDialog.Builder(this)
            .setTitle(R.string.error_title)
            .setMessage(errorMessage)
            .setPositiveButton(R.string.try_again_button_label) { _, _ ->
                viewModel.loadMoreNumbers()
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

    private fun showAddNumberDialog() {
        val view = View.inflate(this, R.layout.add_number_dialog, null)
        val numberEditText = view.findViewById<TextInputEditText>(R.id.number_edit_text)
        val numberTextInputLayout = view.findViewById<TextInputLayout>(R.id.number_text_input_layout)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.show()

        /*setting OnClick after dialog was created in order to not close the dialog instantly after user pressed
          the button*/
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            validateNumberInput(
                numberEditText.text.toString().toIntOrNull(), numberTextInputLayout, dialog
            )
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
