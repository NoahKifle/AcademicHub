package com.example.academichub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.academichub.databinding.FragmentGradesBinding

class GradesFragment : Fragment() {

    private var _binding: FragmentGradesBinding? = null
    private val binding get() = _binding!!

    data class GradeCourse(
        val name: String,
        val code: String,
        val overallPct: Double,
        val letterGrade: String,
        val categories: List<GradeCategory>
    )

    data class GradeCategory(
        val name: String,
        val weight: Int,
        val score: Double,
        val iconRes: Int
    )

    private val sampleGradeCourses = listOf(
        GradeCourse("Mobile Programming", "CSC 3410", 92.4, "A", listOf(
            GradeCategory("Assignments", 30, 95.0, R.drawable.ic_assignment_small),
            GradeCategory("Tests",       35, 88.0, R.drawable.ic_test_small),
            GradeCategory("Projects",    25, 96.0, R.drawable.ic_project_small),
            GradeCategory("Quizzes",     10, 90.0, R.drawable.ic_quiz_small)
        )),
        GradeCourse("Data Structures", "CSC 2310", 86.1, "B+", listOf(
            GradeCategory("Assignments", 25, 88.0, R.drawable.ic_assignment_small),
            GradeCategory("Tests",       40, 82.0, R.drawable.ic_test_small),
            GradeCategory("Projects",    25, 90.0, R.drawable.ic_project_small),
            GradeCategory("Quizzes",     10, 85.0, R.drawable.ic_quiz_small)
        )),
        GradeCourse("Calculus II", "MAT 2410", 78.5, "C+", listOf(
            GradeCategory("Assignments", 20, 82.0, R.drawable.ic_assignment_small),
            GradeCategory("Tests",       50, 75.0, R.drawable.ic_test_small),
            GradeCategory("Projects",    10, 80.0, R.drawable.ic_project_small),
            GradeCategory("Quizzes",     20, 78.0, R.drawable.ic_quiz_small)
        )),
        GradeCourse("Technical Writing", "ENG 3310", 95.0, "A", listOf(
            GradeCategory("Assignments", 40, 96.0, R.drawable.ic_assignment_small),
            GradeCategory("Tests",       20, 92.0, R.drawable.ic_test_small),
            GradeCategory("Projects",    30, 97.0, R.drawable.ic_project_small),
            GradeCategory("Quizzes",     10, 94.0, R.drawable.ic_quiz_small)
        ))
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as androidx.appcompat.app.AppCompatActivity).setSupportActionBar(binding.toolbarGrades)
        populateSemesterGpa()
        populateCourseCards()
        setupCalculatorDropdown()
        setupCalculateButton()
    }

    private fun populateSemesterGpa() {
        val currentGpa = 3.74
        val targetGpa  = 3.80
        binding.tvSemesterGpa.text    = "%.2f".format(currentGpa)
        binding.tvGpaTargetGrade.text = "%.2f".format(targetGpa)
        binding.tvGpaDistance.text    = "+%.2f to go".format(targetGpa - currentGpa)
        binding.pbGpaTarget.max       = 400
        binding.pbGpaTarget.progress  = (currentGpa * 100).toInt()
        binding.tvGpaProgressLabel.text =
            "${"%.0f".format(currentGpa / targetGpa * 100)}% of the way to your ${"%.2f".format(targetGpa)} goal"
    }

    private fun populateCourseCards() {
        binding.llGradeCourses.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        sampleGradeCourses.forEach { course ->
            val cardView = inflater.inflate(R.layout.item_grade_course_card, binding.llGradeCourses, false)
            cardView.findViewById<TextView>(R.id.tv_gc_course_name).text = course.name
            cardView.findViewById<TextView>(R.id.tv_gc_course_code).text = course.code
            cardView.findViewById<TextView>(R.id.tv_gc_pct).text = "${"%.1f".format(course.overallPct)}%"

            val letterView = cardView.findViewById<TextView>(R.id.tv_gc_letter)
            letterView.text = course.letterGrade
            letterView.setTextColor(ContextCompat.getColor(requireContext(), gradeColorRes(course.letterGrade)))

            // Populate category rows via the ll_categories container
            val categoriesContainer = cardView.findViewById<ViewGroup>(R.id.ll_categories)
            course.categories.forEachIndexed { index, cat ->
                if (index < categoriesContainer.childCount) {
                    val rowView = categoriesContainer.getChildAt(index)
                    rowView.findViewById<TextView>(R.id.tv_cat_name).text   = cat.name
                    rowView.findViewById<TextView>(R.id.tv_cat_weight).text = "${cat.weight}%"
                    val scoreView = rowView.findViewById<TextView>(R.id.tv_cat_score)
                    scoreView.text = "${"%.0f".format(cat.score)}%"
                    scoreView.setTextColor(ContextCompat.getColor(requireContext(), scoreColorRes(cat.score)))
                    rowView.findViewById<ProgressBar>(R.id.pb_cat_progress).progress = cat.score.toInt()
                    rowView.findViewById<ImageView>(R.id.iv_cat_icon).setImageResource(cat.iconRes)
                }
            }

            val lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (12 * resources.displayMetrics.density).toInt() }
            cardView.layoutParams = lp
            binding.llGradeCourses.addView(cardView)
        }
    }

    private fun setupCalculatorDropdown() {
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            sampleGradeCourses.map { it.name })
        binding.actCalcCourse.setAdapter(adapter)
    }

    private fun setupCalculateButton() {
        binding.btnCalculate.setOnClickListener {
            val newGrade = binding.etNewGrade.text?.toString()?.trim()?.toDoubleOrNull()
            if (newGrade == null || newGrade < 0 || newGrade > 100) {
                binding.tvCalcResult.visibility = View.VISIBLE
                binding.tvCalcResult.text = "Please enter a valid grade (0–100)"
                binding.tvCalcResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.coral_400))
                return@setOnClickListener
            }
            val hypotheticalGpa = (3.74 + (newGrade - 85) * 0.004).coerceIn(0.0, 4.0)
            binding.tvCalcResult.visibility = View.VISIBLE
            binding.tvCalcResult.text = "New GPA would be: ${"%.2f".format(hypotheticalGpa)}"
            binding.tvCalcResult.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_400))
        }
    }

    private fun gradeColorRes(letter: String): Int = when {
        letter.startsWith("A") -> R.color.grade_a
        letter.startsWith("B") -> R.color.grade_b
        letter.startsWith("C") -> R.color.grade_c
        else -> R.color.grade_d
    }

    private fun scoreColorRes(score: Double): Int = when {
        score >= 90 -> R.color.grade_a
        score >= 80 -> R.color.grade_b
        score >= 70 -> R.color.grade_c
        else -> R.color.grade_d
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
