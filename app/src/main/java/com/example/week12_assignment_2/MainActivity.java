package com.example.week12_assignment_2;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button btnPrev, btnNext, btnPlay;
    int[] musicResIds = {R.raw.idle1, R.raw.iu2, R.raw.ive3};
    int selectedMusic = 0;
    TextView tvCurrentTime, tvTotalTime, tvCurMusic;
    SeekBar seekMP3;
    MediaPlayer mPlayer;
    boolean PAUSED = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnPlay = findViewById(R.id.btnPlay);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        seekMP3 = findViewById(R.id.seekBar);
        tvCurMusic = findViewById(R.id.tvCurMusic);
        mPlayer = MediaPlayer.create(getApplicationContext(), musicResIds[selectedMusic % 3]);

        final String[] musicNames = {"여자이이들 - 나는 아픈 건 딱 질색이니까", "아이유 - Shopper", "아이브 - 해야"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, musicNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlayer.stop();
                selectedMusic = position;
                mPlayer = MediaPlayer.create(getApplicationContext(), musicResIds[selectedMusic % 3]);
                tvCurMusic.setText(musicNames[selectedMusic % 3]);
                mPlayer.start();
//                btnPlay.setText("일시정지");
                PAUSED = false;
                makeThread();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMusic--;
                mPlayer.stop();
                mPlayer = MediaPlayer.create(getApplicationContext(), musicResIds[selectedMusic % 3]);
                tvCurMusic.setText(musicNames[selectedMusic % 3]);
                mPlayer.start();
//                btnPlay.setText("일시정지");
                PAUSED = false;
                makeThread();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMusic++;
                mPlayer.stop();
                mPlayer = MediaPlayer.create(getApplicationContext(), musicResIds[selectedMusic % 3]);
                tvCurMusic.setText(musicNames[selectedMusic % 3]);
                mPlayer.start();
//                btnPlay.setText("일시정지");
                PAUSED = false;
                makeThread();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PAUSED == false) {
                    mPlayer.pause();
//                    btnPlay.setText("재생");
                    PAUSED = true;
                } else {
                    tvCurMusic.setText(musicNames[selectedMusic % 3]);
                    mPlayer.start();
//                    btnPlay.setText("일시정지");
                    PAUSED = false;
                }
                makeThread();
            }
        });

        seekMP3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mPlayer.seekTo(progress);
                }

            }
        });

    }

    void makeThread() {
        new Thread() {
            public void run() {
                // 음악이 계속 작동 중이라면
                final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
                if (mPlayer == null) return;
                seekMP3.setMax(mPlayer.getDuration()); // 음악의 시간을 최대로 설정
                tvTotalTime.setText(String.format(timeFormat.format(mPlayer.getDuration())));

                while (mPlayer.isPlaying()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekMP3.setProgress(mPlayer.getCurrentPosition());  // runOnUIThread 에서 안해도 no error.
                            tvCurrentTime.setText(String.format(timeFormat.format(mPlayer.getCurrentPosition())));
                        }
                    });
                    SystemClock.sleep(100);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        btnPlay.setText("재생");
                        PAUSED = true;

                    }
                });
            }
        }.start();
    }
}