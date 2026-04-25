package com.example.academichub

import android.content.Intent
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.academichub.databinding.FragmentGradesBinding
import com.example.academichub.viewmodel.AssignmentManagerViewModel
import com.example.academichub.viewmodel.AssignmentViewModelFactory
import kotlinx.coroutines.launch

class GradesFragment : Fragment() {

    private var _binding: FragmentGradesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentManagerViewModel by activityViewModels {
        val app = requireActivity().application as AcademicHubApplication
        AssignmentViewModelFactory(app, app.repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as androidx.appcompat.app.AppCompatActivity).setSupportActionBar(binding.toolbarGrades)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.assignments.collect { assignments ->
                populateGradesUI(assignments)
                setupCalculator(assignments)
            }
        }
        
        setupCalculateButton()
    }

    private fun populateGradesUI(assignments: List<com.example.academichub.model.AssignmentDetails>) {
        val uniqueCourses = assignments.map { it.classCode }.distinct()

        var totalPointsPossible = 0.0
        var totalPointsEarned = 0.0
        
        uniqueCourses.forEach { code ->
            val courseAssignments = assignments.filter { it.classCode == code && it.isDone && it.earnedPoints.isNotEmpty() }
            totalPointsPossible += courseAssignments.sumOf { it.points.toDoubleOrNull() ?: 0.0 }
            totalPointsEarned += courseAssignments.sumOf { it.earnedPoints.toDoubleOrNull() ?: 0.0 }
        }

        val semesterPct = if (totalPointsPossible > 0) (totalPointsEarned / totalPointsPossible) * 100 else 0.0
        val gpa = (semesterPct / 100 * 4.0)

        binding.tvSemesterGpa.text    = "%.2f".format(gpa)
        binding.tvGpaTargetGrade.text = "4.00"
        binding.tvGpaDistance.text    = "%.2f to go".format(4.0 - gpa)
        binding.pbGpaTarget.max       = 400
        binding.pbGpaTarget.progress  = (gpa * 100).toInt()
        binding.tvGpaProgressLabel.text = "${"%.0f".format(semesterPct)}% overall average"

        // Populate Course Cards
        binding.llGradeCourses.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        uniqueCourses.forEach { courseCode ->
            val cardView = inflater.inflate(R.layout.item_grade_course_card, binding.llGradeCourses, false)
            cardView.findViewById<TextView>(R.id.tv_gc_course_name).text = "Course: $courseCode"
            cardView.findViewById<TextView>(R.id.tv_gc_course_code).text = courseCode
            
            val courseAssignments = assignments.filter { it.classCode == courseCode && it.isDone && it.earnedPoints.isNotEmpty() }
            val cEarned = courseAssignments.sumOf { it.earnedPoints.toDoubleOrNull() ?: 0.0 }
            val cPossible = courseAssignments.sumOf { it.points.toDoubleOrNull() ?: 0.0 }
            val cPct = if (cPossible > 0) (cEarned / cPossible) * 100 else 0.0
            val letter = getLetterGrade(cPct)

            cardView.findViewById<TextView>(R.id.tv_gc_pct).text = "${"%.1f".format(cPct)}%"
            val letterView = cardView.findViewById<TextView>(R.id.tv_gc_letter)
            letterView.text = letter
            letterView.setTextColor(ContextCompat.getColor(requireContext(), gradeColorRes(letter)))

            // Group by category
            val categoriesContainer = cardView.findViewById<ViewGroup>(R.id.ll_categories)
            categoriesContainer.removeAllViews()
            
            val types = courseAssignments.map { it.assignmentType }.distinct()
            types.forEach { type ->
                val typeAssignments = courseAssignments.filter { it.assignmentType == type }
                val tEarned = typeAssignments.sumOf { it.earnedPoints.toDoubleOrNull() ?: 0.0 }
                val tPossible = typeAssignments.sumOf { it.points.toDoubleOrNull() ?: 0.0 }
                val tPct = (tEarned / tPossible) * 100

                val rowView = inflater.inflate(R.layout.item_grade_category_row, categoriesContainer, false)
                rowView.findViewById<TextView>(R.id.tv_cat_name).text = type
                rowView.findViewById<TextView>(R.id.tv_cat_weight).text = "Count: ${typeAssignments.size}"
                val scoreView = rowView.findViewById<TextView>(R.id.tv_cat_score)
                scoreView.text = "${"%.0f".format(tPct)}%"
                scoreView.setTextColor(ContextCompat.getColor(requireContext(), scoreColorRes(tPct)))
                rowView.findViewById<ProgressBar>(R.id.pb_cat_progress).progress = tPct.toInt()
                
                categoriesContainer.addView(rowView)
            }

            val lp = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = (12 * resources.displayMetrics.density).toInt() }
            cardView.layoutParams = lp
            binding.llGradeCourses.addView(cardView)
        }
    }

    private fun setupCalculator(assignments: List<com.example.academichub.model.AssignmentDetails>) {
        val uniqueCourses = assignments.map { it.classCode }.distinct()
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            uniqueCourses)
        binding.actCalcCourse.setAdapter(adapter)
    }

    private fun setupCalculateButton() {
        binding.btnCalculate.setOnClickListener {
            // Launch the new calculator
            val intent = Intent(requireContext(), MainActivity3::class.java)
            startActivity(intent)
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
