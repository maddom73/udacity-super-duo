package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class scoresAdapter extends RecyclerView.Adapter<scoresAdapter.ViewHolder> {


    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    private Cursor cursor;
    Context mContext;

    public scoresAdapter(Context context){
        mContext = context;
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder (LayoutInflater.from(parent.getContext()).inflate(R.layout.scores_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        cursor.moveToPosition(position);

        holder.home_name.setText(cursor.getString(COL_HOME));
        holder.away_name.setText(cursor.getString(COL_AWAY));
        holder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        holder.match_id = cursor.getDouble(COL_ID);
        holder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        holder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)
        ));

        holder.match_day.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                cursor.getInt(COL_LEAGUE)));
        holder.league.setText(Utilies.getLeague(cursor.getInt(COL_LEAGUE)));
        holder.share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add Share Action
                mContext.startActivity(createShareForecastIntent(holder.home_name.getText() + " "
                        + holder.score.getText() + " " + holder.away_name.getText() + " "));
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView home_name;
        public TextView away_name;
        public TextView score;
        public TextView match_day;
        public TextView league;
        public Button share_button;
        public ImageView home_crest;
        public ImageView away_crest;
        public double match_id;

        public ViewHolder(View view) {
            super(view);
            home_name = (TextView) view.findViewById(R.id.home_name);
            away_name = (TextView) view.findViewById(R.id.away_name);
            score = (TextView) view.findViewById(R.id.score_textview);
            home_crest = (ImageView) view.findViewById(R.id.home_crest);
            away_crest = (ImageView) view.findViewById(R.id.away_crest);
            match_day = (TextView) view.findViewById(R.id.matchday_textview);
            league = (TextView) view.findViewById(R.id.league_textview);
            share_button = (Button) view.findViewById(R.id.share_button);

        }
    }


    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
