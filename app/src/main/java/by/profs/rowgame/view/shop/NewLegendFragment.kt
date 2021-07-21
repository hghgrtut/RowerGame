package by.profs.rowgame.view.shop

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.profs.rowgame.R
import by.profs.rowgame.data.items.Rower
import by.profs.rowgame.data.items.util.Ages
import by.profs.rowgame.data.preferences.PreferenceEditor
import by.profs.rowgame.databinding.FragmentNewLegendBinding
import by.profs.rowgame.presenter.database.MyRoomDatabase
import by.profs.rowgame.presenter.imageloader.CoilImageLoader
import by.profs.rowgame.presenter.imageloader.ImageLoader
import by.profs.rowgame.presenter.traders.Recruiter
import by.profs.rowgame.view.utils.HelperFuns.showToast
import by.profs.rowgame.view.utils.clearError
import by.profs.rowgame.view.utils.getIntOrZero
import by.profs.rowgame.view.utils.hasText
import by.profs.rowgame.view.utils.setError
import com.google.android.material.textfield.TextInputLayout

class NewLegendFragment : Fragment(R.layout.fragment_new_legend) {
    private var _binding: FragmentNewLegendBinding? = null
    private val binding: FragmentNewLegendBinding get() = requireNotNull(_binding)
    private var _characteristics: Array<TextInputLayout>? = null
    private val characteristics: Array<TextInputLayout> get() = requireNotNull(_characteristics)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewLegendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefEditor = PreferenceEditor(requireContext())
        binding.fame.text = this.getString(R.string.fame_balance, prefEditor.getFame())
        _characteristics =
            arrayOf(binding.editEndurance, binding.editPower, binding.editTechnicality)
        binding.create.setOnClickListener {
            if (validate()) recruit(prefEditor)
            else showToast(requireContext(), R.string.recruit_fail)
        }

        val linkWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { return }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { return }
            override fun afterTextChanged(p0: Editable?) {
                val lin = binding.editPhotoLink.editText?.text.toString()
                if (lin != "") {
                    binding.photoPreview.visibility = View.VISIBLE
                    (CoilImageLoader as ImageLoader).loadImageFromNetwork(binding.photoPreview, lin)
                } else { binding.photoPreview.visibility = View.GONE }
            }
        }
        val characteristicsWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { return }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { return }
            override fun afterTextChanged(p0: Editable?) { showCurrentCost() }
        }
        binding.editPhotoLink.editText?.addTextChangedListener(linkWatcher)
        binding.editAge.editText?.addTextChangedListener(characteristicsWatcher)
        characteristics.forEach { it.editText?.addTextChangedListener(characteristicsWatcher) }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun getAgeCoefficient(): Double? {
        val age = binding.editAge.getIntOrZero()
        return when {
            age <= Ages.TooYoung.age -> null
            // age <= Ages.Kid.age -> 2.2
            age <= Ages.Jun.age -> JUN_COEF
            age <= Ages.Youth.age -> YOUTH_COEF
            else -> MASTERS_COEF
        }
    }

    private fun getCurrentCost(): Int {
        val coef = getAgeCoefficient() ?: YOUTH_COEF
        return (characteristics.sumOf { it.getIntOrZero() } * coef).toInt()
    }

    private fun showCurrentCost() {
        binding.create.text = getString(R.string.fame_cost, getCurrentCost()) }

    private fun validate(): Boolean {
        var isValid = true
        fun invalidate(layout: TextInputLayout, @StringRes errorId: Int) {
            layout.setError(errorId)
            isValid = false
        }

        if (binding.editName.hasText()) binding.editName.clearError()
        else invalidate(binding.editName, R.string.error_empty_name)

        if (getAgeCoefficient() != null) binding.editAge.clearError()
        else invalidate(binding.editAge, R.string.error_too_young)

        val minWeightHeight = intArrayOf(MIN_WEIGHT, MIN_HEIGHT)
        val maxWeightHeight = intArrayOf(MAX_WEIGHT, MAX_HEIGHT)
        arrayOf(binding.editWeight, binding.editHeight).forEachIndexed { index, field ->
            val value = field.getIntOrZero()
            if (value < minWeightHeight[index]) invalidate(field, R.string.error_too_tiny)
            else if (value > maxWeightHeight[index]) invalidate(field, R.string.error_too_much)
            else field.clearError()
        }

        characteristics.forEach {
            if (it.getIntOrZero() > 0) it.clearError()
            else invalidate(it, R.string.error_value_expected)
        }

        return isValid
    }

    private fun recruit(prefEditor: PreferenceEditor) {
        val cost = getCurrentCost()
        val fame = prefEditor.getFame()
        if (fame < cost) showToast(requireContext(), R.string.recruit_fail)
        else {
            val dao = MyRoomDatabase.getDatabase(requireContext()).rowerDao()
            binding.run {
                val link = editPhotoLink.editText?.text.toString()
                Recruiter(prefEditor, dao).buy(Rower(
                    id = null,
                    name = editName.editText?.text.toString(),
                    gender = if (editGender.checkedRadioButtonId == R.id.gender_male) Rower.MALE
                    else Rower.FEMALE,
                    age = editAge.getIntOrZero(),
                    height = editHeight.getIntOrZero(),
                    weight = editWeight.getIntOrZero(),
                    power = editPower.getIntOrZero(),
                    technics = editTechnicality.getIntOrZero(),
                    endurance = editEndurance.getIntOrZero(),
                    thumb = if (link == "") null else link,
                    about = editAbout.editText?.text.toString())) }
            prefEditor.setFame(fame - cost)
            showToast(requireContext(), R.string.recruit_success)
            findNavController().navigate(R.id.action_newLegendFragment_to_inventoryFragment)
        }
    }

    companion object {
        private const val MIN_WEIGHT = 22
        private const val MIN_HEIGHT = 112
        private const val MAX_WEIGHT = 150
        private const val MAX_HEIGHT = 260

        private const val JUN_COEF = 1.8
        private const val YOUTH_COEF = 1.4
        private const val MASTERS_COEF = 1.0
    }
}