package com.example.academichub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.academichub.databinding.FragmentAssignmentsBinding
import com.example.academichub.databinding.ItemAssignmentDetailCardBinding
import com.google.android.material.snackbar.Snackbar

class AssignmentsFragment : Fragment() {

    private var _binding: FragmentAssignmentsBinding? = null
    private val binding get() = _binding!!

    enum class AssignmentType { ASSIGNMENT, PROJECT, TEST, QUIZ }

    data class AssignmentItem(
        val id: Int, val name: String, val course: String, val description: String,
        val dueDate: String, val maxPoints: Int, val studyHours: Double,
        val type: AssignmentType, val isOverdue: Boolean = false, val isDone: Boolean = false,
        val progressMilestones: Pair<Int, Int>? = null, val accentColorRes: Int = R.color.teal_400
    )

    private val allAssignments = listOf(
        AssignmentItem(1, "Final Project: AcademicHub App", "Mobile Programming",
            "Build a full Android app with UI and navigation following the proposal specifications.",
            "Apr 25", 100, 4.5, AssignmentType.PROJECT, progressMilestones = Pair(2, 5), accentColorRes = R.color.teal_400),
        AssignmentItem(2, "Chapter 5 Lab: RecyclerView", "Mobile Programming",
            "Implement a RecyclerView displaying a list of items with custom ViewHolder.",
            "Apr 11", 50, 1.5, AssignmentType.ASSIGNMENT, accentColorRes = R.color.teal_400),
        AssignmentItem(3, "Problem Set 7: Graph Algorithms", "Data Structures",
            "Solve 8 problems covering BFS, DFS, Dijkstra's, and topological sort.",
            "Apr 10", 80, 2.0, AssignmentType.ASSIGNMENT, accentColorRes = R.color.amber_400),
        AssignmentItem(4, "Midterm Reflection Essay", "Technical Writing",
            "Write a 1000-word reflection on your writing progress this semester.",
            "Apr 8", 60, 1.0, AssignmentType.ASSIGNMENT, isOverdue = true, accentColorRes = R.color.overdue_red),
        AssignmentItem(5, "Midterm Exam", "Calculus II",
            "Covers integration by parts, trig substitution, and partial fractions.",
            "Apr 15", 100, 5.0, AssignmentType.TEST, accentColorRes = R.color.coral_400),
        AssignmentItem(6, "Week 10 Quiz", "Data Structures",
            "Short quiz on heap operations and priority queues.",
            "Apr 12", 20, 0.5, AssignmentType.QUIZ, accentColorRes = R.color.amber_400),
        AssignmentItem(7, "Technical Manual Draft", "Technical Writing",
            "First draft of the 10-page technical manual for your chosen software tool.",
            "Apr 5", 75, 3.0, AssignmentType.PROJECT, isDone = true,
            progressMilestones = Pair(5, 5), accentColorRes = R.color.green_400)
    )

    private val filtered = allAssignments.toMutableList()
    private lateinit var adapter: AssignmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AssignmentAdapter(filtered)
        binding.rvAssignments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssignments.adapter = adapter

        binding.chipgroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val result = when {
                checkedIds.contains(R.id.chip_filter_due)     -> allAssignments.filter { !it.isDone && !it.isOverdue }
                checkedIds.contains(R.id.chip_filter_overdue) -> allAssignments.filter { it.isOverdue }
                checkedIds.contains(R.id.chip_filter_done)    -> allAssignments.filter { it.isDone }
                else -> allAssignments
            }
            filtered.clear(); filtered.addAll(result); adapter.notifyDataSetChanged()
        }

        binding.fabAddAssignment.setOnClickListener {
            Snackbar.make(binding.root, "Add Assignment — coming soon!", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.navy_800))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white)).show()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    inner class AssignmentAdapter(private val items: List<AssignmentItem>)
        : RecyclerView.Adapter<AssignmentAdapter.VH>() {

        inner class VH(val b: ItemAssignmentDetailCardBinding) : RecyclerView.ViewHolder(b.root) {
            fun bind(item: AssignmentItem) {
                b.viewTypeBand.setBackgroundColor(ContextCompat.getColor(requireContext(), item.accentColorRes))
                b.tvTypeChip.text = item.type.name
                b.tvTypeChip.setTextColor(ContextCompat.getColor(requireContext(), item.accentColorRes))
                b.tvDetailCourse.text  = item.course
                b.tvDetailName.text    = item.name
                b.tvDetailDesc.text    = item.description
                b.tvDetailDue.text     = item.dueDate
                b.tvDetailHours.text   = "${item.studyHours} hrs"
                b.tvDetailPoints.text  = "${item.maxPoints} pts"
                b.cbDetailDone.isChecked = item.isDone
                if (item.isOverdue) b.tvDetailDue.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.overdue_red))
                if (item.progressMilestones != null) {
                    b.llProjectProgress.visibility = View.VISIBLE
                    val (done, total) = item.progressMilestones
                    b.tvProgressLabel.text = "$done / $total milestones"
                    b.pbProjectProgress.max = total
                    b.pbProjectProgress.progress = done
                } else b.llProjectProgress.visibility = View.GONE
                b.btnStartTimer.setOnClickListener {
                    Snackbar.make(it, "Timer started!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.navy_800))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.white)).show()
                }
                b.btnViewAssignment.setOnClickListener {
                    Snackbar.make(it, "Detail view coming soon!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.teal_400))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.white)).show()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            ItemAssignmentDetailCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
        override fun getItemCount() = items.size
    }
}
