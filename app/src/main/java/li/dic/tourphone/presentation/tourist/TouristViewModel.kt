package li.dic.tourphone.presentation.tourist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import li.dic.tourphone.domain.Message

class TouristViewModel : ViewModel() {

    val messages = MutableLiveData<MutableList<Message>>()
}