package com.example.lr3.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lr3.R

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val firstFragment = NotesListFragment()

        //if we're being restored from a previous state or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, firstFragment).commit()
        }

    }

}
