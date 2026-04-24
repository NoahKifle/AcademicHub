package com.example.academichub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.academichub.databinding.FragmentDashboardBinding
import com.example.academichub.databinding.ItemQuickStatBinding
import com.google.android.material.chip.Chip

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    data class Course(
        val name: String, val code: String, val grade: Double,
        val letter: String, val colorRes: Int
    )

    data class Assignment(
        val name: String, val course: String,
        val dueDate: String, val isOverdue: Boolean = false
    )

    private val courses = listOf(
        Course("Mobile Programming",  "CSC 3410 · 3 Credits", 92.4, "A",  R.color.teal_400),
        Course("Data Structures",     "CSC 2310 · 3 Credits", 86.1, "B+", R.color.amber_400),
        Course("Calculus II",         "MAT 2410 · 4 Credits", 78.5, "C+", R.color.coral_400),
        Course("Technical Writing",   "ENG 3310 · 3 Credits", 95.0, "A",  R.color.green_400)
    )

    private val assignments = listOf(
        Assignment("Chapter 5 Lab: RecyclerView", "Mobile Programming", "Apr 11"),
        Assignment("Problem Set 7",               "Data Structures",    "Apr 10"),
        Assignment("Midterm Reflection Essay",    "Technical Writing",  "Apr 8", isOverdue = true)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateQuickStats()
        populateCourses()
        populateUpcoming()
    }

    // ── Fix: use ItemQuickStatBinding directly instead of casting to View ──
    private fun populateQuickStats() {
        setupStatTile(
            statBinding  = binding.statCourses,
            iconRes      = R.drawable.ic_book,
            iconBgTint   = R.color.teal_100,
            iconTint     = R.color.teal_400,
            value        = "5",
            label        = "Courses"
        )
        setupStatTile(
            statBinding  = binding.statDue,
            iconRes      = R.drawable.ic_calendar_small,
            iconBgTint   = R.color.amber_200,
            iconTint     = R.color.amber_400,
            value        = "3",
            label        = "Due Today"
        )
        setupStatTile(
            statBinding  = binding.statHours,
            iconRes      = R.drawable.ic_timer_small,
            iconBgTint   = R.color.teal_100,
            iconTint     = R.color.teal_400,
            value        = "12.5",
            label        = "Hrs This Week"
        )
    }

    private fun setupStatTile(
        statBinding: ItemQuickStatBinding,
        iconRes: Int,
        iconBgTint: Int,
        iconTint: Int,
        value: String,
        label: String
    ) {
        statBinding.ivStatIcon.setImageResource(iconRes)
        statBinding.ivStatIcon.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), iconBgTint)
        statBinding.ivStatIcon.imageTintList =
            ContextCompat.getColorStateList(requireContext(), iconTint)
        statBinding.tvStatValue.text = value
        statBinding.tvStatLabel.text = label
    }

    private fun populateCourses() {
        binding.llCourses.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        courses.forEach { course ->
            val card = inflater.inflate(R.layout.item_course_card, binding.llCourses, false)
            card.findViewById<TextView>(R.id.tv_course_name).text = course.name
            card.findViewById<TextView>(R.id.tv_course_code).text = course.code
            card.findViewById<View>(R.id.view_accent_dot)
                .backgroundTintList = ContextCompat.getColorStateList(requireContext(), course.colorRes)
            val chip = card.findViewById<Chip>(R.id.chip_grade)
            chip.text = "${course.letter}  ${"%.1f".format(course.grade)}%"
            chip.chipBackgroundColor =
                ContextCompat.getColorStateList(requireContext(), gradeColorRes(course.letter))
            card.findViewById<ProgressBar>(R.id.pb_course_grade).progress = course.grade.toInt()
            val lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10) }
            card.layoutParams = lp
            binding.llCourses.addView(card)
        }
    }

    private fun populateUpcoming() {
        binding.llUpcoming.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        assignments.forEach { a ->
            val row = inflater.inflate(R.layout.item_assignment_row, binding.llUpcoming, false)
            row.findViewById<TextView>(R.id.tv_assignment_name).text = a.name
            row.findViewById<TextView>(R.id.tv_course_tag).text      = a.course
            row.findViewById<TextView>(R.id.tv_due_date).text        = a.dueDate
            row.findViewById<TextView>(R.id.tv_overdue_badge).visibility =
                if (a.isOverdue) View.VISIBLE else View.GONE
            if (a.isOverdue) {
                row.findViewById<View>(R.id.view_type_stripe)
                    .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.overdue_red))
            }
            val lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
            row.layoutParams = lp
            binding.llUpcoming.addView(row)
        }
    }

    private fun gradeColorRes(letter: String): Int = when {
        letter.startsWith("A") -> R.color.grade_a
        letter.startsWith("B") -> R.color.grade_b
        letter.startsWith("C") -> R.color.grade_c
        else                   -> R.color.grade_d
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}