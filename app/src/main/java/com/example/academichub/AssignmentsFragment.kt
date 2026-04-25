package com.example.academichub

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.academichub.databinding.FragmentAssignmentsBinding
import com.example.academichub.databinding.ItemAssignmentDetailCardBinding
import com.example.academichub.model.AssignmentDetails
import com.example.academichub.viewmodel.AssignmentManagerViewModel
import com.example.academichub.viewmodel.AssignmentViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AssignmentsFragment : Fragment() {

    private var _binding: FragmentAssignmentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentManagerViewModel by activityViewModels {
        val app = requireActivity().application as AcademicHubApplication
        AssignmentViewModelFactory(app, app.repository)
    }
    private lateinit var adapter: AssignmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = AssignmentAdapter(emptyList())
        binding.rvAssignments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssignments.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.assignments.collect { assignments ->
                adapter.updateItems(assignments)
            }
        }

        binding.fabAddAssignment.setOnClickListener {
            showAssignmentDialog(null)
        }
    }

    private fun showAssignmentDialog(assignment: AssignmentDetails?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_assignment, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        val etClass = dialogView.findViewById<EditText>(R.id.et_class_code)
        val etDue = dialogView.findViewById<EditText>(R.id.et_due_date)
        val etPoints = dialogView.findViewById<EditText>(R.id.et_points)
        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinner_type)

        // Date Auto-formatter
        etDue.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyy = "MMDDYY"
            private val cal = java.util.Calendar.getInstance()

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    var clean = s.toString().replace("[^\\d]".toRegex(), "")
                    val cleanC = current.replace("[^\\d]".toRegex(), "")

                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    if (clean == cleanC) sel--

                    if (clean.length < 6) {
                        clean = clean + ddmmyy.substring(clean.length)
                    } else {
                        var mon = clean.substring(0, 2).toInt()
                        var day = clean.substring(2, 4).toInt()
                        var year = clean.substring(4, 6).toInt()

                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal.set(java.util.Calendar.MONTH, mon - 1)

                        year = if (year < 1) 1 else if (year > 99) 99 else year
                        cal.set(java.util.Calendar.YEAR, 2000 + year)

                        day = if (day > cal.getActualMaximum(java.util.Calendar.DATE)) cal.getActualMaximum(java.util.Calendar.DATE) else day
                        clean = String.format("%02d%02d%02d", mon, day, year)
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 6))

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    etDue.setText(current)
                    etDue.setSelection(if (sel < current.length) sel else current.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val types = listOf("Homework", "Test", "Quiz", "Project", "Paper", "Other")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = spinnerAdapter

        assignment?.let {
            etName.setText(it.name)
            etClass.setText(it.classCode)
            etDue.setText(it.dueDate)
            etPoints.setText(it.points)
            spinnerType.setSelection(types.indexOf(it.assignmentType))
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (assignment == null) "Add Assignment" else "Edit Assignment")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val classCode = etClass.text.toString()
                val dueDate = etDue.text.toString()
                val points = etPoints.text.toString()
                val type = spinnerType.selectedItem.toString()

                if (assignment == null) {
                    viewModel.addAssignment(name, classCode, dueDate, points, type)
                } else {
                    // Update existing assignment fields
                    val updated = assignment.copy(
                        name = name,
                        classCode = classCode,
                        dueDate = dueDate,
                        points = points,
                        assignmentType = type
                    )
                    viewModel.deleteAssignment(assignment.id)
                    viewModel.addAssignment(name, classCode, dueDate, points, type)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPointsDialog(assignment: AssignmentDetails) {
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val input = EditText(requireContext())
        input.layoutParams = lp
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Points earned out of ${assignment.points}"
        
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(60, 20, 60, 20)
        input.layoutParams = params
        container.addView(input)

        AlertDialog.Builder(requireContext())
            .setTitle("Assignment Completed")
            .setMessage("How many points did you earn?")
            .setView(container)
            .setPositiveButton("Submit") { _, _ ->
                val earned = input.text.toString()
                viewModel.toggleDone(assignment.id, earned)
            }
            .setNegativeButton("Skip") { _, _ ->
                viewModel.toggleDone(assignment.id)
            }
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    inner class AssignmentAdapter(private var items: List<AssignmentDetails>)
        : RecyclerView.Adapter<AssignmentAdapter.VH>() {

        fun updateItems(newItems: List<AssignmentDetails>) {
            items = newItems
            notifyDataSetChanged()
        }

        inner class VH(val b: ItemAssignmentDetailCardBinding) : RecyclerView.ViewHolder(b.root) {
            fun bind(item: AssignmentDetails) {
                b.tvDetailName.text = item.name
                b.tvDetailCourse.text = item.classCode
                b.tvDetailDue.text = item.dueDate
                
                if (item.isDone && item.earnedPoints.isNotEmpty()) {
                    b.tvDetailPoints.text = "${item.earnedPoints} / ${item.points} pts"
                    b.tvDetailPoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_400))
                } else {
                    b.tvDetailPoints.text = "${item.points} pts"
                    b.tvDetailPoints.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                }

                b.tvTypeChip.text = item.assignmentType
                b.cbDetailDone.isChecked = item.isDone
                
                b.tvDetailHours.text = formatTime(item.timeSpent)
                b.tvDetailHours.setTextColor(
                    if (item.isTimerRunning) ContextCompat.getColor(requireContext(), R.color.teal_400)
                    else ContextCompat.getColor(requireContext(), R.color.text_secondary)
                )

                b.btnStartTimer.text = if (item.isTimerRunning) "Stop" else "Start"
                b.btnStartTimer.setOnClickListener {
                    viewModel.toggleTimer(item.id)
                }

                b.cbDetailDone.setOnClickListener {
                    if (!item.isDone) {
                        showPointsDialog(item)
                    } else {
                        viewModel.toggleDone(item.id)
                    }
                }

                b.btnViewAssignment.setOnClickListener {
                    showAssignmentDialog(item)
                }
            }
        }

        private fun formatTime(seconds: Long): String {
            val h = seconds / 3600
            val m = (seconds % 3600) / 60
            val s = seconds % 60
            return String.format("%02d:%02d:%02d", h, m, s)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            ItemAssignmentDetailCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
        override fun getItemCount() = items.size
    }
}
