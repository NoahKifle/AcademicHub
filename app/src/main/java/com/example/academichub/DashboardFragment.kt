package com.example.academichub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.academichub.databinding.FragmentDashboardBinding
import com.example.academichub.databinding.ItemQuickStatBinding
import com.example.academichub.ui.theme.AcademicHubTheme
import com.example.academichub.ui.theme.CalendarScreen
import com.example.academichub.viewmodel.AssignmentManagerViewModel
import com.example.academichub.viewmodel.AssignmentViewModelFactory
import com.example.academichub.viewmodel.CalendarViewModel
import com.example.academichub.viewmodel.SettingsViewModel
import com.example.academichub.viewmodel.SettingsViewModelFactory
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val assignmentViewModel: AssignmentManagerViewModel by activityViewModels {
        val app = requireActivity().application as AcademicHubApplication
        AssignmentViewModelFactory(app, app.repository)
    }
    private val settingsViewModel: SettingsViewModel by activityViewModels {
        SettingsViewModelFactory((requireActivity().application as AcademicHubApplication).settingsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStatTiles()

        binding.composeViewCalendar.apply {
            setContent {
                AcademicHubTheme {
                    CalendarScreen(viewModel = calendarViewModel)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            assignmentViewModel.assignments.collect { assignments ->
                calendarViewModel.setAssignments(assignments)
                updateUIWithDate(calendarViewModel.selectedDate.value, assignments)
                updateQuickStats(assignments)
                populateCourses(assignments)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.userSettings.collect { settings ->
                settings?.let {
                    binding.tvGreeting.text = if (it.userName.isNotEmpty()) "Hello, ${it.userName} 👋" else "Hello 👋"
                    binding.tvGpa.text = "%.2f".format(it.currentGpa)
                    binding.tvTargetGpa.text = "%.2f".format(it.targetGpa)
                    
                    updateSemesterProgress(it.semesterStartDate, it.semesterEndDate)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            calendarViewModel.selectedDate.collect { date ->
                updateUIWithDate(date, assignmentViewModel.assignments.value)
            }
        }
    }

    private fun updateSemesterProgress(startDateStr: String, endDateStr: String) {
        if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
            binding.tvSemesterPct.text = "0%"
            binding.pbSemester.progress = 0
            return
        }

        try {
            val sdf = SimpleDateFormat("MM/dd/yy", Locale.US)
            val startDate = sdf.parse(startDateStr)
            val endDate = sdf.parse(endDateStr)
            val today = Calendar.getInstance().time

            if (startDate != null && endDate != null) {
                val totalDuration = endDate.time - startDate.time
                val elapsed = today.time - startDate.time

                val progress = if (totalDuration > 0) {
                    ((elapsed.toDouble() / totalDuration.toDouble()) * 100).toInt()
                } else 0

                val clampedProgress = progress.coerceIn(0, 100)
                binding.tvSemesterPct.text = "$clampedProgress%"
                binding.pbSemester.progress = clampedProgress
            }
        } catch (e: Exception) {
            binding.tvSemesterPct.text = "0%"
            binding.pbSemester.progress = 0
        }
    }

    private fun updateUIWithDate(date: Calendar, assignments: List<com.example.academichub.model.AssignmentDetails>) {
        val dateString = String.format("%02d/%02d/%02d",
            date.get(Calendar.MONTH) + 1,
            date.get(Calendar.DAY_OF_MONTH),
            date.get(Calendar.YEAR) % 100
        )
        
        val filtered = assignments.filter { it.dueDate == dateString }
        populateUpcoming(filtered)
    }

    private fun setupStatTiles() {
        setupStatTile(binding.statCourses, R.drawable.ic_book, R.color.teal_100, R.color.teal_400, "0", "Courses")
        setupStatTile(binding.statDue, R.drawable.ic_calendar_small, R.color.amber_200, R.color.amber_400, "0", "Due Soon")
        setupStatTile(binding.statHours, R.drawable.ic_timer_small, R.color.teal_100, R.color.teal_400, "0.0", "Hrs This Week")
    }

    private fun updateQuickStats(assignments: List<com.example.academichub.model.AssignmentDetails>) {
        val sdf = SimpleDateFormat("MM/dd/yy", Locale.US)
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayStr = sdf.format(now.time)
        
        // Count anything due in the next 3 days that isn't done (including overdue items)
        val upcomingLimit = Calendar.getInstance().apply {
            time = now.time
            add(Calendar.DAY_OF_YEAR, 3)
        }

        val dueSoon = assignments.count { assignment ->
            if (assignment.isDone) return@count false
            if (assignment.dueDate == todayStr) return@count true
            try {
                val dueDate = sdf.parse(assignment.dueDate)
                if (dueDate != null) {
                    val dueCal = Calendar.getInstance().apply { time = dueDate }
                    dueCal.before(upcomingLimit)
                } else false
            } catch (e: Exception) {
                false
            }
        }
        
        val totalSeconds = assignments.sumOf { it.timeSpent }
        val hours = totalSeconds / 3600.0
        val courseCount = assignments.map { it.classCode }.distinct().size

        binding.statCourses.tvStatValue.text = courseCount.toString()
        binding.statDue.tvStatValue.text = dueSoon.toString()
        binding.statHours.tvStatValue.text = "%.1f".format(hours)
    }

    private fun populateCourses(assignments: List<com.example.academichub.model.AssignmentDetails>) {
        binding.llCourses.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        //make sure the class codes recognize its the same even when its lowercase and uppercase
        val uniqueCourses = assignments.map { it.classCode.uppercase() }.distinct()

        uniqueCourses.forEach { courseCode ->
            val card = inflater.inflate(R.layout.item_course_card, binding.llCourses, false)
            card.findViewById<TextView>(R.id.tv_course_name).text = "Course: $courseCode"
            card.findViewById<TextView>(R.id.tv_course_code).text = courseCode
            
            val courseAssignments = assignments.filter { it.classCode == courseCode && it.isDone && it.earnedPoints.isNotEmpty() }


            val totalEarned = courseAssignments.sumOf { it.earnedPoints.toDoubleOrNull() ?: 0.0 }
            val totalPossible = courseAssignments.sumOf { it.points.toDoubleOrNull() ?: 0.0 }

            val gradePct = if (totalPossible > 0) (totalEarned / totalPossible) * 100 else 0.0
            val letter = getLetterGrade(gradePct)

            val chip = card.findViewById<Chip>(R.id.chip_grade)
            chip.text = "${letter}  ${"%.1f".format(gradePct)}%"
            chip.chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), gradeColorRes(letter))
            
            card.findViewById<ProgressBar>(R.id.pb_course_grade).progress = gradePct.toInt()
            
            val lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10) }
            card.layoutParams = lp
            binding.llCourses.addView(card)
        }
    }

    private fun getLetterGrade(pct: Double): String = when {
        pct >= 90 -> "A"
        pct >= 80 -> "B"
        pct >= 70 -> "C"
        pct >= 60 -> "D"
        else -> "F"
    }

    private fun gradeColorRes(letter: String): Int = when {
        letter.startsWith("A") -> R.color.grade_a
        letter.startsWith("B") -> R.color.grade_b
        letter.startsWith("C") -> R.color.grade_c
        else                   -> R.color.grade_d
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

    private fun populateUpcoming(assignments: List<com.example.academichub.model.AssignmentDetails>) {
        binding.llUpcoming.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        if (assignments.isEmpty()) {
            val tv = TextView(requireContext())
            tv.text = "No assignments for this date"
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            tv.setPadding(dp(8), dp(8), dp(8), dp(8))
            binding.llUpcoming.addView(tv)
        } else {
            assignments.forEach { a ->
                val row = inflater.inflate(R.layout.item_assignment_row, binding.llUpcoming, false)
                row.findViewById<TextView>(R.id.tv_assignment_name).text = a.name
                row.findViewById<TextView>(R.id.tv_course_tag).text      = a.classCode
                row.findViewById<TextView>(R.id.tv_due_date).text        = a.dueDate
                
                val lp = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(8) }
                row.layoutParams = lp
                binding.llUpcoming.addView(row)
            }
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
