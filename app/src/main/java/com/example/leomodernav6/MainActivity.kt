package com.example.leomodernav6

// ... other imports ...
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "joke") {
        composable("joke") { JokeView(navController) }
        composable("another") { AnotherView() }
    }
}

data class ChuckNorrisJoke(
    @SerializedName("id") val id: String,
    @SerializedName("value") val joke: String
)

interface ChuckNorrisAPIService {
    @GET("jokes/random")
    suspend fun getRandomJoke(): ChuckNorrisJoke
}

class MainViewModel : ViewModel() {
    private val _jokeState = MutableStateFlow<ChuckNorrisJoke?>(null)
    val jokeState: StateFlow<ChuckNorrisJoke?> = _jokeState

    init {
        fetchJoke()
    }

    fun fetchJoke() {
        viewModelScope.launch {
            val apiService = Retrofit.Builder()
                .baseUrl("https://api.chucknorris.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChuckNorrisAPIService::class.java)

            try {
                val joke = apiService.getRandomJoke()
                _jokeState.value = joke
            } catch (e: Exception) {
                println("Error fetching joke: ${e.message}")
            }
        }
    }
}


@Composable
fun ChuckNorrisJokeApp() {
    val viewModel: MainViewModel = viewModel<MainViewModel>()
    val jokeState = viewModel.jokeState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            jokeState.value?.let { joke ->
                Text(text = joke.joke)
            }
            Button(onClick = { viewModel.fetchJoke() }) {
                Text("Get New Joke")
            }
        }
    }
}

@Composable
fun JokeView(navController: NavHostController, viewModel: JokeViewModel = viewModel()) {
    val jokeState = viewModel.jokeState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        jokeState.value?.let { joke ->
            Text(text = joke.joke)
        }
        Button(onClick = { viewModel.fetchJoke() }) {
            Text("Get New Joke")
        }

        // Navigation button to AnotherView
        Button(onClick = { navController.navigate("another") }) {
            Text("Go to Another View")
        }
    }
}

class JokeViewModel : ViewModel() {
    private val _jokeState = MutableStateFlow<ChuckNorrisJoke?>(null)
    val jokeState: StateFlow<ChuckNorrisJoke?> = _jokeState

    init {
        fetchJoke()
    }

    fun fetchJoke() {
        viewModelScope.launch {
            val apiService = Retrofit.Builder()
                .baseUrl("https://api.chucknorris.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChuckNorrisAPIService::class.java)

            try {
                val joke = apiService.getRandomJoke()
                _jokeState.value = joke
            } catch (e: Exception) {
                println("Error fetching joke: ${e.message}")
            }
        }
    }
}

@Composable
fun AnotherView(viewModel: AnotherViewModel = viewModel()) {
    val counter by viewModel.counter.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Counter: $counter")
        Button(onClick = { viewModel.incrementCounter() }) {
            Text("Increment")
        }
    }
}
class AnotherViewModel : ViewModel() {
    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter.asStateFlow()

    fun incrementCounter() {
        _counter.value++
    }
}