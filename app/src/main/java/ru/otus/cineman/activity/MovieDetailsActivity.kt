//package ru.otus.cineman.activity
//
//import android.app.Activity
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.CheckBox
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import mu.KLogging
//import ru.otus.cineman.R
//import ru.otus.cineman.activity.MainActivity.Companion.MOVIE_ID
//import ru.otus.cineman.activity.MainActivity.Companion.IS_LIKED
//import ru.otus.cineman.activity.MainActivity.Companion.USER_COMMENT
//
//class MovieDetailsActivity : AppCompatActivity() {
//    companion object : KLogging()
//
//    lateinit var isLikedFilm: CheckBox
//    lateinit var userComment: EditText
//
//    var filmId: Int = Int.MAX_VALUE
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.movie_details)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        filmId = intent.getIntExtra("film_id", Int.MAX_VALUE)
//        val imageId = intent.getIntExtra("image_id", Int.MAX_VALUE)
//        val descriptionId = intent.getIntExtra("film_description", Int.MAX_VALUE)
//        val isLiked = intent.getBooleanExtra("is_liked", false)
//        val userCommentValue = intent.getStringExtra("user_comment")
//        val position = intent?.getIntExtra("position", Int.MAX_VALUE)
//
//        val filmImage = findViewById<ImageView>(R.id.film_poster)
//        val filmDescription = findViewById<TextView>(R.id.film_details_description)
//        isLikedFilm = findViewById(R.id.checked_like)
//        userComment = findViewById(R.id.user_comment)
//
//        isLikedFilm.isChecked = isLiked
//        userComment.setText(userCommentValue)
//
//
//        filmImage.setImageResource(imageId)
//        filmDescription.setText(descriptionId)
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                logger.info { "Comment: ${userComment.text}" }
//                logger.info { "Is Liked: ${isLikedFilm.isChecked}" }
//                val intentPreviewFilms = Intent()
//                intentPreviewFilms.putExtra(IS_LIKED, isLikedFilm.isChecked)
//                intentPreviewFilms.putExtra(USER_COMMENT, userComment.text.toString())
//                intentPreviewFilms.putExtra(MOVIE_ID, filmId)
//                setResult(Activity.RESULT_OK, intentPreviewFilms)
//                finish()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
//}
