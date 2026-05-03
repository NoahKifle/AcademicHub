package com.example.academichub

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.academichub.databinding.FragmentSettingsBinding
import com.example.academichub.notifications.NotificationSettingsFragment
import com.example.academichub.viewmodel.SettingsViewModel
import com.example.academichub.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModelFactory((requireActivity().application as AcademicHubApplication).settingsRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateAutoFormat(binding.etStartDate)
        setupDateAutoFormat(binding.etEndDate)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userSettings.collect { settings ->
                settings?.let {
                    binding.etUserName.setText(it.userName)
                    binding.etStartDate.setText(it.semesterStartDate)
                    binding.etEndDate.setText(it.semesterEndDate)
                    binding.etTargetGpa.setText(it.targetGpa.toString())
                    binding.etCurrentGpa.setText(it.currentGpa.toString())
                }
            }
        }

        binding.btnSaveSettings.setOnClickListener {
            val name = binding.etUserName.text.toString()
            val start = binding.etStartDate.text.toString()
            val end = binding.etEndDate.text.toString()
            val target = binding.etTargetGpa.text.toString().toDoubleOrNull() ?: 4.0
            val current = binding.etCurrentGpa.text.toString().toDoubleOrNull() ?: 0.0

            viewModel.saveSettings(name, start, end, target, current)
            Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show()
        }

        binding.btnNotificationSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationSettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupDateAutoFormat(editText: android.widget.EditText) {
        editText.addTextChangedListener(object : TextWatcher {
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
                    editText.setText(current)
                    editText.setSelection(if (sel < current.length) sel else current.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
